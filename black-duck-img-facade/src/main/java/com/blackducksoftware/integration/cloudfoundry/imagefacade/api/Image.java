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
package com.blackducksoftware.integration.cloudfoundry.imagefacade.api;

import java.util.Objects;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fisherj
 *
 */
@Validated
public final class Image {
    public static final String DELIMITER = "@sha256:";

    private final String pullSpec;

    @JsonCreator
    public Image(@JsonProperty("PullSpec") String pullSpec) {
        this.pullSpec = pullSpec;
    }

    public final String getPullSpec() {
        return pullSpec;
    }

    @JsonIgnore
    public final String asAppId() {
        return pullSpec.split(DELIMITER)[0];
    }

    @JsonIgnore
    public final String asSha() {
        return pullSpec.split(DELIMITER)[1];
    }

    @Override
    public int hashCode() {
        return Objects.hash(pullSpec);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Image)) {
            return false;
        }

        Image otherPullSpec = (Image) other;
        return Objects.equals(getPullSpec(), otherPullSpec.getPullSpec());
    }

    @Override
    public String toString() {
        return getPullSpec();
    }
}
