package com.durbin.automation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.durbin.automation.base.BasePage;

public class AssetPage extends BasePage {

    public AssetPage(WebDriver driver) {
        super(driver);
    }

    // Menu locators
    @FindBy(xpath = "(//span[.='Assets'])[1]")
    private WebElement assetsMenu;

    @FindBy(xpath = "//button[normalize-space()='Create']")
    private WebElement createAssetButton;

    // Asset Details
    @FindBy(xpath = "//input[@placeholder='Asset Number']")
    private WebElement assetNumberInput;

    @FindBy(xpath = "//input[@placeholder='Make']")
    private WebElement makeInput;

    @FindBy(xpath = "//input[@placeholder='Model Number']")
    private WebElement modelInput;

    @FindBy(xpath = "//input[@placeholder='e.g. GS46D-12345']")
    private WebElement serialNumberInput;

    @FindBy(xpath = "//input[@placeholder='e.g. TATA']")
    private WebElement purchasedFromInput;

    @FindBy(xpath = "//input[@placeholder='Enter Valid Device Number']")
    private WebElement deviceNumberInput;

    // Dropdowns (Often require clicking the element, then selecting an option)
    @FindBy(xpath = "//input[@placeholder='Select Year']")
    private WebElement yearDropdown;

    @FindBy(xpath = "//input[@placeholder='Search for a location...']")
    private WebElement locationInput;

    // Optional Fields
    @FindBy(xpath = "//input[@placeholder='e.g. 10 ft']")
    private WebElement heightInput;

    @FindBy(xpath = "//input[@placeholder='e.g. 80 kg.']")
    private WebElement capacityInput;

    // Buttons
    @FindBy(xpath = "//button[normalize-space()='Back']")
    private WebElement backButton;

    @FindBy(xpath = "//button[normalize-space()='Save & Next']")
    private WebElement saveAndNextButton;

    public void goToAssetPage() {
        clickWhenVisible(assetsMenu);
    }

    public void clickCreateAssetButton() {
        clickWhenVisible(createAssetButton);
    }

    public void enterAssetNumber(String assetNumber) {
        enterText(assetNumberInput, assetNumber);
    }

    public void enterMake(String make) {
        enterText(makeInput, make);
    }

    public void enterModelNumber(String modelNumber) {
        enterText(modelInput, modelNumber);
    }

    public void enterSerialNumber(String serialNumber) {
        enterText(serialNumberInput, serialNumber);
    }

    public void enterPurchasedFrom(String purchasedFrom) {
        enterText(purchasedFromInput, purchasedFrom);
    }

    public void enterDeviceNumber(String deviceNumber) {
        enterText(deviceNumberInput, deviceNumber);
    }

    public void enterHeight(String height) {
        enterText(heightInput, height);
    }

    public void enterCapacity(String capacity) {
        enterText(capacityInput, capacity);
    }

    public void selectYear(String year) {
        yearDropdown.click();
        yearDropdown.sendKeys(year);
        yearDropdown.sendKeys(Keys.ENTER);
    }

    public void selectLocation(String location) {
        locationInput.click();
        locationInput.sendKeys(location);
        locationInput.sendKeys(Keys.ENTER);
    }

    public void clickBackButton() {
        clickWhenVisible(backButton);
    }

    public void clickSaveAndNextButton() {
        clickWhenVisible(saveAndNextButton);
    }

    // ---Configuration details ---

    // Used / New Dropdown
    // Used / New
    // ===================== Configuration Details =====================

    // Used / New
    @FindBy(xpath = "//div[@data-slot='form-item'][.//label[normalize-space()='Used / New']]//button")
    private WebElement usedNewDropdown;

    // ANSI / CE
    @FindBy(xpath = "//div[@data-slot='form-item'][.//label[normalize-space()='ANSI / CE']]//button")
    private WebElement ansiCeDropdown;

    // Drive Train
    @FindBy(xpath = "//div[@data-slot='form-item'][.//label[normalize-space()='Drive Train']]//button")
    private WebElement driveTrainDropdown;

    // Wheel Drive Type
    @FindBy(xpath = "//div[@data-slot='form-item'][.//label[normalize-space()='Wheel Drive Type']]//button")
    private WebElement wheelDriveTypeDropdown;

    // Tires
    @FindBy(xpath = "//div[@data-slot='form-item'][.//label[normalize-space()='Tires']]//button")
    private WebElement tiresDropdown;

    // Engine Serial No
    @FindBy(xpath = "//div[@data-slot='form-item'][.//label[contains(normalize-space(),'Engine Serial No')]]//input")
    private WebElement engineSerialInput;

    // Accessories
    @FindBy(xpath = "//div[@data-slot='form-item'][.//label[contains(normalize-space(),'Accessories')]]//input")
    private WebElement accessoriesInput;

    // ===================== Common Dropdown Method =====================

