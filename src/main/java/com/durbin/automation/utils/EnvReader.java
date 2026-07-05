package com.durbin.automation.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Utility to read key-value pairs from a .env file at the project root.
 * Falls back to system environment variables if the .env file is missing
 * or does not contain the requested key.
 */
public class EnvReader {
    private static final Properties envProperties = new Properties();

    static {
        // Resolve .env relative to the working directory (project root)
        Path envPath = Paths.get(System.getProperty("user.dir"), ".env");
        try (BufferedReader reader = new BufferedReader(new FileReader(envPath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Skip blank lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String key = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();
                    envProperties.setProperty(key, value);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: .env file not found at " + envPath + ", falling back to system environment variables.");
        }
    }

    /**
     * Gets a value by key from the .env file.
     * Falls back to the system environment variable if not found in .env.
     */
    public static String get(String key) {
        String value = envProperties.getProperty(key);
        if (value == null || value.isEmpty()) {
            value = System.getenv(key);
        }
        return value;
    }

    /**
     * Gets a boolean value by key from the .env file or system environment.
     * Returns true only if the value is "true" (case-insensitive).
     * Defaults to false if the key is missing or null.
     */
    public static boolean getBoolean(String key) {
        String value = get(key);
        return value != null && value.equalsIgnoreCase("true");
    }
}

