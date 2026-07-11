package com.durbin.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.durbin.automation.base.BasePage;

public class LeasePage extends BasePage {

    public LeasePage(WebDriver driver) {
        super(driver);
    }

    // lease details

    @FindBy(xpath = "//span[.='Leases & Logistics']`")
    private WebElement leaseAndLogistics;

    // leases

    @FindBy(xpath = "//a[@href='/awp/leases']")
    private WebElement leases;

    // button

    @FindBy(xpath = "//button[normalize-space()='Create']")
    private WebElement createLeaseButton;

    public void goToLeasePage() {
        clickWhenVisible(leaseAndLogistics);
        clickWhenClickable(leases);
    }

    public void clickCreateLeaseButton() {
        clickWhenClickable(createLeaseButton);
    }

}
