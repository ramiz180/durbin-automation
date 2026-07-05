package com.durbin.automation.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.ThreadContext;
import com.durbin.automation.utils.ExecutionLogManager;

@RestController
@RequestMapping("/automation")
public class AutomationController {

    @PostMapping("/run-esb-form")
    public ResponseEntity<ApiResponse> runForm(@RequestBody JsonNode payload) {
        String username = "unknown_user";

        if (payload != null
                && payload.has("form001")
                && payload.get("form001").has("customer")
                && payload.get("form001").get("customer").has("name")) {
            username = payload.get("form001")
                    .get("customer")
                    .get("name")
                    .asText();
        }

        username = username.replaceAll("[^a-zA-Z0-9]", "_");

        String logFilePath = ExecutionLogManager.startLog(username);
        ThreadContext.put("logFileName", logFilePath);

        try {
            com.durbin.automation.utils.ExecutionLogCollector.start();
            TestDataHolder.setData(payload);
            AutomationService automationService = new AutomationService();
            ApiResponse response = automationService.runFormAutomation();
            response.setFinalSubmission(com.durbin.automation.utils.EnvReader.getBoolean("finalSubmission"));

            if ("FAILED".equalsIgnoreCase(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            return ResponseEntity.ok(response);
        } finally {
            com.durbin.automation.utils.ExecutionLogCollector.clear();
            ThreadContext.clearAll();
            ExecutionLogManager.clear();
            TestDataHolder.clear();
            ExecutionResultHolder.clear();
        }
    }

    @PostMapping("/run-login")
    public ResponseEntity<ApiResponse> runLoginTest(@RequestBody JsonNode payload) {
        String username = "login";
        String logFilePath = ExecutionLogManager.startLog(username);
        ThreadContext.put("logFileName", logFilePath);

        try {
            com.durbin.automation.utils.ExecutionLogCollector.start();
            TestDataHolder.setData(payload);
            AutomationService automationService = new AutomationService();
            ApiResponse response = automationService.runLoginTest();

            if ("FAILED".equalsIgnoreCase(response.getStatus())) {
                String msg = response.getMessage() != null ? response.getMessage().toLowerCase() : "";
                if (msg.contains("invalid username") || msg.contains("invalid password")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            return ResponseEntity.ok(response);
        } finally {
            com.durbin.automation.utils.ExecutionLogCollector.clear();
            ThreadContext.clearAll();
            ExecutionLogManager.clear();
            TestDataHolder.clear();
            ExecutionResultHolder.clear();
        }
    }

    @GetMapping("/run-health-check")
    public ResponseEntity<ApiResponse> runHealthCheck() {
        String username = "healthcheck";
        String logFilePath = ExecutionLogManager.startLog(username);
        ThreadContext.put("logFileName", logFilePath);

        try {
            com.durbin.automation.utils.ExecutionLogCollector.start();
            AutomationService automationService = new AutomationService();
            ApiResponse response = automationService.runHealthCheck();

            if ("FAILED".equalsIgnoreCase(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            return ResponseEntity.ok(response);
        } finally {
            com.durbin.automation.utils.ExecutionLogCollector.clear();
            ThreadContext.clearAll();
            ExecutionLogManager.clear();
            TestDataHolder.clear();
            ExecutionResultHolder.clear();
        }
    }

    @PostMapping("/download-application")
    public ResponseEntity<ApiResponse> runDownloadApplicationTest(@RequestBody JsonNode payload) {
        String username = "download_app";

        if (payload != null && payload.has("login") && payload.get("login").has("username")) {
            username = payload.get("login").get("username").asText().replaceAll("[^a-zA-Z0-9]", "_");
        }

        String logFilePath = ExecutionLogManager.startLog(username);
        ThreadContext.put("logFileName", logFilePath);

        try {
            com.durbin.automation.utils.ExecutionLogCollector.start();
            TestDataHolder.setData(payload);
            AutomationService automationService = new AutomationService();
            ApiResponse response = automationService.runDownloadApplicationTest();

            if ("FAILED".equalsIgnoreCase(response.getStatus())) {
                String msg = response.getMessage() != null ? response.getMessage().toLowerCase() : "";
                if (msg.contains("invalid username") || msg.contains("invalid password")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            return ResponseEntity.ok(response);
        } finally {
            com.durbin.automation.utils.ExecutionLogCollector.clear();
            ThreadContext.clearAll();
            ExecutionLogManager.clear();
            TestDataHolder.clear();
        }
    }

    @PostMapping("/run-create-asset")
    public ResponseEntity<ApiResponse> runCreateAssetTest(@RequestBody JsonNode payload) {
        String username = "create_asset";
        if (payload != null && payload.has("login") && payload.get("login").has("username")) {
            username = payload.get("login").get("username").asText().replaceAll("[^a-zA-Z0-9]", "_");
        }
        String logFilePath = ExecutionLogManager.startLog(username);
        ThreadContext.put("logFileName", logFilePath);

        try {
            com.durbin.automation.utils.ExecutionLogCollector.start();
            TestDataHolder.setData(payload);
            AutomationService automationService = new AutomationService();
            ApiResponse response = automationService.runCreateAssetTest();

            if ("FAILED".equalsIgnoreCase(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            return ResponseEntity.ok(response);
        } finally {
            com.durbin.automation.utils.ExecutionLogCollector.clear();
            ThreadContext.clearAll();
            ExecutionLogManager.clear();
            TestDataHolder.clear();
            ExecutionResultHolder.clear();
        }
    }
}

