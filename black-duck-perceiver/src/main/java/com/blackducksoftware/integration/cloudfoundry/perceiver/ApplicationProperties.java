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

    private AnalyticsProperties analytics = new AnalyticsProperties();

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

    public AnalyticsProperties getAnalytics() {
        return analytics;
    }

    public void setAnalytics(AnalyticsProperties analytics) {
        this.analytics = analytics;
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

    public static class AnalyticsProperties {
        private boolean enabled;

        private IntegrationVendor artifactId;

        private String artifactVersion;

        private IntegrationSource integrationSource;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public IntegrationVendor getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(String artifactId) {
            try {
                this.artifactId = IntegrationVendor.valueOf(artifactId);
            } catch (IllegalArgumentException e) {
                this.artifactId = IntegrationVendor.UNKNOWN;
            }
        }

        public String getArtifactVersion() {
            return artifactVersion;
        }

        public void setArtifactVersion(String artifactVersion) {
            this.artifactVersion = artifactVersion;
        }

        public IntegrationSource getIntegrationSource() {
            return integrationSource;
        }

        public void setIntegrationSource(String integrationSource) {
            try {
                this.integrationSource = IntegrationSource.valueOf(integrationSource);
            } catch (IllegalArgumentException e) {
                this.integrationSource = IntegrationSource.UNKNOWN;
            }
        }
    }
}
