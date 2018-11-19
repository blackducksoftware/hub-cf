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
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.generated.response.CurrentVersionView;
import com.synopsys.integration.blackduck.rest.BlackduckRestConnection;
import com.synopsys.integration.blackduck.rest.CredentialsRestConnectionBuilder;
import com.synopsys.integration.blackduck.service.HubRegistrationService;
import com.synopsys.integration.blackduck.service.HubServicesFactory;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.phonehome.PhoneHomeClient;
import com.synopsys.integration.phonehome.PhoneHomeRequestBody;
import com.synopsys.integration.phonehome.enums.ProductIdEnum;

/**
 * @author fisherj
 *
 */
@Configuration
public class PhoneHomeConfiguration {
    public static final String ALLIANCES_TRACKING_ID = "UA-116682967-3";

    private static final String BLACKDUCK_SCHEME = "https";

    private static final String SOURCE_KEY = "Source";

    private static final Logger logger = LoggerFactory.getLogger(PhoneHomeConfiguration.class);

    private ApplicationProperties appProperties;

    private BlackduckProperties blackduckProperties;

    @Autowired
    public PhoneHomeConfiguration(ApplicationProperties appProperties, BlackduckProperties blackduckProperties) {
        this.appProperties = appProperties;
        this.blackduckProperties = blackduckProperties;
    }

    @Bean
    public PhoneHomeClient phoneHomeClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        return new PhoneHomeClient(ALLIANCES_TRACKING_ID, getIntLogger(), getHttpClientBuilder());
    }

    @Bean
    public PhoneHomeRequestBody phoneHomeRequestBody() {
        PhoneHomeRequestBody reqBody = PhoneHomeRequestBody.DO_NOT_PHONE_HOME;
        try {
            if (appProperties.getAnalytics().isEnabled()) {
                PhoneHomeRequestBody.Builder reqBodyBuilder = new PhoneHomeRequestBody.Builder();
                reqBodyBuilder.setProductId(ProductIdEnum.HUB);
                reqBodyBuilder.setProductVersion(getBlackduckVersion());
                reqBodyBuilder.setArtifactId(appProperties.getAnalytics().getArtifactId().toString().toLowerCase());
                reqBodyBuilder.setArtifactVersion(appProperties.getAnalytics().getArtifactVersion());
                reqBodyBuilder.setHostName(blackduckProperties.getHost());
                reqBodyBuilder.setCustomerId(getBlackduckRegistrationId());
                reqBodyBuilder.addToMetaData(SOURCE_KEY, appProperties.getAnalytics().getIntegrationSource().text());

                reqBody = reqBodyBuilder.build();
                logger.debug("Using PhoneHomeRequest params: version: {}; artifactId: {}; artifactVersion: {}; hostName: {}; customerId: {}",
                        reqBody.getProductVersion(), reqBody.getArtifactId(), reqBody.getArtifactVersion(), reqBody.getHostName(), reqBody.getCustomerId());
            } else {
                logger.debug("Analytics disabled, not configuring PhoneHomeRequestBody");
            }
        } catch (MalformedURLException | IllegalStateException | IntegrationException e) {
            // Log the exception, but continue with PhoneHome disabled as an error here should not derail the system
            logger.warn("Initialization of PhoneHome Analytics failed; disabling PhoneHome", e);
        }
        return reqBody;
    }

    private HubServicesFactory getHubServicesFactory() throws MalformedURLException {
        return new HubServicesFactory(HubServicesFactory.createDefaultGson(), HubServicesFactory.createDefaultJsonParser(), getBlackduckRestConnection(),
                getIntLogger());
    }

    private BlackduckRestConnection getBlackduckRestConnection() throws MalformedURLException {
        final CredentialsRestConnectionBuilder crcb = new CredentialsRestConnectionBuilder();
        crcb.setLogger(getIntLogger());
        URL baseUrl = new URL(BLACKDUCK_SCHEME, blackduckProperties.getHost(), blackduckProperties.getPort(), "");
        crcb.setBaseUrl(baseUrl.toString());
        crcb.setUsername(blackduckProperties.getUser());
        crcb.setPassword(blackduckProperties.getUserPassword());
        crcb.setAlwaysTrustServerCertificate(true); // Can we just always trust here?

        return crcb.build();
    }

    private String getBlackduckRegistrationId() throws MalformedURLException {
        // TODO fisherj Contact Black Duck Registration Id Endpoint
        String regId = null;
        HubRegistrationService hrs = getHubServicesFactory().createHubRegistrationService();
        try {
            regId = hrs.getRegistrationId();
        } catch (IntegrationException e) {
            // Fail silently...just log and continue
            logger.warn("Failed retrieving registration id for analytics", e);
        }
        return regId;
    }

    private String getBlackduckVersion() throws MalformedURLException, IntegrationException {
        final CurrentVersionView currentVersionView = getHubServicesFactory().createHubService().getResponse(ApiDiscovery.CURRENT_VERSION_LINK_RESPONSE);
        return currentVersionView.version;
    }

    private IntLogger getIntLogger() {
        return new Slf4jIntLogger(logger);
    }

    private HttpClientBuilder getHttpClientBuilder() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }
                }).build());
        return httpClientBuilder;
    }
}
