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

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

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

    private final String projectName;

    private final String codeLocationName;

    private final boolean isInsecure;

    private final String pluginVersion;

    private final String integrationSource;

    private final String integrationVendor;

    public BindingInstance(String scheme, String host, int port, String username, String password, String projectName, String codeLocationName,
            boolean isInsecure, String pluginVersion, String integrationSource, String integrationVendor) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
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

    public static final BindingProvisionResponse toBindingProvisionResponse(final BindingInstance bindingInstance) {
        BindingProvisionResponse brp = new BindingProvisionResponse();
        if (bindingInstance != null) {
            Stream.of(BindingInstance.class.getMethods())
                    .filter(m -> m.getName().indexOf("get") == 0 && !m.getName().equals("getClass"))
                    .forEach(m -> {
                        String name = m.getName().toLowerCase().substring(3, 4) + m.getName().substring(4);
                        try {
                            brp.addCredential(name, m.invoke(bindingInstance, new Object[0]));
                        } catch (IllegalAccessException e) {
                            // TODO jfisher Auto-generated catch block
                            throw new RuntimeException(e);
                        } catch (IllegalArgumentException e) {
                            // TODO jfisher Auto-generated catch block
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            // TODO jfisher Auto-generated catch block
                            throw new RuntimeException(e);
                        }
                    });

        }
        return brp;
    }
}
