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
package com.blackducksoftware.integration.cloudfoundry.perceiver.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author fisherj
 *
 */
@Configuration
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    public ApplicationSecurity() {
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable basic auth
        http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic().disable().authorizeRequests().anyRequest().permitAll();
    }
}
