package com.modular.blockchain.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public enum Level { INFO, ERROR, DEBUG }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(Level level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] [" + level + "] " + message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    public static void debug(String message) {
        log(Level.DEBUG, message);
    }
}

