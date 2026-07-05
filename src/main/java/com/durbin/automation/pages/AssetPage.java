package com.durbin.automation.pages;

import java.util.List;

import org.openqa.selenium.By;
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
        selectByVisibleText(yearDropdown, year);
    }

    public void selectLocation(String location) {
        selectByVisibleText(locationInput, location);
    }

    public void clickBackButton() {
        clickWhenVisible(backButton);
    }

    public void clickSaveAndNextButton() {
        clickWhenVisible(saveAndNextButton);
    }
}

