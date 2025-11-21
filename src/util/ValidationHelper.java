package util;

import java.util.regex.Pattern;

/**
 * ValidationHelper provides utility methods for input validation
 * Demonstrates Single Responsibility Principle
 */
public class ValidationHelper {
    
    /**
     * Validates if a string is not null or empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return Pattern.matches(Constants.EMAIL_PATTERN, email);
    }
    
    /**
     * Validates phone number format (Pakistani format)
     */
    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) {
            return false;
        }
        return Pattern.matches(Constants.PHONE_PATTERN, phone);
    }
    
    /**
     * Validates username format
     */
    public static boolean isValidUsername(String username) {
        if (!isNotEmpty(username)) {
            return false;
        }
        return Pattern.matches(Constants.USERNAME_PATTERN, username);
    }
    
    /**
     * Validates password strength (minimum 6 characters)
     */
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 6;
    }
    
    /**
     * Validates if a number is positive
     */
    public static boolean isPositive(double number) {
        return number > 0;
    }
    
    /**
     * Validates if a number is within a range
     */
    public static boolean isInRange(int number, int min, int max) {
        return number >= min && number <= max;
    }
    
    /**
     * Validates if seat number is valid for bus capacity
     */
    public static boolean isValidSeatNumber(int seatNumber, int capacity) {
        return seatNumber > 0 && seatNumber <= capacity;
    }
    
    /**
     * Sanitizes input to prevent SQL injection (basic)
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove potentially dangerous characters
        return input.replaceAll("[';\"\\\\]", "").trim();
    }
    
    private ValidationHelper() {
        // Private constructor to prevent instantiation
    }
}
