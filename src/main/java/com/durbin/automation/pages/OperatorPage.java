package com.durbin.automation.pages;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.TimeoutException;

import com.durbin.automation.base.BasePage;

public class OperatorPage extends BasePage {

    public OperatorPage(WebDriver driver) {
        super(driver);
    }

    // ===================== Menu and list page =====================
    // "Operators" appears twice in the sidebar (menu and submenu), so each one gets its own exact locator
    private final By operatorsMenu = By.xpath(
            "//button[@data-slot='accordion-trigger'][.//span[normalize-space()='Operators']]");
    private final By operatorsSubMenu = By.xpath(
            "//a[@data-slot='sidebar-menu-button'][@href='/awp/operators']");
    private final By createButton = By.xpath("//button[normalize-space()='Create']");

    // ===================== Operator Verification page =====================
    private final By aadhaarInput = By.xpath("//input[@placeholder='Enter Aadhaar Number']");
    private final By createNewButton = By.xpath("//button[normalize-space()='Create New']");

        private final By dobButton = By.xpath(
            "//label[normalize-space()='Date of Birth']/following::button[@data-slot='popover-trigger'][1]");
    private final By calendarPopup = By.xpath("//div[@data-slot='popover-content'][@role='dialog']");
    private final By calendarMonthSelect = By.xpath(
            "//div[@data-slot='popover-content']//select[@aria-label='Choose the Month']");
    private final By calendarYearSelect = By.xpath(
            "//div[@data-slot='popover-content']//select[@aria-label='Choose the Year']");
    private final By calendarGrid = By.xpath("//div[@data-slot='popover-content']//table[@role='grid']");

    // ===================== Create Operator form =====================
    private final By operatorNameInput = By.xpath("//input[@name='name']");
    private final By mobileInput = By.xpath("//input[@name='phone_no']");
    private final By emergencyMobileInput = By.xpath("//input[@name='emergency_mobile']");
    private final By drivingLicenseInput = By.xpath("//input[@name='driving_license']");
    private final By operatorRoleSelect = By.xpath("//select[@name='operator_role']");
    private final By salesPersonSelect = By.xpath("//select[@name='operator_sale_person_id']");
    private final By employeeNumberInput = By.xpath("//input[@name='odoo_employee_no']");
    private final By bankAccountInput = By.xpath("//input[@name='account_no']");
    private final By ifscInput = By.xpath("//input[@name='ifsc_code']");
    private final By pfAccountInput = By.xpath("//input[@name='pf_account_no']");
    private final By netSalaryInput = By.xpath("//input[@name='net_inhand_salary']");
    private final By joiningDateInput = By.xpath("//input[@name='joining_date']");
    private final By saveButton = By.xpath("//button[normalize-space()='Save']");

    // ===================== Navigation =====================

    public void openOperatorsMenu() {
        click(operatorsMenu, "Operators Menu");
    }

    public void openOperatorsSubMenu() {
        click(operatorsSubMenu, "Operators Sub Menu");
    }

    public void clickCreateButton() {
        click(createButton, "Create Button");
    }

    // ===================== Verification =====================

    public void enterAadhaar(String aadhaar) {
        sendKeys(aadhaarInput, aadhaar, "Aadhaar Number Field");
    }

        // Picks the date in the calendar: year, then month, then day. Each step is checked.
    public void selectDateOfBirth(LocalDate dob) {
        String expectedText = dob.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String expectedCaption = dob.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + dob.getYear();

        click(dobButton, "Date of Birth field");
        wait.until(ExpectedConditions.visibilityOfElementLocated(calendarPopup));

        setHiddenSelectValue(calendarYearSelect, String.valueOf(dob.getYear()), "Calendar Year");
        setHiddenSelectValue(calendarMonthSelect, String.valueOf(dob.getMonthValue() - 1), "Calendar Month");

        // Make sure the calendar moved to the right month before clicking a day
        wait.until(ExpectedConditions.attributeToBe(calendarGrid, "aria-label", expectedCaption));

        // dob.toString() is yyyy-MM-dd, the same as the day cell's data-day
        By day = By.xpath("//div[@data-slot='popover-content']//td[@data-day='" + dob + "']//button");
        click(day, "Day " + dob.getDayOfMonth() + " in calendar");

        // The date field must now show the chosen date
        wait.until(ExpectedConditions.textToBe(dobButton, expectedText));

        // The open calendar can cover the Create New button, so make sure it is closed
        try {
            waitUntilGone(calendarPopup, 3, "the calendar to close");
        } catch (TimeoutException e) {
            driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
            waitUntilGone(calendarPopup, 5, "the calendar to close after ESC");
        }
        step("Date of Birth set to " + expectedText);
    }

    public void clickCreateNew() {
        click(createNewButton, "Create New Button");
    }

    // ===================== Form =====================

    public void enterOperatorName(String v) { sendKeys(operatorNameInput, v, "Operator Name Field"); }
    public void enterMobileNumber(String v) { appendKeys(mobileInput, v, "Mobile Number Field"); }
    public void enterEmergencyMobile(String v) { appendKeys(emergencyMobileInput, v, "Emergency Mobile Field"); }
    public void enterDrivingLicense(String v) { sendKeys(drivingLicenseInput, v, "Driving License Field"); }
    public void selectOperatorRole(String v) { selectByTextIgnoreCase(operatorRoleSelect, v, "Operator Role"); }
    public void selectSalesPerson(String v) { selectByTextIgnoreCase(salesPersonSelect, v, "Operator Sales Person"); }
    public void enterEmployeeNumber(String v) { sendKeys(employeeNumberInput, v, "Employee Number Field"); }
    public void enterBankAccountNumber(String v) { sendKeys(bankAccountInput, v, "Bank Account Number Field"); }
    public void enterIfscCode(String v) { sendKeys(ifscInput, v, "Bank IFSC Code Field"); }
    public void enterPfAccountNumber(String v) { sendKeys(pfAccountInput, v, "PF Account Number Field"); }
    public void enterNetSalary(String v) { sendKeys(netSalaryInput, v, "Net In-hand Salary Field"); }

    // isoDate is yyyy-MM-dd
    public void setJoiningDate(String isoDate) { setNativeDate(joiningDateInput, isoDate, "Date of Joining"); }

    public void clickSave() {
        click(saveButton, "Save Button");
    }

    // If Save is still on the page after 15 seconds, the portal did not accept the form
    public void waitForFormToClose() {
        waitUntilGone(saveButton, 15,
                "the Create Operator form to close after Save (check required fields or validation messages)");
    }
}