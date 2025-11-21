package ui;

import bl.Complaint;
import bl.User;
import dal.ComplaintDAO;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * ComplaintFrame - Complete complaint submission functionality
 */
public class ComplaintFrame extends JFrame {
    
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
        setTitle("Submit Complaint/Feedback");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Submit Your Complaint or Feedback");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(204, 0, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Complaint Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel typeLabel = new JLabel("Complaint Type:");
        typeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(typeLabel, gbc);
        
        String[] types = {"SERVICE", "DRIVER", "BUS_CONDITION", "ROUTE", "PAYMENT", "OTHER"};
        typeComboBox = new JComboBox<>(types);
        typeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(typeComboBox, gbc);
        
        // Priority
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(priorityLabel, gbc);
        
        String[] priorities = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        priorityComboBox = new JComboBox<>(priorities);
        priorityComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        priorityComboBox.setSelectedIndex(1); // Default to MEDIUM
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(priorityComboBox, gbc);
        
        // Subject
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(subjectLabel, gbc);
        
        subjectField = new JTextField(30);
        subjectField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(subjectField, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(descLabel, gbc);
        
        descriptionArea = new JTextArea(8, 30);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        formPanel.add(scrollPane, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(255, 255, 224));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.ORANGE, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel infoLabel = new JLabel("<html><b>Please provide detailed information:</b><br>" +
            "• Describe the issue clearly<br>" +
            "• Include date and time if applicable<br>" +
            "• Mention bus number or route if relevant<br>" +
            "• We will respond within 24-48 hours</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoPanel.add(infoLabel);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        submitButton = new JButton("Submit Complaint");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(204, 0, 0));
        submitButton.setForeground(Color.WHITE);
        submitButton.setPreferredSize(new Dimension(160, 40));
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> submitComplaint());
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        // Bottom panel with info and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void submitComplaint() {
        // Validate inputs
        String type = (String) typeComboBox.getSelectedItem();
        String priority = (String) priorityComboBox.getSelectedItem();
        String subject = subjectField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (subject.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a subject for your complaint.",
                "Subject Required",
                JOptionPane.WARNING_MESSAGE);
            subjectField.requestFocus();
            return;
        }
        
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please provide a description of your complaint.",
                "Description Required",
                JOptionPane.WARNING_MESSAGE);
            descriptionArea.requestFocus();
            return;
        }
        
        if (subject.length() < 10) {
            JOptionPane.showMessageDialog(this,
                "Subject must be at least 10 characters long.",
                "Subject Too Short",
                JOptionPane.WARNING_MESSAGE);
            subjectField.requestFocus();
            return;
        }
        
        if (description.length() < 20) {
            JOptionPane.showMessageDialog(this,
                "Description must be at least 20 characters long.",
                "Description Too Short",
                JOptionPane.WARNING_MESSAGE);
            descriptionArea.requestFocus();
            return;
        }
        
        // Confirm submission
        int confirm = JOptionPane.showConfirmDialog(this,
            "Submit this complaint?\n\nType: " + type + "\nPriority: " + priority,
            "Confirm Submission",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Submit complaint
        submitButton.setEnabled(false);
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
                        JOptionPane.showMessageDialog(ComplaintFrame.this,
                            String.format("Complaint submitted successfully!\n\n" +
                                        "Complaint ID: %d\n" +
                                        "Status: SUBMITTED\n\n" +
                                        "We will review your complaint and respond within 24-48 hours.\n" +
                                        "You can track the status in your dashboard.",
                                        complaintId),
                            "Complaint Submitted",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Clear form
                        subjectField.setText("");
                        descriptionArea.setText("");
                        typeComboBox.setSelectedIndex(0);
                        priorityComboBox.setSelectedIndex(1);
                        
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ComplaintFrame.this,
                            "Failed to submit complaint. Please try again.",
                            "Submission Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ComplaintFrame.this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    submitButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
}
