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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jfisher
 *
 */
public final class ServiceProvisionRequest {
    @JsonProperty(value = "servie_id", required = true)
    private String serviceId;

    @JsonProperty(value = "plan_id", required = true)
    private String planId;

    @JsonProperty(value = "orangization_guid", required = true)
    private String orgGuid;

    @JsonProperty(value = "space_guid", required = true)
    private String spaceGuid;

    /**
     * @return the serviceId
     */
    public final String getServiceId() {
        return serviceId;
    }

    /**
     * @param serviceId
     *            the serviceId to set
     */
    public final void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * @return the planId
     */
    public final String getPlanId() {
        return planId;
    }

    /**
     * @param planId
     *            the planId to set
     */
    public final void setPlanId(String planId) {
        this.planId = planId;
    }

    /**
     * @return the orgGuid
     */
    public final String getOrgGuid() {
        return orgGuid;
    }

    /**
     * @param orgGuid
     *            the orgGuid to set
     */
    public final void setOrgGuid(String orgGuid) {
        this.orgGuid = orgGuid;
    }

    /**
     * @return the spaceGuid
     */
    public final String getSpaceGuid() {
        return spaceGuid;
    }

    /**
     * @param spaceGuid
     *            the spaceGuid to set
     */
    public final void setSpaceGuid(String spaceGuid) {
        this.spaceGuid = spaceGuid;
    }
}
