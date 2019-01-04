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

import java.net.MalformedURLException;
import java.net.URL;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.ClientCredentialsGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.UaaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author fisherj
 *
 */
@Configuration
@Lazy
public class CloudControllerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(CloudControllerConfiguration.class);

    private final CloudFoundryProperties cfProperties;

    @Autowired
    public CloudControllerConfiguration(final CloudFoundryProperties cfProperties) {
        this.cfProperties = cfProperties;
    }

    @Bean
    public DefaultConnectionContext connectionContext() throws MalformedURLException {
        final URL baseUrl = new URL(cfProperties.getBaseUrl());
        return DefaultConnectionContext.builder()
                .apiHost(baseUrl.getHost())
                .skipSslValidation(cfProperties.isSkipSslValidation())
                .build();
    }

    @Bean
    public ClientCredentialsGrantTokenProvider clientCredentialsGrantTokenProvider() {
        return ClientCredentialsGrantTokenProvider.builder()
                .clientId(cfProperties.getOauth2().getClient().getClientId())
                .clientSecret(cfProperties.getOauth2().getClient().getClientSecret()).build();
    }

    @Bean
    public CloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, ClientCredentialsGrantTokenProvider tokenProvider)
            throws MalformedURLException {
        logger.trace("In reactorCloudFoundryClient with properties: \n{}", cfProperties);

        return ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(tokenProvider)
                .build();
    }

    @Bean
    public UaaClient uaaClient(ConnectionContext connectionContext, ClientCredentialsGrantTokenProvider tokenProvider)
            throws MalformedURLException {
        logger.trace("In reactorCloudFoundryClient with properties: \n{}", cfProperties);

        return ReactorUaaClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(tokenProvider)
                .build();
    }
}
