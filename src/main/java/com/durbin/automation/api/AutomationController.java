package com.durbin.automation.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.ThreadContext;
import com.durbin.automation.utils.ExecutionLogManager;

@RestController
@RequestMapping("/automation")
public class AutomationController {

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

    @PostMapping("/run-create-lease")
    public ResponseEntity<ApiResponse> runCreateLeaseTest(@RequestBody JsonNode payload) {
        String username = "create_lease";
        if (payload != null && payload.has("login") && payload.get("login").has("username")) {
            username = payload.get("login").get("username").asText().replaceAll("[^a-zA-Z0-9]", "_");
        }
        String logFilePath = ExecutionLogManager.startLog(username);
        ThreadContext.put("logFileName", logFilePath);

        try {
            com.durbin.automation.utils.ExecutionLogCollector.start();
            TestDataHolder.setData(payload);
            AutomationService automationService = new AutomationService();
            ApiResponse response = automationService.runCreateLeaseTest();

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

    @PostMapping("/run-create-operator")
    public ResponseEntity<ApiResponse> runCreateOperatorTest(@RequestBody JsonNode payload) {
        String username = "create_operator";
        if (payload != null && payload.has("login") && payload.get("login").has("username")) {
            username = payload.get("login").get("username").asText().replaceAll("[^a-zA-Z0-9]", "_");
        }
        String logFilePath = ExecutionLogManager.startLog(username);
        ThreadContext.put("logFileName", logFilePath);

        try {
            com.durbin.automation.utils.ExecutionLogCollector.start();
            TestDataHolder.setData(payload);
            AutomationService automationService = new AutomationService();
            ApiResponse response = automationService.runCreateOperatorTest();

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

    @PostMapping("/run-create-maintenance")
    public ResponseEntity<ApiResponse> runCreateMaintenanceTest(@RequestBody JsonNode payload) {
        String username = "create_maintenance";
        if (payload != null && payload.has("login") && payload.get("login").has("username")) {
            username = payload.get("login").get("username").asText().replaceAll("[^a-zA-Z0-9]", "_");
        }
        String logFilePath = ExecutionLogManager.startLog(username);
        ThreadContext.put("logFileName", logFilePath);

        try {
            com.durbin.automation.utils.ExecutionLogCollector.start();
            TestDataHolder.setData(payload);
            AutomationService automationService = new AutomationService();
            ApiResponse response = automationService.runCreateMaintenanceTest();

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

    @PostMapping("/run-fill-timesheet")
    public ResponseEntity<ApiResponse> runFillTimesheetTest(@RequestBody JsonNode payload) {
        String username = "fill_timesheet";
        if (payload != null && payload.has("login") && payload.get("login").has("username")) {
            username = payload.get("login").get("username").asText().replaceAll("[^a-zA-Z0-9]", "_");
        }
        String logFilePath = ExecutionLogManager.startLog(username);
        ThreadContext.put("logFileName", logFilePath);

        try {
            com.durbin.automation.utils.ExecutionLogCollector.start();
            TestDataHolder.setData(payload);
            AutomationService automationService = new AutomationService();
            ApiResponse response = automationService.runFillTimesheetTest();

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

    // Shared handler for new endpoints. Same set-up and clean-up as the other
    // endpoints.
    private ResponseEntity<ApiResponse> execute(JsonNode payload, String defaultUser,
            java.util.function.Supplier<ApiResponse> call) {
        String username = defaultUser;
        if (payload != null && payload.has("login") && payload.get("login").has("username")) {
            username = payload.get("login").get("username").asText().replaceAll("[^a-zA-Z0-9]", "_");
        }
        String logFilePath = ExecutionLogManager.startLog(username);
        ThreadContext.put("logFileName", logFilePath);

        try {
            com.durbin.automation.utils.ExecutionLogCollector.start();
            TestDataHolder.setData(payload);
            ApiResponse response = call.get();

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

    @PostMapping("/run-erp-login")
    public ResponseEntity<ApiResponse> runErpLoginTest(@RequestBody JsonNode payload) {
        return execute(payload, "erp_login", () -> new AutomationService().runErpLoginTest());
    }

    @PostMapping("/run-create-visit")
    public ResponseEntity<ApiResponse> runCreateVisitTest(@RequestBody JsonNode payload) {
        return execute(payload, "create_visit", () -> new AutomationService().runCreateVisitTest());
    }
}