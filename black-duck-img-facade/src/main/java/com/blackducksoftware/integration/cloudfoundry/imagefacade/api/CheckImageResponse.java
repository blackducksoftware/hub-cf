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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fisherj
 *
 */
public final class CheckImageResponse {
    private final String pullSpec;

    private final int imageStatus;

    @JsonCreator
    public CheckImageResponse(String pullSpec, int imageStatus) {
        this.pullSpec = pullSpec;
        this.imageStatus = imageStatus;
    }

    @JsonProperty(value = "PullSpec")
    public final String getPullSpec() {
        return pullSpec;
    }

    @JsonProperty(value = "ImageStatus")
    public final int getImageStatus() {
        return imageStatus;
    }
}
