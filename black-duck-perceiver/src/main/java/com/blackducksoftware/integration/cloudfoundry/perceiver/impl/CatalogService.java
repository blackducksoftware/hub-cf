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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.cloudfoundry.v2.model.Catalog;

/**
 * @author fisherj
 *
 */
public class CatalogService {
    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    private final RestTemplate restTemplate;

    private final String brokerBaseUrlString;

    private final int brokerPort;

    public CatalogService(RestTemplate restTemplate, String brokerBaseUrlString, int brokerPort) {
        this.restTemplate = restTemplate;
        this.brokerBaseUrlString = brokerBaseUrlString;
        this.brokerPort = brokerPort;
    }

    public Catalog getCatalog() {
        logger.info("Getting catalog from broker catalog service");
        URI brokerUri;
        try {
            URI brokerBaseUri = new URI(brokerBaseUrlString);
            brokerUri = new URI(brokerBaseUri.getScheme(),
                    null,
                    brokerBaseUri.getHost(),
                    brokerPort,
                    "/v2/catalog",
                    null, null);
        } catch (URISyntaxException e) {
            logger.error("URI to broker backend not created successfully. app ids by binding ids not retrieved.", e);
            return null;
        }
        return restTemplate.getForObject(brokerUri, Catalog.class);
    }
}
