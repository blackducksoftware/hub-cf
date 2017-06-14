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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.exception.BlackDuckServiceBrokerException;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.json.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author jfisher
 *
 */
public class BindingProvisionRequestTest {
    private static String TEST_SERVICEID = "serviceId";

    private static String TEST_PLANID = "planId";

    private static String TEST_APPGUID = "appGuid";

    private static String TEST_ROUTE = "route";

    private static String TEST_PROJECTNAME = "projectName";

    private static String TEST_CODELOCATION = "codeLocation";

    private static Optional<BindResource> TEST_BINDRESOURCE = Optional.of(new BindResource(Optional.of(TEST_APPGUID), Optional.of(TEST_ROUTE)));

    private static Optional<HubProjectParameters> TEST_HUBPROJECTPARAMS = Optional
            .of(new HubProjectParameters(Optional.of(TEST_PROJECTNAME), Optional.of(TEST_CODELOCATION)));

    private String bindingProvisionRequestJsonAllPresent;

    private String bindingProvisionRequestJsonNoServiceId;

    private String bindingProvisionRequestJsonNoPlanId;

    private String bindingProvisionRequestJsonNoBindResource;

    private String bindingProvisionRequestJsonNoHubProjectParameters;

    @BeforeClass
    public void setupData() {
        String bindResourceJson;
        String hubProjectParamsJson;
        try {
            bindingProvisionRequestJsonAllPresent = JsonUtil.getObjectMapper()
                    .writeValueAsString(new BindingProvisionRequest(TEST_SERVICEID, TEST_PLANID, TEST_BINDRESOURCE, TEST_HUBPROJECTPARAMS));
            bindingProvisionRequestJsonNoBindResource = JsonUtil.getObjectMapper()
                    .writeValueAsString(new BindingProvisionRequest(TEST_SERVICEID, TEST_PLANID, null, TEST_HUBPROJECTPARAMS));
            bindingProvisionRequestJsonNoHubProjectParameters = JsonUtil.getObjectMapper()
                    .writeValueAsString(new BindingProvisionRequest(TEST_SERVICEID, TEST_PLANID, TEST_BINDRESOURCE, null));
            bindResourceJson = JsonUtil.getObjectMapper().writeValueAsString(TEST_BINDRESOURCE.get());
            hubProjectParamsJson = JsonUtil.getObjectMapper().writeValueAsString(TEST_HUBPROJECTPARAMS.get());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{\"plan_id\": \"").append(TEST_PLANID).append("\", \"bind_resource\": ").append(bindResourceJson).append(", \"parameters\": ")
                .append(hubProjectParamsJson).append("}");
        bindingProvisionRequestJsonNoServiceId = sb.toString();

        sb = new StringBuilder();
        sb.append("{\"service_id\": \"").append(TEST_SERVICEID).append("\", \"bind_resource\": ").append(bindResourceJson).append(", \"parameters\": ")
                .append(hubProjectParamsJson).append("}");
        bindingProvisionRequestJsonNoPlanId = sb.toString();
    }

    @DataProvider(name = "testBindingProvisionRequestInvalidJson")
    public Object[][] createBindingProvisionRequestInvalidJson() {
        return new Object[][] {
                { bindingProvisionRequestJsonNoServiceId },
                { bindingProvisionRequestJsonNoPlanId },
        };
    }

    @Test
    public void testBindingProvisionRequestJsonAllPresent() {
        BindingProvisionRequest bpr = JsonUtil.readValue(bindingProvisionRequestJsonAllPresent, BindingProvisionRequest.class);

        Assert.assertNotNull(bpr, "BindingProvisionRequest deserialized object should not be null");
        Assert.assertEquals(bpr.getServiceId(), TEST_SERVICEID, "BindingProvisionRequest serviceId incorrect");
        Assert.assertEquals(bpr.getPlanId(), TEST_PLANID, "BindingProvisionRequest planId incorrect");
        Assert.assertTrue(bpr.getBindResource().isPresent(), "BindingProvisionRequest bindResource should be present");
        Assert.assertTrue(bpr.getHubProjectParams().isPresent(), "BindingProvisionRequest hubProjectParams should be present");
    }

    @Test
    public void testBindingProvisionRequestJsonNoBindResource() {
        BindingProvisionRequest bpr = JsonUtil.readValue(bindingProvisionRequestJsonNoBindResource, BindingProvisionRequest.class);

        Assert.assertNotNull(bpr, "BindingProvisionRequest deserialized object should not be null");
        Assert.assertEquals(bpr.getServiceId(), TEST_SERVICEID, "BindingProvisionRequest serviceId incorrect");
        Assert.assertEquals(bpr.getPlanId(), TEST_PLANID, "BindingProvisionRequest planId incorrect");
        Assert.assertFalse(bpr.getBindResource().isPresent(), "BindingProvisionRequest bindResource should not be present");
        Assert.assertTrue(bpr.getHubProjectParams().isPresent(), "BindingProvisionRequest hubProjectParams should be present");
    }

    @Test
    public void testBindingProvisionRequestJsonNoHubProjectParameters() {
        BindingProvisionRequest bpr = JsonUtil.readValue(bindingProvisionRequestJsonNoHubProjectParameters, BindingProvisionRequest.class);

        Assert.assertNotNull(bpr, "BindingProvisionRequest deserialized object should not be null");
        Assert.assertEquals(bpr.getServiceId(), TEST_SERVICEID, "BindingProvisionRequest serviceId incorrect");
        Assert.assertEquals(bpr.getPlanId(), TEST_PLANID, "BindingProvisionRequest planId incorrect");
        Assert.assertTrue(bpr.getBindResource().isPresent(), "BindingProvisionRequest bindResource should be present");
        Assert.assertFalse(bpr.getHubProjectParams().isPresent(), "BindingProvisionRequest hubProjectParams should not be present");
    }

    @Test(dataProvider = "testBindingProvisionRequestInvalidJson", expectedExceptions = { BlackDuckServiceBrokerException.class })
    public void testBindingProvisionRequestInvalidJson(String invalidJson) {
        BindingProvisionRequest bpr = JsonUtil.readValue(invalidJson, BindingProvisionRequest.class);

        Assert.assertNull(bpr, "Should have thrown Exception during construction");
    }
}
