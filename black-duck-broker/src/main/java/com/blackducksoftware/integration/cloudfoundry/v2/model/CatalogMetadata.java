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
package com.blackducksoftware.integration.cloudfoundry.v2.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fisherj
 *
 */
public final class CatalogMetadata {
    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("providerDisplayName")
    private String providerDisplayName;

    @JsonProperty("documentationUrl")
    private String documentationUrl;

    @JsonProperty("longDescription")
    private String longDescription;

    public CatalogMetadata() {

    }

    public final String getDisplayName() {
        return displayName;
    }

    public final void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public final String getImageUrl() {
        return imageUrl;
    }

    public final void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public final String getProviderDisplayName() {
        return providerDisplayName;
    }

    public final void setProviderDisplayName(String providerDisplayName) {
        this.providerDisplayName = providerDisplayName;
    }

    public final String getDocumentationUrl() {
        return documentationUrl;
    }

    public final void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }

    public final String getLongDescription() {
        return longDescription;
    }

    public final void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}
