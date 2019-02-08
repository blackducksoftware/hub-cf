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
package com.blackducksoftware.integration.cloudfoundry.imagefacade.model;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.blackducksoftware.integration.cloudfoundry.imagefacade.api.Image;
import com.blackducksoftware.integration.cloudfoundry.imagefacade.exception.ImagePullerException;
import com.blackducksoftware.integration.cloudfoundry.imagefacade.impl.ImagePuller;

/**
 * @author fisherj
 *
 */
public final class ImageModel implements ListenableFutureCallback<ImagePullerResult> {
    private static final Logger logger = LoggerFactory.getLogger(ImageModel.class);

    private ModelState modelState;

    private Map<Image, ImageStatus> status = new HashMap<>();

    private ImagePuller imagePuller;

    public ImageModel(ImagePuller imagePuller) {
        modelState = ModelState.READY;
        this.imagePuller = imagePuller;
    }

    public boolean pullImage(Image image, String dropletLocation) throws Exception {
        if (ModelState.READY != modelState) {
            logger.error("unable to pull image  for app {}, image pull in progress {}", image.asAppId(), modelState);
            return false;
        }

        logger.info("about to start pulling image for app {} -- model state {}", image.asAppId(), modelState);
        if (null != status.put(image, ImageStatus.INPROGRESS)) {
            logger.warn("ImageModel status already contains value for key: {}, replacing value", image);
        }
        modelState = ModelState.PULLING;
        ListenableFuture<ImagePullerResult> f = imagePuller.pull(image);
        f.addCallback(this);
        logger.trace("added callback for image: {}, exiting pullImage", image);
        return true;
    }

    public ImageStatus getImageStatus(Image image) {
        return status.getOrDefault(image, ImageStatus.UNKNOWN);
    }

    @Override
    public void onSuccess(ImagePullerResult result) {
        logger.debug("Completed image pull task for image: {}", result.getImage());
        if (!result.getError().isPresent()) {
            logger.info("Successfully finished image pull for image: {}", result.getImage());
            status.put(result.getImage(), ImageStatus.DONE);
        } else {
            logger.info("finished image pull for image: {} with error: {}", result.getImage(), result.getError().get());
            status.put(result.getImage(), ImageStatus.ERROR);
        }
        modelState = ModelState.READY;
    }

    @Override
    public void onFailure(Throwable ex) {
        logger.error("Fatal error completing image pull task", ex);
        if (ex instanceof ImagePullerException) {
            ImagePullerException ipe = (ImagePullerException) ex;
            logger.debug("attempting to remove image: {} from status map", ipe.getImage());
            status.remove(ipe.getImage());
        }
        modelState = ModelState.READY;
    }
}
