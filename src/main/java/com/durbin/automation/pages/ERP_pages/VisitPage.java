package com.durbin.automation.pages.ERP_pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.durbin.automation.base.BasePage;

public class VisitPage extends BasePage {

    public VisitPage(WebDriver driver) {
        super(driver);
    }

    // ===================== Navigation =====================
    private final By visitsTab = By.xpath("(//*[self::a or self::button][normalize-space()='Visits'])[1]");
    private final By newVisitButton = By.xpath("//a[@href='/visits/create']");

    // ===================== Create Visit Form =====================
    private final By formTitle = By.xpath("//*[@data-slot='card-title'][normalize-space()='Create Visit']");
    private final By planningSwitch = By.xpath("//button[@role='switch']");

    private final By dateRangeTrigger = By.xpath(
            "//label[contains(normalize-space(),'Date Range')]/following::button[@data-slot='popover-trigger'][1]");

    // The Interaction Type input has no placeholder once a value (Physical) is
    // selected, so find it by its label
    private final By interactionTypeInput = By.xpath(
            "//label[contains(normalize-space(),'Interaction Type')]/following::input[@role='combobox'][1]");
    private final By customerInput = By.xpath("//input[@placeholder='Select customer']");
    private final By visitedWithInput = By.xpath("//input[@placeholder='Select employees']");

    // TODO: needs the real HTML of the Address section after a customer is picked
    private final By firstAddressOption = By.xpath(
            "//label[contains(normalize-space(),'Address')]/following::*[@role='radio' or @role='checkbox' or @type='radio'][1]");

    private final By createVisitButton = By.cssSelector("form#visit-basic-form button[type='submit']");

    // ===================== Navigation =====================

    public void goToVisitsPage() {
        click(visitsTab, "Visits Tab");
    }

    public void clickNewVisitButton() {
        click(newVisitButton, "New Visit Button");
    }

    public boolean isCreateVisitFormDisplayed() {
        return isElementDisplayed(driver.findElement(formTitle));
    }

    // ===================== Form =====================

    public void togglePlanningVisit() {
        click(planningSwitch, "Are you planning a visit? switch");
    }

    // TODO: needs the calendar HTML to confirm these day locators
    public void selectDateRange(String fromDay, String toDay) {
        click(dateRangeTrigger, "Date Range");
        click(dayButton(fromDay), "From day " + fromDay);
        click(dayButton(toDay), "To day " + toDay);
        // close the calendar popover so it cannot cover the next field
        driver.switchTo().activeElement().sendKeys(org.openqa.selenium.Keys.ESCAPE);
        step("Date range selected: " + fromDay + " to " + toDay);
    }

    private static By dayButton(String day) {
        return By.xpath(
                "(//*[@role='dialog']//td[not(@data-outside='true')]//button[normalize-space()='" + day + "'])[1]");
    }

    public void selectInteractionType(String type) {
        typeAndSelectFirstOption(interactionTypeInput, type, "Interaction Type");
    }

    public void selectCustomer(String customer) {
        typeAndSelectFirstOption(customerInput, customer, "Customer");
    }

    public void selectFirstAddress() {
        click(firstAddressOption, "First Address");
    }

    public void selectVisitedWith(String employee) {
        typeAndSelectFirstOption(visitedWithInput, employee, "Visited With");
    }

    public void clickCreateVisit() {
        click(createVisitButton, "Create Visit Button");
    }
}