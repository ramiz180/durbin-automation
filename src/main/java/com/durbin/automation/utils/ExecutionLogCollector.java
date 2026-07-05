package com.durbin.automation.utils;

import java.util.ArrayList;
import java.util.List;

public class ExecutionLogCollector {

    private static final ThreadLocal<List<String>> logs = new ThreadLocal<>();

    public static void start() {
        logs.set(new ArrayList<>());
    }

    public static void log(String message) {
        List<String> list = logs.get();
        if (list != null) {
            String time = java.time.LocalTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            list.add(time + " - " + message);
        }
    }

    public static String getLogs() {
        List<String> list = logs.get();
        if (list == null || list.isEmpty())
            return "No logs captured";
        // Return as array-friendly string â€” each step on its own line
        return String.join("\n", list);
    }

    // âœ… NEW: Returns as JSON array of strings for clean API response
    public static List<String> getLogLines() {
        List<String> list = logs.get();
        if (list == null || list.isEmpty()) {
            return java.util.Collections.singletonList("No logs captured");
        }
        return new ArrayList<>(list);
    }

    public static void clear() {
        logs.remove();
    }
}
