package com.durbin.automation.pages;

import com.durbin.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CreateLeasePage extends BasePage {

    public CreateLeasePage(WebDriver driver) {
        super(driver);
    }

    // Create Lease Page Elements
    @FindBy(xpath = "//h3[contains(text(), 'Create Lease')]")
    private WebElement createLeaseTitle;

    @FindBy(xpath = "//input[contains(@id, 'effective_date_field')]")
    private WebElement effectiveDateInput;

    @FindBy(xpath = "//input[contains(@id, 'renewal_term_field')]")
    private WebElement renewalTermInput;

    @FindBy(xpath = "//input[contains(@id, 'rent_increase_date_field')]")
    private WebElement rentIncreaseDateInput;

    @FindBy(xpath = "//input[contains(@id, 'renewal_date_field')]")
    private WebElement renewalDateInput;

    @FindBy(xpath = "//select[contains(@id, 'lease_type_field')]")
    private WebElement leaseTypeSelect;

    @FindBy(xpath = "//select[contains(@id, 'lease_fulfillment_method_field')]")
    private WebElement fulfillmentMethodSelect;

    @FindBy(xpath = "//textarea[contains(@id, 'lease_space_description_field')]")
    private WebElement spaceDescription;

    @FindBy(xpath = "//input[contains(@id, 'additional_rent_field')]")
    private WebElement additionalRentInput;

    @FindBy(xpath = "//textarea[contains(@id, 'renewal_lease_space_description_field')]")
    private WebElement renewalSpaceDescription;

    @FindBy(xpath = "//input[contains(@id, 'lease_commencement_date_field')]")
    private WebElement leaseCommencementDateInput;

    @FindBy(xpath = "//input[contains(@id, 'lease_expiry_date_field')]")
    private WebElement leaseExpiryDateInput;

    @FindBy(xpath = "//input[contains(@id, 'base_rent_field')]")
    private WebElement baseRentInput;

    @FindBy(xpath = "//input[contains(@id, 'renewal_rent_field')]")
    private WebElement renewalRentInput;

    @FindBy(xpath = "//input[contains(@id, 'width_field')]")
    private WebElement dimensionsWidth;

    @FindBy(xpath = "//input[contains(@id, 'depth_field')]")
    private WebElement dimensionsDepth;

    @FindBy(xpath = "//input[contains(@id, 'total_area_field')]")
    private WebElement dimensionsTotalArea;

    @FindBy(xpath = "//input[contains(@id, 'renewal_width_field')]")
    private WebElement renewalDimensionsWidth;

    @FindBy(xpath = "//input[contains(@id, 'renewal_depth_field')]")
    private WebElement renewalDimensionsDepth;

    @FindBy(xpath = "//input[contains(@id, 'renewal_total_area_field')]")
    private WebElement renewalDimensionsTotalArea;

    @FindBy(xpath = "//input[contains(@id, 'rental_rate_rate_field')]")
    private WebElement rentalRateRate;

    @FindBy(xpath = "//input[contains(@id, 'renewal_rental_rate_rate_field')]")
    private WebElement renewalRentalRateRate;

    @FindBy(xpath = "//select[contains(@id, 'rental_rate_type_field')]")
    private WebElement rentalRateType;

    @FindBy(xpath = "//select[contains(@id, 'renewal_rental_rate_type_field')]")
    private WebElement renewalRentalRateType;

    @FindBy(xpath = "//select[contains(@id, 'rental_rate_per_field')]")
    private WebElement rentalRatePer;

    @FindBy(xpath = "//select[contains(@id, 'renewal_rental_rate_per_field')]")
    private WebElement renewalRentalRatePer;

    @FindBy(xpath = "//button[contains(.,'Save') and @type='submit'] | //button[contains(.,'Save') and @id='create-lease-btn']")
    private WebElement saveButton;

    @FindBy(xpath = "//h3[contains(.,'Success')]")
    private WebElement successMessage;

    @FindBy(xpath = "//button[contains(.,'Create Lease') and @type='submit'] | //button[contains(.,'Create New Lease')] | //button[contains(@id, 'create_new_lease')]")
    private WebElement createNewLeaseButton;

    // Methods
    public boolean isCreateLeasePageDisplayed() {
        return isElementDisplayed(createLeaseTitle, 10);
    }

    public void fillBasicInformation(String effectiveDate,
            String renewalTerm,
            String rentIncreaseDate,
            String renewalDate,
            String leaseType,
            String fulfillmentMethod,
            String spaceDescription,
            String additionalRent,
            String renewalSpaceDescription,
            String leaseCommencementDate,
            String leaseExpiryDate,
            String baseRent,
            String renewalRent) {

        // Fill basic fields with default values from test data
        fillInput(effectiveDateInput, effectiveDate, "Effective Date");
        fillInput(renewalTermInput, renewalTerm, "Renewal Term");
        fillInput(rentIncreaseDateInput, rentIncreaseDate, "Rent Increase Date");
        fillInput(renewalDateInput, renewalDate, "Renewal Date");
        selectDropdownByVisibleText(leaseTypeSelect, leaseType, "Lease Type");
        selectDropdownByVisibleText(fulfillmentMethodSelect, fulfillmentMethod,
                "Fulfillment Method");
        fillInput(spaceDescription, spaceDescription, "Space Description");
        fillInput(additionalRentInput, additionalRent, "Additional Rent");
        fillInput(renewalSpaceDescription, renewalSpaceDescription,
                "Renewal Space Description");
        fillInput(leaseCommencementDateInput, leaseCommencementDate,
                "Lease Commencement Date");
        fillInput(leaseExpiryDateInput, leaseExpiryDate, "Lease Expiry Date");
        fillInput(baseRentInput, baseRent, "Base Rent");
        fillInput(renewalRentInput, renewalRent, "Renewal Rent");
    }

    public void fillDimensions(String width,
            String depth,
            String totalArea,
            String renewalWidth,
            String renewalDepth,
            String renewalTotalArea) {

        fillInput(dimensionsWidth, width, "Dimensions Width");
        fillInput(dimensionsDepth, depth, "Dimensions Depth");
        fillInput(dimensionsTotalArea, totalArea, "Dimensions Total Area");
        fillInput(renewalDimensionsWidth, renewalWidth, "Renewal Dimensions Width");
        fillInput(renewalDimensionsDepth, renewalDepth, "Renewal Dimensions Depth");
        fillInput(renewalDimensionsTotalArea, renewalTotalArea,
                "Renewal Dimensions Total Area");
    }

    public void fillRentalRate(String rate,
            String renewalRate,
            String type,
            String renewalType,
            String per,
            String renewalPer) {

        fillInput(rentalRateRate, rate, "Rental Rate Rate");
        fillInput(renewalRentalRateRate, renewalRate, "Renewal Rental Rate Rate");
        selectDropdownByVisibleText(rentalRateType, type, "Rental Rate Type");
        selectDropdownByVisibleText(renewalRentalRateType, renewalType,
                "Renewal Rental Rate Type");
        selectDropdownByVisibleText(rentalRatePer, per, "Rental Rate Per");
        selectDropdownByVisibleText(renewalRentalRatePer, renewalPer,
                "Renewal Rental Rate Per");
    }

    public void clickSave() {
        clickElement(saveButton, 10, "Save button");
    }

public boolean isLeaseCreated(String expectedLeaseId
