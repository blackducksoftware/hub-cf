/*
 * Copyright (C) 2018 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fisherj
 *
 */
public final class BackendBindingProvisionRequest {
    private final String bindingId;

    private final String resourceId;

    private final BindResource bindResource;

    private final HubProjectParameters hubProjectParams;

    @JsonCreator
    public BackendBindingProvisionRequest(@JsonProperty("bindingId") String bindingId,
            @JsonProperty("resourceId") String resourceId,
            @JsonProperty("bindResource") BindResource bindResource,
            @JsonProperty("hubProjectParameters") HubProjectParameters hubProjectParams) {
        this.bindingId = bindingId;
        this.resourceId = resourceId;
        this.bindResource = bindResource;
        this.hubProjectParams = hubProjectParams;
    }

    @JsonProperty("bindingId")
    public final String getBindingId() {
        return bindingId;
    }

    @JsonProperty("resourceId")
    public final String getResourceId() {
        return resourceId;
    }

    @JsonProperty("bindResource")
    public final BindResource getBindResource() {
        return bindResource;
    }

    @JsonProperty("hubProjectParameters")
    public final HubProjectParameters getHubProjectParams() {
        return hubProjectParams;
    }
}
