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
package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.iface.ICloudControllerEventMonitorService;

/**
 * This service runs continuously and monitors the cloud controller for new events
 * and signals the perceptor to queue a new scan when an event is received.
 *
 * @author fisherj
 *
 */
@Service
public class CloudControllerEventMonitorService implements ICloudControllerEventMonitorService {
    private static final Logger logger = LoggerFactory.getLogger(CloudControllerEventMonitorService.class);

    private static final String APPS_ENDPOINT = "apps";

    private RestTemplate perceiverRestTemplate;

    private UriComponentsBuilder perceiverUriBuilder;

    @Autowired
    public CloudControllerEventMonitorService(RestTemplate perceiverRestTemplate,
            @Value("${perceiver.baseUrl}") String perceiverBaseUrlString,
            @Value("${perceiver.port}") int perceiverPort) {
        this.perceiverRestTemplate = perceiverRestTemplate;
        perceiverUriBuilder = UriComponentsBuilder.fromUriString(perceiverBaseUrlString).port(perceiverPort).path(APPS_ENDPOINT);
    }

    @Override
    public boolean registerId(UUID appId) {
        logger.debug("Adding app: {} to receive events", appId);
        ResponseEntity<UUID> resp = perceiverRestTemplate.postForEntity(perceiverUriBuilder.build().toUri(), appId, UUID.class);
        if (resp.getStatusCode().is2xxSuccessful() && appId.equals(resp.getBody())) {
            return true;
        } else {
            logger.warn("unable to register id: {}, perceiver returned response code: {}, message: {}", appId, resp.getStatusCode().toString(),
                    resp.getStatusCode().getReasonPhrase());
            return false;
        }
    }

    // TODO jfisher Generate CFPerceiver REST Server endpoint to unregisterId (DELETE)
    @Override
    public boolean unregisterId(UUID appId) {
        logger.debug("Removing app: {} from receiveing events", appId);
        perceiverRestTemplate.delete(perceiverUriBuilder.pathSegment(appId.toString()).build().encode().toUri());
        return true;
    }
}
