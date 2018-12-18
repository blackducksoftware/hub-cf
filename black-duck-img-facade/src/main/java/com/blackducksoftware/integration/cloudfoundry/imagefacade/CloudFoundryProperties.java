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
package com.blackducksoftware.integration.cloudfoundry.imagefacade;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * @author fisherj
 *
 */
@Configuration
@ConfigurationProperties(prefix = "cf")
public class CloudFoundryProperties {
    private String baseUrl;

    private String organization;

    private String space;

    private boolean skipSslValidation;

    private Oauth2 oauth2;

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(final String organization) {
        this.organization = organization;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(final String space) {
        this.space = space;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isSkipSslValidation() {
        return skipSslValidation;
    }

    public void setSkipSslValidation(final boolean skipSslValidation) {
        this.skipSslValidation = skipSslValidation;
    }

    public Oauth2 getOauth2() {
        return oauth2;
    }

    public void setOauth2(final Oauth2 oauth2) {
        this.oauth2 = oauth2;
    }

    public static class Oauth2 {
        @NestedConfigurationProperty
        private ClientCredentialsResourceDetails client;

        public ClientCredentialsResourceDetails getClient() {
            return client;
        }

        public void setClient(final ClientCredentialsResourceDetails client) {
            this.client = client;
        }
    }
}
