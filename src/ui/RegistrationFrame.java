package ui;

import bl.Passenger;
import bl.User;
import dal.UserDAO;
import util.Constants;
import util.ValidationHelper;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * RegistrationFrame - User Registration UI
 */
public class RegistrationFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JButton registerButton;
    private JButton cancelButton;
    private UserDAO userDAO;
    private JFrame parentFrame;
    
    public RegistrationFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Register New Account");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username
        addLabel(mainPanel, "Username:", gbc, 1);
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        // Password
        addLabel(mainPanel, "Password:", gbc, 2);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        // Confirm Password
        addLabel(mainPanel, "Confirm Password:", gbc, 3);
        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);
        
        // Full Name
        addLabel(mainPanel, "Full Name:", gbc, 4);
        fullNameField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(fullNameField, gbc);
        
        // Email
        addLabel(mainPanel, "Email:", gbc, 5);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);
        
        // Phone
        addLabel(mainPanel, "Phone (03XXXXXXXXX):", gbc, 6);
        phoneField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(phoneField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(0, 153, 76));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.addActionListener(e -> handleRegistration());
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(new Color(204, 0, 0));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
    private void addLabel(JPanel panel, String text, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);
    }
    
    private void handleRegistration() {
        // Get input values
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
        // Validate inputs
        if (!validateInputs(username, password, confirmPassword, fullName, email, phone)) {
            return;
        }
        
        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        registerButton.setEnabled(false);
        
        // Perform registration in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            String errorMessage = null;
            
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Check if username exists
                    if (userDAO.usernameExists(username)) {
                        errorMessage = Constants.ERROR_USERNAME_EXISTS;
                        return false;
                    }
                    
                    // Check if email exists
                    if (userDAO.emailExists(email)) {
                        errorMessage = Constants.ERROR_EMAIL_EXISTS;
                        return false;
                    }
                    
                    // Create new passenger user
                    User newUser = new Passenger(username, password, fullName, email, phone);
                    return userDAO.registerUser(newUser);
                    
                } catch (SQLException e) {
                    errorMessage = "Database error: " + e.getMessage();
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    
                    if (success) {
                        JOptionPane.showMessageDialog(RegistrationFrame.this,
                            Constants.SUCCESS_REGISTRATION,
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegistrationFrame.this,
                            errorMessage != null ? errorMessage : "Registration failed",
                            "Registration Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RegistrationFrame.this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    registerButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private boolean validateInputs(String username, String password, String confirmPassword,
                                   String fullName, String email, String phone) {
        // Check if all fields are filled
        if (!ValidationHelper.isNotEmpty(username) ||
            !ValidationHelper.isNotEmpty(password) ||
            !ValidationHelper.isNotEmpty(confirmPassword) ||
            !ValidationHelper.isNotEmpty(fullName) ||
            !ValidationHelper.isNotEmpty(email) ||
            !ValidationHelper.isNotEmpty(phone)) {
            
            showError(Constants.ERROR_REQUIRED_FIELDS);
            return false;
        }
        
        // Validate username format
        if (!ValidationHelper.isValidUsername(username)) {
            showError("Username must be 4-20 characters, alphanumeric only.");
            usernameField.requestFocus();
            return false;
        }
        
        // Validate password
        if (!ValidationHelper.isValidPassword(password)) {
            showError("Password must be at least 6 characters long.");
            passwordField.requestFocus();
            return false;
        }
        
        // Check password match
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match!");
            confirmPasswordField.requestFocus();
            return false;
        }
        
        // Validate email
        if (!ValidationHelper.isValidEmail(email)) {
            showError(Constants.ERROR_INVALID_EMAIL);
            emailField.requestFocus();
            return false;
        }
        
        // Validate phone
        if (!ValidationHelper.isValidPhone(phone)) {
            showError(Constants.ERROR_INVALID_PHONE);
            phoneField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
