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

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

/**
 * @author fisherj
 *
 */
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
    private String realm;

    private ServiceProperties eventMonitorService = new ServiceProperties();

    private ServiceProperties dumperService = new ServiceProperties();

    private ServiceProperties scanResultsService = new ServiceProperties();

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public ServiceProperties getEventMonitorService() {
        return eventMonitorService;
    }

    public void setEventMonitorService(ServiceProperties eventMonitorService) {
        this.eventMonitorService = eventMonitorService;
    }

    public ServiceProperties getDumperService() {
        return dumperService;
    }

    public void setDumperService(ServiceProperties dumperService) {
        this.dumperService = dumperService;
    }

    public ServiceProperties getScanResultsService() {
        return scanResultsService;
    }

    public void setScanResultsService(ServiceProperties scanResultsService) {
        this.scanResultsService = scanResultsService;
    }

    public static class ServiceProperties {
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration pollingPeriod;

        public Duration getPollingPeriod() {
            return pollingPeriod;
        }

        public void setPollingPeriod(Duration pollingPeriod) {
            this.pollingPeriod = pollingPeriod;
        }
    }
}
