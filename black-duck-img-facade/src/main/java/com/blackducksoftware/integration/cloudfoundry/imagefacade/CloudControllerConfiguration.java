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

import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.ClientCredentialsGrantTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * @author fisherj
 *
 */
@Configuration
public class CloudControllerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(CloudControllerConfiguration.class);

    @Bean
    @ConfigurationProperties("cf.oauth2.client")
    protected ClientCredentialsResourceDetails oAuthDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    public ReactorCloudFoundryClient reactorCloudFoundryClient(
            @Value("${cf.baseUrl}") String baseUrlString,
            @Value("${cf.skip-ssl-validation}") boolean insecure,
            ClientCredentialsResourceDetails oauthDetails) throws MalformedURLException {
        logger.trace("In reactorCloudFoundryClient with args: baseUrl: {}, insecure: {}, oauthClientId: {}", baseUrlString, insecure,
                oauthDetails.getClientId());
        URL baseUrl = new URL(baseUrlString);
        DefaultConnectionContext.Builder conxCtxBuilder = DefaultConnectionContext.builder()
                .apiHost(baseUrl.getHost())
                .port(baseUrl.getPort())
                .skipSslValidation(insecure);

        ClientCredentialsGrantTokenProvider.Builder oauthTokenProviderBuilder = ClientCredentialsGrantTokenProvider.builder()
                .clientId(oauthDetails.getClientId())
                .clientSecret(oauthDetails.getClientSecret());

        return ReactorCloudFoundryClient.builder()
                .connectionContext(conxCtxBuilder.build())
                .tokenProvider(oauthTokenProviderBuilder.build())
                .build();
    }
}
