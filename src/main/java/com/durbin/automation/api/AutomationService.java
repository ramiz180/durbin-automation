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

        // fallback â†’ only first line (removes stacktrace)
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

        // âœ… IMPROVED: Extract WHICH element timed out
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
    public ApiResponse runFormAutomation() {
        long start = System.currentTimeMillis();
        ExecutionResultHolder.clear();
        com.durbin.automation.utils.ExecutionLogCollector.start();
        try {
            logger.info("[TEST START] Form Automation | Started at " + new java.util.Date());
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList("src/test/resources/testng.xml"));
            testng.addListener(new TestExecutionListener());
            testng.run();
            long executionTime = System.currentTimeMillis() - start;

            flushAndWait();

            String status = (testng.hasFailure() || testng.hasSkip()) ? "FAILED" : "PASSED";
            String failureReason = ExecutionResultHolder.getFailureReason();
            String message = "PASSED".equals(status) ? "Form automation executed successfully"
                    : sanitizeMessage(failureReason);

            if ("FAILED".equals(status)) {
                logger.error("[TEST FAILED] Form Automation | Reason: " + message);
                String cleanReason = extractCleanMessage(failureReason);
                com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanReason);
            } else {
                logger.info("[TEST PASSED] Form Automation | Completed in " + executionTime + "ms");
            }
            flushAndWait();

            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse(status, message, executionTime, logs, null);

            if ("FAILED".equalsIgnoreCase(status)) {
                response.setError(buildErrorDetails(failureReason));
            } else {
                // âœ… Attach Application ID if captured during form submission
                String capturedAppId = ExecutionResultHolder.getCapturedApplicationId();
                if (capturedAppId != null) {
                    response.setApplicationId(capturedAppId);
                    logger.info("Returning Application ID in response: " + capturedAppId);
                }
            }
            return response;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            logger.error("[EXCEPTION] Form Automation | " + e.getMessage(), e);
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

    public ApiResponse runHealthCheck() {
        long start = System.currentTimeMillis();
        ExecutionResultHolder.clear();
        com.durbin.automation.utils.ExecutionLogCollector.start();
        try {
            logger.info("[TEST START] Health Check | Started at " + new java.util.Date());
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList("src/test/resources/testng-health.xml"));
            testng.addListener(new TestExecutionListener());
            testng.run();
            long executionTime = System.currentTimeMillis() - start;

            flushAndWait();

            String status = (testng.hasFailure() || testng.hasSkip()) ? "FAILED" : "PASSED";
            String failureReason = ExecutionResultHolder.getFailureReason();
            String message = "PASSED".equals(status) ? "Health check executed successfully"
                    : sanitizeMessage(failureReason);

            if ("FAILED".equals(status)) {
                logger.error("[TEST FAILED] Health Check | Reason: " + message);
                String cleanReason = extractCleanMessage(failureReason);
                com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanReason);
            } else {
                logger.info("[TEST PASSED] Health Check | Completed in " + executionTime + "ms");
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
            logger.error("[EXCEPTION] Health Check | " + e.getMessage(), e);
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

    public ApiResponse runDownloadApplicationTest() {
        long start = System.currentTimeMillis();
        ExecutionResultHolder.clear();
        com.durbin.automation.utils.ExecutionLogCollector.start();
        try {
            logger.info("[TEST START] Download Application | Started at " + new java.util.Date());
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList("src/test/resources/testng-download.xml"));
            testng.addListener(new TestExecutionListener());
            testng.run();
            long executionTime = System.currentTimeMillis() - start;

            flushAndWait();

            String status = (testng.hasFailure() || testng.hasSkip()) ? "FAILED" : "PASSED";
            String failureReason = ExecutionResultHolder.getFailureReason();
            String message = "PASSED".equals(status) ? "Download triggered successfully"
                    : sanitizeMessage(failureReason);

            if ("FAILED".equals(status)) {
                logger.error("[TEST FAILED] Download Application | Reason: " + message);
                String cleanReason = extractCleanMessage(failureReason);
                com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: " + cleanReason);
            } else {
                logger.info("[TEST PASSED] Download Application | Completed in " + executionTime + "ms");
            }
            flushAndWait();

            List<String> logs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
            ApiResponse response = new ApiResponse(status, message, executionTime, logs, null);

            if ("PASSED".equalsIgnoreCase(status)) {
                String capturedId = ExecutionResultHolder.getCapturedApplicationId();
                if (capturedId != null) {
                    response.setApplicationId(capturedId);
                }
                String filePath = ExecutionResultHolder.getDownloadedFilePath();
                if (filePath != null) {
                    response.setFilePath(filePath);
                }
                String fileBase64 = ExecutionResultHolder.getDownloadedFileBase64();
                if (fileBase64 != null) {
                    try {
                        String[] encrypted = com.durbin.automation.utils.EncryptionUtils.encrypt(fileBase64);
                        response.setFileBase64(encrypted[0]);
                        response.setFileContentType("application/pdf");
                        response.setEncryptionIv(encrypted[1]);
                    } catch (Exception e) {
                        logger.error("Failed to encrypt PDF content", e);
                        com.durbin.automation.utils.ExecutionLogCollector.log("ERROR: Failed to encrypt PDF document");

                        List<String> encryptionLogs = com.durbin.automation.utils.ExecutionLogCollector.getLogLines();
                        ApiResponse errorResponse = new ApiResponse(
                "FAILED",
                "Failed to encrypt PDF document",
                executionTime,
                encryptionLogs,
                null);

    errorResponse.setApplicationId(response.getApplicationId());
    errorResponse.setFilePath(response.getFilePath());
    errorResponse.setError(new ErrorDetails(
            "ENCRYPTION_FAILED",
            "Failed to encrypt PDF document",
            "Verify DOCUMENT_ENCRYPTION_KEY configuration"));

    return errorResponse;
                    }
                }
            } else {
                response.setError(buildErrorDetails(failureReason));
            }

            return response;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            logger.error("[EXCEPTION] Download Application | " + e.getMessage(), e);
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
}
