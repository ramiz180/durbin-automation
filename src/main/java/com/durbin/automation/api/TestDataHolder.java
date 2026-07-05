package com.durbin.automation.api;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * NOTE:
 * This class uses ThreadLocal to store API request data per execution.
 *
 * This works because TestNG is executed synchronously in the SAME thread
 * as the Spring controller (no parallel execution).
 *
 * âš ï¸ IMPORTANT:
 * Do NOT enable TestNG parallel execution (parallel="tests" or methods)
 * without redesigning data handling, as ThreadLocal will not propagate
 * across different threads.
 */
public class TestDataHolder {
    private static final ThreadLocal<JsonNode> data = new ThreadLocal<>();

    public static void setData(JsonNode json) {
        data.set(json);
    }

    public static JsonNode getData() {
        return data.get();
    }

    public static JsonNode getNode(String key) {
        JsonNode current = data.get();
        if (current != null && current.has(key)) {
            return current.get(key);
        }
        return null;
    }

    public static void clear() {
        data.remove();
    }
}
