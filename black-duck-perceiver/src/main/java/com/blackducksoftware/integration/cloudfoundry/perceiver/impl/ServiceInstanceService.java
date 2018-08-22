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
package com.blackducksoftware.integration.cloudfoundry.perceiver.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.cloudfoundry.perceiver.api.ServiceProvisionRequest;

/**
 * @author fisherj
 *
 */
public class ServiceInstanceService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceService.class);

    private final RestTemplate restTemplate;

    private final String brokerBaseUrlString;

    private final int brokerPort;

    public ServiceInstanceService(RestTemplate restTemplate,
            String brokerBaseUrlString,
            int brokerPort) {
        this.restTemplate = restTemplate;
        this.brokerBaseUrlString = brokerBaseUrlString;
        this.brokerPort = brokerPort;
    }

    public Set<String> getServiceInstances() {
        logger.debug("Getting service instance ids from service instance backend");
        URI brokerUri;
        try {
            URI brokerBaseUri = new URI(brokerBaseUrlString);
            brokerUri = new URI(brokerBaseUri.getScheme(),
                    null,
                    brokerBaseUri.getHost(),
                    brokerPort,
                    "/service_instances",
                    null, null);
        } catch (URISyntaxException e) {
            logger.error("URI to broker backend not created successfully. app ids by binding ids not retrieved.", e);
            return Collections.emptySet();
        }
        Set<String> resp = restTemplate.getForObject(brokerUri, Set.class);
        return resp;
    }

    public boolean create(String id) {
        logger.debug("Creating service instance with id: {} in service instance backend", id);
        if (null == id) {
            logger.warn("Invalid id. Cannot be null");
            throw new IllegalStateException("id cannot be null");
        }
        ServiceProvisionRequest body = new ServiceProvisionRequest(id);
        URI brokerUri;
        try {
            URI brokerBaseUri = new URI(brokerBaseUrlString);
            brokerUri = new URI(brokerBaseUri.getScheme(),
                    null,
                    brokerBaseUri.getHost(),
                    brokerPort,
                    "/service_instances",
                    null, null);
        } catch (URISyntaxException e) {
            logger.error("URI to broker backend not created successfully. app ids by binding ids not retrieved.", e);
            return false;
        }
        ResponseEntity<String> resp = restTemplate.postForEntity(brokerUri, body, String.class);
        logger.info("Create service instance: {}, returned status: {}", resp.getBody(), resp.getStatusCode());

        return (HttpStatus.CREATED == resp.getStatusCode());
    }
}
