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
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.client.HttpClientErrorException;

import com.blackducksoftware.integration.cloudfoundry.perceiver.iface.IControllerService;
import com.blackducksoftware.integration.cloudfoundry.perceiver.impl.CatalogService;
import com.blackducksoftware.integration.cloudfoundry.perceiver.impl.DumperService;
import com.blackducksoftware.integration.cloudfoundry.perceiver.impl.ScanResultsService;

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

    private DumperService dumperService;

    private ScanResultsService scanResultsService;

    private ApplicationProperties appProperties;

    private TaskScheduler taskScheduler;

    private CatalogService catalogService;

    @Autowired
    public BDServiceBrokerPerceiver(ApplicationProperties appProperties, TaskScheduler taskScheduler, CatalogService catalogService) {
        this.appProperties = appProperties;
        this.taskScheduler = taskScheduler;
        this.catalogService = catalogService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BDServiceBrokerPerceiver.class, args);
    }

    @Autowired
    @Lazy
    public void setIControllerService(IControllerService controllerService) {
        this.controllerService = controllerService;
    }

    @Autowired
    @Lazy
    public void setDumperService(DumperService dumperService) {
        this.dumperService = dumperService;
    }

    @Autowired
    @Lazy
    public void setScanResultsService(ScanResultsService scanResultsService) {
        this.scanResultsService = scanResultsService;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent appEvent) throws InterruptedException {
        logger.trace("received ContextRefreshEvent: displayName: {}, startTime: {}", appEvent.getApplicationContext().getDisplayName(),
                appEvent.getApplicationContext().getStartupDate());

        waitForServiceBroker();

        logger.info("Connection to Service Broker established. Starting services");

        // Kickoff main background thread to listen for Cloud Foundry Cloud Controller events
        controllerService.run();
        // Schedule the services that run periodically
        taskScheduler.scheduleAtFixedRate(dumperService, appProperties.getDumperService().getPollingPeriod());
        taskScheduler.scheduleAtFixedRate(scanResultsService, appProperties.getScanResultsService().getPollingPeriod());
    }

    // This method will block until the connection to the service broker is made
    private void waitForServiceBroker() throws InterruptedException {
        boolean connectionEstablished = false;
        try {
            logger.trace("Attempt to reach Service Broker via catalog endpoint");
            connectionEstablished = (catalogService.getCatalog() != null ? true : false);
        } catch (HttpClientErrorException e) {
            logger.trace("Service Broker unreachable", e);
            connectionEstablished = false;
        }
        while (!connectionEstablished) {
            logger.debug("Service Broker: UNAVAILABLE");
            Thread.sleep(60000);
            try {
                logger.trace("Attempt to reach Service Brojer via catalog endpoint");
                connectionEstablished = (catalogService.getCatalog() != null ? true : false);
            } catch (HttpClientErrorException e) {
                logger.trace("Service Broker unreachable", e);
                connectionEstablished = false;
            }
        }
        logger.info("Service Broker: AVAILABLE");
    }
}
