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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fisherj
 *
 */
public final class Image {
    @JsonProperty("DockerImage")
    private String repository;

    // @JsonProperty("Tags")
    @JsonIgnore
    private List<String> tags;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Sha")
    private String sha;

    public Image() {

    }

    public final String getRepository() {
        return repository;
    }

    public final void setRepository(String repository) {
        this.repository = repository;
    }

    public final List<String> getTags() {
        return tags;
    }

    public final void setTags(List<String> tags) {
        this.tags = tags;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getSha() {
        return sha;
    }

    public final void setSha(String sha) {
        this.sha = sha;
    }
}
