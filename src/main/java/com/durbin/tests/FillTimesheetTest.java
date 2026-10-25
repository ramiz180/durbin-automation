package com.durbin.tests;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.durbin.automation.api.TestDataHolder;
import com.durbin.automation.base.BaseTest;
import com.durbin.automation.pages.LoginPage;
import com.durbin.automation.pages.TimesheetPage;

public class FillTimesheetTest extends BaseTest {

    // Month in the payload is written like 09-2026
    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

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
            throw new RuntimeException(key + " is required in the timesheet payload");
        }
        return v;
    }

    // The portal field is hh.mm, for example 09.00 or 19.30
    private static void checkHours(String value, String field) {
        if (!value.matches("\\d{1,2}(\\.\\d{2})?")) {
            throw new RuntimeException(field + " must look like hh.mm, for example 09.00, but was '" + value + "'");
        }
        if (value.contains(".") && Integer.parseInt(value.substring(value.indexOf('.') + 1)) > 59) {
            throw new RuntimeException(field + " has minutes above 59: '" + value + "'");
        }
    }

    @Test
    public void testFillTimesheet() {
        step("-------Starting Timesheet Test----------");

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

        // Timesheet data
        if (!root.has("timesheet")) {
            throw new RuntimeException("timesheet data is required in API payload");
        }
        JsonNode ts = root.get("timesheet");

        String assetNo = req(ts, "assetNo");
        if (!assetNo.matches("[A-Za-z0-9._-]+")) {
            throw new RuntimeException("assetNo can only have letters, numbers, dot, dash and underscore: '" + assetNo + "'");
        }

        String monthText = req(ts, "month");
        YearMonth month;
        try {
            month = YearMonth.parse(monthText, MONTH_FORMAT);
        } catch (Exception e) {
            throw new RuntimeException("Invalid month: '" + monthText + "'. Expected MM-yyyy, e.g. 09-2026");
        }

        String timeIn = req(ts, "timeIn");
        String timeOut = req(ts, "timeOut");
        checkHours(timeIn, "timeIn");
        checkHours(timeOut, "timeOut");
        String breakdown = opt(ts, "breakdown");
        if (breakdown != null) {
            checkHours(breakdown, "breakdown");
        }
        String overtime = opt(ts, "overtime");
        if (overtime != null) {
            checkHours(overtime, "overtime");
        }
        boolean submit = ts.path("submit").asBoolean(false);

        LoginPage loginPage = new LoginPage(driver);
        TimesheetPage timesheetPage = new TimesheetPage(driver);

        step("Login------------------------");
        loginPage.login(user, pass);
        step("----------Login Completed----------");

        timesheetPage.openLeasesMenu();
        timesheetPage.openLeaseSubMenu();
        step("Opened Lease list----->>>>>>");

        timesheetPage.searchLease(assetNo);
        timesheetPage.openFirstLease();
        step("Opened lease details----->>>>>>");

        timesheetPage.clickAddManually();
        timesheetPage.selectMonth(month);

        int filled = timesheetPage.fillActiveRows(timeIn, timeOut, breakdown, overtime);
        step("Timesheet hours filled for " + filled + " active days----->>>>>>");

        if (submit) {
            timesheetPage.clickSubmit();
            timesheetPage.waitForFormToClose();
            step("Timesheet submitted----->>>>>>");
        } else {
            step("Submit was not clicked (submit is false in the payload)");
        }
    }
}