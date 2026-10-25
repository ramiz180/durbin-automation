package com.durbin.tests;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.durbin.automation.api.TestDataHolder;
import com.durbin.automation.base.BaseTest;
import com.durbin.automation.pages.LoginPage;
import com.durbin.automation.pages.MaintenancePage;

public class CreateMaintenanceTest extends BaseTest {

    // Same format as the operator test, for example 14-10-2026. STRICT rejects dates like 31-02-2026.
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
            throw new RuntimeException(key + " is required in the maintenance payload");
        }
        return v;
    }

    private static LocalDate parseDate(String value, String field) {
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (Exception e) {
            throw new RuntimeException("Invalid " + field + ": '" + value + "'. Expected dd-MM-yyyy, e.g. 14-10-2026");
        }
    }

    @Test
    public void testCreateMaintenance() {
        step("-------Starting Maintenance Creation Test----------");

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

        // Maintenance data
        if (!root.has("maintenance")) {
            throw new RuntimeException("maintenance data is required in API payload");
        }
        JsonNode m = root.get("maintenance");

        String assetNo = req(m, "assetNo");
        String title = req(m, "breakdownTitle");
        if (title.length() > 35) {
            throw new RuntimeException("breakdownTitle can have at most 35 characters but has " + title.length());
        }
        String serviceCategory = req(m, "serviceCategory");
        String breakdownType = req(m, "breakdownType");

        String description = opt(m, "breakdownDescription");
        if (description != null && description.length() > 250) {
            throw new RuntimeException("breakdownDescription can have at most 250 characters but has " + description.length());
        }

        // Breakdown date is optional. If missing, today is used. The portal does not allow future dates.
        String dateText = opt(m, "breakdownDate");
        LocalDate breakdownDate = dateText == null ? LocalDate.now() : parseDate(dateText, "breakdownDate");
        if (breakdownDate.isAfter(LocalDate.now())) {
            throw new RuntimeException("breakdownDate cannot be in the future (the portal allows today or earlier)");
        }

        LoginPage loginPage = new LoginPage(driver);
        MaintenancePage maintenancePage = new MaintenancePage(driver);

        step("Login------------------------");
        loginPage.login(user, pass);
        step("----------Login Completed----------");

        maintenancePage.openMaintenanceMenu();
        maintenancePage.openRentalSubMenu();
        maintenancePage.clickCreateButton();
        step("Opened Register Complaint form----->>>>>>");

        maintenancePage.selectAsset(assetNo);
        maintenancePage.enterBreakdownTitle(title);
        maintenancePage.setBreakdownDate(breakdownDate.toString()); // yyyy-MM-dd
        maintenancePage.selectServiceCategory(serviceCategory);
        maintenancePage.selectBreakdownType(breakdownType);
        if (description != null) {
            maintenancePage.enterDescription(description);
        }
        step("Complaint details filled----->>>>>>");

        maintenancePage.clickSubmit();
        maintenancePage.waitForFormToClose();
        step("Maintenance complaint submitted----->>>>>>");
    }
}