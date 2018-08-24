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

import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author fisherj
 *
 */
@Component
public class ScanResultsService {
    private static final Logger logger = LoggerFactory.getLogger(ScanResultsService.class);

    @SuppressWarnings("unused")
    private final ReactorCloudFoundryClient reactorCloudFoundryClient;

    @Autowired
    public ScanResultsService(ReactorCloudFoundryClient reactorCloudFoundryClient,
            BindingInstanceService bindingInstanceService) {
        this.reactorCloudFoundryClient = reactorCloudFoundryClient;
    }

    @Scheduled(fixedRateString = "#{${application.scan-results-service.polling-period-seconds} * 1000}")
    public void HandleScanResults() {
        logger.debug("Starting query perceptor for scan results");
    }
}
