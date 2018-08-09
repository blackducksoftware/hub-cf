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
package com.blackducksoftware.integration.cloudfoundry.imagefacade.exception;

import com.blackducksoftware.integration.cloudfoundry.imagefacade.api.Image;

/**
 * @author fisherj
 *
 */
public class ImagePullerException extends RuntimeException {
    private final Image image;

    public ImagePullerException(String message, Image image) {
        super(message);
        this.image = image;
    }

    public ImagePullerException(String message, Throwable throwable, Image image) {
        super(message, throwable);
        this.image = image;
    }

    public ImagePullerException(Throwable throwable, Image image) {
        super(throwable);
        this.image = image;
    }

    public final Image getImage() {
        return image;
    }
}
