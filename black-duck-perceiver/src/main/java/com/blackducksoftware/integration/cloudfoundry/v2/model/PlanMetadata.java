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
package com.blackducksoftware.integration.cloudfoundry.v2.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fisherj
 *
 */
public final class PlanMetadata {
    @JsonProperty("bullets")
    private List<String> bullets;

    public PlanMetadata() {

    }

    public final List<String> getBullets() {
        return bullets;
    }

    public final void setBullets(List<String> bullets) {
        this.bullets = bullets;
    }
}
