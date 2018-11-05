/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.cloudfoundry;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.iface.ICloudControllerEventMonitorService;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl.BindingInstanceService;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl.ServiceInstanceService;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.security.AuthenticationEntryPoint;
import com.blackducksoftware.integration.cloudfoundry.v2.model.Catalog;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author jfisher
 *
 */
@Configuration
public class ServiceBrokerConfiguration {

    @Bean
    public Catalog serviceBrokerCatalog() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        Catalog catalog = om.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream("service.yml"),
                Catalog.class);
        return catalog;
    }

    @Bean
    public ServiceInstanceService serviceInstanceService() {
        return new ServiceInstanceService();
    }

    @Bean
    public BindingInstanceService bindingInstanceService(ServiceInstanceService serviceInstanceService,
            @Value("${plugin.version}") String pluginVersion,
            ICloudControllerEventMonitorService ccEventMonitorHandler) {
        return new BindingInstanceService(serviceInstanceService, pluginVersion, ccEventMonitorHandler);
    }

    @Bean
    public AuthenticationEntryPoint authEntryPoint(@Value(value = "${application.realm}") final String realm) {
        return new AuthenticationEntryPoint(realm);
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
    public RestTemplate perceiverRestTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }
}
