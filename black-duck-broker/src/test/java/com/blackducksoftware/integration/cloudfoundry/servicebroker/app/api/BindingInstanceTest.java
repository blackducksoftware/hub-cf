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

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jfisher
 *
 */
public class BindingInstanceTest {
    private static String TEST_SCHEME = "https";

    private static String TEST_HOST = "test.host";

    private static int TEST_PORT = 9090;

    private static String TEST_USERNAME = "tester";

    private static String TEST_PASSWORD = "not_private";

    private static String TEST_PROJECTNAME = "test project";

    private static String TEST_CODELOCATIONNAME = "test/location";

    private static boolean TEST_INSECURE = true;

    private static String TEST_PLUGIN_VERSION = "testVer";

    private BindingInstance testInstance;

    @BeforeClass
    public void setupObject() {
        testInstance = new BindingInstance(TEST_SCHEME, TEST_HOST, TEST_PORT, TEST_USERNAME, TEST_PASSWORD, TEST_PROJECTNAME, TEST_CODELOCATIONNAME,
                TEST_INSECURE, TEST_PLUGIN_VERSION);
    }

    @Test(priority = 0)
    public void testInstanceValid() {
        Assert.assertNotNull(testInstance, "BindingInstance object not created successfully");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testSchemeValue() {
        Assert.assertEquals(testInstance.getScheme(), TEST_SCHEME, "BindingInstance scheme incorrect");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testHostValid() {
        Assert.assertEquals(testInstance.getHost(), TEST_HOST, "BindingInstance host incorrect");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testPortValid() {
        Assert.assertEquals(testInstance.getPort(), TEST_PORT, "BindingInstance port incorrect");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testUserNameValid() {
        Assert.assertEquals(testInstance.getUsername(), TEST_USERNAME, "BindingInstance username incorrect");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testPasswordValid() {
        Assert.assertEquals(testInstance.getPassword(), TEST_PASSWORD, "BindingInstance password incorrect");
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
    public void testIsInsecureValid() {
        Assert.assertEquals(testInstance.getIsInsecure(), TEST_INSECURE, "BindingInstance isInsecure incorrect");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testPluginVersionValid() {
        Assert.assertEquals(testInstance.getPluginVersion(), TEST_PLUGIN_VERSION, "BindingInstance pluginVersion incorrect");
    }
}
