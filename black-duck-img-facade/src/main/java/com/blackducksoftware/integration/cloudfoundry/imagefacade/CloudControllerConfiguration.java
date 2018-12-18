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

import java.net.MalformedURLException;
import java.net.URL;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.organizations.OrganizationSummary;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.ClientCredentialsGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
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
    public CloudControllerConfiguration(final CloudFoundryProperties cfProperties) {
        this.cfProperties = cfProperties;
    }

    @Bean
    public DefaultConnectionContext defaultConnectionContext() throws MalformedURLException {
        final URL baseUrl = new URL(cfProperties.getBaseUrl());
        return DefaultConnectionContext.builder()
                .apiHost(baseUrl.getHost())
                .port(baseUrl.getPort())
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
    public ReactorCloudFoundryClient reactorCloudFoundryClient(final DefaultConnectionContext connectionContext,
            final ClientCredentialsGrantTokenProvider oauthTokenProviderBuilder) throws MalformedURLException {
        logger.trace("In reactorCloudFoundryClient with properties: \n{}", cfProperties);

        return ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(oauthTokenProviderBuilder)
                .build();
    }

    @Bean
    public ReactorDopplerClient reactorDopplerClient(final DefaultConnectionContext connectionContext,
            final ClientCredentialsGrantTokenProvider oauthTokenProviderBuilder) throws MalformedURLException {
        logger.trace("In reactorCloudFoundryClient with properties: \n{}", cfProperties);

        return ReactorDopplerClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(oauthTokenProviderBuilder)
                .build();
    }

    @Bean
    public ReactorUaaClient reactorUaaClient(final DefaultConnectionContext connectionContext,
            final ClientCredentialsGrantTokenProvider oauthTokenProviderBuilder) throws MalformedURLException {
        logger.trace("In reactorCloudFoundryClient with properties: \n{}", cfProperties);

        return ReactorUaaClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(oauthTokenProviderBuilder)
                .build();
    }

    @Bean
    public CloudFoundryOperations reactorCloudFoundryOperations(final ReactorCloudFoundryClient cloudFoundryClient, final ReactorDopplerClient dopplerClient,
            final ReactorUaaClient uaaClient) throws MalformedURLException {
        logger.trace("In reactorCloudFoundryOperations with properties: \n{}", cfProperties);

        final CloudFoundryOperations cloudFoundryOperation = DefaultCloudFoundryOperations.builder()
                .cloudFoundryClient(cloudFoundryClient)
                .dopplerClient(dopplerClient)
                .uaaClient(uaaClient)
                .organization(cfProperties.getOrganization())
                .space(cfProperties.getSpace())
                .build();

        cloudFoundryOperation.organizations()
                .list()
                .map(OrganizationSummary::getName)
                .subscribe(System.out::println);
        return cloudFoundryOperation;
    }

}
