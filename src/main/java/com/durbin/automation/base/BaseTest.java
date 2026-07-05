package com.durbin.automation.base;

import com.durbin.automation.utils.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.time.Duration;
import org.openqa.selenium.TimeoutException;

public class BaseTest {
    protected WebDriver driver;
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeMethod
    public void setUp() {
        logger.info("----------Initializing Selenium WebDriver------------");
        step("Initializing WebDriver");

        boolean isHeadless = Boolean.parseBoolean(
                com.durbin.automation.utils.EnvReader.get("headless") != null
                        ? com.durbin.automation.utils.EnvReader.get("headless")
                        : "true");

        ChromeOptions chromeOptions = buildBaseChromeOptions();
        if (isHeadless) {
            chromeOptions.addArguments("--headless=new");
        }

        driver = new ChromeDriver(chromeOptions);

        if (!isHeadless) {
            driver.manage().window().maximize();
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        String url = ConfigReader.getProperty("login.url");
        logger.info("Navigating to login page: " + url);
        step("Navigating to application");
        driver.get(url);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            logger.info("----------closed WebDriver----------");
        }
    }

    private ChromeOptions buildBaseChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            options.addArguments("--kiosk-printing");
        }
        return options;
    }

    protected void step(String message) {
        logger.info(message);
        com.durbin.automation.utils.ExecutionLogCollector.log(message);
    }
}

