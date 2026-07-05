package com.durbin.tests;

import org.testng.annotations.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.durbin.automation.api.TestDataHolder;
import com.durbin.automation.base.BaseTest;
import com.durbin.automation.pages.AssetPage;
import com.durbin.automation.pages.LoginPage;

public class CreateAssetTest extends BaseTest {

    @Test
    public void testCreateAsset() throws InterruptedException {
        step("-------Starting Asset Creation Test----------");

        JsonNode root = TestDataHolder.getData();
        if (root == null) {
            throw new RuntimeException("API test data is required. No fallback allowed.");
        }
        step("Using API-driven test data");

        // Extract Login Data
        if (!root.has("login")) {
            throw new RuntimeException("login data is required in API payload");
        }
        JsonNode login = root.get("login");
        String user = login.get("username").asText();
        String pass = login.get("password").asText();

        // Extract Asset Data
        if (!root.has("asset")) {
            throw new RuntimeException("asset data is required in API payload");
        }
        JsonNode asset = root.get("asset");
        if (!asset.has("assetNumber") || !asset.has("deviceNumber")) {
            throw new RuntimeException("assetNumber and deviceNumber are required in the asset payload");
        }
        String assetNumber = asset.get("assetNumber").asText();
        String deviceNumber = asset.get("deviceNumber").asText();

        // Optional/Fallback Asset Fields
        String make = asset.has("make") ? asset.get("make").asText() : "Make";
        String model = asset.has("model") ? asset.get("model").asText() : "Model";
        String serial = asset.has("serialNumber") ? asset.get("serialNumber").asText() : "Serial";
        String purchasedFrom = asset.has("purchasedFrom") ? asset.get("purchasedFrom").asText() : "Purchased From";
        String height = asset.has("height") ? asset.get("height").asText() : "10";
        String capacity = asset.has("capacity") ? asset.get("capacity").asText() : "100";
        String year = asset.has("year") ? asset.get("year").asText() : "2022";
        String location = asset.has("location") ? asset.get("location").asText() : "Location";

        LoginPage loginPage = new LoginPage(driver);
        AssetPage assetPage = new AssetPage(driver);

        step("Login------------------------");
        loginPage.login(user, pass);
        step("----------Login Completed----------");

        // Navigate to dashboard and wait for transition
        Thread.sleep(5000);

        assetPage.goToAssetPage();
        assetPage.clickCreateAssetButton();
        assetPage.enterAssetNumber(assetNumber);
        assetPage.enterMake(make);
        assetPage.enterModelNumber(model);
        assetPage.enterSerialNumber(serial);
        assetPage.enterPurchasedFrom(purchasedFrom);
        assetPage.enterDeviceNumber(deviceNumber);
        assetPage.enterHeight(height);
        // assetPage.enterCapacity(capacity);

        assetPage.selectYear(year);
        assetPage.selectLocation(location);
        assetPage.clickSaveAndNextButton();
        step("Asset Creation's Basic details filled----->>>>>>");
        Thread.sleep(5000);

        JsonNode configuration = asset.path("configuration");
        String usedNew = configuration.path("usedNew").asText("New");
        String ansiCe = configuration.path("ansiCe").asText("ANSI");
        String driveTrain = configuration.path("driveTrain").asText("Engine");
        String wheelDriveType = configuration.path("wheelDriveType").asText("4 WD");
        String tires = configuration.path("tires").asText("Pneumatic");
        String engineSerial = configuration.path("engineSerial").asText("Eng-9000");
        String accessories = configuration.path("accessories").asText("None");

        assetPage.selectUsedNew("New");
        assetPage.selectAnsiCe("CE");
        assetPage.selectDriveTrain("Diesel");
        assetPage.selectWheelDriveType("2WD");
        assetPage.selectTires("Solid");
        assetPage.enterEngineSerial("Eng-9000");
        assetPage.enterAccessories("GPS Tracker");
        step("Configuration details filled----->>>>>>");
        Thread.sleep(5000);

        assetPage.clickNextButton();
        step("Clicked Next Button----->>>>>>");
        Thread.sleep(5000);

        // ---Commercial Details---
        String poNumber = configuration.path("poNumber").asText("PO-2024-001");
        String gstAmount = configuration.path("gstAmount").asText("18000");
        String exRate = configuration.path("exRate").asText("85.50");
        String clearingCharges = configuration.path("clearingCharges").asText("5000");
        String chaCharges = configuration.path("chaCharges").asText("7000");
        String transportationCharges = configuration.path("transportationCharges").asText("3000");
        String dispatchPort = configuration.path("dispatchPort").asText("Port A");
        String clearancePort = configuration.path("clearancePort").asText("Port B");
        String periodOfInsurance = configuration.path("periodOfInsurance").asText("12");
        String totalLandedCost = configuration.path("totalLandedCost").asText("1000000");
        String totalLandedCostIncludingGST = configuration.path("totalLandedCostIncludingGST").asText("1180000");

        assetPage.enterPONumber(poNumber);
        assetPage.enterGSTAmount(gstAmount);
        assetPage.enterExRate(exRate);
        assetPage.enterClearingCharges(clearingCharges);
        assetPage.enterCHACharges(chaCharges);
        assetPage.enterTransportationCharges(transportationCharges);
        // assetPage.selectDispatchPort(dispatchPort);
        // assetPage.selectClearancePort(clearancePort);
        assetPage.enterPeriodOfInsurance(periodOfInsurance);
        assetPage.enterTotalLandedCost(totalLandedCost);
        assetPage.enterTotalLandedCostIncludingGST(totalLandedCostIncludingGST);
        step("Commercial details filled----->>>>>>");
        Thread.sleep(5000);

        assetPage.clickCreateNewAsset();
        step("Clicked Create New Asset----->>>>>>");
        Thread.sleep(5000);

    }

}
