package com.durbin.automation.pages.ERP_pages;

import com.durbin.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ERPLoginPage extends BasePage {

    private static final By EMAIL_INPUT = By.name("email");
    private static final By PASSWORD_INPUT = By.name("password");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit'][form='login-form']");

    public ERPLoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String user, String pass) {
        sendKeys(EMAIL_INPUT, user, "Email");
        sendKeys(PASSWORD_INPUT, pass, "Password");
        click(LOGIN_BUTTON, "Login button");
        waitForLoginToFinish();
    }

    // Login worked when the browser has left the /auth/login page
    private void waitForLoginToFinish() {
        try {
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/auth/login")));
        } catch (TimeoutException e) {
            throw new RuntimeException("Login failed - still on the login page after 20 seconds");
        }
        step("Login completed, now on: " + driver.getCurrentUrl());
    }
}