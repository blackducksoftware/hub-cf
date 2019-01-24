package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BackendBindingProvisionRequestTest {
    private static final String TEST_BINDINGID = "TestBindingId";

    private static final String TEST_RESOURCEID = "TestResourceId";

    private static final String TEST_APPID = "TestAppId";

    private static final String TEST_ROUTE = "TestRoute";

    private static final BindResource TEST_BINDRESOURCE = new BindResource(TEST_APPID, TEST_ROUTE);

    private static final String TEST_PROJECTNAME = "TestProject";

    private static final String TEST_CODELOCATION = "TestCodeLocation";

    private static final HubProjectParameters TEST_HUBPROJECTPARAMS = new HubProjectParameters(TEST_PROJECTNAME, TEST_CODELOCATION);

    private BackendBindingProvisionRequest testInstance;

    @BeforeClass
    public void setupObject() {
        testInstance = new BackendBindingProvisionRequest(TEST_BINDINGID, TEST_RESOURCEID, TEST_BINDRESOURCE, TEST_HUBPROJECTPARAMS);
    }

    @Test(priority = 0)
    public void testInstanceValid() {
        Assert.assertNotNull(testInstance, "BackendBindingProvisionRequest instance not created properly");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testBindResource() {
        BindResource br = testInstance.getBindResource();
        Assert.assertNotNull(br, "BackendBindingProvisionRequest bindResource should not be null");

        Assert.assertEquals(br.getAppGuid().orElse(null), TEST_APPID, "BackendBindingProvisionRequest bindResource appGuid incorrect");
        Assert.assertEquals(br.getRoute().orElse(null), TEST_ROUTE, "BackendBindingProvisionRequest bindResource route incorrect");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testBindingId() {
        Assert.assertEquals(testInstance.getBindingId(), TEST_BINDINGID, "BackendBindingProvisionRequest bindingId incorrect");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void testHubProjectParams() {
        HubProjectParameters hpp = testInstance.getHubProjectParams();
        Assert.assertNotNull(hpp, "BackendBindingProvisionRequest hubProjectParams should not be null");

        Assert.assertEquals(hpp.getProjectName().orElse(null), TEST_PROJECTNAME, "BackendBindingProvisionRequest hubProjectParams projectName incorrect");
        Assert.assertEquals(hpp.getCodeLocation().orElse(null), TEST_CODELOCATION, "BackendBindingProvisionRequest hubProjectParams codeLocation incorrect");
    }

    @Test(dependsOnMethods = { "testInstanceValid" })
    public void getResourceId() {
        Assert.assertEquals(testInstance.getResourceId(), TEST_RESOURCEID, "BackendBindingProvisionRequest resourceId incorrecrt");
    }
}
