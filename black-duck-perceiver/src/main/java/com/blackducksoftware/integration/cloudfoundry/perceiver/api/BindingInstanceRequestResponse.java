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
package com.blackducksoftware.integration.cloudfoundry.perceiver.api;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author fisherj
 *
 */
public class BindingInstanceRequestResponse {
    private final Map<String, BindingInstance> bindingsById;

    @JsonCreator
    public BindingInstanceRequestResponse(@JsonDeserialize(contentAs = BindingInstance.class) Map<String, BindingInstance> bindingsById) {
        this.bindingsById = bindingsById;
    }

    public final Map<String, BindingInstance> getBindingsById() {
        return new HashMap<>(bindingsById);
    }
}
