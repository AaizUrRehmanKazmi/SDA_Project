package ui;
import bl.*;
import dal.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * PassengerDashboard - Professional redesigned interface
 * Modern color scheme with enhanced visual hierarchy
 */
public class PassengerDashboard extends JFrame {
    
    // Professional Color Palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Modern Blue
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);    // Light Blue
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Green
    private static final Color WARNING_COLOR = new Color(230, 126, 34);      // Orange
    private static final Color DANGER_COLOR = new Color(231, 76, 60);        // Red
    private static final Color DARK_BG = new Color(44, 62, 80);              // Dark Blue-Gray
    private static final Color LIGHT_BG = new Color(236, 240, 241);          // Light Gray
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    
    private Passenger passenger;
    private JTabbedPane tabbedPane;
    private BookingDAO bookingDAO;
    private RouteDAO routeDAO;
    private RouteChangeRequestDAO routeChangeRequestDAO;
    
    // Statistics labels for dynamic updates
    private JLabel totalBookingsValue;
    private JLabel totalSpentValue;
    private JLabel activeBookingsValue;
    
    public PassengerDashboard(Passenger passenger) {
        this.passenger = passenger;
        this.bookingDAO = new BookingDAO();
        this.routeDAO = new RouteDAO();
        this.routeChangeRequestDAO = new RouteChangeRequestDAO();
        initializeUI();
        loadPassengerData();
    }
    
    private void initializeUI() {
        setTitle("Uzair Transport - Passenger Portal");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main container with gradient background
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(LIGHT_BG);
        
        // Create modern tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(CARD_BG);
        tabbedPane.setForeground(TEXT_PRIMARY);
        
        // Add tabs with icons (using unicode symbols)
        tabbedPane.addTab("Home", createHomePanel());
        tabbedPane.addTab("Book Ticket", createBookingPanel());
        tabbedPane.addTab("My Bookings", createMyBookingsPanel());
        tabbedPane.addTab("Track Bus", createTrackingPanel());
        tabbedPane.addTab("Route Changes", new RouteChangeRequestPanel(passenger.getUserId(), "PASSENGER"));
        tabbedPane.addTab("Complaints", createComplaintsPanel());
        tabbedPane.addTab("Profile", createProfilePanel());
        
        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        add(mainContainer);
    }
    
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(LIGHT_BG);
        
        // Header with gradient effect
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 150));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Welcome section
        JPanel welcomeSection = new JPanel(new GridLayout(2, 1, 0, 5));
        welcomeSection.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + passenger.getFullName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Your journey starts here • Uzair Transport System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        welcomeSection.add(welcomeLabel);
        welcomeSection.add(subtitleLabel);
        headerPanel.add(welcomeSection, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Content area
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(LIGHT_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Statistics cards with modern design
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        statsPanel.setOpaque(false);
        
        JPanel totalBookingsCard = createModernStatCard("Total Bookings", "0", "📊", SUCCESS_COLOR);
        totalBookingsValue = (JLabel) ((JPanel) totalBookingsCard.getComponent(0)).getComponent(1);
        
        JPanel totalSpentCard = createModernStatCard("Total Spent", "Rs. 0.00", "💰", WARNING_COLOR);
        totalSpentValue = (JLabel) ((JPanel) totalSpentCard.getComponent(0)).getComponent(1);
        
        JPanel activeBookingsCard = createModernStatCard("Active Bookings", "0", "🎫", PRIMARY_COLOR);
        activeBookingsValue = (JLabel) ((JPanel) activeBookingsCard.getComponent(0)).getComponent(1);
        
        statsPanel.add(totalBookingsCard);
        statsPanel.add(totalSpentCard);
        statsPanel.add(activeBookingsCard);
        
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Quick actions section
        JPanel actionsSection = new JPanel(new BorderLayout(0, 15));
        actionsSection.setOpaque(false);
        
        JLabel actionsTitle = new JLabel("Quick Actions");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        actionsTitle.setForeground(TEXT_PRIMARY);
        actionsSection.add(actionsTitle, BorderLayout.NORTH);
        
        JPanel actionsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        actionsPanel.setOpaque(false);
        
        JButton bookTicketBtn = createModernActionButton("Book New Ticket", 
            "Reserve your seat now", SUCCESS_COLOR);
        bookTicketBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        
        JButton viewBookingsBtn = createModernActionButton("View Bookings", 
            "Check your reservations", PRIMARY_COLOR);
        viewBookingsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        JButton trackBusBtn = createModernActionButton("Track Bus", 
            "Real-time location", WARNING_COLOR);
        trackBusBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        
        actionsPanel.add(bookTicketBtn);
        actionsPanel.add(viewBookingsBtn);
        actionsPanel.add(trackBusBtn);
        
        actionsSection.add(actionsPanel, BorderLayout.CENTER);
        contentPanel.add(actionsSection, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createModernStatCard(String title, String value, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 30), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Content panel
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        contentPanel.setOpaque(false);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accentColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_SECONDARY);
        
        contentPanel.add(valueLabel);
        contentPanel.add(titleLabel);
        
        // Icon label
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(new Color(accentColor.getRed(), accentColor.getGreen(), 
                                         accentColor.getBlue(), 50));
        
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);
        
        // Add subtle shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0, 0, 0, 20)),
            card.getBorder()
        ));
        
        return card;
    }
    
    private JButton createModernActionButton(String title, String subtitle, Color color) {
        JPanel buttonPanel = new JPanel(new BorderLayout(10, 5));
        buttonPanel.setBackground(color);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        buttonPanel.add(titleLabel, BorderLayout.CENTER);
        buttonPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Create button wrapper
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
            }
        };
        
        button.setLayout(new BorderLayout());
        button.add(buttonPanel);
        button.setPreferredSize(new Dimension(250, 100));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        return button;
    }
    
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(LIGHT_BG);
        
        // Modern header
        JPanel headerPanel = createSectionHeader("🎫 Book Your Ticket", 
            "Reserve your seat in just a few clicks", PRIMARY_COLOR);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Content with info card
        JPanel contentPanel = new JPanel(new BorderLayout(0, 25));
        contentPanel.setBackground(LIGHT_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Info card
        JPanel infoCard = new JPanel(new BorderLayout(20, 20));
        infoCard.setBackground(CARD_BG);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        JLabel instructionsLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<h2 style='color: #2980b9; margin-bottom: 15px;'>How to Book Your Seat</h2>" +
            "<p style='font-size: 14px; color: #7f8c8d; line-height: 1.8;'>" +
            "Follow these simple steps to complete your booking:<br><br></p>" +
            "<div style='text-align: left; margin-left: 50px;'>" +
            "<p style='font-size: 13px; margin: 8px 0;'><b>1.</b> Select your desired route and travel date</p>" +
            "<p style='font-size: 13px; margin: 8px 0;'><b>2.</b> Choose your pickup and dropoff stops</p>" +
            "<p style='font-size: 13px; margin: 8px 0;'><b>3.</b> Pick an available seat from the visual seat map</p>" +
            "<p style='font-size: 13px; margin: 8px 0;'><b>4.</b> Confirm your booking and complete payment</p>" +
            "</div><br>" +
            "<p style='font-size: 13px; color: #27ae60; margin-top: 15px;'>" +
            "<b>💳 Payment Methods:</b> Cash • Credit Card • Debit Card • Online Banking</p>" +
            "</div></html>"
        );
        infoCard.add(instructionsLabel, BorderLayout.CENTER);
        
        contentPanel.add(infoCard, BorderLayout.CENTER);
        
        // Call-to-action button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setOpaque(false);
        
        JButton bookNowButton = createStyledButton("Book Seat Now", SUCCESS_COLOR, 220, 55);
        bookNowButton.addActionListener(e -> openBookingFrame());
        
        buttonPanel.add(bookNowButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void openBookingFrame() {
        BookingFrame bookingFrame = new BookingFrame(passenger);
        bookingFrame.setVisible(true);
    }
    
    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(LIGHT_BG);
        
        // Header
        JPanel headerPanel = createSectionHeader("My Bookings", 
            "View and manage your reservations", PRIMARY_COLOR);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(LIGHT_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Table container
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(CARD_BG);
        tableContainer.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1));
        
        String[] columnNames = {"Booking ID", "Date", "Route", "Bus", "Seat", "Fare", "Status"};
       
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable bookingsTable = new JTable(tableModel);
        styleTable(bookingsTable);
        JTableHeader header = bookingsTable.getTableHeader();
        header.setForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BG);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(tableContainer, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton refreshBtn = createStyledButton("Refresh", SECONDARY_COLOR, 120, 40);
        refreshBtn.addActionListener(e -> loadBookings(tableModel));
        
        JButton cancelBtn = createStyledButton("Cancel Selected", DANGER_COLOR, 150, 40);
        cancelBtn.addActionListener(e -> cancelSelectedBooking(bookingsTable, tableModel));
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(cancelBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Load bookings
        loadBookings(tableModel);
        
        return panel;
    }
    
    private void loadBookings(DefaultTableModel tableModel) {
        SwingWorker<List<Booking>, Void> worker = new SwingWorker<List<Booking>, Void>() {
            @Override
            protected List<Booking> doInBackground() throws Exception {
                return bookingDAO.getBookingsByPassenger(passenger.getUserId());
            }
            
            @Override
            protected void done() {
                try {
                    List<Booking> bookings = get();
                    tableModel.setRowCount(0);
                    
                    for (Booking booking : bookings) {
                        tableModel.addRow(new Object[]{
                            booking.getBookingId(),
                            booking.getBookingDate(),
                            booking.getRouteName(),
                            booking.getBusNumber(),
                            booking.getSeatNumber(),
                            String.format("Rs. %.2f", booking.getFare()),
                            booking.getStatus()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PassengerDashboard.this,
                        "Error loading bookings: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void cancelSelectedBooking(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showStyledMessage("Please select a booking to cancel", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 6);
        
        if (!"CONFIRMED".equals(status)) {
            showStyledMessage("Only confirmed bookings can be cancelled", "Cannot Cancel", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this booking?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (bookingDAO.cancelBooking(bookingId)) {
                    showStyledMessage("Booking cancelled successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBookings(tableModel);
                }
            } catch (SQLException e) {
                showStyledMessage("Error cancelling booking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JPanel createTrackingPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(LIGHT_BG);
        
        JPanel headerPanel = createSectionHeader("Live Bus Tracking", 
            "Real-time location updates", WARNING_COLOR);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(LIGHT_BG);
        
        JLabel infoLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<p style='font-size: 48px; margin-bottom: 20px;'>🗺️</p>" +
            "<h2 style='color: #2c3e50;'>GPS Tracking Coming Soon</h2>" +
            "<p style='color: #7f8c8d; font-size: 14px; margin-top: 10px;'>" +
            "Track your bus in real-time with GPS coordinates<br>" +
            "and current stop information</p>" +
            "</div></html>"
        );
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        contentPanel.add(infoLabel);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createComplaintsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(LIGHT_BG);
        
        JPanel headerPanel = createSectionHeader("Complaints & Feedback", 
            "We value your feedback", DANGER_COLOR);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 25));
        contentPanel.setBackground(LIGHT_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Info card
        JPanel infoCard = new JPanel(new BorderLayout(20, 20));
        infoCard.setBackground(CARD_BG);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        JLabel infoLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<h2 style='color: #e74c3c; margin-bottom: 15px;'>We Value Your Feedback</h2>" +
            "<p style='font-size: 14px; color: #7f8c8d; margin-bottom: 20px;'>" +
            "Have a complaint or suggestion? We're here to help!</p>" +
            "<div style='text-align: left; margin-left: 80px;'>" +
            "<p style='font-size: 13px; margin: 6px 0;'><b>📋 Service Quality</b> - Report service-related issues</p>" +
            "<p style='font-size: 13px; margin: 6px 0;'><b>👨‍✈️ Driver Behavior</b> - Feedback about driver conduct</p>" +
            "<p style='font-size: 13px; margin: 6px 0;'><b>🚌 Bus Condition</b> - Report maintenance issues</p>" +
            "<p style='font-size: 13px; margin: 6px 0;'><b>🗺️ Route Issues</b> - Schedule or route concerns</p>" +
            "<p style='font-size: 13px; margin: 6px 0;'><b>💳 Payment Problems</b> - Billing or refund issues</p>" +
            "<p style='font-size: 13px; margin: 6px 0;'><b>ℹ️ Other Concerns</b> - Any other feedback</p>" +
            "</div><br>" +
            "<p style='font-size: 13px; color: #27ae60; margin-top: 15px;'>" +
            "⏱️ <b>Response Time:</b> We review all complaints within 24-48 hours</p>" +
            "</div></html>"
        );
        infoCard.add(infoLabel, BorderLayout.CENTER);
        
        contentPanel.add(infoCard, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        JButton submitBtn = createStyledButton("Submit New Complaint", DANGER_COLOR, 220, 50);
        submitBtn.addActionListener(e -> openComplaintFrame());
        
        JButton viewBtn = createStyledButton("View My Complaints", PRIMARY_COLOR, 220, 50);
        viewBtn.addActionListener(e -> viewMyComplaints());
        
        buttonPanel.add(submitBtn);
        buttonPanel.add(viewBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void openComplaintFrame() {
        ComplaintFrame complaintFrame = new ComplaintFrame(passenger);
        complaintFrame.setVisible(true);
    }
    
    private void viewMyComplaints() {
        JDialog complaintsDialog = new JDialog(this, "My Complaints", true);
        complaintsDialog.setSize(900, 600);
        complaintsDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("📋 My Complaints History");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(TEXT_PRIMARY);
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Table
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(CARD_BG);
        tableContainer.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1));
        
        String[] columnNames = {"ID", "Type", "Subject", "Priority", "Status", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        styleTable(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BG);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(tableContainer, BorderLayout.CENTER);
        
        // Load complaints
        SwingWorker<java.util.List<bl.Complaint>, Void> worker = 
            new SwingWorker<java.util.List<bl.Complaint>, Void>() {
            @Override
            protected java.util.List<bl.Complaint> doInBackground() throws Exception {
                dal.ComplaintDAO dao = new dal.ComplaintDAO();
                return dao.getComplaintsByUser(passenger.getUserId());
            }
            
            @Override
            protected void done() {
                try {
                    java.util.List<bl.Complaint> complaints = get();
                    for (bl.Complaint c : complaints) {
                        tableModel.addRow(new Object[]{
                            c.getComplaintId(),
                            c.getComplaintType(),
                            c.getSubject(),
                            c.getPriority(),
                            c.getStatus(),
                            c.getCreatedAt()
                        });
                    }
                } catch (Exception e) {
                    showStyledMessage("Error loading complaints: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
        
        complaintsDialog.add(panel);
        complaintsDialog.setVisible(true);
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(LIGHT_BG);
        
        JPanel headerPanel = createSectionHeader("My Profile", 
            "Your account information", SECONDARY_COLOR);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(LIGHT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Profile card
        JPanel profileCard = new JPanel(new GridBagLayout());
        profileCard.setBackground(CARD_BG);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(12, 12, 12, 12);
        cardGbc.anchor = GridBagConstraints.WEST;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        
        addModernProfileField(profileCard, "Username", passenger.getUsername(), cardGbc, 0);
        addModernProfileField(profileCard, "Full Name", passenger.getFullName(), cardGbc, 1);
        addModernProfileField(profileCard, "Email", passenger.getEmail(), cardGbc, 2);
        addModernProfileField(profileCard, "Phone", passenger.getPhone(), cardGbc, 3);
        addModernProfileField(profileCard, "Status", passenger.getStatus(), cardGbc, 4);
        
        // Logout button
        cardGbc.gridy = 5;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(25, 12, 12, 12);
        
        JButton logoutBtn = createStyledButton("Logout", DANGER_COLOR, 200, 45);
        logoutBtn.addActionListener(e -> logout());
        profileCard.add(logoutBtn, cardGbc);
        
        contentPanel.add(profileCard);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addModernProfileField(JPanel panel, String label, String value, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        
        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComp.setForeground(TEXT_SECONDARY);
        panel.add(labelComp, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        valueComp.setForeground(TEXT_PRIMARY);
        panel.add(valueComp, gbc);
    }
    
    // Helper methods for modern UI components
    
    private JPanel createSectionHeader(String title, String subtitle, Color color) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(color);
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        headerPanel.add(textPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JButton createStyledButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color btnColor;
                if (getModel().isPressed()) {
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
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(width, height));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(52, 152, 219, 50));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(0, 0, 0, 10));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // Center align specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            if (table.getColumnCount() > 4) {
                table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            }
        }
        
        // Custom cell renderer for alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });
    }
    
    private void showStyledMessage(String message, String title, int messageType) {
        UIManager.put("OptionPane.background", CARD_BG);
        UIManager.put("Panel.background", CARD_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    private void loadPassengerData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            int totalBookings;
            double totalSpent;
            
            @Override
            protected Void doInBackground() throws Exception {
                totalBookings = bookingDAO.getTotalBookingsCount(passenger.getUserId());
                totalSpent = bookingDAO.getTotalAmountSpent(passenger.getUserId());
                
                passenger.setTotalBookings(totalBookings);
                passenger.setTotalAmountSpent(totalSpent);
                
                return null;
            }
            
            @Override
            protected void done() {
                updateHomeStatistics();
            }
        };
        worker.execute();
    }
    
    private void updateHomeStatistics() {
        if (totalBookingsValue != null) {
            totalBookingsValue.setText(String.valueOf(passenger.getTotalBookings()));
        }
        if (totalSpentValue != null) {
            totalSpentValue.setText(String.format("Rs. %.2f", passenger.getTotalAmountSpent()));
        }
        if (activeBookingsValue != null) {
            // Count active bookings (CONFIRMED status)
            try {
                List<Booking> bookings = bookingDAO.getBookingsByPassenger(passenger.getUserId());
                long activeCount = bookings.stream()
                    .filter(b -> "CONFIRMED".equals(b.getStatus()))
                    .count();
                activeBookingsValue.setText(String.valueOf(activeCount));
            } catch (Exception e) {
                activeBookingsValue.setText("0");
            }
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}