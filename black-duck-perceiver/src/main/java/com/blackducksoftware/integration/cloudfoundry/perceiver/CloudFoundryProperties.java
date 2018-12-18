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

        public static final String encodeSecretForLog(final CloudFoundryProperties.Oauth2 oauth) {
            String ret = null;
            final String workerPasswd = oauth.getClient().getClientSecret();
            if (workerPasswd != null && !workerPasswd.isEmpty()) {
                final StringBuffer encoded = new StringBuffer();
                for (int i = 0; i < workerPasswd.length(); i++) {
                    encoded.append("*");
                }
                encoded.replace(0, 3, workerPasswd.substring(0, 3));
                ret = encoded.toString();
            }
            return ret;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("    ").append("client:").append("\n");
            sb.append("      ").append("id: ").append(client.getId()).append("\n");
            sb.append("      ").append("grantType: ").append(client.getGrantType()).append("\n");
            sb.append("      ").append("clientId: ").append(client.getClientId()).append("\n");
            sb.append("      ").append("accessTokenUri: ").append(client.getAccessTokenUri()).append("\n");
            sb.append("      ").append("scope: ").append(client.getScope()).append("\n");
            sb.append("      ").append("clientSecret: ").append(CloudFoundryProperties.Oauth2.encodeSecretForLog(this)).append("\n");
            sb.append("      ").append("clientAuthenticationScheme: ").append(client.getClientAuthenticationScheme()).append("\n");
            sb.append("      ").append("authorizationScheme: ").append(client.getAuthenticationScheme()).append("\n");
            sb.append("      ").append("tokenName: ").append(client.getTokenName()).append("\n");

            return sb.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("  ").append("baseUrl: ").append(baseUrl).append("\n");
        sb.append("  ").append("organization: ").append(organization).append("\n");
        sb.append("  ").append("space: ").append(space).append("\n");
        sb.append("  ").append("skipSslValidation: ").append(skipSslValidation).append("\n");
        sb.append("  ").append("oauth2:").append("\n");
        sb.append(oauth2.toString());

        return sb.toString();
    }
}
