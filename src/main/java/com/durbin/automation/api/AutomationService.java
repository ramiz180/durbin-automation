package com.durbin.automation.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.testng.TestNG;
import com.durbin.automation.base.TestExecutionListener;
import java.util.Collections;
import java.util.List;

public class AutomationService {

    private static final Logger logger = LogManager.getLogger(AutomationService.class);

    private static final long LOG_FLUSH_DELAY_MS = 2000;

    private void flushLogs() {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            context.getConfiguration().getAppenders().values().forEach(appender -> {
                if (appender instanceof org.apache.logging.log4j.core.appender.FileAppender) {
                    ((org.apache.logging.log4j.core.appender.FileAppender) appender).getManager().flush();
                }
            });
        } catch (Exception e) {
            // Swallow
        }
    }

    private void flushAndWait() {
        flushLogs();
        try {
            Thread.sleep(LOG_FLUSH_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String sanitizeMessage(String rawMessage) {
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            return "Unknown failure - check logs for details";
        }
        String firstLine = rawMessage.split("\\r?\\n")[0].trim();
        if (firstLine.length() > 500) {
            firstLine = firstLine.substring(0, 500) + "...";
        }
        return firstLine;
    }

    private String extractCleanMessage(String raw) {
        if (raw == null || raw.isEmpty())
            return "Unknown error";

        String lower = raw.toLowerCase();

        if (lower.contains("expected condition failed")) {
            return "Element not found within timeout";
        }
        if (lower.contains("no such element")) {
            return "Element not found";
        }
        if (lower.contains("timeout")) {
            return "Operation timed out";
        }
        if (lower.contains("invalid username") || lower.contains("invalid password")) {
            return "Invalid username or password";
        }

        // fallback -> only first line (removes stacktrace)
        return raw.split("\\n")[0];
    }

    private ErrorDetails buildErrorDetails(String reason) {
        if (reason == null) {
            return new ErrorDetails("UNKNOWN", "Unknown failure", "Check logs");
        }

        String lower = reason.toLowerCase();

        if (lower.contains("invalid username") || lower.contains("invalid password")) {
            return new ErrorDetails(
                    "AUTHENTICATION_FAILED",
                    "Invalid username or password",
                    "Verify credentials in API payload");
        }

        // Extract WHICH element timed out
        if (lower.contains("expected condition failed") || lower.contains("timeout")) {
            String elementDetail = "unknown element";

            // Selenium puts the locator between "waiting for ... located by" and "(tried
            // for"
            if (reason.contains("located by")) {
                int start = reason.indexOf("located by") + "located by".length();
                int end = reason.contains("(tried for")
                        ? reason.indexOf("(tried for")
                        : Math.min(start + 150, reason.length());
                if (start > 0 && end > start) {
                    elementDetail = reason.substring(start, end).trim();
                }
            } else if (reason.contains("waiting for")) {
                int start = reason.indexOf("waiting for") + "waiting for".length();
                int end = reason.contains("(tried for")
                        ? reason.indexOf("(tried for")
                        : Math.min(start + 150, reason.length());
                if (start > 0 && end > start) {
                    elementDetail = reason.substring(start, end).trim();
                }
            }

            return new ErrorDetails(
                    "TIMEOUT",
                    "Timed out waiting for: " + elementDetail,
                    "Verify element exists on page and locator is correct");
        }

        if (lower.contains("no such element")) {
            // Extract the selector from "no such element: Unable to locate element: {...}"
            String selector = "unknown";
            if (reason.contains("locate element:")) {
                int start = reason.indexOf("locate element:") + "locate element:".length();
                selector = reason.substring(start, Math.min(start + 100, reason.length())).trim();
            }
            return new ErrorDetails(
                    "ELEMENT_NOT_FOUND",
                    "Element not found: " + selector,
                    "Check XPath or CSS selector");
        }

        return new ErrorDetails(
                "AUTOMATION_FAILURE",
                reason.split("\\n")[0], // only first line
                "Check logs for full details");
    }

    // NOTE:
    // TestNG is executed synchronously in the same thread.
    // This ensures ThreadLocal data (TestDataHolder) is accessible.
    // Do NOT enable parallel execution without redesign.

    public ApiResponse runLoginTest() {
        long start = System.currentTimeMillis();
        ExecutionResultHolder.clear();
        com.durbin.automation.utils.ExecutionLogCollector.start();
        try {
            logger.info("[TEST START] Login Test | Started at " + new java.util.Date());
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList("src/test/resources/testng-login.xml"));
            testng.addListener(new TestExecutionListener());
            testng.run();
            long executionTime = System.currentTimeMillis() - start;

            flushAndWait();

            String status = (testng.hasFailure() || testng.hasSkip()) ? "FAILED" : "PASSED";
            String failureReason = ExecutionResultHolder.getFailureReason();
            String message = "PASSED".equals(status) ? "Login test executed successfully"
                    : sanitizeMessage(failureReason);

            if ("FAILED".equals(status)) {
                logger.error("[TEST FAILED] Login Test | Reason: " + message);
                String cleanReason = extractCleanMessage(failureReason);
                com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanReason);
            } else {
                logger.info("[TEST PASSED] Login Test | Completed in " + executionTime + "ms");
            }
            flushAndWait();

            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse(status, message, executionTime, logs, null);

            if ("FAILED".equalsIgnoreCase(status)) {
                response.setError(buildErrorDetails(failureReason));
            }
            return response;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            logger.error("[EXCEPTION] Login Test | " + e.getMessage(), e);
            flushAndWait();

            String cleanException = extractCleanMessage(e.getMessage());
            com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanException);
            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse("FAILED", sanitizeMessage(e.getMessage()), executionTime, logs,
                    null);
            response.setError(buildErrorDetails(e.getMessage()));
            return response;
        }
    }

    public ApiResponse runCreateAssetTest() {
        long start = System.currentTimeMillis();
        ExecutionResultHolder.clear();
        com.durbin.automation.utils.ExecutionLogCollector.start();
        try {
            logger.info("[TEST START] Create Asset Test | Started at " + new java.util.Date());
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList("src/test/resources/testng-create-asset.xml"));
            testng.addListener(new TestExecutionListener());
            testng.run();
            long executionTime = System.currentTimeMillis() - start;

            flushAndWait();

            String status = (testng.hasFailure() || testng.hasSkip()) ? "FAILED" : "PASSED";
            String failureReason = ExecutionResultHolder.getFailureReason();
            String message = "PASSED".equals(status) ? "Create Asset test executed successfully"
                    : sanitizeMessage(failureReason);

            if ("FAILED".equals(status)) {
                logger.error("[TEST FAILED] Create Asset Test | Reason: " + message);
                String cleanReason = extractCleanMessage(failureReason);
                com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanReason);
            } else {
                logger.info("[TEST PASSED] Create Asset Test | Completed in " + executionTime + "ms");
            }
            flushAndWait();

            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse(status, message, executionTime, logs, null);

            if ("FAILED".equalsIgnoreCase(status)) {
                response.setError(buildErrorDetails(failureReason));
            }
            return response;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            logger.error("[EXCEPTION] Create Asset Test | " + e.getMessage(), e);
            flushAndWait();

            String cleanException = extractCleanMessage(e.getMessage());
            com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanException);
            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse("FAILED", sanitizeMessage(e.getMessage()), executionTime, logs,
                    null);
            response.setError(buildErrorDetails(e.getMessage()));
            return response;
        }
    }

    public ApiResponse runCreateLeaseTest() {
        long start = System.currentTimeMillis();
        ExecutionResultHolder.clear();
        com.durbin.automation.utils.ExecutionLogCollector.start();
        try {
            logger.info("[TEST START] Create Lease Test | Started at " + new java.util.Date());
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList("src/test/resources/testng-create-lease.xml"));
            testng.addListener(new TestExecutionListener());
            testng.run();
            long executionTime = System.currentTimeMillis() - start;

            flushAndWait();

            String status = (testng.hasFailure() || testng.hasSkip()) ? "FAILED" : "PASSED";
            String failureReason = ExecutionResultHolder.getFailureReason();
            String message = "PASSED".equals(status) ? "Create Lease test executed successfully"
                    : sanitizeMessage(failureReason);

            if ("FAILED".equals(status)) {
                logger.error("[TEST FAILED] Create Lease Test | Reason: " + message);
                String cleanReason = extractCleanMessage(failureReason);
                com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanReason);
            } else {
                logger.info("[TEST PASSED] Create Lease Test | Completed in " + executionTime + "ms");
            }
            flushAndWait();

            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse(status, message, executionTime, logs, null);

            if ("FAILED".equalsIgnoreCase(status)) {
                response.setError(buildErrorDetails(failureReason));
            }
            return response;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            logger.error("[EXCEPTION] Create Lease Test | " + e.getMessage(), e);
            flushAndWait();

            String cleanException = extractCleanMessage(e.getMessage());
            com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanException);
            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse("FAILED", sanitizeMessage(e.getMessage()), executionTime, logs,
                    null);
            response.setError(buildErrorDetails(e.getMessage()));
            return response;
        }
    }

        public ApiResponse runCreateOperatorTest() {
        long start = System.currentTimeMillis();
        ExecutionResultHolder.clear();
        com.durbin.automation.utils.ExecutionLogCollector.start();
        try {
            logger.info("[TEST START] Create Operator Test | Started at " + new java.util.Date());
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList("src/test/resources/testng-create-operator.xml"));
            testng.addListener(new TestExecutionListener());
            testng.run();
            long executionTime = System.currentTimeMillis() - start;

            flushAndWait();

            String status = (testng.hasFailure() || testng.hasSkip()) ? "FAILED" : "PASSED";
            String failureReason = ExecutionResultHolder.getFailureReason();
            String message = "PASSED".equals(status) ? "Create Operator test executed successfully"
                    : sanitizeMessage(failureReason);

            if ("FAILED".equals(status)) {
                logger.error("[TEST FAILED] Create Operator Test | Reason: " + message);
                String cleanReason = extractCleanMessage(failureReason);
                com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanReason);
            } else {
                logger.info("[TEST PASSED] Create Operator Test | Completed in " + executionTime + "ms");
            }
            flushAndWait();

            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse(status, message, executionTime, logs, null);

            if ("FAILED".equalsIgnoreCase(status)) {
                response.setError(buildErrorDetails(failureReason));
            }
            return response;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            logger.error("[EXCEPTION] Create Operator Test | " + e.getMessage(), e);
            flushAndWait();

            String cleanException = extractCleanMessage(e.getMessage());
            com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanException);
            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse("FAILED", sanitizeMessage(e.getMessage()), executionTime, logs,
                    null);
            response.setError(buildErrorDetails(e.getMessage()));
            return response;
        }
    }
        public ApiResponse runCreateMaintenanceTest() {
        long start = System.currentTimeMillis();
        ExecutionResultHolder.clear();
        com.durbin.automation.utils.ExecutionLogCollector.start();
        try {
            logger.info("[TEST START] Create Maintenance Test | Started at " + new java.util.Date());
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList("src/test/resources/testng-create-maintenance.xml"));
            testng.addListener(new TestExecutionListener());
            testng.run();
            long executionTime = System.currentTimeMillis() - start;

            flushAndWait();

            String status = (testng.hasFailure() || testng.hasSkip()) ? "FAILED" : "PASSED";
            String failureReason = ExecutionResultHolder.getFailureReason();
            String message = "PASSED".equals(status) ? "Create Maintenance test executed successfully"
                    : sanitizeMessage(failureReason);

            if ("FAILED".equals(status)) {
                logger.error("[TEST FAILED] Create Maintenance Test | Reason: " + message);
                String cleanReason = extractCleanMessage(failureReason);
                com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanReason);
            } else {
                logger.info("[TEST PASSED] Create Maintenance Test | Completed in " + executionTime + "ms");
            }
            flushAndWait();

            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse(status, message, executionTime, logs, null);

            if ("FAILED".equalsIgnoreCase(status)) {
                response.setError(buildErrorDetails(failureReason));
            }
            return response;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            logger.error("[EXCEPTION] Create Maintenance Test | " + e.getMessage(), e);
            flushAndWait();

            String cleanException = extractCleanMessage(e.getMessage());
            com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanException);
            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse("FAILED", sanitizeMessage(e.getMessage()), executionTime, logs,
                    null);
            response.setError(buildErrorDetails(e.getMessage()));
            return response;
        }
    }

        public ApiResponse runFillTimesheetTest() {
        long start = System.currentTimeMillis();
        ExecutionResultHolder.clear();
        com.durbin.automation.utils.ExecutionLogCollector.start();
        try {
            logger.info("[TEST START] Fill Timesheet Test | Started at " + new java.util.Date());
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList("src/test/resources/testng-fill-timesheet.xml"));
            testng.addListener(new TestExecutionListener());
            testng.run();
            long executionTime = System.currentTimeMillis() - start;

            flushAndWait();

            String status = (testng.hasFailure() || testng.hasSkip()) ? "FAILED" : "PASSED";
            String failureReason = ExecutionResultHolder.getFailureReason();
            String message = "PASSED".equals(status) ? "Fill Timesheet test executed successfully"
                    : sanitizeMessage(failureReason);

            if ("FAILED".equals(status)) {
                logger.error("[TEST FAILED] Fill Timesheet Test | Reason: " + message);
                String cleanReason = extractCleanMessage(failureReason);
                com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanReason);
            } else {
                logger.info("[TEST PASSED] Fill Timesheet Test | Completed in " + executionTime + "ms");
            }
            flushAndWait();

            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse(status, message, executionTime, logs, null);

            if ("FAILED".equalsIgnoreCase(status)) {
                response.setError(buildErrorDetails(failureReason));
            }
            return response;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            logger.error("[EXCEPTION] Fill Timesheet Test | " + e.getMessage(), e);
            flushAndWait();

            String cleanException = extractCleanMessage(e.getMessage());
            com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanException);
            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse("FAILED", sanitizeMessage(e.getMessage()), executionTime, logs,
                    null);
            response.setError(buildErrorDetails(e.getMessage()));
            return response;
        }
    }
}
