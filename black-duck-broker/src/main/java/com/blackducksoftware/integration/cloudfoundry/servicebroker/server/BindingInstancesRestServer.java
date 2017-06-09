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

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.BindingInstance;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.BindingProvisionRequest;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl.BindingInstanceService;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl.ServiceInstanceService;

/**
 * @author jfisher
 *
 */
@RestController
@RequestMapping("/v2/service_instances/{instance_id}/service_bindings/{binding_id}")
public class BindingInstancesRestServer {
    private static final Logger logger = LoggerFactory.getLogger(BindingInstancesRestServer.class);

    private final ServiceInstanceService serviceInstanceService;

    private final BindingInstanceService bindingInstanceService;

    @Autowired
    public BindingInstancesRestServer(ServiceInstanceService serviceInstanceService, BindingInstanceService bindingInstanceService) {
        this.serviceInstanceService = serviceInstanceService;
        this.bindingInstanceService = bindingInstanceService;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> update(@PathVariable("instance_id") String instanceId, @PathVariable("binding_id") String bindingId,
            @RequestBody BindingProvisionRequest body) {
        logger.info("Entered PUT binding instance");
        HttpStatus respCode = HttpStatus.BAD_REQUEST;
        BindingInstance binding = null;
        if (serviceInstanceService.isExists(instanceId)) {
            if (!bindingInstanceService.isExists(instanceId, bindingId)) {
                binding = bindingInstanceService.create(bindingId, instanceId, body.getHubProjectParams());
                respCode = HttpStatus.CREATED;
                logger.trace("Created binding");
            } else {
                respCode = HttpStatus.OK;
                logger.warn("Binding: " + instanceId + " already exists");
            }
        } else {
            logger.warn("Could not create binding instance. Service Instance: " + instanceId + " does not exist");
        }
        logger.debug("Returning: " + respCode + ", Binding: " + BindingInstance.toBindingProvisionResponse(binding));
        return new ResponseEntity<>(BindingInstance.toBindingProvisionResponse(binding), respCode);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable("instance_id") String instanceId, @PathVariable("binding_id") String bindingId) {
        logger.info("Entered DELETE of binding instance");
        HttpStatus respCode;
        if (bindingInstanceService.isExists(instanceId, bindingId)) {
            bindingInstanceService.delete(bindingId);
            respCode = HttpStatus.OK;
            logger.trace("Deleted binding instance");
        } else {
            respCode = HttpStatus.GONE;
            logger.warn("Binding: " + bindingId + " does not exist");
        }
        logger.debug("Returning: " + respCode);
        return new ResponseEntity<>(Collections.emptyMap(), respCode);
    }
}
