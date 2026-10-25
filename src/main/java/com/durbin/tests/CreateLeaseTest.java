package com.durbin.tests;

import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.durbin.automation.api.TestDataHolder;
import com.durbin.automation.base.BaseTest;
import com.durbin.automation.pages.CreateLeasePage;
import com.durbin.automation.pages.LoginPage;

public class CreateLeaseTest extends BaseTest {

    // Returns the value, or null if the key is missing or empty
    private static String opt(JsonNode node, String key) {
        JsonNode v = node.get(key);
        if (v == null || v.isNull() || v.asText().trim().isEmpty()) {
            return null;
        }
        return v.asText().trim();
    }

    @Test
    public void testCreateLease() {
        step("-------Starting Lease Creation Test----------");

        JsonNode root = TestDataHolder.getData();
        if (root == null) {
            throw new RuntimeException("API test data is required. No fallback allowed.");
        }
        step("Using API-driven test data");

        // Login data
        if (!root.has("login")) {
            throw new RuntimeException("login data is required in API payload");
        }
        JsonNode login = root.get("login");
        String user = login.get("username").asText();
        String pass = login.get("password").asText();

        // Lease data
        if (!root.has("lease")) {
            throw new RuntimeException("lease data is required in API payload");
        }
        JsonNode lease = root.get("lease");
        for (String key : new String[] { "customerName", "customerPoNo", "salesPerson", "leaseType" }) {
            if (!lease.has(key) || lease.get(key).asText().trim().isEmpty()) {
                throw new RuntimeException(key + " is required in the lease payload");
            }
        }
        String customerName = lease.get("customerName").asText().trim();
        String customerPoNo = lease.get("customerPoNo").asText().trim();
        String salesPerson = lease.get("salesPerson").asText().trim();
        String leaseTypeInput = lease.get("leaseType").asText().trim();

        String leaseType;
        if (leaseTypeInput.equalsIgnoreCase("Dry")) {
            leaseType = "Dry";
        } else if (leaseTypeInput.equalsIgnoreCase("Wet")) {
            leaseType = "Wet";
        } else {
            throw new RuntimeException("leaseType must be 'Dry' or 'Wet' but was '" + leaseTypeInput + "'");
        }

        // Machine data
        if (!root.has("machine")) {
            throw new RuntimeException("machine data is required in API payload");
        }
        JsonNode machine = root.get("machine");
        String assetNo = opt(machine, "assetNo");
        if (assetNo == null) {
            throw new RuntimeException("assetNo is required in the machine payload");
        }
        String operatorNo = opt(machine, "operatorNo");
        String operatorName = opt(machine, "operatorName");
        if (operatorName != null && operatorNo == null) {
            throw new RuntimeException("operatorNo is required when operatorName is given");
        }

        LoginPage loginPage = new LoginPage(driver);
        CreateLeasePage leasePage = new CreateLeasePage(driver);

        step("Login------------------------");
        loginPage.login(user, pass);
        step("----------Login Completed----------");

        leasePage.openLeasesMenu();
        leasePage.openLeaseSubMenu();
        leasePage.clickCreateButton();
        step("Opened Create Lease page----->>>>>>");

        leasePage.enterCustomerPoNo(customerPoNo);
        leasePage.selectSalesPerson(salesPerson);
        leasePage.selectLeaseType(leaseType);
        leasePage.selectCustomerName(customerName);
        step("Lease basic details filled----->>>>>>");

        // Machine section
        leasePage.clickAddMachine();
        leasePage.selectAsset(assetNo);

        if (operatorNo != null) {
            leasePage.enterOperatorNo(operatorNo);
            if (operatorName != null) {
                leasePage.selectOperatorName(operatorName);
            }
        }

        // Rental Start and Rental End: added in the next step

        String location = opt(machine, "location");
        if (location != null) {
            leasePage.selectMachineLocation(location);
        }
        String monthlyRent = opt(machine, "monthlyRent");
        if (monthlyRent != null) {
            leasePage.enterMonthlyRent(monthlyRent);
        }
        String shiftsPerDay = opt(machine, "shiftsPerDay");
        if (shiftsPerDay != null) {
            leasePage.enterShiftsPerDay(shiftsPerDay);
        }
        String nominalHrs = opt(machine, "nominalHrsPerShift");
        if (nominalHrs != null) {
            leasePage.enterNominalHrs(nominalHrs);
        }
        String workingDays = opt(machine, "workingDaysPerMonth");
        if (workingDays != null) {
            leasePage.enterWorkingDays(workingDays);
        }
        String contractValue = opt(machine, "contractValue");
        if (contractValue != null) {
            leasePage.enterContractValue(contractValue);
        }
        step("Machine details filled----->>>>>>");
    }
}