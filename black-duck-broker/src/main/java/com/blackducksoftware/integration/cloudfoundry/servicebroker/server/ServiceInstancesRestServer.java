/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.cloudfoundry.servicebroker.server;

import java.util.Collections;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.ServiceProvisionRequest;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl.ServiceInstanceService;

/**
 * @author jfisher
 *
 */
@RestController
@RequestMapping("/v2/service_instances/{instance_id}")
public class ServiceInstancesRestServer {
    private static final Logger logger = LoggerFactory.getLogger(ServiceInstancesRestServer.class);

    private final ServiceInstanceService serviceInstanceService;

    @Autowired
    public ServiceInstancesRestServer(ServiceInstanceService serviceInstanceService) {
        this.serviceInstanceService = serviceInstanceService;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> update(@PathVariable("instance_id") String instanceId, @Valid @RequestBody ServiceProvisionRequest body) {
        logger.info("Entered PUT service instance");
        HttpStatus respCode;

        if (serviceInstanceService.create(instanceId)) {
            respCode = HttpStatus.CREATED;
            logger.trace("Service Instance created");
        } else {
            respCode = HttpStatus.OK;
            logger.warn("Service Instance: " + instanceId + " already exists");
        }
        logger.debug("Returning: " + respCode);
        return new ResponseEntity<>(Collections.emptyMap(), respCode);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("instance_id") String instanceId) {
        logger.info("Entered DELETE service instance");
        return new ResponseEntity<>(Collections.emptyMap(), (serviceInstanceService.delete(instanceId) ? HttpStatus.OK : HttpStatus.GONE));
    }
}
