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

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fisherj
 *
 */
public final class CatalogPlan {
    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("metadata")
    private PlanMetadata metadata;

    public CatalogPlan() {

    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final UUID getId() {
        return id;
    }

    public final void setId(UUID id) {
        this.id = id;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final PlanMetadata getMetadata() {
        return metadata;
    }

    public final void setMetadata(PlanMetadata metadata) {
        this.metadata = metadata;
    }
}
