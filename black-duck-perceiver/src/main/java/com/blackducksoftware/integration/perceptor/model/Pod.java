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
package com.blackducksoftware.integration.perceptor.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fisherj
 *
 */
public final class Pod {
    @JsonProperty("Containers")
    private List<Container> containers;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Namespace")
    private String namespcae;

    @JsonProperty("UID")
    private String id;

    public Pod() {

    }

    public final List<Container> getContainers() {
        return containers;
    }

    public final void setContainers(List<Container> containers) {
        this.containers = containers;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getNamespcae() {
        return namespcae;
    }

    public final void setNamespcae(String namespcae) {
        this.namespcae = namespcae;
    }

    public final String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }
}
