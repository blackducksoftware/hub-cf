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
package com.blackducksoftware.integration.cloudfoundry.perceiver.impl;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.blackducksoftware.integration.cloudfoundry.perceiver.api.BindResource;
import com.blackducksoftware.integration.cloudfoundry.perceiver.api.HubProjectParameters;

/**
 * @author fisherj
 *
 */
public class BindingInstanceService {
    public Optional<Map<String, UUID>> toAppIdByBindingId() {
        return null;
    }

    public boolean create(String bindingId, String resourceId, Optional<BindResource> bindResource, Optional<HubProjectParameters> hubProjParams) {
        return false;
    }
}
