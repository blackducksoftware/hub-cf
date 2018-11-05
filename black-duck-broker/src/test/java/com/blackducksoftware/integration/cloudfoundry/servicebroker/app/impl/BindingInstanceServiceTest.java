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
import java.util.UUID;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.BindResource;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.HubProjectParameters;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.iface.ICloudControllerEventMonitorService;

/**
 *
 * @author jfisher
 *
 */
public class BindingInstanceServiceTest {
    private static final String PROJ_NAME = "testProj";

    private static final UUID APP_GUID = UUID.randomUUID();

    private static final String ROUTE = "testRoute";

    private static final String CODE_LOC = "codeLocationName";

    private static final String SERVICE_ID = "testServiceId";

    private static final String BINDING_ID = "testBindId";

    private static final String PLUGIN_VERSION = "testPluginVer";

    @Mock
    private ServiceInstanceService serviceInstanceService;

    @Mock
    private ICloudControllerEventMonitorService ccEventMonitorHandler;

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

    @DataProvider(name = "TestInvalidBindResource")
    public Object[][] createTestInvalidBindResource() {
        return new Object[][] {
                { Optional.empty() },
                { Optional.ofNullable(null) },
                { Optional.ofNullable(new BindResource(null, ROUTE)) },
                { Optional.ofNullable(new BindResource(null, null)) },
        };
    }

    @BeforeMethod
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);

        bindingInstanceService = new BindingInstanceService(serviceInstanceService, PLUGIN_VERSION, ccEventMonitorHandler);
    }

    @Test(dataProvider = "TestHubProjectParameters")
    public void testCreateWithHubProjectParameters(Optional<HubProjectParameters> hpp) {
        Mockito.when(serviceInstanceService.isExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(ccEventMonitorHandler.registerId(Mockito.any(UUID.class))).thenReturn(true);

        Optional<BindResource> bind = Optional.of(new BindResource(APP_GUID.toString(), null));

        bindingInstanceService.create(BINDING_ID, SERVICE_ID, bind, hpp);

        Assert.assertTrue(bindingInstanceService.isExists(Mockito.anyString(), BINDING_ID), "An internal error occurred and the binding was not created.");
    }

    @Test
    public void testCreateWithValidAppGuid() {
        Mockito.when(serviceInstanceService.isExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(ccEventMonitorHandler.registerId(Mockito.any(UUID.class))).thenReturn(true);

        Optional<HubProjectParameters> hpp = Optional.of(Mockito.mock(HubProjectParameters.class));

        Optional<BindResource> bind = Optional.of(new BindResource(APP_GUID.toString(), null));

        bindingInstanceService.create(BINDING_ID, SERVICE_ID, bind, hpp);

        Assert.assertTrue(bindingInstanceService.isExists(Mockito.anyString(), BINDING_ID), "An internal error occurred and the binding was not created.");
    }

    @Test(dataProvider = "TestInvalidBindResource")
    public void testCreateWithInvalidAppGuid(Optional<BindResource> bind) {
        Mockito.when(serviceInstanceService.isExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(ccEventMonitorHandler.registerId(Mockito.any(UUID.class))).thenReturn(true);

        Optional<HubProjectParameters> hpp = Optional.of(Mockito.mock(HubProjectParameters.class));

        bindingInstanceService.create(BINDING_ID, SERVICE_ID, bind, hpp);

        Assert.assertFalse(bindingInstanceService.isExists(Mockito.anyString(), BINDING_ID), "Binding created in error.");
    }
}
