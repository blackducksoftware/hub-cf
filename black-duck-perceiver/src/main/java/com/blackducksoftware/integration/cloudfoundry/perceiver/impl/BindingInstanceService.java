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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.cloudfoundry.perceiver.api.BindResource;
import com.blackducksoftware.integration.cloudfoundry.perceiver.api.BindingInstance;
import com.blackducksoftware.integration.cloudfoundry.perceiver.api.BindingInstanceRequestResponse;
import com.blackducksoftware.integration.cloudfoundry.perceiver.api.BindingProvisionRequest;
import com.blackducksoftware.integration.cloudfoundry.perceiver.api.HubProjectParameters;

/**
 * @author fisherj
 *
 */
public class BindingInstanceService {
    public static final Logger logger = LoggerFactory.getLogger(BindingInstanceService.class);

    private final RestTemplate restTemplate;

    private final String brokerBaseUrlString;

    private final int brokerPort;

    public BindingInstanceService(RestTemplate restTemplate,
            String brokerBaseUrlString,
            int brokerPort) {
        this.restTemplate = restTemplate;
        this.brokerBaseUrlString = brokerBaseUrlString;
        this.brokerPort = brokerPort;
    }

    public Optional<Map<String, UUID>> toAppIdByBindingId() {
        logger.debug("Getting app ids by binding ids from binding service backend");
        URI brokerUri;
        try {
            URI brokerBaseUri = new URI(brokerBaseUrlString);
            brokerUri = new URI(brokerBaseUri.getScheme(),
                    null,
                    brokerBaseUri.getHost(),
                    brokerPort,
                    "/binding_instances",
                    null, null);
        } catch (URISyntaxException e) {
            logger.error("URI to broker backend not created successfully. app ids by binding ids not retrieved.", e);
            return Optional.ofNullable(null);
        }
        logger.debug("Using URI: {}", brokerUri);
        BindingInstanceRequestResponse resp = restTemplate.getForObject(brokerUri, BindingInstanceRequestResponse.class);
        return Optional.ofNullable(convertToAppIdByBindingId(resp.getBindingsById()));
    }

    public boolean create(String bindingId, String resourceId, Optional<BindResource> bindResource, Optional<HubProjectParameters> hubProjParams) {
        logger.debug("Creating binding with bindingId: {} for serviceId: {} in binding service backend", bindingId, resourceId);
        URI brokerUri;
        try {
            URI brokerBaseUri = new URI(brokerBaseUrlString);
            brokerUri = new URI(brokerBaseUri.getScheme(),
                    null,
                    brokerBaseUri.getHost(),
                    brokerPort,
                    "/binding_instances",
                    null, null);
        } catch (URISyntaxException e) {
            logger.error("URI to broker backend not created successfully. binding not created in broker", e);
            return false;
        }
        BindingProvisionRequest bindingRequest = new BindingProvisionRequest(bindingId, resourceId, bindResource.orElse(null), hubProjParams.orElse(null));
        ResponseEntity<BindingInstance> resp = restTemplate.postForEntity(brokerUri, bindingRequest, BindingInstance.class);
        return true;
    }

    private Map<String, UUID> convertToAppIdByBindingId(Map<String, BindingInstance> bindingsById) {
        if (bindingsById == null || bindingsById.isEmpty()) {
            return null;
        }

        return bindingsById.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getAppGuid()));
    }
}
