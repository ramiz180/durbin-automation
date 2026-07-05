package com.durbin.automation.utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Centralized utility for reading automation log tails.
 * Designed to be safe â€” never throws, never crashes the API.
 * Uses RandomAccessFile to efficiently read only the last N lines
 * without loading the entire file into memory.
 */
public class LogUtils {

    private static final String LOG_FILE_PATH = System.getProperty("user.dir") + File.separator + "logs"
            + File.separator + "automation.log";
    private static final String NO_LOGS_MESSAGE = "No logs captured. Possible failure before logger initialization OR log path issue.";

    /**
     * Returns the last N meaningful lines from the automation log file
     * as a List, with stacktrace and framework noise filtered out.
     *
     * @param lines number of meaningful lines to return
     * @return filtered list of log lines, or a safe fallback
     */
    public static List<String> getLastLogLines(int lines) {
        try {
            String activePath = null;
            
            // 1. Try ThreadLocal
            String currentExecutionFile = ExecutionLogManager.getCurrentLogFile();
            
            // 2. If null, try MDC
            if (currentExecutionFile == null) {
                currentExecutionFile = org.apache.logging.log4j.ThreadContext.get("logFileName");
            }
            
            // 3. Final fallback
            activePath = (currentExecutionFile != null) ? currentExecutionFile : LOG_FILE_PATH;
            
            File logFile = new File(activePath);
            if (!logFile.exists() || logFile.length() == 0) {
                return Collections.singletonList(NO_LOGS_MESSAGE);
            }

            List<String> lastLines = readLastLines(logFile, lines * 3);
            List<String> cleanLines = new ArrayList<>();

            for (String line : lastLines) {
                if (line.contains("[TEST START]") ||
                    line.contains("[TEST PASSED]") ||
                    line.contains("[TEST FAILED]") ||
                    line.contains("Navigating") ||
                    line.contains("Login") ||
                    line.contains("Form") ||
                    line.contains("ERROR") ||
                    line.contains("WARN")) {

                    cleanLines.add(line);
                }
            }

            if (cleanLines.isEmpty()) {
                cleanLines = lastLines;
            }

            int maxLines = 15;
            int start = Math.max(0, cleanLines.size() - maxLines);
            return cleanLines.subList(start, cleanLines.size());

        } catch (Exception e) {
            return Collections.singletonList(NO_LOGS_MESSAGE);
        }
    }

    /**
     * Returns the last N lines from the automation log file as a string.
     * Now delegates to getLastLogLines for consistent noise filtering.
     *
     * @param lines number of lines to return
     * @return last N meaningful lines joined by newline, or a safe fallback message
     */
    public static String getLastLogs(int lines) {
        return String.join("\n", getLastLogLines(lines));
    }

    /**
     * Efficiently reads the last N lines from a file by seeking
     * backwards from the end. Avoids loading the entire file.
     */
    private static List<String> readLastLines(File file, int lineCount) throws Exception {
        List<String> lines = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long fileLength = raf.length();
            if (fileLength == 0) {
                return lines;
            }

            long pointer = fileLength - 1;
            int linesFound = 0;

            // Skip trailing newline if present
            raf.seek(pointer);
            if (raf.readByte() == '\n') {
                pointer--;
            }

            // Seek backwards counting newlines
            while (pointer >= 0 && linesFound < lineCount) {
                raf.seek(pointer);
                if (raf.readByte() == '\n') {
                    linesFound++;
                }
                pointer--;
            }

            // Position to start of the lines we want
            if (pointer < 0) {
                raf.seek(0);
            } else {
                raf.seek(pointer + 2); // +2 to skip the newline character
            }

            // Read lines from current position to end
            String line;
            while ((line = raf.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }
}


