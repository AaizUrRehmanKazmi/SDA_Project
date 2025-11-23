package ui;

import bl.Complaint;
import bl.User;
import dal.ComplaintDAO;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * ComplaintFrame - Professional modern design with enhanced UX
 */
public class ComplaintFrame extends JFrame {
    
    // Professional color palette
    private static final Color PRIMARY_COLOR = new Color(231, 76, 60);       // Red
    private static final Color SECONDARY_COLOR = new Color(192, 57, 43);     // Dark Red
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Green
    private static final Color WARNING_COLOR = new Color(243, 156, 18);      // Orange
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BG_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    
    private User user;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> priorityComboBox;
    private JTextField subjectField;
    private JTextArea descriptionArea;
    private JButton submitButton;
    private JButton cancelButton;
    private ComplaintDAO complaintDAO;
    
    public ComplaintFrame(User user) {
        this.user = user;
        this.complaintDAO = new ComplaintDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Submit Complaint - Uzair Transport");
        setSize(700, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing for better usability
        
        // Main container with gradient background
        JPanel mainContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainContainer.setLayout(new BorderLayout());
        
        // Main card - removed fixed size to allow scrolling
        JPanel mainCard = new JPanel(new BorderLayout(0, 20));
        mainCard.setBackground(CARD_BG);
        mainCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 30), 1),
            BorderFactory.createEmptyBorder(35, 40, 35, 40)
        ));
        
        // Header Section
        JPanel headerPanel = new JPanel(new BorderLayout(15, 10));
        headerPanel.setBackground(CARD_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Icon
        JLabel iconLabel = new JLabel("💬");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(70, 70));
        headerPanel.add(iconLabel, BorderLayout.WEST);
        
        // Title section
        JPanel titleSection = new JPanel(new GridLayout(2, 1, 0, 5));
        titleSection.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Submit Your Complaint");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JLabel subtitleLabel = new JLabel("We value your feedback and will respond within 24-48 hours");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        
        titleSection.add(titleLabel);
        titleSection.add(subtitleLabel);
        headerPanel.add(titleSection, BorderLayout.CENTER);
        
        mainCard.add(headerPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Complaint Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 5, 0);
        JLabel typeLabel = createFieldLabel("Complaint Type");
        formPanel.add(typeLabel, gbc);
        
        String[] types = {"SERVICE", "DRIVER", "BUS_CONDITION", "ROUTE", "PAYMENT", "OTHER"};
        typeComboBox = createStyledComboBox(types);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        formPanel.add(typeComboBox, gbc);
        
        // Priority
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 5, 0);
        JLabel priorityLabel = createFieldLabel("Priority Level");
        formPanel.add(priorityLabel, gbc);
        
        String[] priorities = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        priorityComboBox = createStyledComboBox(priorities);
        priorityComboBox.setSelectedIndex(1); // Default to MEDIUM
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        formPanel.add(priorityComboBox, gbc);
        
        // Subject
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 5, 0);
        JLabel subjectLabel = createFieldLabel("Subject (minimum 10 characters)");
        formPanel.add(subjectLabel, gbc);
        
        subjectField = createStyledTextField();
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 15, 0);
        formPanel.add(subjectField, gbc);
        
        // Description
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 0, 5, 0);
        JLabel descLabel = createFieldLabel("Detailed Description (minimum 20 characters)");
        formPanel.add(descLabel, gbc);
        
        descriptionArea = new JTextArea(10, 30); // Increased rows from 8 to 10
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        // Focus effect for text area
        descriptionArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(570, 200)); // Set preferred size for scroll pane
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.weighty = 0; // Changed from 1.0 to 0 to prevent excessive stretching
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(scrollPane, gbc);
        
        mainCard.add(formPanel, BorderLayout.CENTER);
        
        // Bottom Section
        JPanel bottomSection = new JPanel(new BorderLayout(0, 15));
        bottomSection.setBackground(CARD_BG);
        
        // Info Panel with modern styling
        JPanel infoPanel = new JPanel(new BorderLayout(12, 0));
        infoPanel.setBackground(new Color(255, 243, 205));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WARNING_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel infoIcon = new JLabel("ℹ️");
        infoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        infoPanel.add(infoIcon, BorderLayout.WEST);
        
        JLabel infoLabel = new JLabel(
            "<html><div style='color: #856404;'>" +
            "<b>Guidelines for submitting complaints:</b><br>" +
            "• Be specific and describe the issue clearly<br>" +
            "• Include date, time, and location if applicable<br>" +
            "• Mention bus number or route name if relevant<br>" +
            "• Use professional language</div></html>"
        );
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        
        bottomSection.add(infoPanel, BorderLayout.NORTH);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_BG);
        
        submitButton = createPrimaryButton("📝 Submit Complaint", PRIMARY_COLOR, 200);
        submitButton.addActionListener(e -> submitComplaint());
        
        cancelButton = createSecondaryButton("Cancel", new Color(149, 165, 166), 130);
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        bottomSection.add(buttonPanel, BorderLayout.SOUTH);
        
        mainCard.add(bottomSection, BorderLayout.SOUTH);
        
        // Create scroll pane for entire card
        JScrollPane mainScrollPane = new JScrollPane(mainCard);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainContainer.add(mainScrollPane, BorderLayout.CENTER);
        add(mainContainer);
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(570, 42));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Custom renderer for better appearance
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                if (isSelected) {
                    setBackground(PRIMARY_COLOR);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(TEXT_PRIMARY);
                }
                
                return c;
            }
        });
        
        return comboBox;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(570, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Focus effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }
    
    private JButton createPrimaryButton(String text, Color color, int width) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color btnColor;
                if (!isEnabled()) {
                    btnColor = new Color(189, 195, 199);
                } else if (getModel().isPressed()) {
                    btnColor = color.darker();
                } else if (getModel().isRollover()) {
                    btnColor = new Color(
                        Math.min(255, color.getRed() + 20),
                        Math.min(255, color.getGreen() + 20),
                        Math.min(255, color.getBlue() + 20)
                    );
                } else {
                    btnColor = color;
                }
                
                g2d.setColor(btnColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(width, 45));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JButton createSecondaryButton(String text, Color color, int width) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color btnColor = color;
                if (getModel().isPressed()) {
                    btnColor = color.darker();
                } else if (getModel().isRollover()) {
                    btnColor = color.brighter();
                }
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2d.setColor(btnColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                
                g2d.setColor(btnColor);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(width, 45));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void submitComplaint() {
        // Validate inputs
        String type = (String) typeComboBox.getSelectedItem();
        String priority = (String) priorityComboBox.getSelectedItem();
        String subject = subjectField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (subject.isEmpty()) {
            showStyledMessage(
                "Please enter a subject for your complaint.",
                "Subject Required",
                JOptionPane.WARNING_MESSAGE
            );
            subjectField.requestFocus();
            return;
        }
        
        if (description.isEmpty()) {
            showStyledMessage(
                "Please provide a description of your complaint.",
                "Description Required",
                JOptionPane.WARNING_MESSAGE
            );
            descriptionArea.requestFocus();
            return;
        }
        
        if (subject.length() < 10) {
            showStyledMessage(
                "Subject must be at least 10 characters long.\nCurrent length: " + subject.length(),
                "Subject Too Short",
                JOptionPane.WARNING_MESSAGE
            );
            subjectField.requestFocus();
            return;
        }
        
        if (description.length() < 20) {
            showStyledMessage(
                "Description must be at least 20 characters long.\nCurrent length: " + description.length(),
                "Description Too Short",
                JOptionPane.WARNING_MESSAGE
            );
            descriptionArea.requestFocus();
            return;
        }
        
        // Confirm submission
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Submit this complaint?\n\nType: %s\nPriority: %s\nSubject: %s",
                type, priority, subject),
            "Confirm Submission",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Submit complaint
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            int complaintId = 0;
            
            @Override
            protected Boolean doInBackground() throws Exception {
                Complaint complaint = new Complaint(
                    user.getUserId(),
                    type,
                    subject,
                    description,
                    priority
                );
                
                boolean success = complaintDAO.submitComplaint(complaint);
                if (success) {
                    complaintId = complaint.getComplaintId();
                }
                return success;
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    
                    if (success) {
                        showStyledMessage(
                            String.format(
                                "Complaint submitted successfully!\n\n" +
                                "Complaint ID: #%d\n" +
                                "Status: SUBMITTED\n" +
                                "Priority: %s\n\n" +
                                "✓ We will review your complaint within 24-48 hours\n" +
                                "✓ You can track the status in your dashboard\n" +
                                "✓ You'll receive a response once it's reviewed",
                                complaintId, priority
                            ),
                            "Complaint Submitted Successfully",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        // Clear form
                        subjectField.setText("");
                        descriptionArea.setText("");
                        typeComboBox.setSelectedIndex(0);
                        priorityComboBox.setSelectedIndex(1);
                        
                        dispose();
                    } else {
                        showStyledMessage(
                            "Failed to submit complaint. Please try again.\n\n" +
                            "If the problem persists, please contact support.",
                            "Submission Failed",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception e) {
                    showStyledMessage(
                        "Error: " + e.getMessage() + "\n\nPlease try again later.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    submitButton.setEnabled(true);
                    submitButton.setText("📝 Submit Complaint");
                }
            }
        };
        
        worker.execute();
    }
    
    private void showStyledMessage(String message, String title, int messageType) {
        UIManager.put("OptionPane.background", CARD_BG);
        UIManager.put("Panel.background", CARD_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}