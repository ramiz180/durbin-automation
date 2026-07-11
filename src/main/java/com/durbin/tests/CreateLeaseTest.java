package com.durbin.tests;

import com.durbin.automation.base.BaseTest;
import com.durbin.automation.pages.CreateLeasePage;
import com.durbin.automation.pages.LeasePage;
import com.durbin.automation.utils.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class CreateLeaseTest extends BaseTest {

    private CreateLeasePage createLeasePage;
    private LeasesPage leasesPage;
    private static final String TEST_DATA_FILE = "src/test/resources/testdata.xlsx";

    @BeforeClass
    public void setUpPages() {
        createLeasePage = new CreateLeasePage(driver);
        leasesPage = new LeasesPage(driver);
    }

    @DataProvider(name = "leaseData")
    public Object[][] getLeaseData() {
        return ExcelUtils.getTestData(TEST_DATA_FILE, "CreateLease");
    }

    @Test(dataProvider = "leaseData")
    public void createLeaseTest(Map<String, String> testData) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        log.info("--- Starting Test: " + methodName + " ---");

        try {
            // Navigate to Leases page
            leasesPage.navigateToLeases();

            // Verify Leases page title
            Assert.assertTrue(driver.getTitle().contains("Leases"), "Leases page should open");
            log.info("Leases page opened successfully");

            // Click "Create New Lease" button
            leasesPage.clickCreateNewLease();

            // Verify Create Lease page is displayed
            Assert.assertTrue(createLeasePage.isCreateLeasePageDisplayed(),
                    "Create Lease page should display");
            log.info("Create Lease page opened successfully");

            // Fill Create Lease form
            log.info("Filling Create Lease form with test data");

            createLeasePage.fillBasicInformation(
                    testData.get("EffectiveDate"),
                    testData.get("RenewalTerm"),
                    testData.get("RentIncreaseDate"),
                    testData.get("RenewalDate"),
                    testData.get("LeaseType"),
                    testData.get("FulfillmentMethod"),
                    testData.get("SpaceDescription"),
                    testData.get("AdditionalRent"),
                    testData.get("RenewalSpaceDescription"),
                    testData.get("LeaseCommencementDate"),
                    testData.get("LeaseExpiryDate"),
                    testData.get("BaseRent"),
                    testData.get("RenewalRent"));

            log.info("Basic information filled");

            createLeasePage.fillDimensions(
                    testData.get("DimensionsWidth"),
                    testData.get("DimensionsDepth"),
                    testData.get("DimensionsTotalArea"),
                    testData.get("RenewalDimensionsWidth"),
                    testData.get("RenewalDimensionsDepth"),
                    testData.get("RenewalDimensionsTotalArea"));

            log.info("Dimensions filled");

            createLeasePage.fillRentalRate(
                    testData.get("RentalRateRate"),
                    testData.get("RenewalRentalRateRate"),
                    testData.get("RentalRateType"),
                    testData.get("RenewalRentalRateType"),
                    testData.get("RentalRatePer"),
                    testData.get("RenewalRentalRatePer"));

            log.info("Rental rate filled");

            // Click Save button
            log.info("Saving the lease");
            createLeasePage.clickSave();

            // Verify lease was created successfully
            boolean leaseCreated = createLeasePage.isLeaseCreated(
                    testData.get("ExpectedLeaseId"),
                    testData.get("ExpectedLeaseName"));

            if (leaseCreated) {
                log.info("SUCCESS: Lease created successfully!");
                log.info("Lease ID: " + testData.get("ExpectedLeaseId"));
                log.info("Lease Name: " + testData.get("ExpectedLeaseName"));
            } else {
                log.error("FAILURE: Lease creation failed");
                Assert.fail("Lease creation failed - Lease ID or Name not found");
            }

        } catch (Exception e) {
            log.error("Exception in createLeaseTest: " + e.getMessage(), e);
            throw e;
        }
    }
}
