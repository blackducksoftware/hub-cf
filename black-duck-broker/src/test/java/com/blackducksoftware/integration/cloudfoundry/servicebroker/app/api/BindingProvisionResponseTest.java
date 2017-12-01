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

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jfisher
 *
 */
public class BindingProvisionResponseTest {
    private static String TEST_KEY_SCHEME = "scheme";

    private static String TEST_SCHEME = "https";

    private static String TEST_KEY_HOST = "host";

    private static String TEST_HOST = "test.host.org";

    private static String TEST_KEY_PORT = "port";

    private static int TEST_PORT = 9090;

    private static String TEST_KEY_USERNAME = "username";

    private static String TEST_USERNAME = "testUser";

    private static String TEST_KEY_PASSWORD = "password";

    private static String TEST_PASSWORD = "testPassword";

    private static String TEST_KEY_PROJECTNAME = "projectName";

    private static String TEST_PROJECTNAME = "Test Project";

    private static String TEST_KEY_CODELOCATIONNAME = "codeLocationName";

    private static String TEST_CODELOCATIONNAME = "testCodeLocName";

    private static String TEST_KEY_ISINSECURE = "isInsecure";

    private static Boolean TEST_ISINSECURE = Boolean.FALSE;

    private static String TEST_KEY_PLUGINVERSION = "pluginVersion";

    private static String TEST_PLUGIN_VERSION = "testPluginVer";

    private static String TEST_KEY_INTEGRATIONSOURCE = "integrationSource";

    private static String TEST_INTEGRATION_SOURCE = "testIntegrationSource";

    private static String TEST_KEY_INTEGRATIONVENDOR = "integrationVendor";

    private static String TEST_INTEGRATION_VENDOR = "integrationVendor";

    private static Map<String, Object> DATA_MAP = new HashMap<String, Object>() {
        {
            put(TEST_KEY_SCHEME, TEST_SCHEME);
            put(TEST_KEY_HOST, TEST_HOST);
            put(TEST_KEY_PORT, TEST_PORT);
            put(TEST_KEY_USERNAME, TEST_USERNAME);
            put(TEST_KEY_PASSWORD, TEST_PASSWORD);
            put(TEST_KEY_PROJECTNAME, TEST_PROJECTNAME);
            put(TEST_KEY_CODELOCATIONNAME, TEST_CODELOCATIONNAME);
            put(TEST_KEY_ISINSECURE, TEST_ISINSECURE);
            put(TEST_KEY_PLUGINVERSION, TEST_PLUGIN_VERSION);
            put(TEST_KEY_INTEGRATIONSOURCE, TEST_INTEGRATION_SOURCE);
            put(TEST_KEY_INTEGRATIONVENDOR, TEST_INTEGRATION_VENDOR);
        }
    };

    private BindingInstance bindInst;

    @BeforeClass
    public void setupData() {
        bindInst = new BindingInstance(TEST_SCHEME, TEST_HOST, TEST_PORT, TEST_USERNAME, TEST_PASSWORD, TEST_PROJECTNAME, TEST_CODELOCATIONNAME,
                TEST_ISINSECURE, TEST_PLUGIN_VERSION, TEST_INTEGRATION_SOURCE, TEST_INTEGRATION_VENDOR);
    }

    @Test
    public void testBindProvisionResponse() {
        BindingProvisionResponse bpr = BindingInstance.toBindingProvisionResponse(bindInst);

        Assert.assertNotNull(bpr, "BindingProvisionResponse object should not be null");
        Assert.assertNotNull(bpr.getCredentials(), "BindingProvisionResponse credentials should not be null");

        Map<String, Object> creds = bpr.getCredentials();
        DATA_MAP.forEach((k, v) -> {
            Assert.assertTrue(creds.containsKey(k), "BindingProvisionResponse should contain key: " + k);
            Assert.assertEquals(creds.get(k), v, "BindingProvisionResponse value for key: " + k + " incorrect");
            creds.remove(k);
        });
        Assert.assertEquals(creds.size(), 0, "BindingProvisionResponse credentials should not contain extra fields");
    }
}
