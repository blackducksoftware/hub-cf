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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jfisher
 *
 */
public final class ServiceProvisionRequest {

    private final String serviceId;

    private final String planId;

    private final String orgGuid;

    private final String spaceGuid;

    @JsonCreator
    public ServiceProvisionRequest(@JsonProperty(value = "service_id", required = true) String serviceId,
            @JsonProperty(value = "plan_id", required = true) String planId,
            @JsonProperty(value = "organization_guid", required = true) String orgGuid,
            @JsonProperty(value = "space_guid", required = true) String spaceGuid) {
        this.serviceId = serviceId;
        this.planId = planId;
        this.orgGuid = orgGuid;
        this.spaceGuid = spaceGuid;
    }

    /**
     * @return the serviceId
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @return the planId
     */
    public String getPlanId() {
        return planId;
    }

    /**
     * @return the orgGuid
     */
    public String getOrgGuid() {
        return orgGuid;
    }

    /**
     * @return the spaceGuid
     */
    public String getSpaceGuid() {
        return spaceGuid;
    }
}
