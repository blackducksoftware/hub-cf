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
package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl;

import java.util.Optional;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.HubCredentials;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.HubProjectParameters;

/**
 *
 * @author jfisher
 *
 */
public class BindingInstanceServiceTest {
    private static final String HUB_SCHEME = "https";

    private static final String HUB_HOST = "test_host";

    private static final Integer HUB_PORT = 1;

    private static final String HUB_LOGIN_JSON = "{\"identity\": \"testUser\", \"password\": \"testPass\"}";

    private static final String PROJ_NAME = "testProj";

    private static final String CODE_LOC = "codeLocationName";

    private static final String SERVICE_ID = "testServiceId";

    private static final String BINDING_ID = "testBindId";

    @Mock
    private ServiceInstanceService serviceInstanceService;

    private HubCredentials hubCreds;

    private BindingInstanceService bindingInstanceService;

    @DataProvider(name = "TestHubProjectParameters")
    public Object[][] createTestHubProjectParameters() {
        return new Object[][] {
                { Optional.empty() },
                { Optional.ofNullable(null) },
                { Optional.ofNullable(new HubProjectParameters(null, null)) },
                { Optional.ofNullable(new HubProjectParameters(PROJ_NAME, CODE_LOC)) },
        };
    }

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);

        hubCreds = new HubCredentials(HUB_SCHEME, HUB_HOST, HUB_PORT, HUB_LOGIN_JSON);

        bindingInstanceService = new BindingInstanceService(serviceInstanceService, hubCreds);
    }

    @Test(dataProvider = "TestHubProjectParameters")
    public void testCreate(Optional<HubProjectParameters> hpp) {
        Mockito.when(serviceInstanceService.isExists(Mockito.anyString())).thenReturn(true);

        bindingInstanceService.create(BINDING_ID, SERVICE_ID, hpp);

        Assert.assertTrue(bindingInstanceService.isExists(Mockito.anyString(), BINDING_ID), "An internal error occurred and the binding was not created.");
    }
}
