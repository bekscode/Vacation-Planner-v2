package com.romang.vacationplanner.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {


    private static final int DEFAULT_LOG_ROUNDS = 12;


    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password required");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(DEFAULT_LOG_ROUNDS));
    }

    public static String hashPassword(String plainTextPassword, int logRounds) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password required");
        }
        if (logRounds < 4 || logRounds > 31) {
            throw new IllegalArgumentException("Log rounds must be between 4 and 31");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(logRounds));
    }

    // Verify against hashed password
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
