package com.durbin.automation.api;

/**
 * Structured error details for API failure responses.
 */
public class ErrorDetails {

    private String errorType;
    private String reason;
    private String suggestion;

    public ErrorDetails(String errorType, String reason, String suggestion) {
        this.errorType = errorType;
        this.reason = reason;
        this.suggestion = suggestion;
    }

    public String getErrorType() { return errorType; }
    public String getReason() { return reason; }
    public String getSuggestion() { return suggestion; }
}

