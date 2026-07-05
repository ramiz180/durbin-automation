package com.durbin.automation.api;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

public class ApiResponse {

    private String status;
    private String message;
    private long executionTime;
    // private String logs;
    private List<String> logs;
    private List<String> logLines;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorDetails error;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String applicationId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String filePath;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fileBase64;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fileContentType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String encryptionIv;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean finalSubmission;

    /** Original constructor â€” kept for backward compatibility. */
    public ApiResponse(String status, String message, long executionTime) {
        this.status = status;
        this.message = message;
        this.executionTime = executionTime;
    }

    /** Extended constructor with log attachment. */
    public ApiResponse(String status, String message, long executionTime, List<String> logs) {
        this.status = status;
        this.message = message;
        this.executionTime = executionTime;
        this.logs = logs;
    }

    /** Full constructor with structured log lines. */
    public ApiResponse(String status, String message, long executionTime, List<String> logs, List<String> logLines) {
        this.status = status;
        this.message = message;
        this.executionTime = executionTime;
        this.logs = logs;
        this.logLines = logLines;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public List<String> getLogs() {
        return logs;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public List<String> getLogLines() {
        return logLines;
    }

    public ErrorDetails getError() {
        return error;
    }

    public void setError(ErrorDetails error) {
        this.error = error;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileBase64() {
        return fileBase64;
    }

    public void setFileBase64(String fileBase64) {
        this.fileBase64 = fileBase64;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getEncryptionIv() {
        return encryptionIv;
    }

    public void setEncryptionIv(String encryptionIv) {
        this.encryptionIv = encryptionIv;
    }

    public Boolean getFinalSubmission() {
        return finalSubmission;
    }

    public void setFinalSubmission(Boolean finalSubmission) {
        this.finalSubmission = finalSubmission;
    }
}

