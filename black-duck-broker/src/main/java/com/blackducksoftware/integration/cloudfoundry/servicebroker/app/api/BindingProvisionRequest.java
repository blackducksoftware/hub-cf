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
public final class BindingProvisionRequest {
    @JsonProperty(value = "service_id", required = true)
    private String serviceId;

    @JsonProperty(value = "plan_id", required = true)
    private String planId;

    @JsonProperty(value = "bind_resource", required = false)
    private BindResource bindResource;

    @JsonProperty(value = "parameters", required = false)
    private HubProjectParameters hubProjectParams;

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
     * @return the bindResource
     */
    public final BindResource getBindResource() {
        return bindResource;
    }

    /**
     * @param bindResource
     *            the bindResource to set
     */
    public final void setBindResource(BindResource bindResource) {
        this.bindResource = bindResource;
    }

    /**
     * @return the hubProjectParams
     */
    public final HubProjectParameters getHubProjectParams() {
        return hubProjectParams;
    }

    /**
     * @param hubProjectParams
     *            the hubProjectParams to set
     */
    public final void setHubProjectParams(HubProjectParameters hubProjectParams) {
        this.hubProjectParams = hubProjectParams;
    }

    private class BindResource {
        @JsonProperty(value = "app_guid", required = false)
        private String appGuid;

        @JsonProperty(value = "route", required = false)
        private String route;

        @SuppressWarnings("unused")
        public BindResource() {

        }

        /**
         * @return the appGuid
         */
        @SuppressWarnings("unused")
        public final String getAppGuid() {
            return appGuid;
        }

        /**
         * @param appGuid
         *            the appGuid to set
         */
        @SuppressWarnings("unused")
        public final void setAppGuid(String appGuid) {
            this.appGuid = appGuid;
        }

        /**
         * @return the route
         */
        @SuppressWarnings("unused")
        public final String getRoute() {
            return route;
        }

        /**
         * @param route
         *            the route to set
         */
        @SuppressWarnings("unused")
        public final void setRoute(String route) {
            this.route = route;
        }
    }
}
