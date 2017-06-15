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

import com.blackducksoftware.integration.cloudfoundry.servicebroker.json.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author jfisher
 *
 */
public class HubProjectParametersTest {
    private static String TEST_PROJECTNAME = "Test Project";

    private static String TEST_CODELOCATION = "testCodeLocation";

    private static String HUB_PROJECT_PARAMETERS_EMPTY_JSON = "{}";

    private String hubProjectParametersJsonAllPresent;

    private String hubProjectParametersJsonNoProject;

    private String hubProjectParametersJsonNoCodeLoc;

    @BeforeClass
    public void setupData() {
        try {
            hubProjectParametersJsonAllPresent = JsonUtil.getObjectMapper().writeValueAsString(new HubProjectParameters(TEST_PROJECTNAME, TEST_CODELOCATION));
            hubProjectParametersJsonNoProject = JsonUtil.getObjectMapper().writeValueAsString(new HubProjectParameters(null, TEST_CODELOCATION));
            hubProjectParametersJsonNoCodeLoc = JsonUtil.getObjectMapper().writeValueAsString(new HubProjectParameters(TEST_PROJECTNAME, null));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHubProjectParametersJsonAllPresent() {
        HubProjectParameters hubProjectParams = JsonUtil.readValue(hubProjectParametersJsonAllPresent, HubProjectParameters.class);

        Assert.assertNotNull(hubProjectParams, "HubProjectParameters deserialized object should not be null");
        Assert.assertTrue(hubProjectParams.getProjectName().isPresent(), "HubProjectParameters projectName should be present");
        Assert.assertTrue(hubProjectParams.getCodeLocation().isPresent(), "HubProjectParameters codeLocations should be present");

        Assert.assertEquals(hubProjectParams.getProjectName().get(), TEST_PROJECTNAME, "HubProjectParameters projectName incorrect");
        Assert.assertEquals(hubProjectParams.getCodeLocation().get(), TEST_CODELOCATION, "HubProjectParameters codeLocation incorrect");
    }

    @Test
    public void testHubProjectParametersJsonNoProject() {
        HubProjectParameters hubProjectParams = JsonUtil.readValue(hubProjectParametersJsonNoProject, HubProjectParameters.class);

        Assert.assertNotNull(hubProjectParams, "HubProjectParameters deserialized object should not be null");
        Assert.assertFalse(hubProjectParams.getProjectName().isPresent(), "HubProjectParameters projectName should not be present");
        Assert.assertTrue(hubProjectParams.getCodeLocation().isPresent(), "HubProjectParameters codeLoation should be present");

        Assert.assertEquals(hubProjectParams.getCodeLocation().get(), TEST_CODELOCATION, "HubProjectParameters codeLocation incorrect");
    }

    @Test
    public void testHubProjectParametersJsonNoCodeLocation() {
        HubProjectParameters hubProjectParams = JsonUtil.readValue(hubProjectParametersJsonNoCodeLoc, HubProjectParameters.class);

        Assert.assertNotNull(hubProjectParams, "HubProjectParameters deserialized object should not be null");
        Assert.assertTrue(hubProjectParams.getProjectName().isPresent(), "HubProjectParameters projectName should be present");
        Assert.assertFalse(hubProjectParams.getCodeLocation().isPresent(), "HubProjectParameters codeLocation should not be present");

        Assert.assertEquals(hubProjectParams.getProjectName().get(), TEST_PROJECTNAME, "HubProjectParameters projectName incorrecrt");
    }

    @Test
    public void testHubProjectParametersEmptyJson() {
        HubProjectParameters hubProjectParams = JsonUtil.readValue(HUB_PROJECT_PARAMETERS_EMPTY_JSON, HubProjectParameters.class);

        Assert.assertNotNull(hubProjectParams, "HubProjectParameters deserialized object should not be null");
        Assert.assertFalse(hubProjectParams.getProjectName().isPresent(), "HubProjectParameters projectName should not be present");
        Assert.assertFalse(hubProjectParams.getCodeLocation().isPresent(), "HubProjectParameters codeLocation should not be present");
    }
}
