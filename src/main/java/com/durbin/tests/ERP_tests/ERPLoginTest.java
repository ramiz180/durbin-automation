package com.durbin.tests.ERP_tests;

import com.durbin.automation.api.TestDataHolder;
import com.durbin.automation.base.BaseTest;
import com.durbin.automation.pages.ERP_pages.ERPLoginPage;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;

public class ERPLoginTest extends BaseTest {

    @Test
    public void testLogin() {
        step("Starting ERP Login Test");

        JsonNode root = TestDataHolder.getData();
        if (root == null) {
            throw new RuntimeException("API test data is required. No fallback allowed.");
        }
        step("Using API-driven test data");

        if (!root.has("login")) {
            throw new RuntimeException("login data is required in API payload");
        }
        JsonNode login = root.get("login");
        String user = login.get("username").asText();
        String pass = login.get("password").asText();

        step("Logging in as: " + user);
        ERPLoginPage loginPage = new ERPLoginPage(driver);
        loginPage.login(user, pass);

        step("ERP login test completed successfully.");
    }
}