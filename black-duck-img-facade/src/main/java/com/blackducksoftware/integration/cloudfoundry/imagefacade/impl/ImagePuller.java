/*
 * Copyright (C) 2018 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.cloudfoundry.imagefacade.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.Duration;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.DownloadApplicationDropletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.concurrent.ListenableFuture;

import com.blackducksoftware.integration.cloudfoundry.imagefacade.api.Image;
import com.blackducksoftware.integration.cloudfoundry.imagefacade.exception.ImagePullerException;
import com.blackducksoftware.integration.cloudfoundry.imagefacade.model.ImagePullerResult;

import reactor.core.scheduler.Schedulers;

/**
 * @author fisherj
 *
 */
public class ImagePuller {
    private static final Logger logger = LoggerFactory.getLogger(ImagePuller.class);

    private final String dropletLocation;

    private final CloudFoundryClient cloudFoundryClient;

    private final int retries;

    private final Duration pullTimeout;

    public ImagePuller(CloudFoundryClient cloudFoundryClient, String dropletLocation, int retries, Duration pullTimeout) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.dropletLocation = dropletLocation;
        this.retries = retries;
        this.pullTimeout = pullTimeout;
    }

    @Async
    public ListenableFuture<ImagePullerResult> pull(Image image) throws Exception {
        logger.trace("start pull of image for app: {}", image.asAppId());

        Path location = Paths.get(dropletLocation, image.getPullSpec() + ".tar");
        logger.trace("image pull, file location: {}", location);

        Status status = new Status(retries);
        do {
            logger.trace("pull image {}", status);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            Path retPath = cloudFoundryClient.applicationsV2()
                    .downloadDroplet(DownloadApplicationDropletRequest.builder()
                            .applicationId(image.asAppId())
                            .build())
                    .onErrorMap((e) -> {
                        logger.error("fatal issue downloading image", e);
                        return new ImagePullerException("issue with remote image", e, image);
                    })
                    .publishOn(Schedulers.elastic())
                    .reduceWith(() -> {
                        try {
                            return Files.newOutputStream(location);
                        } catch (IOException e) {
                            // This will cause the processing of this Reactive Stream to stop immediately
                            throw new ImagePullerException("issue with output file", e, image);
                        }
                    }, (out, bytes) -> {
                        try {
                            md.update(bytes);
                            out.write(bytes);
                            return out;
                        } catch (IOException e) {
                            // This will cause the processing of this Reactive Stream to stop immediately
                            throw new ImagePullerException("issue writing data", e, image);
                        }
                    })
                    .doOnSuccessOrError((out, t) -> {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            // This will cause the processing of this Reactive Stream to stop immediately
                            throw new ImagePullerException(e, image);
                        }
                    })
                    .doOnSuccess(out -> {
                        // Check the retrieved file checksum against the expected to ensure read parity
                        String computedSha = convertDigestBytesToHex(md.digest());
                        logger.trace("computed file hash: {}, for image: {}", computedSha, image);
                        if (!image.asSha().equals(computedSha)) {
                            logger.debug("computed sha: {} did not match expected: {}", computedSha, image.asSha());
                            status.shaInvalid();
                        } else {
                            status.pass();
                        }
                    })
                    .doOnError((e) -> {
                        // No need to update "status" here as exception will cause processing to stop
                        logger.error("image pull fatal error:", e);
                    })
                    .map(out -> location)
                    .block(pullTimeout);
            if (retPath == null) {
                logger.debug("image pull of image: {} timed out", image);
                status.timeout();
            }
        } while (status.shouldRetry());

        logger.trace("end pull of image: {}", image.asAppId());
        return new AsyncResult<ImagePullerResult>(new ImagePullerResult(image, status.getError()));
    }

    private static String convertDigestBytesToHex(byte[] mdBytes) {
        StringBuffer hexString = new StringBuffer();
        for (byte mdByte : mdBytes) {
            hexString.append(Integer.toString((mdByte & 0xff) + 0x100, 16).substring(1));
        }

        return hexString.toString();
    }

    private static class Status {
        private final int retries;

        private int attempt;

        private boolean pass;

        private String error = null;

        public Status(int retries) {
            this.retries = retries;
            attempt = 1;
            pass = true;
        }

        public void timeout() {
            attempt++;
            pass = false;
            error = "image pull timeout";
        }

        public void shaInvalid() {
            attempt++;
            pass = false;
            error = "computed checksum did not match expected";
        }

        public void pass() {
            pass = true;
            error = null;
        }

        public boolean shouldRetry() {
            boolean ret = true;
            if ((attempt > retries) || pass) {
                ret = false;
            }

            return ret;
        }

        public String getError() {
            return error;
        }

        @Override
        public String toString() {
            return String.format("attempt %d of %d, last attempt error: %s", attempt, retries, error);
        }
    }
}
