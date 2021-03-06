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
package com.blackducksoftware.integration.cloudfoundry.perceiver;

/**
 * @author fisherj
 *
 */
public enum IntegrationSource {
    ALLIANCES("Alliance Integrations"),

    UNKNOWN("<unknown>");

    private final String text;

    IntegrationSource(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
