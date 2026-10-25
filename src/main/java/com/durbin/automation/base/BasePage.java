package com.durbin.automation.base;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.StaleElementReferenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final Logger logger = LogManager.getLogger(BasePage.class);

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    protected void clickWhenVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.click();
        step("Clicked on " + element + "");
    }

    protected void enterText(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
        step("Entered " + text + " in the " + element + "Field.");
    }

    protected void selectByVisibleText(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    protected void selectByValue(WebElement element, String value) {
        wait.until(ExpectedConditions.visibilityOf(element));
        Select select = new Select(element);
        select.selectByValue(value);
    }

    protected void selectByIndex(WebElement element, int index) {
        wait.until(ExpectedConditions.visibilityOf(element));
        Select select = new Select(element);
        select.selectByIndex(index);
    }

    protected String getText(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getText();
    }

    protected boolean isElementDisplayed(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.isDisplayed();
    }

    protected void clearText(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
    }

    protected void clickWhenClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    protected void switchToFrame(String frameNameOrId) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameNameOrId));
    }

    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    protected void waitForPageLoad() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }

    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


        // ===================== By-based methods (wait -> act -> log) =====================

    protected void click(By locator, String elementName) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        step("Clicked on " + elementName);
    }

    protected void sendKeys(By locator, String text, String elementName) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
        step(elementName.contains("Password")
                ? "Typed '********' into " + elementName
                : "Typed '" + text + "' into " + elementName);
    }

    // For search-style inputs: click, type, press ENTER (used for Year and Location)
    protected void typeAndEnter(By locator, String text, String elementName) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
        element.sendKeys(text);
        element.sendKeys(Keys.ENTER);
        step("Typed '" + text + "' and pressed ENTER in " + elementName);
    }

    protected void selectByVisibleText(By locator, String text, String elementName) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Select(element).selectByVisibleText(text);
        step("Selected '" + text + "' from dropdown " + elementName);
    }

    protected String getText(By locator, String elementName) {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
        step("Got text from " + elementName);
        return text;
    }

            // For search-style dropdowns (Customer Name, Sales Person, Asset No, Operator Name, Location)
    protected void typeAndSelectFirstOption(By field, String text, String fieldName) {
        String value = text.trim();
        String needle = value.toLowerCase();
        String lastChar = value.substring(value.length() - 1);

        // An implicit wait makes every "is it absent?" check take as long as the wait.
        // Switch it off here (explicit waits do the waiting) and put it back at the end.
        WebDriver.Timeouts timeouts = driver.manage().timeouts();
        Duration originalImplicitWait;
        try {
            originalImplicitWait = timeouts.getImplicitWaitTimeout();
        } catch (Exception e) {
            originalImplicitWait = Duration.ZERO;
        }
        timeouts.implicitlyWait(Duration.ZERO);

        try {
            click(field, fieldName);
            driver.findElement(field).sendKeys(value);

            long[] state = { System.currentTimeMillis(), 0 }; // [0] = time of last typing, [1] = retries used
            WebDriverWait searchWait = new WebDriverWait(driver, Duration.ofSeconds(45));
            searchWait.withMessage("an option containing '" + value + "' in " + fieldName);

            WebElement option = searchWait.until(d -> {
                WebElement match = findOptionContaining(d, needle);
                if (match != null) {
                    return match;
                }
                // Nothing after 8 seconds: backspace and retype the last character, like a person would
                long now = System.currentTimeMillis();
                if (state[1] < 3 && now - state[0] > 8000) {
                    state[1]++;
                    state[0] = now;
                    step("No suggestion yet for '" + value + "', retyping last character (retry " + state[1] + ")");
                    try {
                        WebElement f = d.findElement(field);
                        f.sendKeys(Keys.BACK_SPACE);
                        f.sendKeys(lastChar);
                    } catch (Exception e) {
                        logger.warn("Could not retype in " + fieldName + ": " + e.getMessage());
                    }
                }
                return null;
            });

            String picked = option.getText();
            try {
                option.click();
            } catch (StaleElementReferenceException e) {
                // list refreshed between find and click, so find it again
                WebElement again = findOptionContaining(driver, needle);
                if (again == null) {
                    throw e;
                }
                picked = again.getText();
                again.click();
            }

            closeDropdown(field, fieldName);
            step("Typed '" + value + "' and selected first option '" + picked + "' in " + fieldName);
        } finally {
            timeouts.implicitlyWait(originalImplicitWait);
        }
    }

    private WebElement findOptionContaining(WebDriver d, String needle) {
        for (WebElement o : d.findElements(By.xpath("//*[@role='option']"))) {
            try {
                if (o.isDisplayed() && o.getText().toLowerCase().contains(needle)) {
                    return o;
                }
            } catch (StaleElementReferenceException ignored) {
                // list refreshed while reading, so try again on the next poll
            }
        }
        return null;
    }

    // Closes the open list so it cannot cover the next field. Stops as soon as the list is closed.
    private void closeDropdown(By field, String fieldName) {
        try {
            WebElement input = driver.findElement(field);
            // Some multi-selects ignore blur while the mouse is over their list, so move the mouse onto the field first
            new Actions(driver).moveToElement(input).perform();
            input.sendKeys(Keys.ESCAPE);
            if (isListClosed(1500)) {
                return;
            }
            ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", input);
            if (isListClosed(1500)) {
                return;
            }
            logger.warn("Dropdown list still open after selecting in " + fieldName);
        } catch (Exception e) {
            logger.warn("Could not close dropdown " + fieldName + ": " + e.getMessage());
        }
    }

    private boolean isListClosed(long millis) {
        try {
            new WebDriverWait(driver, Duration.ofMillis(millis))
                    .pollingEvery(Duration.ofMillis(200))
                    .until(d -> d.findElements(By.xpath("//*[@role='option']")).stream().noneMatch(o -> {
                        try {
                            return o.isDisplayed();
                        } catch (StaleElementReferenceException e) {
                            return false;
                        }
                    }));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

        // For fields that already contain text we must keep (the phone fields already show +91)
    protected void appendKeys(By locator, String text, String elementName) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
        element.sendKeys(Keys.END, text);
        step("Typed '" + text + "' into " + elementName);
    }

    // For a real <select> tag: match the visible text ignoring upper/lower case and extra spaces
    protected void selectByTextIgnoreCase(By locator, String text, String elementName) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        Select select = new Select(element);
        java.util.List<String> available = new java.util.ArrayList<>();
        for (WebElement option : select.getOptions()) {
            String optionText = option.getText().trim();
            available.add(optionText);
            if (optionText.equalsIgnoreCase(text.trim())) {
                select.selectByValue(option.getAttribute("value"));
                step("Selected '" + optionText + "' from dropdown " + elementName);
                return;
            }
        }
        throw new RuntimeException("Option '" + text + "' not found in " + elementName
                + ". Available options: " + available);
    }

    // For a native <input type="date">. Sets the value as yyyy-MM-dd, so it does not depend on the browser's date format
    protected void setNativeDate(By locator, String isoDate, String elementName) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript(
                "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;"
                        + "setter.call(arguments[0], arguments[1]);"
                        + "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
                        + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                element, isoDate);
        String actual = element.getDomProperty("value");
        if (!isoDate.equals(actual)) {
            throw new RuntimeException(elementName + " was not accepted. Set '" + isoDate
                    + "' but field shows '" + actual + "'");
        }
        step("Set " + elementName + " to " + isoDate);
    }

    // Waits until an element is gone, with the implicit wait switched off so the check is not slow
    protected void waitUntilGone(By locator, int seconds, String failMessage) {
        WebDriver.Timeouts timeouts = driver.manage().timeouts();
        Duration original;
        try {
            original = timeouts.getImplicitWaitTimeout();
        } catch (Exception e) {
            original = Duration.ZERO;
        }
        timeouts.implicitlyWait(Duration.ZERO);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .withMessage(failMessage)
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } finally {
            timeouts.implicitlyWait(original);
        }
    }
        // For a <select> that is hidden on screen (opacity 0), like the month and year lists in the calendar.
    // Sets the value and tells the page it changed.
    protected void setHiddenSelectValue(By locator, String value, String elementName) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        Object result = ((JavascriptExecutor) driver).executeScript(
                "var s = arguments[0];"
                        + "var found = false;"
                        + "for (var i = 0; i < s.options.length; i++) {"
                        + "  if (s.options[i].value === arguments[1]) { found = true; break; }"
                        + "}"
                        + "if (!found) { return 'missing'; }"
                        + "s.value = arguments[1];"
                        + "s.dispatchEvent(new Event('change', { bubbles: true }));"
                        + "return 'ok';",
                element, value);
        if (!"ok".equals(result)) {
            throw new RuntimeException("Value '" + value + "' does not exist in the list for " + elementName);
        }
        step("Set " + elementName + " to " + value);
    }

        // For a shadcn/Radix dropdown (a button that opens a list of role="option" items).
    // Matches the whole option text, ignoring upper/lower case, so "Warranty" never picks "Non-Warranty".
    protected void selectRadixOption(By trigger, String text, String elementName) {
        click(trigger, elementName + " dropdown");
        String wanted = text.trim();
        By optionsLocator = By.xpath("//*[@role='option']");

        wait.until(d -> d.findElements(optionsLocator).stream().anyMatch(o -> {
            try {
                return o.isDisplayed();
            } catch (StaleElementReferenceException e) {
                return false;
            }
        }));

        java.util.List<String> available = new java.util.ArrayList<>();
        for (WebElement option : driver.findElements(optionsLocator)) {
            String optionText = option.getText().trim();
            available.add(optionText);
            if (optionText.equalsIgnoreCase(wanted)) {
                option.click();
                step("Selected '" + optionText + "' in " + elementName);
                return;
            }
        }
        throw new RuntimeException("Option '" + text + "' not found in " + elementName
                + ". Available options: " + available);
    }

    protected void step(String message) {
        logger.info(message);
        com.durbin.automation.utils.ExecutionLogCollector.log(message);
    }

}

