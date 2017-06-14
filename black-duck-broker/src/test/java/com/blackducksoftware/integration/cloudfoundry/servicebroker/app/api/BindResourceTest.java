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
package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api;

import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.json.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author jfisher
 *
 */
public class BindResourceTest {
    private static Optional<String> TEST_APPGUID = Optional.of("testAppGuid");

    private static Optional<String> TEST_ROUTE = Optional.of("testRoute");

    private static String BIND_RESOURCE_EMPTY_JSON = "{}";

    private String bindResourceJsonAllPresent;

    private String bindResourceJsonNoAppGuid;

    private String bindResourceJsonNoRoute;

    @BeforeClass
    public void setupData() {
        try {
            bindResourceJsonAllPresent = JsonUtil.getObjectMapper().writeValueAsString(new BindResource(TEST_APPGUID, TEST_ROUTE));
            bindResourceJsonNoAppGuid = JsonUtil.getObjectMapper().writeValueAsString(new BindResource(Optional.empty(), TEST_ROUTE));
            bindResourceJsonNoRoute = JsonUtil.getObjectMapper().writeValueAsString(new BindResource(TEST_APPGUID, Optional.empty()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBindResourceJsonAllPresent() {
        BindResource bindResource = JsonUtil.readValue(bindResourceJsonAllPresent, BindResource.class);

        Assert.assertNotNull(bindResource, "BindResource deserialized object should not be null");
        Assert.assertTrue(bindResource.getAppGuid().isPresent(), "BindResource appGuid should be present");
        Assert.assertTrue(bindResource.getRoute().isPresent(), "BindResource route should be present");

        Assert.assertEquals(bindResource.getAppGuid().get(), TEST_APPGUID.get(), "BindResource appGuid incorrect");
        Assert.assertEquals(bindResource.getRoute().get(), TEST_ROUTE.get(), "BindResource route incorrect");
    }

    @Test
    public void testBindResourceJsonNoAppGuid() {
        BindResource bindResource = JsonUtil.readValue(bindResourceJsonNoAppGuid, BindResource.class);

        Assert.assertNotNull(bindResource, "BindResource deserialized object should not be null");
        Assert.assertFalse(bindResource.getAppGuid().isPresent(), "BindResource appGuid should not be present");
        Assert.assertTrue(bindResource.getRoute().isPresent(), "BindResource route should be present");

        Assert.assertEquals(bindResource.getRoute().get(), TEST_ROUTE.get(), "BindResource route incorrect");
    }

    @Test
    public void testBindResourceJsonNoRoute() {
        BindResource bindResource = JsonUtil.readValue(bindResourceJsonNoRoute, BindResource.class);

        Assert.assertNotNull(bindResource, "BindResource deserialized object should not be null");
        Assert.assertTrue(bindResource.getAppGuid().isPresent(), "BindResource appGuid should be present");
        Assert.assertFalse(bindResource.getRoute().isPresent(), "BindResource route should not be present");

        Assert.assertEquals(bindResource.getAppGuid().get(), TEST_APPGUID.get(), "BindResource appGuid incorrect");
    }

    @Test
    public void testBindResourceEmptyJson() {
        BindResource bindResource = JsonUtil.readValue(BIND_RESOURCE_EMPTY_JSON, BindResource.class);

        Assert.assertNotNull(bindResource, "BindResource deserialized object should not be null");
        Assert.assertFalse(bindResource.getAppGuid().isPresent(), "BindResource appGuid should not be present");
        Assert.assertFalse(bindResource.getRoute().isPresent(), "BindResource route should not be present");
    }
}
