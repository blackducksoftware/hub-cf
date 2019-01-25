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
package com.blackducksoftware.integration.cloudfoundry.perceiver.api;

import org.cloudfoundry.client.v3.droplets.DropletResource;

import com.blackducksoftware.integration.perceptor.model.Image;

public class CfResourceData {
    private String resourceId;

    private String bindingId;

    private String applicationId;

    private DropletResource dropletData;

    private HubProjectParameters hubProjectParameters;

    public CfResourceData() {
    }

    public CfResourceData(String resourceId, String bindingId, String applicationId, DropletResource dropletData, HubProjectParameters hubProjectParameters) {
        this.resourceId = resourceId;
        this.bindingId = bindingId;
        this.applicationId = applicationId;
        this.dropletData = dropletData;
        this.hubProjectParameters = hubProjectParameters;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getBindingId() {
        return bindingId;
    }

    public void setBindingId(String bindingId) {
        this.bindingId = bindingId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public DropletResource getDropletData() {
        return dropletData;
    }

    public void setDropletData(DropletResource dropletData) {
        this.dropletData = dropletData;
    }

    public HubProjectParameters getHubProjectParameters() {
        return hubProjectParameters;
    }

    public void setHubProjectParameters(HubProjectParameters hubProjectParameters) {
        this.hubProjectParameters = hubProjectParameters;
    }

    public Image toImage() {
        // Repository -> Blackduck Project
        // Sha -> Application Id + Checksum
        // Tag -> Blackduck Project Version

        Image img = new Image();
        // The Respository is the location where the blob data lives
        img.setRepository(getApplicationId());

        // The checksum of the "current" droplet. Used to ensure read consistency later
        img.setSha(getDropletData().getChecksum().getValue());

        // Set the custom Project Name to appear in BlackDuck if one was provided
        img.setBlackDuckProjectName(getHubProjectParameters().getProjectName().orElse(null));

        // Set the custom Project Version to use in BlackDuck if one was provided
        img.setBlackDuckProjectVersion(getHubProjectParameters().getProjectVersion().orElse(null));

        return img;
    }
}
