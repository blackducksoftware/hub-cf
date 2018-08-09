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
package com.blackducksoftware.integration.cloudfoundry.imagefacade.server;

import java.util.Collections;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.blackducksoftware.integration.cloudfoundry.imagefacade.api.CheckImageResponse;
import com.blackducksoftware.integration.cloudfoundry.imagefacade.api.Image;
import com.blackducksoftware.integration.cloudfoundry.imagefacade.impl.ImageFacadeService;
import com.blackducksoftware.integration.cloudfoundry.imagefacade.validation.ImageValidator;

/**
 * @author fisherj
 *
 */
@Controller
public class ImageFacadeRestServer {
    private final Logger logger = LoggerFactory.getLogger(ImageFacadeRestServer.class);

    private final ImageFacadeService imageFacadeService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(new ImageValidator());
    }

    @Autowired
    public ImageFacadeRestServer(ImageFacadeService imageFacadeService) {
        this.imageFacadeService = imageFacadeService;
    }

    @PostMapping(path = "/pullimage")
    public ResponseEntity<?> pullImage(@Valid @RequestBody Image body) {
        logger.info("Entered POST pull image");
        HttpStatus respCode = imageFacadeService.pullImage(body) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(Collections.emptyMap(), respCode);
    }

    @PostMapping(path = "/checkimage")
    public ResponseEntity<CheckImageResponse> checkImage(@Valid @RequestBody Image body) {
        logger.info("Entered POST check image");
        logger.trace("Checking pullSpec: {}", body);
        return new ResponseEntity<>(new CheckImageResponse(body.getPullSpec(), imageFacadeService.checkImage(body).value()), HttpStatus.OK);
    }
}
