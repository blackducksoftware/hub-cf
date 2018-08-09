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
package com.blackducksoftware.integration.cloudfoundry.imagefacade.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.cloudfoundry.imagefacade.api.Image;
import com.blackducksoftware.integration.cloudfoundry.imagefacade.model.ImageModel;
import com.blackducksoftware.integration.cloudfoundry.imagefacade.model.ImageStatus;

/**
 * @author fisherj
 *
 */
public class ImageFacadeService {
    private static final Logger logger = LoggerFactory.getLogger(ImageFacadeService.class);

    private final String dropletLocation;

    private final ImageModel imageModel;

    public ImageFacadeService(ImageModel imageModel, String dropletLocation) {
        this.dropletLocation = dropletLocation;
        this.imageModel = imageModel;
    }

    public boolean pullImage(Image image) {
        logger.trace("entering pullImage");
        boolean result = false;
        try {
            result = imageModel.pullImage(image, dropletLocation);
        } catch (Exception e) {
            logger.error("fatal error executing image pull", e);
        }
        logger.trace("exiting pullImage");
        return result;
    }

    public ImageStatus checkImage(Image image) {
        return imageModel.getImageStatus(image);
    }
}
