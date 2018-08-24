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
package com.blackducksoftware.integration.cloudfoundry.perceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import com.blackducksoftware.integration.cloudfoundry.perceiver.iface.IControllerService;

/**
 * @author fisherj
 *
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableOAuth2Client
public class BDServiceBrokerPerceiver {
    private static final Logger logger = LoggerFactory.getLogger(BDServiceBrokerPerceiver.class);

    private IControllerService controllerService;

    @Autowired
    public BDServiceBrokerPerceiver(IControllerService controllerService) {
        this.controllerService = controllerService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BDServiceBrokerPerceiver.class, args);
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent appEvent) {
        logger.trace("received ContextRefreshEvent: displayName: {}, startTime: {}", appEvent.getApplicationContext().getDisplayName(),
                appEvent.getApplicationContext().getStartupDate());
        // Kickoff main background thread to listen for Cloud Foundry Cloud Controller events
        controllerService.run();
    }
}
