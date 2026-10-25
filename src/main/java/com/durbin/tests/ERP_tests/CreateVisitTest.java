package com.durbin.tests.ERP_tests;

import java.time.LocalDate;

import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.durbin.automation.api.TestDataHolder;
import com.durbin.automation.base.BaseTest;
import com.durbin.automation.pages.ERP_pages.ERPLoginPage;
import com.durbin.automation.pages.ERP_pages.VisitPage;

public class CreateVisitTest extends BaseTest {

    @Test
    public void testCreateVisit() throws InterruptedException {
        step("-------Starting Visit Creation Test----------");

        JsonNode root = TestDataHolder.getData();
        if (root == null) {
            throw new RuntimeException("API test data is required. No fallback allowed.");
        }
        step("Using API-driven test data");

        // Extract Login Data
        if (!root.has("login")) {
            throw new RuntimeException("login data is required in API payload");
        }
        JsonNode login = root.get("login");
        String user = login.get("username").asText();
        String pass = login.get("password").asText();

        // Extract Visit Data
        if (!root.has("visit")) {
            throw new RuntimeException("visit data is required in API payload");
        }
        JsonNode visit = root.get("visit");
        if (!visit.has("customer")) {
            throw new RuntimeException("customer is required in the visit payload");
        }
        String customer = visit.get("customer").asText();

        // Optional/Fallback Visit Fields
        LocalDate today = LocalDate.now();
        String dateFromDay = visit.path("dateFromDay").asText(String.valueOf(today.getDayOfMonth()));
        String dateToDay = visit.path("dateToDay").asText(String.valueOf(today.plusDays(1).getDayOfMonth()));
        String interactionType = visit.path("interactionType").asText(""); // Physical is already selected by default
        String visitedWith = visit.path("visitedWith").asText("");
        boolean planningVisit = visit.path("planningVisit").asBoolean(false);

        ERPLoginPage loginPage = new ERPLoginPage(driver);
        VisitPage visitPage = new VisitPage(driver);

        step("Login------------------------");
        loginPage.login(user, pass);
        step("----------Login Completed----------");

        // Wait for dashboard transition
        Thread.sleep(5000);

        visitPage.goToVisitsPage();
        visitPage.clickNewVisitButton();
        step("Create Visit form opened----->>>>>>");

        if (planningVisit) {
            visitPage.togglePlanningVisit();
        }
        visitPage.selectDateRange(dateFromDay, dateToDay);
        if (!interactionType.isEmpty()) {
            visitPage.selectInteractionType(interactionType);
        }
        visitPage.selectCustomer(customer);
        Thread.sleep(2000); // address list loads after the customer is picked
        visitPage.selectFirstAddress();
        if (!visitedWith.isEmpty()) {
            visitPage.selectVisitedWith(visitedWith);
        }
        step("Visit details filled----->>>>>>");

        visitPage.clickCreateVisit();
        step("Clicked Create Visit----->>>>>>");
        Thread.sleep(5000);
    }
}