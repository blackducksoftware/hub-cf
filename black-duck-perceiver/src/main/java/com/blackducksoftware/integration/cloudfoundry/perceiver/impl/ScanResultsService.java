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

import org.cloudfoundry.client.CloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author fisherj
 *
 */
@Service
public class ScanResultsService implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ScanResultsService.class);

    private CloudFoundryClient cloudFoundryClient;

    private BindingInstanceService bindingInstanceService;

    @Autowired
    public ScanResultsService(BindingInstanceService bindingInstanceService) {
        this.bindingInstanceService = bindingInstanceService;
    }

    @Autowired
    @Lazy
    public void setCloudFoundryClient(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public void run() {
        logger.debug("Starting query perceptor for scan results");
    }
}
