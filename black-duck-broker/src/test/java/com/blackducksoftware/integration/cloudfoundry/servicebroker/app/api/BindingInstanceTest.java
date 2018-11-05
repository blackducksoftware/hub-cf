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

import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jfisher
 *
 */
public class BindingInstanceTest {
    private static UUID TEST_APPGUID = UUID.randomUUID();

    private static String TEST_PROJECTNAME = "test project";

    private static String TEST_CODELOCATIONNAME = "test/location";

    private static String TEST_PLUGIN_VERSION = "testVer";

    private BindingInstance testInstance;

    @BeforeClass
    public void setupObject() {
        testInstance = new BindingInstance(TEST_APPGUID, TEST_PROJECTNAME,
                TEST_CODELOCATIONNAME,
                TEST_PLUGIN_VERSION);
    }

    @Test(priority = 0)
    public void testInstanceValid() {
        Assert.assertNotNull(testInstance, "BindingInstance object not created successfully");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testAppGuid() {
        Assert.assertEquals(testInstance.getAppGuid(), TEST_APPGUID, "BindingInstance appGuid incorrect");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testProjectNameValid() {
        Assert.assertEquals(testInstance.getProjectName(), TEST_PROJECTNAME, "BindingInstance projectName incorrect");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testCodeLocationNameValid() {
        Assert.assertEquals(testInstance.getCodeLocationName(), TEST_CODELOCATIONNAME, "BindingInstance codeLocationName incorrect");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testPluginVersionValid() {
        Assert.assertEquals(testInstance.getPluginVersion(), TEST_PLUGIN_VERSION, "BindingInstance pluginVersion incorrect");
    }
}
