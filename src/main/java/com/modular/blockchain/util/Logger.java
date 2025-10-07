package com.modular.blockchain.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger utility class for handling application logging
 * Supports logging to console and file with different severity levels
 */
public class Logger {
    /** Enum defining available logging levels */
    public enum Level { INFO, ERROR, DEBUG }

    // Date/time format for log timestamps
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // Directory where log files are stored
    private static final String LOG_DIR = "logs";
    // Full path to the log file
    private static final String LOG_FILE = LOG_DIR + File.separator + "server.log";
    // Flag to track if log directory has been created
    private static boolean logDirCreated = false;

    /**
     * Ensures the logging directory exists, creates it if necessary
     * Only runs directory creation once per session
     */
    private static void ensureLogDir() {
        if (!logDirCreated) {
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                var results = dir.mkdirs();
                System.out.printf("Directory creation result: %s\n", results);
            }
            logDirCreated = true;
        }
    }

    /**
     * Main logging method that handles both console and file output
     * @param level Severity level of the log message
     * @param message Content to be logged
     */
    public static void log(Level level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMsg = "[" + timestamp + "] [" + level + "] " + message;
        System.out.println(logMsg);
        // Only write non-debug messages to file
        if (level != Level.DEBUG) writeToFile(logMsg);
    }

    /**
     * Writes a log message to the log file
     * @param logMsg Formatted log message to write
     */
    private static void writeToFile(String logMsg) {
        ensureLogDir();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logMsg);
            writer.newLine();
        } catch (IOException e) {
            // If logging to file fails, print error to console
            System.err.println("[Logger] Failed to write log: " + e.getMessage());
        }
    }

    /**
     * Convenience method for INFO level logging
     * @param message Message to log at INFO level
     */
    public static void info(String message) {
        log(Level.INFO, message);
    }

    /**
     * Convenience method for ERROR level logging
     * @param message Message to log at ERROR level
     */
    public static void error(String message) {
        log(Level.ERROR, message);
    }

    /**
     * Convenience method for DEBUG level logging
     * @param message Message to log at DEBUG level
     */
    public static void debug(String message) {
        log(Level.DEBUG, message);
    }
}