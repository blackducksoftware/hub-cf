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
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author jfisher
 *
 */
public final class BindingInstance {
    private final UUID appGuid;

    private final String projectName;

    private final String codeLocationName;

    private final String pluginVersion;

    public BindingInstance(UUID appGuid, String projectName, String codeLocationName, String pluginVersion) {
        this.appGuid = appGuid;
        this.projectName = projectName;
        this.codeLocationName = codeLocationName;
        this.pluginVersion = pluginVersion;
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
     * @return the pluginVersion
     */
    public final String getPluginVersion() {
        return pluginVersion;
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
