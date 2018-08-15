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
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fisherj
 *
 */
public final class Service {
    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("bindable")
    private boolean bindable;

    @JsonProperty("metadata")
    private CatalogMetadata metadata;

    @JsonProperty("plan_updateable")
    private boolean planUpdateable;

    @JsonProperty("plans")
    private List<CatalogPlan> plans;

    public Service() {

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

    public final List<String> getTags() {
        return tags;
    }

    public final void setTags(List<String> tags) {
        this.tags = tags;
    }

    public final boolean isBindable() {
        return bindable;
    }

    public final void setBindable(boolean bindable) {
        this.bindable = bindable;
    }

    public final CatalogMetadata getMetadata() {
        return metadata;
    }

    public final void setMetadata(CatalogMetadata metadata) {
        this.metadata = metadata;
    }

    public final boolean isPlanUpdateable() {
        return planUpdateable;
    }

    public final void setPlanUpdateable(boolean planUpdateable) {
        this.planUpdateable = planUpdateable;
    }

    public final List<CatalogPlan> getPlans() {
        return plans;
    }

    public final void setPlans(List<CatalogPlan> plans) {
        this.plans = plans;
    }

    public final Optional<CatalogPlan> findFirstPlanByName(String planName) {
        if (planName == null) {
            throw new IllegalArgumentException("Must provide a plan name");
        }

        if (plans == null) {
            return Optional.empty();
        }

        return plans.stream().filter(plan -> planName.equals(plan.getName())).findFirst();
    }
}
