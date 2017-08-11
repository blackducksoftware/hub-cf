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
package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.exception.BlackDuckServiceBrokerException;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.json.JsonUtil;

/**
 * @author jfisher
 *
 */
public final class HubCredentials {
    private final Logger logger = LoggerFactory.getLogger(HubCredentials.class);

    private final String scheme;

    private final String host;

    private final int port;

    private final HubLogin loginInfo;

    private final boolean insecure;

    public HubCredentials(
            String scheme,
            String host,
            int port,
            String loginInfo,
            boolean insecure) {
        // HUB_SCHEME environment variable must exist
        this.scheme = Objects.requireNonNull(scheme, "HUB_SCHEME environment variable not provided");

        // HUB_HOST environment variable must exist
        this.host = Objects.requireNonNull(host, "HUB_HOST environment variable not provided");

        // HUB_PORT environment variable is optional
        this.port = port;

        // HUB_LOGIN environment variable must exist
        try {
            this.loginInfo = JsonUtil.readValue(loginInfo, HubLogin.class);
        } catch (BlackDuckServiceBrokerException e) {
            logger.error("Error while processing HUB_LOGIN environment variable", e);
            throw e;
        }

        // HUB_INSECURE environment variable will always exist
        this.insecure = insecure;

        logger.debug(
                "Using: scheme: " + scheme + "; host: " + host + "; port: " + port + "; username: " + getLoginInfo().getUsername()
                        + "; password: <hidden>; insecure: " + insecure);
    }

    /**
     * @return the scheme
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the loginInfo
     */
    public HubLogin getLoginInfo() {
        return loginInfo;
    }

    /**
     * @return the insecure
     */
    public boolean isInsecure() {
        return insecure;
    }
}
