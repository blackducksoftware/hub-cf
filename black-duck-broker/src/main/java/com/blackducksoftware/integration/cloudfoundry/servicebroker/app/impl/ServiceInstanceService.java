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

import java.util.HashSet;
import java.util.Set;

/**
 * @author jfisher
 *
 */
public class ServiceInstanceService {
    private final Set<String> serviceInstances = new HashSet<>();

    public ServiceInstanceService() {
    }

    public boolean create(String id) {
        return serviceInstances.add(id);
    }

    public boolean delete(String id) {
        return serviceInstances.remove(id);
    }

    public boolean isExists(String id) {
        return serviceInstances.contains(id);
    }

    public Set<String> getServiceInstances() {
        return new HashSet<String>(serviceInstances);
    }
}
