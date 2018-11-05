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
package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.BindResource;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.BindingInstance;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.HubProjectParameters;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.iface.ICloudControllerEventMonitorService;

/**
 * @author jfisher
 *
 */
public class BindingInstanceService {
    private final Logger logger = LoggerFactory.getLogger(BindingInstanceService.class);

    private final Map<String, BindingInstance> bindingInstances = new HashMap<>();

    private final ServiceInstanceService serviceInstanceService;

    private final String pluginVersion;

    private final ICloudControllerEventMonitorService ccEventMonitorHandler;

    public BindingInstanceService(ServiceInstanceService serviceInstanceService, String pluginVersion,
            ICloudControllerEventMonitorService ccEventMonitorHandler) {
        this.serviceInstanceService = serviceInstanceService;
        this.pluginVersion = pluginVersion;
        this.ccEventMonitorHandler = ccEventMonitorHandler;
    }

    public BindingInstance create(String bindingId, String instanceId, Optional<BindResource> bind, Optional<HubProjectParameters> parms) {
        String projName = parms.map((hubProjectParameters) -> hubProjectParameters.getProjectName().orElse(null)).orElse(null);
        String codeLocName = parms.map((hubProjectParameters) -> hubProjectParameters.getCodeLocation().orElse(null)).orElse(null);
        Optional<UUID> appGuid = Optional.ofNullable(bind.map((bindResource) -> bindResource.getAppGuid().orElse(null)).map(UUID::fromString).orElse(null));
        BindingInstance bInst = null;
        if (appGuid.isPresent()) {
            bInst = new BindingInstance(appGuid.get(), projName, codeLocName, pluginVersion);
            if (!ccEventMonitorHandler.registerId(appGuid.get())) {
                logger.warn("appId registration with event monitor failed for {}, not monitoring for app events", appGuid.get());
            }
            // TODO fisherj Do something with return code from registerId other than log
            bindingInstances.put(bindingId, bInst);
        } else {
            logger.warn("Could not creating binding for {}. appGuid missing", bindingId);
        }
        return bInst;
    }

    public BindingInstance delete(String bindingId) {
        Optional<BindingInstance> inst = Optional.ofNullable(bindingInstances.remove(bindingId));
        inst.map(BindingInstance::getAppGuid).ifPresent((appUUID) -> ccEventMonitorHandler.unregisterId(appUUID));
        return inst.orElse(null);
    }

    public boolean isExists(String serviceId, String bindingId) {
        return serviceInstanceService.isExists(serviceId) ? bindingInstances.containsKey(bindingId) : false;
    }

    @Deprecated
    public Optional<Map<String, UUID>> toAppIdByBindingId() {
        return Optional.ofNullable(bindingInstances.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getAppGuid())));
    }

    public Map<String, BindingInstance> getBindingInstancesById() {
        return bindingInstances;
    }
}
