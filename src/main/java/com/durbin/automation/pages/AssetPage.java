package com.durbin.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.durbin.automation.base.BasePage;

public class AssetPage extends BasePage {

    public AssetPage(WebDriver driver) {
        super(driver);
    }

    // ===================== Menu =====================
    private final By assetsMenu = By.xpath("(//span[.='Assets'])[1]");
    private final By createAssetButton = By.xpath("//button[normalize-space()='Create']");

    // ===================== Asset Details =====================
    private final By assetNumberInput = By.xpath("//input[@placeholder='Asset Number']");
    private final By makeInput = By.xpath("//input[@placeholder='Make']");
    private final By modelInput = By.xpath("//input[@placeholder='Model Number']");
    private final By serialNumberInput = By.xpath("//input[@placeholder='e.g. GS46D-12345']");
    private final By purchasedFromInput = By.xpath("//input[@placeholder='e.g. TATA']");
    private final By deviceNumberInput = By.xpath("//input[@placeholder='Enter Valid Device Number']");
    private final By categorySelect = By.xpath("//*[@placeholder='Select Category']");
    private final By yearDropdown = By.xpath("//input[@placeholder='Select Year']");
    private final By locationInput = By.xpath("//input[@placeholder='Search for a location...']");
    private final By heightInput = By.xpath("//input[@placeholder='e.g. 10 ft']");
    private final By capacityInput = By.xpath("//input[@placeholder='e.g. 80 kg.']");

    // ===================== Buttons =====================
    private final By backButton = By.xpath("//button[normalize-space()='Back']");
    private final By saveAndNextButton = By.xpath("//button[normalize-space()='Save & Next']");
    private final By createNewAssetButton = By.xpath("//button[normalize-space()='Create New Asset']");

    // ===================== Configuration Details =====================
    private final By usedNewDropdown = formDropdown("Used / New");
    private final By ansiCeDropdown = formDropdown("ANSI / CE");
    private final By driveTrainDropdown = formDropdown("Drive Type");
    private final By wheelDriveTypeDropdown = formDropdown("Wheel Drive Type");
    private final By tiresDropdown = formDropdown("Tires");
    private final By engineSerialInput = By.xpath(
            "//div[@data-slot='form-item'][.//label[contains(normalize-space(),'Engine Serial No')]]//input");
    private final By accessoriesInput = By.xpath(
            "//div[@data-slot='form-item'][.//label[contains(normalize-space(),'Accessories')]]//input");

    // ===================== Commercial Details =====================
    private final By poNumberInput = By.xpath("//input[@placeholder='PO Number']");
    private final By gstAmountInput = By.xpath("//input[@placeholder='e.g. 100000']");
    private final By exRateInput = By.xpath("//label[contains(normalize-space(),'Ex rate as per BOE')]/following::input[1]");
    private final By clearingChargesInput = By.xpath("//label[contains(normalize-space(),'Clearing Charges')]/following::input[1]");
    private final By chaChargesInput = By.xpath("//label[contains(normalize-space(),'CHA Charges')]/following::input[1]");
    private final By transportationChargesInput = By.xpath("//label[contains(normalize-space(),'Transportation charges upto yard')]/following::input[1]");
    private final By dispatchPortDropdown = formDropdown("Dispatch Port");
    private final By clearancePortDropdown = formDropdown("Clearance Port");
    private final By periodOfInsuranceInput = By.xpath("//input[@placeholder='e.g. 2']");
    private final By totalLandedCostInput = By.xpath("//label[normalize-space()='Total Landed Cost (₹)']/following::input[1]");
    private final By totalLandedCostIncludingGSTInput = By.xpath("//label[contains(normalize-space(),'including GST')]/following::input[1]");

    // ===================== Helpers =====================

    // Builds the locator for a dropdown button inside a labelled form item
    private static By formDropdown(String label) {
        return By.xpath("//div[@data-slot='form-item'][.//label[normalize-space()='" + label + "']]//button");
    }

    private void selectDropdownOption(By dropdown, String value, String dropdownName) {
        click(dropdown, dropdownName + " dropdown");
        click(By.xpath("//div[@role='option' and normalize-space()='" + value + "']"),
                "Option '" + value + "' in " + dropdownName);
    }