    private void selectDropdownOption(WebElement dropdown, String value) {

        clickWhenClickable(dropdown);

        WebElement option = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@role='option' and normalize-space()='" + value + "']")));

        option.click();
    }

    // ===================== Configuration Methods =====================

    public void selectUsedNew(String usedNew) {
        selectDropdownOption(usedNewDropdown, usedNew);
    }

    public void selectAnsiCe(String ansiCe) {
        selectDropdownOption(ansiCeDropdown, ansiCe);
    }

    public void selectDriveTrain(String driveTrain) {
        selectDropdownOption(driveTrainDropdown, driveTrain);
    }

    public void selectWheelDriveType(String wheelDriveType) {
        selectDropdownOption(wheelDriveTypeDropdown, wheelDriveType);
    }

    public void selectTires(String tires) {
        selectDropdownOption(tiresDropdown, tires);
    }

    public void enterEngineSerial(String engineSerial) {
        enterText(engineSerialInput, engineSerial);
    }

    public void enterAccessories(String accessories) {
        enterText(accessoriesInput, accessories);
    }

    @FindBy(xpath = "//button[normalize-space()='Save & Next']")
    private WebElement nextButton;

    public void clickNextButton() {
        clickWhenVisible(nextButton);
    }

    // ===================== Commercial Details =====================

    // PO Number
    @FindBy(xpath = "//input[@placeholder='PO Number']")
    private WebElement poNumberInput;

    // Purchase Order Date
    @FindBy(xpath = "//button[contains(@class,'justify-start')][.//span[contains(text(),'Pick a date')] or contains(.,'Pick a date')]")
    private WebElement purchaseOrderDate;

    // GST Amount
    @FindBy(xpath = "//input[@placeholder='e.g. 100000']")
    private WebElement gstAmountInput;

    // Ex rate as per BOE
    @FindBy(xpath = "//label[contains(normalize-space(),'Ex rate as per BOE')]/following::input[1]")
    private WebElement exRateInput;

    // Clearing Charges
    @FindBy(xpath = "//label[contains(normalize-space(),'Clearing Charges')]/following::input[1]")
    private WebElement clearingChargesInput;

    // CHA Charges
    @FindBy(xpath = "//label[contains(normalize-space(),'CHA Charges')]/following::input[1]")
    private WebElement chaChargesInput;

    // Transportation Charges upto yard
    @FindBy(xpath = "//label[contains(normalize-space(),'Transportation charges upto yard')]/following::input[1]")
    private WebElement transportationChargesInput;

    // Dispatch Port
    @FindBy(xpath = "//div[@data-slot='form-item'][.//label[normalize-space()='Dispatch Port']]//button")
    private WebElement dispatchPortDropdown;

    // Clearance Port
    @FindBy(xpath = "//div[@data-slot='form-item'][.//label[normalize-space()='Clearance Port']]//button")
    private WebElement clearancePortDropdown;

    // Period of Insurance
    @FindBy(xpath = "//input[@placeholder='Period of Insurance']")
    private WebElement periodOfInsuranceInput;

    // Renewal Date
    @FindBy(xpath = "//label[normalize-space()=\"Renewal's Date\"]/following::button[1]")
    private WebElement renewalDateButton;

    // Total Landed Cost
    @FindBy(xpath = "//label[normalize-space()='Total Landed Cost (₹)']/following::input[1]")
    private WebElement totalLandedCostInput;

    // Total Landed Cost (including GST)
    @FindBy(xpath = "//label[contains(normalize-space(),'including GST')]/following::input[1]")
    private WebElement totalLandedCostIncludingGSTInput;

    // Create New Asset
    @FindBy(xpath = "//button[normalize-space()='Create New Asset']")
    private WebElement createNewAssetButton;

    public void enterPONumber(String value) {
        enterText(poNumberInput, value);
    }

    public void enterGSTAmount(String value) {
        enterText(gstAmountInput, value);
    }

    public void enterExRate(String value) {
        enterText(exRateInput, value);
    }

    public void enterClearingCharges(String value) {
        enterText(clearingChargesInput, value);
    }

    public void enterCHACharges(String value) {
        enterText(chaChargesInput, value);
    }

    public void enterTransportationCharges(String value) {
        enterText(transportationChargesInput, value);
    }

    public void selectDispatchPort(String value) {
        selectDropdownOption(dispatchPortDropdown, value);
    }

    public void selectClearancePort(String value) {
        selectDropdownOption(clearancePortDropdown, value);
    }

    public void enterPeriodOfInsurance(String value) {
        enterText(periodOfInsuranceInput, value);
    }

    public void enterTotalLandedCost(String value) {
        enterText(totalLandedCostInput, value);
    }

    public void enterTotalLandedCostIncludingGST(String value) {
        enterText(totalLandedCostIncludingGSTInput, value);
    }

    public void clickCreateNewAsset() {
        clickWhenClickable(createNewAssetButton);
    }
}