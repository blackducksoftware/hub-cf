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
package com.blackducksoftware.integration.cloudfoundry.imagefacade;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fisherj
 *
 */
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
    private String realm;

    private String dropletLocation;

    private int pullRetries;

    private int pullTimeoutSeconds;

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getDropletLocation() {
        return dropletLocation;
    }

    public void setDropletLocation(String dropletLocation) {
        this.dropletLocation = dropletLocation;
    }

    public int getPullRetries() {
        return pullRetries;
    }

    public void setPullRetries(int pullRetries) {
        this.pullRetries = pullRetries;
    }

    public int getPullTimeoutSeconds() {
        return pullTimeoutSeconds;
    }

    public void setPullTimeoutSeconds(int pullTimeoutSeconds) {
        this.pullTimeoutSeconds = pullTimeoutSeconds;
    }
}
