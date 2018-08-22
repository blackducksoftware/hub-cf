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

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.cloudfoundry.perceiver.impl.BindingInstanceService;
import com.blackducksoftware.integration.cloudfoundry.perceiver.impl.CatalogService;
import com.blackducksoftware.integration.cloudfoundry.perceiver.impl.ServiceInstanceService;

/**
 * @author fisherj
 *
 */
@Configuration
public class PerceiverConfiguration {
    private final String brokerBaseUrlString;

    private final int brokerPort;

    private final String brokerUser;

    private final String brokerPass;

    public PerceiverConfiguration(@Value("${broker.baseUrl}") String brokerBaseUrlString,
            @Value("${broker.port}") int brokerPort,
            @Value("${broker.basicAuth.user}") String brokerUser,
            @Value("${broker.basicAuth.pass}") String brokerPass) {
        this.brokerBaseUrlString = brokerBaseUrlString;
        this.brokerPort = brokerPort;
        this.brokerUser = brokerUser;
        this.brokerPass = brokerPass;
    }

    @Bean
    protected CloseableHttpClient httpClient() {
        return HttpClients.createDefault();
    }

    @Bean
    protected HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient());
        return clientHttpRequestFactory;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(brokerUser, brokerPass));
        return restTemplate;
    }

    @Bean
    public ServiceInstanceService getServiceInstanceService() {
        return new ServiceInstanceService(getRestTemplate(), brokerBaseUrlString, brokerPort);
    }

    @Bean
    public BindingInstanceService getBindingInstanceService() {
        return new BindingInstanceService(getRestTemplate(), brokerBaseUrlString, brokerPort);
    }

    @Bean
    public CatalogService getCatalogService() {
        return new CatalogService(getRestTemplate(), brokerBaseUrlString, brokerPort);
    }
}
