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

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.BindResource;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.HubCredentials;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.HubProjectParameters;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.PhoneHomeParameters;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.iface.ICloudControllerEventMonitorService;

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

    private static final Boolean HUB_INSECURE = Boolean.TRUE;

    private static final String PROJ_NAME = "testProj";

    private static final String APP_GUID = "testAppGuid";

    private static final String ROUTE = "testRoute";

    private static final String CODE_LOC = "codeLocationName";

    private static final String SERVICE_ID = "testServiceId";

    private static final String BINDING_ID = "testBindId";

    private static final String PLUGIN_VERSION = "testPluginVer";

    private static final String INTEGRATION_SOURCE = "testIntegrationSource";

    private static final String INTEGRATION_VENDOR = "testIntegrationVendor";

    @Mock
    private ServiceInstanceService serviceInstanceService;

    @Mock
    private ICloudControllerEventMonitorService ccEventMonitorHandler;

    private HubCredentials hubCreds;

    private PhoneHomeParameters phoneHomeParms;

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

        hubCreds = new HubCredentials(HUB_SCHEME, HUB_HOST, HUB_PORT, HUB_LOGIN_JSON, HUB_INSECURE);

        phoneHomeParms = new PhoneHomeParameters(INTEGRATION_SOURCE, INTEGRATION_VENDOR);

        bindingInstanceService = new BindingInstanceService(serviceInstanceService, hubCreds, PLUGIN_VERSION, phoneHomeParms, ccEventMonitorHandler);
    }

    @Test(dataProvider = "TestHubProjectParameters")
    public void testCreateWithHubProjectParameters(Optional<HubProjectParameters> hpp) {
        Mockito.when(serviceInstanceService.isExists(Mockito.anyString())).thenReturn(true);

        Optional<BindResource> bind = Optional.of(Mockito.mock(BindResource.class));

        bindingInstanceService.create(BINDING_ID, SERVICE_ID, bind, hpp);

        Assert.assertTrue(bindingInstanceService.isExists(Mockito.anyString(), BINDING_ID), "An internal error occurred and the binding was not created.");
    }

    @Test
    public void testCreateWithValidAppGuid() {
        Mockito.when(serviceInstanceService.isExists(Mockito.anyString())).thenReturn(true);

        Optional<HubProjectParameters> hpp = Optional.of(Mockito.mock(HubProjectParameters.class));

        Optional<BindResource> bind = Optional.of(Mockito.mock(BindResource.class));

        bindingInstanceService.create(BINDING_ID, SERVICE_ID, bind, hpp);

        Assert.assertTrue(bindingInstanceService.isExists(Mockito.anyString(), BINDING_ID), "An internal error occurred and the binding was not created.");
    }

    @Test(dataProvider = "TestInvalidBindResource")
    public void testCreateWithInvalidAppGuid(Optional<BindResource> bind) {
        Mockito.when(serviceInstanceService.isExists(Mockito.anyString())).thenReturn(true);

        Optional<HubProjectParameters> hpp = Optional.of(Mockito.mock(HubProjectParameters.class));

        bindingInstanceService.create(BINDING_ID, SERVICE_ID, bind, hpp);

        Assert.assertFalse(bindingInstanceService.isExists(Mockito.anyString(), BINDING_ID), "Binding created in error.");
    }
}
