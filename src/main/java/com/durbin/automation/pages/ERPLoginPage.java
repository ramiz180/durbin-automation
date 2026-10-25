package com.durbin.automation.pages;

import com.durbin.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
    }
}