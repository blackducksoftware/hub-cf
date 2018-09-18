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

import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.blackducksoftware.integration.cloudfoundry.imagefacade.impl.ImageFacadeService;
import com.blackducksoftware.integration.cloudfoundry.imagefacade.impl.ImagePuller;
import com.blackducksoftware.integration.cloudfoundry.imagefacade.model.ImageModel;

/**
 * @author fisherj
 *
 */
@Configuration
@Import(CloudControllerConfiguration.class)
public class ImageFacadeConfiguration {
    private final ReactorCloudFoundryClient cloudFoundryClient;

    private final ApplicationProperties applicationProperties;

    @Autowired
    public ImageFacadeConfiguration(ReactorCloudFoundryClient cloudFoundryClient, ApplicationProperties applicationProperties) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public ImageFacadeService imageFacadeService() {
        return new ImageFacadeService(imageModel(), applicationProperties.getDropletLocation());
    }

    @Bean
    public ImageModel imageModel() {
        return new ImageModel(imagePuller());
    }

    @Bean
    public ImagePuller imagePuller() {
        return new ImagePuller(cloudFoundryClient, applicationProperties.getDropletLocation(), applicationProperties.getPullRetries(),
                applicationProperties.getPullTimeoutSeconds());
    }
}
