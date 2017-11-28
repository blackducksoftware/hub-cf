package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api;

import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.blackducksoftware.integration.phonehome.enums.PhoneHomeSource;
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName;

public class PhoneHomeParametersTest {

    @DataProvider(name = "testInvalidSourcePhoneHomeParameters")
    public Object[][] createInvalidSourcePhoneHomeParameters() {
        return new Object[][] {
                new Object[] { "", "OSCF_SCANNER" },
                new Object[] { null, "OSCF_SCANNER" },
                new Object[] { "NOT_GOOD", "OSCF_SCANNER" },
        };
    }

    @DataProvider(name = "testInvalidVendorPhoneHomeParameters")
    public Object[][] createInvalidVendorPhoneHomeParameters() {
        return new Object[][] {
                new Object[] { "ALLIANCES", "" },
                new Object[] { "ALLIANCES", null },
                new Object[] { "ALLIANCES", "NOT_GOOD" },
        };
    }

    @DataProvider(name = "testInvalidSourceVendorPhoneHomeParameters")
    public Object[][] createInvalidSourceVendorPhoneHomeParameters() {
        return new Object[][] {
                new Object[] { "", "" },
                new Object[] { null, null },
                new Object[] { "NOT_GOOD", "NOT_GOOD" },
        };
    }

    @DataProvider(name = "testValidPhoneHomeParameters")
    public Object[][] createValidPhoneHomeParameters() {
        return new Object[][] {
                new Object[] { "ALLIANCES", "OSCF_SCANNER" },
                new Object[] { "alliances", "oscf_scanner" },
        };
    }

    @Test(dataProvider = "testInvalidSourcePhoneHomeParameters")
    public void testInvalidSourcePhoneHomeParemeters(String source, String vendor) {
        PhoneHomeParameters vut = new PhoneHomeParameters(source, vendor);

        Assert.assertNotNull(vut, "Valid PhoneHomeParameters should have been instantiated");
        Assert.assertEquals(vut.getSource(), Optional.empty(), "Invalid PhoneHomeParameters Source should result in empty");
        Assert.assertTrue(vut.getVendor().isPresent(), "PhoneHomeParameters Vendor should have a value");
        Assert.assertEquals(vut.getVendor().get(), ThirdPartyName.OSCF_SCANNER, "PhoneHomeParameters Vendor incorrect value");
    }

    @Test(dataProvider = "testInvalidVendorPhoneHomeParameters")
    public void testInvalidVendorPhoneHomeParameters(String source, String vendor) {
        PhoneHomeParameters vut = new PhoneHomeParameters(source, vendor);

        Assert.assertNotNull(vut, "Valid PhoneHomeParameters should have been instantiated");
        Assert.assertEquals(vut.getVendor(), Optional.empty(), "Invalid PhoneHomeParameters Vendor should result in empty");
        Assert.assertTrue(vut.getSource().isPresent(), "PhoneHomeParameters Source should have a value");
        Assert.assertEquals(vut.getSource().get(), PhoneHomeSource.ALLIANCES, "PhoneHomeParameters Source incorrect value");
    }

    @Test(dataProvider = "testInvalidSourceVendorPhoneHomeParameters")
    public void testInvalidSourceVendorPhoneHomeParameters(String source, String vendor) {
        PhoneHomeParameters vut = new PhoneHomeParameters(source, vendor);

        Assert.assertNotNull(vut, "Valid PhoneHomeParameters should have been instantiated");
        Assert.assertEquals(vut.getSource(), Optional.empty(), "Invalid PhoneHomeParameters Source should result in empty");
        Assert.assertEquals(vut.getVendor(), Optional.empty(), "Invalid PhoneHomeParameters Vendor should result in empty");
    }

    @Test(dataProvider = "testValidPhoneHomeParameters")
    public void testPhoneHomeParameters(String source, String vendor) {
        PhoneHomeParameters vut = new PhoneHomeParameters(source, vendor);

        Assert.assertNotNull(vut, "Valid PhoneHomeParameters should have been instantiated");
        Assert.assertTrue(vut.getSource().isPresent(), "PhoneHomeParaemters Source should have a value");
        Assert.assertEquals(vut.getSource().get(), PhoneHomeSource.ALLIANCES, "PhoneHomeParameters Source incorrect value");
        Assert.assertTrue(vut.getVendor().isPresent(), "PhoneHomeParameters Vendor should have a value");
        Assert.assertEquals(vut.getVendor().get(), ThirdPartyName.OSCF_SCANNER, "PhoneHomeParameters Vendor incorrect value");
    }
}
