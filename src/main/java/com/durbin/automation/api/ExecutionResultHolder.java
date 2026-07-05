package com.durbin.automation.api;

/**
 * Thread-safe holder for capturing TestNG execution failure reasons.
 * Written by TestExecutionListener, read by AutomationService.
 * Must be cleared before each execution via clear().
 */
public class ExecutionResultHolder {

    private static final ThreadLocal<String> failureReason = new ThreadLocal<>();
    private static final ThreadLocal<String> capturedApplicationId = new ThreadLocal<>();
    private static final ThreadLocal<String> downloadedFilePath = new ThreadLocal<>();
    private static final ThreadLocal<String> downloadedFileBase64 = new ThreadLocal<>();

    public static void setDownloadedFilePath(String path) {
        downloadedFilePath.set(path);
    }

    public static String getDownloadedFilePath() {
        return downloadedFilePath.get();
    }

    public static void setDownloadedFileBase64(String base64) {
        downloadedFileBase64.set(base64);
    }

    public static String getDownloadedFileBase64() {
        return downloadedFileBase64.get();
    }

    /**
     * Sets the failure reason. Synchronized to prevent race conditions
     * when multiple test methods fail concurrently.
     * Keeps the first failure reason (most relevant root cause).
     */
    public static void setFailureReason(String reason) {
        if (failureReason.get() == null) {
            failureReason.set(reason);
        }
    }

    public static String getFailureReason() {
        return failureReason.get();
    }

    public static void setCapturedApplicationId(String id) {
        capturedApplicationId.set(id);
    }

    public static String getCapturedApplicationId() {
        return capturedApplicationId.get();
    }

    public static void clear() {
        failureReason.remove();
        capturedApplicationId.remove();
        downloadedFilePath.remove();
        downloadedFileBase64.remove();
    }
}

