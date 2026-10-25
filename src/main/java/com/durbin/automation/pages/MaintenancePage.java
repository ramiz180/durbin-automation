package com.durbin.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.durbin.automation.base.BasePage;

public class MaintenancePage extends BasePage {

    public MaintenancePage(WebDriver driver) {
        super(driver);
    }

    // ===================== Menu and list page =====================
    // "Maintenance" appears twice in the sidebar (AWP menu and MAT list), so the AWP one is picked by its accordion button
    private final By maintenanceMenu = By.xpath(
            "//button[@data-slot='accordion-trigger'][.//span[normalize-space()='Maintenance']]");
    // Exact text "Rental", so "Non-Rental" is not matched
    private final By rentalSubMenu = By.xpath(
            "//div[@data-slot='sidebar-content']//span[normalize-space()='Rental']");
    private final By createButton = By.xpath("//button[normalize-space()='Create']");

    // ===================== Register Complaint form =====================
    private final By assetField = By.xpath("//input[@placeholder='Search asset number...']");
    private final By titleInput = By.xpath("//input[@name='complaint']");
    private final By breakdownDateInput = By.xpath("//input[@name='issue_date']");
    private final By serviceCategoryTrigger = By.xpath(
            "//label[normalize-space()='Service Category']/following::button[@data-slot='select-trigger'][1]");
    private final By breakdownTypeTrigger = By.xpath(
            "//label[normalize-space()='Breakdown Type']/following::button[@data-slot='select-trigger'][1]");
    private final By descriptionInput = By.xpath("//textarea[@name='description']");
    private final By submitButton = By.xpath("//button[@type='submit'][normalize-space()='Submit']");

    // ===================== Navigation =====================

    public void openMaintenanceMenu() {
        click(maintenanceMenu, "Maintenance Menu");
    }

    public void openRentalSubMenu() {
        click(rentalSubMenu, "Rental Sub Menu");
    }

    public void clickCreateButton() {
        click(createButton, "Create Button");
    }

    // ===================== Form =====================

    public void selectAsset(String v) { typeAndSelectFirstOption(assetField, v, "Select Asset Field"); }
    public void enterBreakdownTitle(String v) { sendKeys(titleInput, v, "Breakdown Title Field"); }

    // isoDate is yyyy-MM-dd
    public void setBreakdownDate(String isoDate) { setNativeDate(breakdownDateInput, isoDate, "Breakdown Date"); }

    public void selectServiceCategory(String v) { selectRadixOption(serviceCategoryTrigger, v, "Service Category"); }
    public void selectBreakdownType(String v) { selectRadixOption(breakdownTypeTrigger, v, "Breakdown Type"); }
    public void enterDescription(String v) { sendKeys(descriptionInput, v, "Breakdown Description Field"); }

    public void clickSubmit() {
        click(submitButton, "Submit Button");
    }

    // If the Submit button is still on the page after 15 seconds, the portal did not accept the form
    public void waitForFormToClose() {
        waitUntilGone(submitButton, 15,
                "the Register Complaint form to close after Submit (check required fields or validation messages)");
    }
}