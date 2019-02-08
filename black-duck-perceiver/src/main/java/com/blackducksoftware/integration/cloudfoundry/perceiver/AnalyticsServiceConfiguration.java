/*
 * Copyright (C) 2019 Black Duck Software Inc.
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

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.phonehome.BlackDuckPhoneHomeHelper;
import com.synopsys.integration.blackduck.service.BlackDuckRegistrationService;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.phonehome.PhoneHomeClient;
import com.synopsys.integration.phonehome.PhoneHomeService;
import com.synopsys.integration.util.IntEnvironmentVariables;

/**
 * @author fisherj
 *
 */
@Configuration
public class AnalyticsServiceConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceConfiguration.class);

    private static final String ALLIANCES_TRACKING_ID = "UA-116682967-3";

    private ApplicationProperties applicationProperties;

    private BlackduckProperties blackduckProperties;

    @Autowired
    public AnalyticsServiceConfiguration(ApplicationProperties applicationProperties, BlackduckProperties blackduckProperties) {
        this.applicationProperties = applicationProperties;
        this.blackduckProperties = blackduckProperties;
    }

    @Bean
    public BlackDuckPhoneHomeHelper createPhoneHomeHelper() {
        return new BlackDuckPhoneHomeHelper(getIntLogger(), getBlackDuckService(), getPhoneHomeService(), getBlackDuckRegistrationService(),
                getIntEnvironmentVariables());
    }

    @Bean
    public BlackDuckServerConfig getBlackDuckServerConfig() {
        URI hostAndScheme = null;
        try {
            hostAndScheme = new URI("https", null, blackduckProperties.getHost(), blackduckProperties.getPort(), null, null, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("The provided base host name is not properly formatted", e);
        }
        return BlackDuckServerConfig.newBuilder()
                .setLogger(getIntLogger())
                .setUsername(blackduckProperties.getUser())
                .setPassword(blackduckProperties.getUserPassword())
                .setUrl(hostAndScheme.toString())
                .setTrustCert(blackduckProperties.getInsecure())
                .build();
    }

    @Bean
    public BlackDuckServicesFactory getBlackDuckServicesFactory() {
        BlackDuckServicesFactory blackDuckServicesFactory = getBlackDuckServerConfig().createBlackDuckServicesFactory(getIntLogger());
        blackDuckServicesFactory.addEnvironmentVariables(getIntEnvironmentVariables().getVariables());
        return blackDuckServicesFactory;
    }

    private IntLogger getIntLogger() {
        return new Slf4jIntLogger(logger);
    }

    private IntEnvironmentVariables getIntEnvironmentVariables() {
        IntEnvironmentVariables intEnvironmentVariables = new IntEnvironmentVariables(false);
        intEnvironmentVariables.put(PhoneHomeClient.SKIP_PHONE_HOME_VARIABLE, Boolean.toString((!applicationProperties.getAnalytics().isEnabled())));

        return intEnvironmentVariables;
    }

    private BlackDuckService getBlackDuckService() {
        return getBlackDuckServicesFactory().createBlackDuckService();
    }

    private PhoneHomeService getPhoneHomeService() {
        PhoneHomeClient phoneHomeClient = new PhoneHomeClient(ALLIANCES_TRACKING_ID, getIntLogger());
        return PhoneHomeService.createPhoneHomeService(getIntLogger(), phoneHomeClient);
    }

    private BlackDuckRegistrationService getBlackDuckRegistrationService() {
        return getBlackDuckServicesFactory().createBlackDuckRegistrationService();
    }
}
