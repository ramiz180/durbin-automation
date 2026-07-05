package com.durbin.automation.utils;

import java.io.File;
import java.time.LocalDate;

public class ExecutionLogManager {

    private static final ThreadLocal<String> currentLogFile = new ThreadLocal<>();

    public static String startLog(String identifier) {
        try {
            if (identifier == null || identifier.isEmpty()) {
                identifier = "default";
            }

            identifier = identifier.replaceAll("[^a-zA-Z0-9]", "_");

            String date = LocalDate.now().toString();

            String folderPath = System.getProperty("user.dir")
                    + File.separator + "logs"
                    + File.separator + date;

            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String time = java.time.LocalTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("HH-mm-ss"));

            String fileName = identifier + "_" + time + ".log";

            String fullPath = folderPath + File.separator + fileName;

            currentLogFile.set(fullPath);

            return fullPath;

        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentLogFile() {
        return currentLogFile.get();
    }

    public static void clear() {
        currentLogFile.remove();
    }
}

