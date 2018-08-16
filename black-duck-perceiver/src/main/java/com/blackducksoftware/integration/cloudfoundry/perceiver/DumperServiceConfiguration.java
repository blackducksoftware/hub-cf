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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.blackducksoftware.integration.cloudfoundry.perceiver.impl.BindingInstanceService;
import com.blackducksoftware.integration.cloudfoundry.perceiver.impl.ServiceInstanceService;
import com.blackducksoftware.integration.cloudfoundry.v2.model.Catalog;

/**
 * @author fisherj
 *
 */
@Configuration
public class DumperServiceConfiguration {
    @Bean
    public ServiceInstanceService getServiceInstanceService() {
        return new ServiceInstanceService();
    }

    @Bean
    public BindingInstanceService getBindingInstanceService() {
        return new BindingInstanceService();
    }

    @Bean
    public Catalog getCatalog() {
        return new Catalog();
    }
}
