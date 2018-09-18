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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fisherj
 *
 */
@Configuration
@ConfigurationProperties(prefix = "broker")
public class BrokerProperties {
    private String baseUrl;

    private int port;

    private Authentication basicAuth;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Authentication getBasicAuth() {
        return basicAuth;
    }

    public void setBasicAuth(Authentication basicAuth) {
        this.basicAuth = basicAuth;
    }

    public static class Authentication {
        private String userEnvVblName;

        private String passEnvVblName;

        public String getUserEnvVblName() {
            return userEnvVblName;
        }

        public void setUserEnvVblName(String userEnvVblName) {
            this.userEnvVblName = userEnvVblName;
        }

        public String getPassEnvVblName() {
            return passEnvVblName;
        }

        public void setPassEnvVblName(String passEnvVblName) {
            this.passEnvVblName = passEnvVblName;
        }
    }
}
