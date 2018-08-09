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
package com.blackducksoftware.integration.cloudfoundry.servicebroker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * @author jfisher
 *
 */
@Configuration
@EnableWebSecurity
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
    @Value("${application.security.basicAuth.userEnvVblName}")
    private String userEnvVblName;

    @Value("${application.security.basicAuth.passEnvVblName}")
    private String passEnvVblName;

    @Autowired
    AuthenticationEntryPoint authEntryPoint;

    @Autowired
    Environment env;

    @Autowired
    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        String brokerUsername = env.getProperty(userEnvVblName);
        String brokerPassword = env.getProperty(passEnvVblName);

        // TODO jfisher Ensure environment variable values are legal (not null). Throw exception otherwise.

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        builder.inMemoryAuthentication().passwordEncoder(encoder).withUser(brokerUsername).password("{noop}" + brokerPassword).roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .anyRequest().authenticated()
                .and().httpBasic().authenticationEntryPoint(authEntryPoint);
    }
}
