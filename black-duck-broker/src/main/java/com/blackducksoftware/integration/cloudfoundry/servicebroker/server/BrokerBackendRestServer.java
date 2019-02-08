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
package com.blackducksoftware.integration.cloudfoundry.servicebroker.server;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.BackendBindingProvisionRequest;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.BackendServiceInstanceProvisionRequest;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.BindingInstance;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl.BindingInstanceService;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl.ServiceInstanceService;

/**
 * @author fisherj
 *
 */
@RestController
public class BrokerBackendRestServer {
    private static final Logger logger = LoggerFactory.getLogger(BrokerBackendRestServer.class);

    private final ServiceInstanceService serviceInstanceService;

    private final BindingInstanceService bindingInstanceService;

    @Autowired
    public BrokerBackendRestServer(ServiceInstanceService serviceInstanceService,
            BindingInstanceService bindingInstanceService) {
        this.serviceInstanceService = serviceInstanceService;
        this.bindingInstanceService = bindingInstanceService;
    }

    @GetMapping(path = "/binding_instances")
    public ResponseEntity<Map<String, BindingInstance>> getBindingInstances() {
        logger.info("Entered broker backend GET binding instances");
        return new ResponseEntity<>(bindingInstanceService.getBindingInstancesById(), HttpStatus.OK);
    }

    @PostMapping(path = "/binding_instances")
    public ResponseEntity<BindingInstance> createBindingInstances(@RequestBody BackendBindingProvisionRequest createBindingRequest) {
        logger.info("Entered broker backend POST binding instance");
        HttpStatus respCode = HttpStatus.BAD_REQUEST;
        BindingInstance bindingInstance = null;
        if (serviceInstanceService.isExists(createBindingRequest.getResourceId())) {
            if (!bindingInstanceService.isExists(createBindingRequest.getResourceId(), createBindingRequest.getBindingId())) {
                if ((bindingInstance = bindingInstanceService.create(createBindingRequest.getBindingId(),
                        createBindingRequest.getResourceId(),
                        Optional.ofNullable(createBindingRequest.getBindResource()),
                        Optional.ofNullable(createBindingRequest.getHubProjectParams()))) != null) {
                    respCode = HttpStatus.CREATED;
                    logger.trace("Created binding {}", createBindingRequest.getBindingId());
                } else {
                    respCode = HttpStatus.INTERNAL_SERVER_ERROR;
                    logger.warn("Unable to create binding: {} fpr service: {}", createBindingRequest.getBindingId(), createBindingRequest.getResourceId());
                }
            } else {
                respCode = HttpStatus.OK;
                logger.warn("Binding: {} already exists", createBindingRequest.getBindingId());
            }
        } else {
            logger.warn("Binding instance not created. Service Instance: {} does not exist", createBindingRequest.getResourceId());
        }
        return new ResponseEntity<>(bindingInstance, respCode);
    }

    @GetMapping(path = "/service_instances")
    public ResponseEntity<Set<String>> getServiceInstances() {
        logger.info("Entered backend GET service instances");
        return new ResponseEntity<>(serviceInstanceService.getServiceInstances(), HttpStatus.OK);
    }

    @PostMapping(path = "/service_instances")
    public ResponseEntity<String> createServiceInstances(@RequestBody BackendServiceInstanceProvisionRequest createServiceRequest) {
        logger.info("Entered backend POST service instance");
        HttpStatus respCode = (serviceInstanceService.create(createServiceRequest.getServiceInstanceId()) ? HttpStatus.CREATED : HttpStatus.OK);
        return new ResponseEntity<>(createServiceRequest.getServiceInstanceId(), respCode);
    }
}
