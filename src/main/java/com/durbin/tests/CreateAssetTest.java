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
        assetPage.enterCapacity(capacity);
        assetPage.selectYear(year);
        assetPage.selectLocation(location);
        assetPage.clickSaveAndNextButton();
        step("Asset Creation's Basic details filled----->>>>>>");
    }

}

