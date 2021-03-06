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
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author jfisher
 *
 */
public class BindingProvisionResponseTest {
    private static String TEST_KEY_APPGUID = "appGuid";

    private static UUID TEST_APPGUID = UUID.randomUUID();

    private static String TEST_KEY_PROJECTNAME = "projectName";

    private static String TEST_PROJECTNAME = "Test Project";

    private static String TEST_KEY_CODELOCATIONNAME = "codeLocationName";

    private static String TEST_CODELOCATIONNAME = "testCodeLocName";

    private static String TEST_KEY_PLUGINVERSION = "pluginVersion";

    private static String TEST_PLUGIN_VERSION = "testPluginVer";

    private static Map<String, Object> DATA_MAP = new HashMap<String, Object>() {
        {
            put(TEST_KEY_APPGUID, TEST_APPGUID);
            put(TEST_KEY_PROJECTNAME, TEST_PROJECTNAME);
            put(TEST_KEY_CODELOCATIONNAME, TEST_CODELOCATIONNAME);
            put(TEST_KEY_PLUGINVERSION, TEST_PLUGIN_VERSION);
        }
    };

    private BindingInstance bindInst;

    @BeforeClass
    public void setupData() {
        bindInst = new BindingInstance(TEST_APPGUID, TEST_PROJECTNAME, TEST_CODELOCATIONNAME,
                TEST_PLUGIN_VERSION);
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
