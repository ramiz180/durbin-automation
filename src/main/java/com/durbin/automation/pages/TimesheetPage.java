package com.durbin.automation.pages;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.durbin.automation.base.BasePage;

public class TimesheetPage extends BasePage {

    public TimesheetPage(WebDriver driver) {
        super(driver);
    }

    // ===================== Menu =====================
    private final By leasesLogisticsMenu = By.xpath("//span[normalize-space()='Leases & Logistics']");
    private final By leaseSubMenu = By.xpath("//span[normalize-space()='Lease']");

    // ===================== Lease list =====================
    private final By searchInput = By.xpath("//input[@placeholder='Search...']");
    private static final String FIRST_ROW = "(//tbody[@data-slot='table-body']/tr)[1]";
    private final By firstLeaseRow = By.xpath(FIRST_ROW);

    // ===================== Lease details =====================
    private final By addManuallyTab = By.xpath("//button[@role='tab'][normalize-space()='Add Manually']");

    // ===================== Monthly Log Form =====================
    private final By monthTrigger = By.xpath(
            "//*[normalize-space(.)='Select Month for Timesheet']/following::button[1]");
    private final By monthPopup = By.xpath("//div[@data-slot='popover-content']");

    // The timesheet table is found by its "Operator In" column, so it cannot be mixed up with other tables
    private static final String TS_TABLE = "//table[.//th[normalize-space()='Operator In']]";
    private final By timesheetRows = By.xpath(TS_TABLE + "//tbody/tr");
    // Active row = a row whose hour inputs are not disabled
    private final By activeRows = By.xpath(
            TS_TABLE + "//tbody/tr[.//input[@placeholder='hh.mm'][not(@disabled)]]");
    private final By submitButton = By.xpath("//button[normalize-space()='Submit']");

    // Column numbers inside a timesheet row
    private static final int COL_TIME_IN = 6;
    private static final int COL_TIME_OUT = 7;
    private static final int COL_BREAKDOWN = 8;
    private static final int COL_OVERTIME = 9;

    // ===================== Navigation =====================

    public void openLeasesMenu() {
        click(leasesLogisticsMenu, "Leases & Logistics Menu");
    }

    public void openLeaseSubMenu() {
        click(leaseSubMenu, "Lease Sub Menu");
    }

    // ===================== Lease list =====================

    // Types the asset number and waits until the first row shows that asset
    public void searchLease(String assetNo) {
        sendKeys(searchInput, assetNo, "Search Field");

        String upper = assetNo.toUpperCase();
        By firstRowWithAsset = By.xpath(FIRST_ROW
                + "[.//span[translate(@title,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')='"
                + upper + "']]");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(firstRowWithAsset));
        } catch (TimeoutException e) {
            throw new RuntimeException("No lease found for asset " + assetNo
                    + " in the list (check the asset number and the Active/Exceeded/Free tab)");
        }
        step("First lease row shows asset " + assetNo);
    }

    public void openFirstLease() {
        try {
            click(firstLeaseRow, "First Lease Row");
        } catch (StaleElementReferenceException e) {
            // the list refreshed between find and click, so try once more
            click(firstLeaseRow, "First Lease Row");
        }
    }

    // ===================== Lease details =====================

    public void clickAddManually() {
        click(addManuallyTab, "Add Manually Tab");
    }

    // ===================== Month =====================

    public void selectMonth(YearMonth month) {
        String monthShort = month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String expectedText = month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + month.getYear();

        click(monthTrigger, "Select Month field");
        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(monthPopup));

        // The picker shows one year at a time. Changing the year is not written yet.
        String popupText = popup.getText().replace("\n", " ");
        if (!popupText.contains(String.valueOf(month.getYear()))) {
            throw new RuntimeException("The month picker does not show the year " + month.getYear()
                    + " (picker text: '" + popupText + "'). Changing the year is not written yet.");
        }

        By monthButton = By.xpath("//div[@data-slot='popover-content']//button[normalize-space()='" + monthShort + "']");
        WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(monthButton));
        if (!button.isEnabled() || "true".equals(button.getAttribute("aria-disabled"))) {
            throw new RuntimeException("Month " + expectedText
                    + " is disabled in the picker (it is probably outside the lease period)");
        }
        button.click();
        step("Clicked on Month " + monthShort);

        // The field must now show the chosen month, for example "September 2026"
        wait.until(ExpectedConditions.textToBePresentInElementLocated(monthTrigger, expectedText));
        step("Month set to " + expectedText);
    }

    // ===================== Hours =====================

    // Fills the hour fields in every active row. breakdown and overtime can be null (then they are skipped).
    // Returns the number of rows that were filled.
    public int fillActiveRows(String timeIn, String timeOut, String breakdown, String overtime) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(timesheetRows));
        wait.until(ExpectedConditions.presenceOfElementLocated(activeRows));

        // First collect the dates of the active rows, then find each row again by its date
        List<String> dates = new ArrayList<>();
        for (WebElement row : driver.findElements(activeRows)) {
            dates.add(row.findElement(By.xpath("./td[1]")).getText().trim());
        }
        if (dates.isEmpty()) {
            throw new RuntimeException("No active (editable) rows found in the timesheet for this month");
        }
        step("Found " + dates.size() + " active rows: " + dates.get(0) + " to " + dates.get(dates.size() - 1));

        for (String date : dates) {
            String rowXPath = TS_TABLE + "//tbody/tr[./td[1][normalize-space()='" + date + "']]";
            typeHours(rowXPath, COL_TIME_IN, timeIn, "Time In", date);
            typeHours(rowXPath, COL_TIME_OUT, timeOut, "Time Out", date);
            if (breakdown != null) {
                typeHours(rowXPath, COL_BREAKDOWN, breakdown, "Breakdown", date);
            }
            if (overtime != null) {
                typeHours(rowXPath, COL_OVERTIME, overtime, "Overtime", date);
            }
            step("Filled " + date + ": Time In " + timeIn + ", Time Out " + timeOut
                    + (breakdown != null ? ", Breakdown " + breakdown : "")
                    + (overtime != null ? ", Overtime " + overtime : ""));
        }
        return dates.size();
    }

    private void typeHours(String rowXPath, int column, String value, String fieldName, String date) {
        By input = By.xpath(rowXPath + "/td[" + column + "]//input");
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(input));
        element.clear();
        element.sendKeys(value);
        String actual = element.getAttribute("value");
        if (actual == null || actual.isEmpty()) {
            throw new RuntimeException(fieldName + " on " + date + " was not accepted (typed '"
                    + value + "' but the field is empty)");
        }
    }

    // ===================== Submit =====================

    public void clickSubmit() {
        click(submitButton, "Submit Button");
    }

    public void waitForFormToClose() {
        waitUntilGone(submitButton, 15,
                "the Monthly Log Form to close after Submit (check for a validation message)");
    }
}