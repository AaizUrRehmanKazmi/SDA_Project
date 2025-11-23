package ui;

import bl.*;
import dal.UserDAO;
import util.Constants;
import util.ValidationHelper;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

/**
 * LoginFrame - Enhanced Presentation Layer
 * Modern UI with improved UX and visual design
 */
public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JCheckBox showPasswordCheckBox;
    private UserDAO userDAO;
    
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);
    private static final Color SECONDARY_COLOR = new Color(46, 204, 113);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(52, 73, 94);
    private static final Color BORDER_COLOR = new Color(189, 195, 199);
    
    public LoginFrame() {
        this.userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        // Frame settings
        setTitle(Constants.APP_TITLE + " - Login");
        setSize(550, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main container with gradient background
        JPanel mainContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 152, 219), 
                                                     0, getHeight(), new Color(41, 128, 185));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainContainer.setLayout(new GridBagLayout());
        
        // Login card panel
        JPanel loginCard = createLoginCard();
        
        mainContainer.add(loginCard);
        add(mainContainer);
    }
    
    private JPanel createLoginCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(20, 20, 20, 20),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(30, 40, 30, 40)
            )
        ));
        card.setPreferredSize(new Dimension(450, 600));
        
        // Logo/Icon area
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(CARD_COLOR);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel iconLabel = new JLabel("🚌");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        logoPanel.add(iconLabel);
        
        // Title
        JLabel titleLabel = new JLabel("UZAIR TRANSPORT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("University Transport Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Spacing
        card.add(logoPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(subtitleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Username field
        card.add(createFieldLabel("Username"));
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        usernameField = createStyledTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(usernameField);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Password field
        card.add(createFieldLabel("Password"));
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        passwordField = createStyledPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(passwordField);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Show password checkbox
        showPasswordCheckBox = new JCheckBox("Show password");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.setBackground(CARD_COLOR);
        showPasswordCheckBox.setForeground(TEXT_COLOR);
        showPasswordCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());
        card.add(showPasswordCheckBox);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Login button
        loginButton = createStyledButton("Login", PRIMARY_COLOR, PRIMARY_DARK);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.addActionListener(e -> handleLogin());
        card.add(loginButton);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Register button
        registerButton = createStyledButton("Create New Account", SECONDARY_COLOR, new Color(39, 174, 96));
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        registerButton.addActionListener(e -> openRegistrationForm());
        card.add(registerButton);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Demo credentials panel
        JPanel demoPanel = createDemoCredentialsPanel();
        demoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.add(demoPanel);
        
        // Enter key listener
        passwordField.addActionListener(e -> handleLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());
        
        return card;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add focus effects
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add focus effects
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(hoverColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(hoverColor);
                } else {
                    g2d.setColor(getBackground());
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return button;
    }
    
    private JPanel createDemoCredentialsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(254, 249, 231));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 193, 7), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel headerLabel = new JLabel("📋 Demo Credentials");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerLabel.setForeground(new Color(245, 124, 0));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(headerLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        String[] credentials = {
            "Admin: admin / admin123",
            "Driver: driver1 / driver123",
            "Passenger: passenger1 / pass123"
        };
        
        for (String cred : credentials) {
            JLabel label = new JLabel(cred);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            label.setForeground(TEXT_COLOR);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label);
            panel.add(Box.createRigidArea(new Dimension(0, 3)));
        }
        
        return panel;
    }
    
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('•');
        }
    }
    
    /**
     * Controller method: Handle login action
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Input validation
        if (!ValidationHelper.isNotEmpty(username) || !ValidationHelper.isNotEmpty(password)) {
            showErrorDialog("Please enter both username and password.");
            return;
        }
        
        // Show loading state
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        
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
                        showSuccessDialog("Welcome, " + user.getFullName() + "!");
                        openDashboard(user);
                        dispose();
                    } else {
                        showErrorDialog("Invalid username or password. Please try again.");
                        passwordField.setText("");
                        usernameField.requestFocus();
                    }
                    
                } catch (Exception e) {
                    showErrorDialog("Login error: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };
        
        worker.execute();
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Opens appropriate dashboard based on user role
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