package ui;

import bl.*;
import dal.UserDAO;
import util.Constants;
import util.ValidationHelper;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * LoginFrame - Presentation Layer
 * Implements Controller GRASP pattern for handling UI events
 */
public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserDAO userDAO;
    
    public LoginFrame() {
        this.userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        // Frame settings
        setTitle(Constants.APP_TITLE + " - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("UZAIR TRANSPORT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("University Transport Management");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);
        
        // Reset gridwidth
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 153, 76));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.addActionListener(e -> handleLogin());
        
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(0, 102, 204));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.addActionListener(e -> openRegistrationForm());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(255, 255, 224));
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        JLabel infoLabel = new JLabel("<html><center>Demo Credentials:<br>" +
                                     "Admin: admin / admin123<br>" +
                                     "Driver: driver1 / driver123<br>" +
                                     "Passenger: passenger1 / pass123</center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoPanel.add(infoLabel);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(infoPanel, gbc);
        
        // Enter key listener for login
        passwordField.addActionListener(e -> handleLogin());
        
        add(mainPanel);
    }
    
    /**
     * Controller method: Handle login action
     * Demonstrates Controller GRASP pattern
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Input validation
        if (!ValidationHelper.isNotEmpty(username) || !ValidationHelper.isNotEmpty(password)) {
            JOptionPane.showMessageDialog(this,
                Constants.ERROR_REQUIRED_FIELDS,
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        loginButton.setEnabled(false);
        
        // Perform authentication in background
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.authenticateUser(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    
                    if (user != null) {
                        // Authentication successful
                        JOptionPane.showMessageDialog(LoginFrame.this,
                            "Welcome, " + user.getFullName() + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Open appropriate dashboard based on role
                        openDashboard(user);
                        dispose(); // Close login window
                        
                    } else {
                        // Authentication failed
                        JOptionPane.showMessageDialog(LoginFrame.this,
                            Constants.ERROR_INVALID_CREDENTIALS,
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                        usernameField.requestFocus();
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "Login error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    loginButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Opens appropriate dashboard based on user role
     * Demonstrates Polymorphism - different behavior for different user types
     */
    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            if (user instanceof Passenger) {
                new PassengerDashboard((Passenger) user).setVisible(true);
            } else if (user instanceof Driver) {
                new DriverDashboard((Driver) user).setVisible(true);
            } else if (user instanceof Admin) {
                new AdminDashboard((Admin) user).setVisible(true);
            }
        });
    }
    
    /**
     * Opens registration form
     */
    private void openRegistrationForm() {
        new RegistrationFrame(this).setVisible(true);
    }
    
    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch application on EDT
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}