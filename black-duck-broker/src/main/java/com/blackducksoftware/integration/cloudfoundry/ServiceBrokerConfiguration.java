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
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.HubCredentials;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.PhoneHomeParameters;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.iface.ICloudControllerEventMonitorService;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl.BindingInstanceService;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl.ServiceInstanceService;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.security.AuthenticationEntryPoint;
import com.blackducksoftware.integration.cloudfoundry.v2.model.Catalog;
import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.configuration.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
import com.blackducksoftware.integration.rest.connection.RestConnection;
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
    public HubCredentials hubCredentials(
            @Value(value = "#{ @environment['HUB_SCHEME'] ?: 'https' }") final String scheme,
            @Value(value = "#{ @environment['HUB_HOST'] ?: '0' }") final String host,
            @Value(value = "#{ @environment['HUB_PORT'] ?: -1 }") final int port,
            @Value(value = "#{ @environment['HUB_LOGIN'] ?: '{}' }") final String loginInfo,
            @Value(value = "#{ @environment['HUB_INSECURE'] ?: false }") final boolean insecure,
            @Value(value = "#{ @environment['HUB_API_TOKEN'] ?: '0' }") final String apiToken) {
        return new HubCredentials(scheme, host, port, loginInfo, insecure, apiToken);
    }

    @Bean
    public HubServicesFactory hubServicesFactory(HubCredentials hubCredentials) throws MalformedURLException, EncryptionException {
        final Logger logger = LoggerFactory.getLogger(ServiceBrokerConfiguration.class);
        final IntLogger slf4jIntLogger = new Slf4jIntLogger(logger);

        final HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder();
        URL hubUrl = UriComponentsBuilder.newInstance()
                .scheme(hubCredentials.getScheme())
                .host(hubCredentials.getHost())
                .port(hubCredentials.getPort())
                .build().toUri().toURL();
        hubServerConfigBuilder.setUrl(hubUrl.toString());
        hubServerConfigBuilder.setUsername(hubCredentials.getLoginInfo().getUsername());
        hubServerConfigBuilder.setPassword(hubCredentials.getLoginInfo().getPassword());
        hubServerConfigBuilder.setTrustCert(hubCredentials.isInsecure());
        hubServerConfigBuilder.setApiToken(hubCredentials.getApiToken());
        hubServerConfigBuilder.setLogger(slf4jIntLogger);
        final HubServerConfig hubServerConfig = hubServerConfigBuilder.build();

        final RestConnection restConnection = hubServerConfig.createRestConnection(slf4jIntLogger);

        return new HubServicesFactory(restConnection);
    }

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
            HubCredentials hubCredentials,
            @Value("${plugin.version}") String pluginVersion,
            PhoneHomeParameters phoneHomeParms,
            ICloudControllerEventMonitorService ccEventMonitorHandler) {
        return new BindingInstanceService(serviceInstanceService, hubCredentials, pluginVersion, phoneHomeParms, ccEventMonitorHandler);
    }

    @Bean
    public AuthenticationEntryPoint authEntryPoint(@Value(value = "${application.realm}") final String realm) {
        return new AuthenticationEntryPoint(realm);
    }

    @Bean
    public PhoneHomeParameters phoneHomeParameters(
            @Value(value = "#{ @environment['INTEGRATION_SOURCE'] ?: '0' }") final String source,
            @Value(value = "#{ @environment['INTEGRATION_VENDOR'] ?: '0' }") final String vendor) {
        return new PhoneHomeParameters(source, vendor);
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
