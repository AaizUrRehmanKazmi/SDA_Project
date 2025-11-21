package app;

import dal.DatabaseConnection;
import ui.LoginFrame;
import javax.swing.*;

/**
 * Main class - Application entry point
 * Initializes the system and launches the login interface
 */
public class Main {
    
    public static void main(String[] args) {
        // Print application header
        printHeader();
        
        // Test database connection
        if (!testDatabaseConnection()) {
            showDatabaseError();
            return;
        }
        
        // Set Look and Feel
        setLookAndFeel();
        
        // Launch application on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                System.out.println("Application launched successfully!");
            } catch (Exception e) {
                System.err.println("Error launching application: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error launching application: " + e.getMessage(),
                    "Application Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Print application header to console
     */
    private static void printHeader() {
        System.out.println("========================================");
        System.out.println("   UZAIR TRANSPORT SYSTEM");
        System.out.println("   University Bus Management System");
        System.out.println("   Version 1.0");
        System.out.println("========================================");
        System.out.println();
    }
    
    /**
     * Test database connectivity
     */
    private static boolean testDatabaseConnection() {
        System.out.print("Testing database connection... ");
        
        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            boolean isConnected = dbConnection.testConnection();
            
            if (isConnected) {
                System.out.println("SUCCESS");
                System.out.println(dbConnection.getPoolStats());
                return true;
            } else {
                System.out.println("FAILED");
                return false;
            }
        } catch (Exception e) {
            System.out.println("FAILED");
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Show database error dialog
     */
    private static void showDatabaseError() {
        String errorMessage = "Failed to connect to the database.\n\n" +
                            "Please check:\n" +
                            "1. MySQL server is running\n" +
                            "2. Database 'uzair_transport' exists\n" +
                            "3. Database credentials in Constants.java are correct\n" +
                            "4. MySQL JDBC driver is in classpath\n\n" +
                            "Application will now exit.";
        
        JOptionPane.showMessageDialog(null,
            errorMessage,
            "Database Connection Error",
            JOptionPane.ERROR_MESSAGE);
        
        System.err.println(errorMessage);
        System.exit(1);
    }
    
    /**
     * Set system Look and Feel
     */
    private static void setLookAndFeel() {
        try {
            // Try to use system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Look and Feel: " + UIManager.getLookAndFeel().getName());
        } catch (Exception e) {
            System.err.println("Could not set Look and Feel: " + e.getMessage());
            // Continue with default look and feel
        }
    }
    
    /**
     * Shutdown hook to clean up resources
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down application...");
            try {
                DatabaseConnection dbConnection = DatabaseConnection.getInstance();
                dbConnection.closeAllConnections();
                System.out.println("Resources cleaned up successfully.");
            } catch (Exception e) {
                System.err.println("Error during shutdown: " + e.getMessage());
            }
        }));
    }
}
