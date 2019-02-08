package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BackendServiceInstanceProvisionRequestTest {
    private static final String TEST_SERVICEINSTANCEID = "TestServiceInstanceId";

    private BackendServiceInstanceProvisionRequest testInstance;

    @BeforeClass
    public void setupObject() {
        testInstance = new BackendServiceInstanceProvisionRequest(TEST_SERVICEINSTANCEID);
    }

    @Test(priority = 0)
    public void testInstanceValid() {
        Assert.assertNotNull(testInstance, "BackendServiceInstanceProvisionRequest should not be null");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void getServiceInstanceId() {
        Assert.assertEquals(testInstance.getServiceInstanceId(), TEST_SERVICEINSTANCEID, "BackendServiceInstanceProvisionRequest serviceInstanceId incorrect");
    }
}
