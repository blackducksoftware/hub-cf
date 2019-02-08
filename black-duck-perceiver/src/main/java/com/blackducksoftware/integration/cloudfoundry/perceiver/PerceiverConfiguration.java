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

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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
    private final BrokerProperties brokerProperties;

    private final Environment env;

    @Autowired
    public PerceiverConfiguration(Environment env, BrokerProperties brokerProperties) {
        this.env = env;
        this.brokerProperties = brokerProperties;
    }

    @Bean
    protected PoolingHttpClientConnectionManager getPoolinHttpClientConnectionManager() {

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(50);

        return connMgr;
    }

    @Bean
    protected RequestConfig getRequestConfig() {
        return RequestConfig.DEFAULT;
    }

    @Bean
    protected CloseableHttpClient httpClient() {
        return HttpClients.custom().setConnectionManager(getPoolinHttpClientConnectionManager())
                .setDefaultRequestConfig(getRequestConfig()).build();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        String brokerUsername = env.getProperty(brokerProperties.getBasicAuth().getUserEnvVblName());
        String brokerPassword = env.getProperty(brokerProperties.getBasicAuth().getPassEnvVblName());

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient());

        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        restTemplate.getInterceptors()
                .add(new BasicAuthorizationInterceptor(brokerUsername, brokerPassword));
        return restTemplate;
    }

    @Bean
    public ServiceInstanceService getServiceInstanceService() {
        return new ServiceInstanceService(getRestTemplate(), brokerProperties.getBaseUrl(), brokerProperties.getPort());
    }

    @Bean
    public BindingInstanceService getBindingInstanceService() {
        return new BindingInstanceService(getRestTemplate(), brokerProperties.getBaseUrl(), brokerProperties.getPort());
    }

    @Bean
    public CatalogService getCatalogService() {
        return new CatalogService(getRestTemplate(), brokerProperties.getBaseUrl(), brokerProperties.getPort());
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(3);
        threadPoolTaskScheduler.setThreadNamePrefix("PerceiverService-");
        return threadPoolTaskScheduler;
    }
}
