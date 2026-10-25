package com.durbin.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.durbin.automation.base.BasePage;

public class CreateLeasePage extends BasePage {

    public CreateLeasePage(WebDriver driver) {
        super(driver);
    }

    // ===================== Menu and list page =====================
    private final By leasesLogisticsMenu = By.xpath("//span[normalize-space()='Leases & Logistics']");
    private final By leaseSubMenu = By.xpath("//span[normalize-space()='Lease']");
    private final By createButton = By.xpath("//button[normalize-space()='Create']");

    // ===================== Create Lease form =====================
    private final By customerNameField = dropdownByLabel("Customer Name");
    private final By customerPoNoInput = By.xpath("//input[@placeholder='Customer PO No']");
    private final By salesPersonField = dropdownByLabel("Sales Person");
    private final By leaseTypeField = dropdownByLabel("Lease Type");
    private final By addMachineButton = By.xpath("//button[normalize-space()='Add Machine']");

    // ===================== Helpers =====================

    // Finds the dropdown that comes right after a label
    private static By dropdownByLabel(String label) {
    return By.xpath("//*[normalize-space(text())='" + label + "']/following::*[@role='combobox'][1]");
}

    // ===================== Navigation =====================

    public void openLeasesMenu() {
        click(leasesLogisticsMenu, "Leases & Logistics Menu");
    }

    public void openLeaseSubMenu() {
        click(leaseSubMenu, "Lease Sub Menu");
    }

    public void clickCreateButton() {
        click(createButton, "Create Button");
    }

    // ===================== Create Lease form =====================

    public void selectCustomerName(String customerName) {
        typeAndSelectFirstOption(customerNameField, customerName, "Customer Name Field");
    }

    public void enterCustomerPoNo(String poNo) {
        sendKeys(customerPoNoInput, poNo, "Customer PO No Field");
    }

    public void selectSalesPerson(String salesPerson) {
        typeAndSelectFirstOption(salesPersonField, salesPerson, "Sales Person Field");
    }

    // leaseType must be exactly "Dry" or "Wet" (the test class checks this)
    public void selectLeaseType(String leaseType) {
        click(leaseTypeField, "Lease Type dropdown");
        click(By.xpath("//*[@role='option'][normalize-space()='" + leaseType + "']"),
                "Option '" + leaseType + "' in Lease Type");
    }

    public void clickAddMachine() {
        click(addMachineButton, "Add Machine Button");
    }

        // ===================== Machine section =====================
    private final By machineAssetNoLabel = By.xpath("//label[normalize-space()='Asset No']");
    private final By assetNoField = dropdownByLabel("Asset No");
    private final By operatorNoInput = inputByLabel("Operator No.(s)");
    private final By operatorNameField = dropdownByLabel("Operator Name");
    private final By machineLocationField = dropdownByLabel("Location");
    private final By monthlyRentInput = inputByLabel("Monthly Rent");
    private final By shiftsPerDayInput = inputByLabel("Shifts Per Day");
    private final By nominalHrsInput = inputByLabel("Nominal Hrs/Shift");
    private final By workingDaysInput = inputByLabel("Working Days/Month");
    private final By contractValueInput = inputByLabel("Contract Value");

    // Finds the input that comes right after a label (first machine only)
    private static By inputByLabel(String label) {
        return By.xpath("(//label[contains(normalize-space(),'" + label + "')]/following::input[1])[1]");
    }


    public void selectAsset(String v) { typeAndSelectFirstOption(assetNoField, v, "Asset No Field"); }
    public void enterOperatorNo(String v) { sendKeys(operatorNoInput, v, "Operator No Field"); }
    public void selectOperatorName(String v) { typeAndSelectFirstOption(operatorNameField, v, "Operator Name Field"); }
    public void selectMachineLocation(String v) { typeAndSelectFirstOption(machineLocationField, v, "Location Field"); }
    public void enterMonthlyRent(String v) { sendKeys(monthlyRentInput, v, "Monthly Rent Field"); }
    public void enterShiftsPerDay(String v) { sendKeys(shiftsPerDayInput, v, "Shifts Per Day Field"); }
    public void enterNominalHrs(String v) { sendKeys(nominalHrsInput, v, "Nominal Hrs/Shift Field"); }
    public void enterWorkingDays(String v) { sendKeys(workingDaysInput, v, "Working Days/Month Field"); }
    public void enterContractValue(String v) { sendKeys(contractValueInput, v, "Contract Value Field"); }
}