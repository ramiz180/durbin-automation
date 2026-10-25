package com.durbin.tests;

import com.durbin.automation.api.TestDataHolder;
import com.durbin.automation.base.BaseTest;
import com.durbin.automation.pages.LoginPage;

import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

public class ERPLoginTest extends BaseTest {

    @Test
    public void testLogin() throws InterruptedException {
        step("Starting Login-Only Test");

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

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(user, pass);
        Thread.sleep(5000);

        if (!driver.getCurrentUrl().contains("dashboard")) {
            throw new RuntimeException("Login failed - not redirected after login");
        }

        step("Login test completed successfully.");
    }
}
