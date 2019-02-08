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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.blackducksoftware.integration.cloudfoundry.v2.model.Catalog;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author jfisher
 *
 */
@RestController
@RequestMapping("/v2/catalog")
public class CatalogRestServer {
    private final Logger logger = LoggerFactory.getLogger(CatalogRestServer.class);

    private Catalog catalog;

    @Autowired
    public CatalogRestServer(Catalog catalog) {
        this.catalog = catalog;
    }

    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<?> getCatalog() throws JsonParseException, JsonMappingException, IOException {
        logger.info("Entered GET Service Broker Catalog");

        logger.debug("Returning: " + HttpStatus.OK + " (" + HttpStatus.OK.name() + "), Catalog: " + catalog);
        return new ResponseEntity<>(catalog, HttpStatus.OK);
    }
}
