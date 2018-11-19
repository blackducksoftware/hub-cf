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

import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.ClientCredentialsGrantTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fisherj
 *
 */
@Configuration
public class CloudControllerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(CloudControllerConfiguration.class);

    private final CloudFoundryProperties cfProperties;

    @Autowired
    public CloudControllerConfiguration(CloudFoundryProperties cfProperties) {
        this.cfProperties = cfProperties;
    }

    @Bean
    public ReactorCloudFoundryClient reactorCloudFoundryClient() throws MalformedURLException {
        logger.trace("In reactorCloudFoundryClient with properties: \n{}", cfProperties);
        URL baseUrl = new URL(cfProperties.getBaseUrl());
        DefaultConnectionContext.Builder conxCtxBuilder = DefaultConnectionContext.builder()
                .apiHost(baseUrl.getHost())
                .port(baseUrl.getPort())
                .skipSslValidation(cfProperties.isSkipSslValidation());

        ClientCredentialsGrantTokenProvider.Builder oauthTokenProviderBuilder = ClientCredentialsGrantTokenProvider.builder()
                .clientId(cfProperties.getOauth2().getClient().getClientId())
                .clientSecret(cfProperties.getOauth2().getClient().getClientSecret());

        return ReactorCloudFoundryClient.builder()
                .connectionContext(conxCtxBuilder.build())
                .tokenProvider(oauthTokenProviderBuilder.build())
                .build();
    }
}
