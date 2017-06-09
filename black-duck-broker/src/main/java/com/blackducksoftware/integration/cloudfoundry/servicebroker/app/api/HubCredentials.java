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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.exception.BlackDuckServiceBrokerException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jfisher
 *
 */
@Component
public final class HubCredentials {
    private static final Logger logger = LoggerFactory.getLogger(HubCredentials.class);

    private final String scheme;

    private final String host;

    private final int port;

    private final HubLogin loginInfo;

    public HubCredentials(
            @Value("#{ @environment['HUB_SCHEME'] ?: '0' }") String scheme,
            @Value("#{ @environment['HUB_HOST'] ?: '0' }") String host,
            @Value("#{ @environment['HUB_PORT'] ?: -1 }") int port,
            @Value("#{ @environment['HUB_LOGIN'] ?: '{}' }") String loginInfo) {
        // HUB_SCHEME environment variable must exist
        if (scheme != null) {
            this.scheme = scheme;
        } else {
            logger.error("HUB_SCHEME environment variable not provided");
            throw new BlackDuckServiceBrokerException("HUB_SCHEME environment variable missing");
        }

        // HUB_HOST environment variable must exist
        if (host != null) {
            this.host = host;
        } else {
            logger.error("HUB_HOST environment variable not provided");
            throw new BlackDuckServiceBrokerException("HUB_HOST environment variable missing");
        }

        // HUB_PORT environment variable is optional
        this.port = port;

        // HUB_LOGIN environment variable must exist
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.loginInfo = mapper.readValue(loginInfo, HubLogin.class);
        } catch (JsonParseException e) {
            logger.error("HUB_LOGIN environment variable improperly formatted", e);
            throw new BlackDuckServiceBrokerException("Malformed JSON provided for HUB_LOGIN", e);
        } catch (JsonMappingException e) {
            logger.error("HUB_LOGIN mapping incorrect", e);
            throw new BlackDuckServiceBrokerException("Unable to parse HUB_LOGIN: " + e.getPathReference());
        } catch (IOException e) {
            logger.error("HUB_LOGIN processing exception", e);
            throw new BlackDuckServiceBrokerException(e);
        }

        logger.debug(
                "Using: scheme: " + scheme + "; host: " + host + "; port: " + port + "; username: " + getLoginInfo().getUsername() + "; password: <hidden>");
    }

    /**
     * @return the scheme
     */
    public final String getScheme() {
        return scheme;
    }

    /**
     * @return the host
     */
    public final String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public final int getPort() {
        return port;
    }

    /**
     * @return the loginInfo
     */
    public final HubLogin getLoginInfo() {
        return loginInfo;
    }
}
