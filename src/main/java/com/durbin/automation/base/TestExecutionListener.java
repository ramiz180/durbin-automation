package com.durbin.automation.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.durbin.automation.api.ExecutionResultHolder;

public class TestExecutionListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestExecutionListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        logger.info("[TEST START] " + testName + " [" + className + "]");
        com.durbin.automation.utils.ExecutionLogCollector.log("[TEST START] " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        logger.info("[TEST PASSED] " + testName + " [" + className + "]");
        com.durbin.automation.utils.ExecutionLogCollector.log("[TEST PASSED] " + testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        Throwable throwable = result.getThrowable();

        String failureMessage = (throwable != null) ? throwable.getMessage() : "Unknown failure";

        logger.error("[TEST FAILED] " + testName + " [" + className + "]");
        logger.error("[TEST FAILED] Reason: " + failureMessage);
        if (throwable != null) {
            logger.error("[EXCEPTION] Full stacktrace:", throwable);
        }

        String cleanReason = (failureMessage != null)
                ? failureMessage.split("\\n")[0]
                : "Unknown failure";
        com.durbin.automation.utils.ExecutionLogCollector.log(
                "[TEST FAILED] " + testName + " - " + cleanReason);

        // Store failure reason for API response
        ExecutionResultHolder.setFailureReason(failureMessage);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        String skipReason = (throwable != null) ? throwable.getMessage() : "Unknown skip reason";

        logger.warn("[TEST SKIPPED] " + testName + " - Reason: " + skipReason);

        ExecutionResultHolder.setFailureReason("SKIPPED: " + skipReason);
    }
}

