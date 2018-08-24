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
package com.blackducksoftware.integration.cloudfoundry.perceiver.api;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jfisher
 *
 */
public final class BindingInstance {
    private final String scheme;

    private final String host;

    private final int port;

    private final String username;

    private final String password;

    private final UUID appGuid;

    private final String projectName;

    private final String codeLocationName;

    private final boolean isInsecure;

    private final String pluginVersion;

    private final String integrationSource;

    private final String integrationVendor;

    @JsonCreator()
    public BindingInstance(@JsonProperty("scheme") String scheme, @JsonProperty("host") String host, @JsonProperty("port") int port,
            @JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("appGuid") UUID appGuid,
            @JsonProperty("projectName") String projectName, @JsonProperty("codeLocationName") String codeLocationName,
            @JsonProperty("isInsecure") boolean isInsecure, @JsonProperty("pluginVersion") String pluginVersion,
            @JsonProperty("integrationSource") String integrationSource, @JsonProperty("integrationVendor") String integrationVendor) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.appGuid = appGuid;
        this.projectName = projectName;
        this.codeLocationName = codeLocationName;
        this.isInsecure = isInsecure;
        this.pluginVersion = pluginVersion;
        this.integrationSource = integrationSource;
        this.integrationVendor = integrationVendor;
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
     * @return the username
     */
    public final String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public final String getPassword() {
        return password;
    }

    /**
     * @return the projectName
     */
    public final String getProjectName() {
        return projectName;
    }

    /**
     * @return the appGuid
     */
    public final UUID getAppGuid() {
        return appGuid;
    }

    /**
     * @return the codeLocation
     */
    public final String getCodeLocationName() {
        return codeLocationName;
    }

    /**
     * @return the isInsecure
     */
    public final boolean getIsInsecure() {
        return isInsecure;
    }

    /**
     * @return the pluginVersion
     */
    public final String getPluginVersion() {
        return pluginVersion;
    }

    /**
     * @return the integrationSource
     */
    public final String getIntegrationSource() {
        return integrationSource;
    }

    /**
     * @return the integrationVendor
     */
    public final String getIntegrationVendor() {
        return integrationVendor;
    }
}