    // ===================== Navigation =====================

    public void goToAssetPage() {
        click(assetsMenu, "Assets Menu");
    }

    public void clickCreateAssetButton() {
        click(createAssetButton, "Create Asset Button");
    }

    public void clickBackButton() {
        click(backButton, "Back Button");
    }

    public void clickSaveAndNextButton() {
        click(saveAndNextButton, "Save & Next Button");
    }

    public void clickNextButton() {
        click(saveAndNextButton, "Next Button");
    }

    public void clickCreateNewAsset() {
        click(createNewAssetButton, "Create New Asset Button");
    }

    // ===================== Asset Details =====================

    public void enterAssetNumber(String v) { sendKeys(assetNumberInput, v, "Asset Number Field"); }
    public void enterMake(String v) { sendKeys(makeInput, v, "Make Field"); }
    public void enterModelNumber(String v) { sendKeys(modelInput, v, "Model Number Field"); }
    public void enterSerialNumber(String v) { sendKeys(serialNumberInput, v, "Serial Number Field"); }
    public void enterPurchasedFrom(String v) { sendKeys(purchasedFromInput, v, "Purchased From Field"); }
    public void enterDeviceNumber(String v) { sendKeys(deviceNumberInput, v, "Device Number Field"); }
    public void enterHeight(String v) { sendKeys(heightInput, v, "Height Field"); }
    public void enterCapacity(String v) { sendKeys(capacityInput, v, "Capacity Field"); }

    public void clickCategoryDropdown(String category) {
    selectDropdownOption(categorySelect, category, "Category");
}

    public void selectYear(String year) {
        typeAndEnter(yearDropdown, year, "Year Field");
    }

    public void selectLocation(String location) {
        typeAndEnter(locationInput, location, "Location Field");
    }

    // ===================== Configuration =====================

    public void selectUsedNew(String v) { selectDropdownOption(usedNewDropdown, v, "Used / New"); }
    public void selectAnsiCe(String v) { selectDropdownOption(ansiCeDropdown, v, "ANSI / CE"); }
    public void selectDriveTrain(String v) { selectDropdownOption(driveTrainDropdown, v, "Drive Train"); }
    public void selectWheelDriveType(String v) { selectDropdownOption(wheelDriveTypeDropdown, v, "Wheel Drive Type"); }
    public void selectTires(String v) { selectDropdownOption(tiresDropdown, v, "Tires"); }
    public void enterEngineSerial(String v) { sendKeys(engineSerialInput, v, "Engine Serial Field"); }
    public void enterAccessories(String v) { sendKeys(accessoriesInput, v, "Accessories Field"); }

    // ===================== Commercial =====================

    public void enterPONumber(String v) { sendKeys(poNumberInput, v, "PO Number Field"); }
    public void enterGSTAmount(String v) { sendKeys(gstAmountInput, v, "GST Amount Field"); }
    public void enterExRate(String v) { sendKeys(exRateInput, v, "Ex Rate Field"); }
    public void enterClearingCharges(String v) { sendKeys(clearingChargesInput, v, "Clearing Charges Field"); }
    public void enterCHACharges(String v) { sendKeys(chaChargesInput, v, "CHA Charges Field"); }
    public void enterTransportationCharges(String v) { sendKeys(transportationChargesInput, v, "Transportation Charges Field"); }
    public void selectDispatchPort(String v) { selectDropdownOption(dispatchPortDropdown, v, "Dispatch Port"); }
    public void selectClearancePort(String v) { selectDropdownOption(clearancePortDropdown, v, "Clearance Port"); }
    public void enterPeriodOfInsurance(String v) { sendKeys(periodOfInsuranceInput, v, "Period of Insurance Field"); }
    public void enterTotalLandedCost(String v) { sendKeys(totalLandedCostInput, v, "Total Landed Cost Field"); }
    public void enterTotalLandedCostIncludingGST(String v) { sendKeys(totalLandedCostIncludingGSTInput, v, "Total Landed Cost (incl. GST) Field"); }
}