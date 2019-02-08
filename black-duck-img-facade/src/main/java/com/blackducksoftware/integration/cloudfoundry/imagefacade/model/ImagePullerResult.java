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

import java.util.Optional;

import com.blackducksoftware.integration.cloudfoundry.imagefacade.api.Image;

/**
 * @author fisherj
 *
 */
public final class ImagePullerResult {
    private final Image image;

    private final Optional<String> error;

    public ImagePullerResult(Image image, String error) {
        this.image = image;
        this.error = Optional.ofNullable(error);
    }

    public final Image getImage() {
        return image;
    }

    public final Optional<String> getError() {
        return error;
    }
}
