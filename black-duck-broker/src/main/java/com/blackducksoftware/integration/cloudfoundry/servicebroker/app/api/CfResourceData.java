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
package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api;

import java.util.ArrayList;

import org.cloudfoundry.client.v3.droplets.DropletResource;

import com.blackducksoftware.integration.perceptor.model.Container;
import com.blackducksoftware.integration.perceptor.model.Image;
import com.blackducksoftware.integration.perceptor.model.Pod;

public class CfResourceData {
    private String resourceId;

    private String bindingId;

    private String applicationId;

    private DropletResource dropletData;

    public CfResourceData() {
    }

    public CfResourceData(String resourceId, String bindingId, String applicationId, DropletResource dropletData) {
        this.resourceId = resourceId;
        this.bindingId = bindingId;
        this.applicationId = applicationId;
        this.dropletData = dropletData;
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

    public Pod toPod() {
        Image img = new Image();
        img.setRepository(getApplicationId()); // This is the application Id...this is the only
        // piece required to download the actual droplet
        img.setSha(getDropletData().getChecksum().getValue()); // The checksum of
        // the "current"
        // droplet. Used to ensure read
        // consistency later

        Container ctr = new Container();
        ctr.setImage(img);
        ctr.setName(getDropletData().getId()); // The Droplet Id as the Container
        // Id

        Pod pod = new Pod();
        pod.setContainers(new ArrayList<Container>() {
            {
                add(ctr);
            }
        });
        pod.setId(getBindingId()); // The Service Binding Id as the Pod Id
        pod.setNamespcae(""); // No namespace?????

        return pod;
    }
}
