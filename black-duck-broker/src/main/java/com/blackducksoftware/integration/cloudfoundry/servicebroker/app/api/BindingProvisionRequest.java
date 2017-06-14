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

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jfisher
 *
 */
public final class BindingProvisionRequest {

    private final String serviceId;

    private final String planId;

    private Optional<BindResource> bindResource = Optional.empty();

    private Optional<HubProjectParameters> hubProjectParams = Optional.empty();

    public BindingProvisionRequest(@JsonProperty(value = "service_id", required = true) String serviceId,
            @JsonProperty(value = "plan_id", required = true) String planId,
            @JsonProperty(value = "bind_resource", required = false) Optional<BindResource> bindResource,
            @JsonProperty(value = "parameters", required = false) Optional<HubProjectParameters> hubProjectParams) {
        this.serviceId = serviceId;
        this.planId = planId;
        this.bindResource = bindResource;
        this.hubProjectParams = hubProjectParams;
    }

    /**
     * @return the serviceId
     */
    @JsonProperty(value = "service_id")
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @return the planId
     */
    @JsonProperty(value = "plan_id")
    public String getPlanId() {
        return planId;
    }

    /**
     * @return the bindResource
     */
    @JsonProperty(value = "bind_resource")
    public Optional<BindResource> getBindResource() {
        return bindResource;
    }

    /**
     * @param bindResource
     *            the bindResource to set
     */
    public final void setBindResource(Optional<BindResource> bindResource) {
        this.bindResource = bindResource;
    }

    /**
     * @return the hubProjectParams
     */
    @JsonProperty(value = "parameters")
    public Optional<HubProjectParameters> getHubProjectParams() {
        return hubProjectParams;
    }

    /**
     * @param hubProjectParams
     *            the hubProjectParams to set
     */
    public final void setHubProjectParams(Optional<HubProjectParameters> hubProjectParams) {
        this.hubProjectParams = hubProjectParams;
    }
}
