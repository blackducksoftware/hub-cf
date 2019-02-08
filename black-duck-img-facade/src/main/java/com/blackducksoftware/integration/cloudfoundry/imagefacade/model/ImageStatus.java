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
package com.blackducksoftware.integration.cloudfoundry.imagefacade.model;

/**
 * @author fisherj
 *
 */
public enum ImageStatus {
    UNKNOWN("Unknown"),
    INPROGRESS("InProgress"),
    DONE("Done"),
    ERROR("Error");

    private final String value;

    private ImageStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return name() + "(" + value() + ")";
    }
}
