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

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.BindingInstance;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.HubCredentials;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.HubProjectParameters;

/**
 * @author jfisher
 *
 */
public class BindingInstanceService {
    private final Map<String, BindingInstance> bindingInstances = new HashMap<>();

    private final ServiceInstanceService serviceInstanceService;

    private final HubCredentials creds;

    public BindingInstanceService(ServiceInstanceService serviceInstanceService, HubCredentials creds) {
        this.serviceInstanceService = serviceInstanceService;
        this.creds = creds;
    }

    public BindingInstance create(String bindingId, String instanceId, Optional<HubProjectParameters> parms) {
        String projName = parms.map((hubProjectParameters) -> hubProjectParameters.getProjectName().orElse(null)).orElse(null);
        String codeLocName = parms.map((hubProjectParameters) -> hubProjectParameters.getCodeLocation().orElse(null)).orElse(null);
        BindingInstance bInst = new BindingInstance(creds.getScheme(), creds.getHost(), creds.getPort(), creds.getLoginInfo().getUsername(),
                creds.getLoginInfo().getPassword(), projName, codeLocName);
        bindingInstances.put(bindingId, bInst);
        return bInst;
    }

    public BindingInstance delete(String bindingId) {
        return bindingInstances.remove(bindingId);
    }

    public boolean isExists(String serviceId, String bindingId) {
        return serviceInstanceService.isExists(serviceId) ? bindingInstances.containsKey(bindingId) : false;
    }
}
