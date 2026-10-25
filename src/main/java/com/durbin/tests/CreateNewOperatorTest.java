package com.durbin.tests;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.durbin.automation.api.TestDataHolder;
import com.durbin.automation.base.BaseTest;
import com.durbin.automation.pages.LoginPage;
import com.durbin.automation.pages.OperatorPage;

public class CreateNewOperatorTest extends BaseTest {

    // Same format the portal shows, for example 16-10-1985. STRICT rejects dates like 31-02-1985.
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    // Returns the value, or null if the key is missing or empty
    private static String opt(JsonNode node, String key) {
        JsonNode v = node.get(key);
        if (v == null || v.isNull() || v.asText().trim().isEmpty()) {
            return null;
        }
        return v.asText().trim();
    }

    private static String req(JsonNode node, String key) {
        String v = opt(node, key);
        if (v == null) {
            throw new RuntimeException(key + " is required in the operator payload");
        }
        return v;
    }

    private static LocalDate parseDate(String value, String field) {
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (Exception e) {
            throw new RuntimeException("Invalid " + field + ": '" + value + "'. Expected dd-MM-yyyy, e.g. 16-10-1985");
        }
    }

    @Test
    public void testCreateNewOperator() {
        step("-------Starting Operator Creation Test----------");

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

        // Operator data
        if (!root.has("operator")) {
            throw new RuntimeException("operator data is required in API payload");
        }
        JsonNode op = root.get("operator");

        String aadhaar = req(op, "aadhaarNumber");
        if (!aadhaar.matches("\\d{12}")) {
            throw new RuntimeException("aadhaarNumber must be exactly 12 digits");
        }

        String dobText = req(op, "dateOfBirth");
        LocalDate dob = parseDate(dobText, "dateOfBirth");
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 18 || age > 80) {
            throw new RuntimeException("Operator age must be between 18 and 80, but dateOfBirth "
                    + dobText + " gives age " + age);
        }

        String name = req(op, "name");
        String operatorRole = req(op, "operatorRole");
        String bankAccount = req(op, "bankAccountNumber");
        String ifsc = req(op, "ifscCode");
        String netSalary = req(op, "netInHandSalary");

        String mobile = opt(op, "mobileNumber");
        if (mobile != null && !mobile.matches("\\d{10}")) {
            throw new RuntimeException("mobileNumber must be exactly 10 digits (without +91)");
        }
        String emergencyMobile = opt(op, "emergencyMobile");
        if (emergencyMobile != null && !emergencyMobile.matches("\\d{10}")) {
            throw new RuntimeException("emergencyMobile must be exactly 10 digits (without +91)");
        }
        String drivingLicense = opt(op, "drivingLicense");
        String salesPerson = opt(op, "salesPerson");
        String employeeNumber = opt(op, "employeeNumber");
        String pfAccount = opt(op, "pfAccountNumber");

        // Joining date is optional. If missing, today is used.
        String joiningText = opt(op, "joiningDate");
        LocalDate joining = joiningText == null ? LocalDate.now() : parseDate(joiningText, "joiningDate");

        LoginPage loginPage = new LoginPage(driver);
        OperatorPage operatorPage = new OperatorPage(driver);

        step("Login------------------------");
        loginPage.login(user, pass);
        step("----------Login Completed----------");

        operatorPage.openOperatorsMenu();
        operatorPage.openOperatorsSubMenu();
        operatorPage.clickCreateButton();
        step("Opened Operator Verification page----->>>>>>");

        operatorPage.enterAadhaar(aadhaar);
        operatorPage.selectDateOfBirth(dob);
        operatorPage.clickCreateNew();
        step("Operator verification done----->>>>>>");

        // Create Operator form
        operatorPage.enterOperatorName(name);
        if (mobile != null) {
            operatorPage.enterMobileNumber(mobile);
        }
        if (emergencyMobile != null) {
            operatorPage.enterEmergencyMobile(emergencyMobile);
        }
        if (drivingLicense != null) {
            operatorPage.enterDrivingLicense(drivingLicense);
        }
        operatorPage.selectOperatorRole(operatorRole);
        if (salesPerson != null) {
            operatorPage.selectSalesPerson(salesPerson);
        }
        if (employeeNumber != null) {
            operatorPage.enterEmployeeNumber(employeeNumber);
        }
        operatorPage.enterBankAccountNumber(bankAccount);
        operatorPage.enterIfscCode(ifsc);
        if (pfAccount != null) {
            operatorPage.enterPfAccountNumber(pfAccount);
        }
        operatorPage.enterNetSalary(netSalary);
        operatorPage.setJoiningDate(joining.toString()); // yyyy-MM-dd
        step("Operator details filled----->>>>>>");

        operatorPage.clickSave();
        operatorPage.waitForFormToClose();
        step("Operator saved----->>>>>>");
    }
}