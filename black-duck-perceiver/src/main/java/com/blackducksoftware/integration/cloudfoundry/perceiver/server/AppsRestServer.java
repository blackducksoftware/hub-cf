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
package com.blackducksoftware.integration.cloudfoundry.perceiver.server;

import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.blackducksoftware.integration.cloudfoundry.perceiver.iface.IEventMonitorService;

/**
 * @author fisherj
 *
 */
@Controller
public class AppsRestServer {
    private final Logger logger = LoggerFactory.getLogger(AppsRestServer.class);

    private final IEventMonitorService eventMonitorService;

    @Autowired
    public AppsRestServer(IEventMonitorService eventMonitorService) {
        this.eventMonitorService = eventMonitorService;
    }

    @PostMapping(path = "/apps")
    public ResponseEntity<UUID> registerId(@RequestBody UUID appId) {
        logger.info("Entered POST app id");
        HttpStatus resp = eventMonitorService.registerId(appId) ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<UUID>(appId, resp);
    }

    @DeleteMapping(path = "/apps/{appId}")
    public ResponseEntity<?> unregisterId(@PathVariable("appId") UUID appId) {
        eventMonitorService.unregisterId(appId);
        return new ResponseEntity<>(Collections.emptyMap(), HttpStatus.OK);
    }
}
