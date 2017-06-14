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

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.exception.BlackDuckServiceBrokerException;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.json.JsonUtil;

/**
 *
 * @author jfisher
 *
 */
public class HubLoginTest {

    private static String TEST_USERNAME = "test_user";

    private static String TEST_PASSWORD = "test_pass";

    private static String VALID_HUB_LOGIN = "{\"identity\": \"" + TEST_USERNAME + "\" ,\"password\": \"" + TEST_PASSWORD + "\"}";

    private static String NO_USERNAME_HUB_LOGIN = "{\"password\": \"" + TEST_PASSWORD + "\"}";

    private static String NO_PASSWORD_HUB_LOGIN = "{\"identity\": \"" + TEST_USERNAME + "\"}";

    @DataProvider(name = "testDeserializeInvalidHubLogin")
    public Object[][] createInvalidHubLogins() {
        return new Object[][] {
                { NO_USERNAME_HUB_LOGIN },
                { NO_PASSWORD_HUB_LOGIN },
        };
    }

    @Test
    public void testDeserializeValidHubLogin() {
        HubLogin vut = null;
        try {
            vut = JsonUtil.getObjectMapper().readValue(VALID_HUB_LOGIN, HubLogin.class);
        } catch (IOException e) {
            Assert.fail("Unexpected JSON parsing exception occurred", e);
        }

        Assert.assertNotNull(vut, "Deserialized HubLogin object should not be null");
        Assert.assertEquals(vut.getUsername(), TEST_USERNAME, "HubLogin username incorrect");
        Assert.assertEquals(vut.getPassword(), TEST_PASSWORD, "HubLogin password incorrect");
    }

    @Test(dataProvider = "testDeserializeInvalidHubLogin", expectedExceptions = { BlackDuckServiceBrokerException.class })
    public void testDesserializeInvalidHubLogin(String invalidHubLogin) {
        HubLogin vut = null;
        vut = JsonUtil.readValue(invalidHubLogin, HubLogin.class);

        Assert.assertNull(vut, "Should have thrown BlackDuckServiceBrokerException");
    }
}
