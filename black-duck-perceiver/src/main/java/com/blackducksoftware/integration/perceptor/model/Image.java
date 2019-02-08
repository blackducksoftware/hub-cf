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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fisherj
 *
 */
public final class Image {
    @JsonProperty("Repository")
    private String repository;

    @JsonProperty("Tag")
    private String tag;

    @JsonProperty("Sha")
    private String sha;

    @JsonProperty("Priority")
    private int priority;

    @JsonProperty("BlackDuckProjectName")
    private String blackDuckProjectName;

    @JsonProperty("BlackDuckProjectVersion")
    private String blackDuckProjectVersion;

    public Image() {

    }

    public final String getRepository() {
        return repository;
    }

    public final void setRepository(String repository) {
        this.repository = repository;
    }

    public final String getTag() {
        return tag;
    }

    public final void setTag(String tag) {
        this.tag = tag;
    }

    public final String getSha() {
        return sha;
    }

    public final void setSha(String sha) {
        this.sha = sha;
    }

    public final int getPriority() {
        return priority;
    }

    public final void setPriority(int priority) {
        this.priority = priority;
    }

    public final String getBlackDuckProjectName() {
        return blackDuckProjectName;
    }

    public final void setBlackDuckProjectName(String blackDuckProjectName) {
        this.blackDuckProjectName = blackDuckProjectName;
    }

    public final String getBlackDuckProjectVersion() {
        return blackDuckProjectVersion;
    }

    public final void setBlackDuckProjectVersion(String blackDuckProjectVersion) {
        this.blackDuckProjectVersion = blackDuckProjectVersion;
    }
}
