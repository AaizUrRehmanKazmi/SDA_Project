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
 * PassengerDashboard - Main interface for passenger users
 */
public class PassengerDashboard extends JFrame {
    
    private Passenger passenger;
    private JTabbedPane tabbedPane;
    private BookingDAO bookingDAO;
    private RouteDAO routeDAO;
    
    public PassengerDashboard(Passenger passenger) {
        this.passenger = passenger;
        this.bookingDAO = new BookingDAO();
        this.routeDAO = new RouteDAO();
        initializeUI();
        loadPassengerData();
    }
    
    private void initializeUI() {
        setTitle("Passenger Dashboard - " + passenger.getFullName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add tabs
        tabbedPane.addTab("Home", createHomePanel());
        tabbedPane.addTab("Book Ticket", createBookingPanel());
        tabbedPane.addTab("My Bookings", createMyBookingsPanel());
        tabbedPane.addTab("Track Bus", createTrackingPanel());
        tabbedPane.addTab("Complaints", createComplaintsPanel());
        tabbedPane.addTab("Profile", createProfilePanel());
        
        add(tabbedPane);
    }
    
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Welcome message
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(0, 102, 204));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + passenger.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        
        JLabel subtitleLabel = new JLabel("Uzair Transport System - Passenger Portal");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(Color.WHITE);
        welcomePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        panel.add(welcomePanel, BorderLayout.NORTH);
        
        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        statsPanel.setBackground(Color.WHITE);
        
        JPanel totalBookingsCard = createStatCard("Total Bookings", "0", new Color(0, 153, 76));
        JPanel totalSpentCard = createStatCard("Total Spent", "Rs. 0.00", new Color(255, 153, 0));
        JPanel activeBookingsCard = createStatCard("Active Bookings", "0", new Color(0, 102, 204));
        
        statsPanel.add(totalBookingsCard);
        statsPanel.add(totalSpentCard);
        statsPanel.add(activeBookingsCard);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Quick actions panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        actionsPanel.setBackground(Color.WHITE);
        
        JButton bookTicketBtn = createActionButton("Book New Ticket", new Color(0, 153, 76));
        bookTicketBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        
        JButton viewBookingsBtn = createActionButton("View My Bookings", new Color(0, 102, 204));
        viewBookingsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        JButton trackBusBtn = createActionButton("Track Bus", new Color(255, 153, 0));
        trackBusBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        
        actionsPanel.add(bookTicketBtn);
        actionsPanel.add(viewBookingsBtn);
        actionsPanel.add(trackBusBtn);
        
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Book Your Ticket");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JLabel infoLabel = new JLabel("<html><center><h2>Book Your Bus Seat</h2>" +
            "<p>Follow these simple steps to book your seat:</p>" +
            "<ol style='text-align:left'>" +
            "<li>Select your desired route and date</li>" +
            "<li>Choose your pickup and dropoff stops</li>" +
            "<li>Select an available seat from the seat map</li>" +
            "<li>Confirm your booking and complete payment</li>" +
            "</ol>" +
            "<p><b>Payment methods:</b> Cash, Credit Card, Debit Card, Online</p></center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(infoLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Button to open booking frame
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton bookNowButton = new JButton("Book Seat Now");
        bookNowButton.setFont(new Font("Arial", Font.BOLD, 16));
        bookNowButton.setBackground(new Color(0, 153, 76));
        bookNowButton.setForeground(Color.WHITE);
        bookNowButton.setPreferredSize(new Dimension(200, 50));
        bookNowButton.setFocusPainted(false);
        bookNowButton.addActionListener(e -> openBookingFrame());
        
        buttonPanel.add(bookNowButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void openBookingFrame() {
        BookingFrame bookingFrame = new BookingFrame(passenger);
        bookingFrame.setVisible(true);
    }
    
    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("My Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create table for bookings
        String[] columnNames = {"Booking ID", "Date", "Route", "Bus", "Seat", "Fare", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        bookingsTable.setRowHeight(25);
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        bookingsTable.getTableHeader().setBackground(new Color(0, 102, 204));
        bookingsTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadBookings(tableModel));
        
        JButton cancelBtn = new JButton("Cancel Selected");
        cancelBtn.setBackground(new Color(204, 0, 0));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.addActionListener(e -> cancelSelectedBooking(bookingsTable, tableModel));
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load bookings on panel creation
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
            JOptionPane.showMessageDialog(this,
                "Please select a booking to cancel",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 6);
        
        if (!"CONFIRMED".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "Only confirmed bookings can be cancelled",
                "Cannot Cancel",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this booking?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (bookingDAO.cancelBooking(bookingId)) {
                    JOptionPane.showMessageDialog(this,
                        "Booking cancelled successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadBookings(tableModel);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error cancelling booking: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JPanel createTrackingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Live Bus Tracking");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel infoLabel = new JLabel("<html><center>Track your bus in real-time<br>" +
                                     "Implementation: Show map with GPS coordinates and current stop information</center></html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(infoLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createComplaintsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Submit Complaint/Feedback");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(204, 0, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JLabel infoLabel = new JLabel("<html><center><h2>We Value Your Feedback</h2>" +
            "<p>Have a complaint or suggestion? We're here to help!</p>" +
            "<br><p><b>You can report issues related to:</b></p>" +
            "<ul style='text-align:left'>" +
            "<li>Service Quality</li>" +
            "<li>Driver Behavior</li>" +
            "<li>Bus Condition</li>" +
            "<li>Route Issues</li>" +
            "<li>Payment Problems</li>" +
            "<li>Other Concerns</li>" +
            "</ul>" +
            "<p>We review all complaints within 24-48 hours.</p></center></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(infoLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Button to open complaint frame
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton submitComplaintButton = new JButton("Submit New Complaint");
        submitComplaintButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitComplaintButton.setBackground(new Color(204, 0, 0));
        submitComplaintButton.setForeground(Color.WHITE);
        submitComplaintButton.setPreferredSize(new Dimension(200, 45));
        submitComplaintButton.setFocusPainted(false);
        submitComplaintButton.addActionListener(e -> openComplaintFrame());
        
        JButton viewComplaintsButton = new JButton("View My Complaints");
        viewComplaintsButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewComplaintsButton.setBackground(new Color(0, 102, 204));
        viewComplaintsButton.setForeground(Color.WHITE);
        viewComplaintsButton.setPreferredSize(new Dimension(200, 45));
        viewComplaintsButton.setFocusPainted(false);
        viewComplaintsButton.addActionListener(e -> viewMyComplaints());
        
        buttonPanel.add(submitComplaintButton);
        buttonPanel.add(viewComplaintsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void openComplaintFrame() {
        ComplaintFrame complaintFrame = new ComplaintFrame(passenger);
        complaintFrame.setVisible(true);
    }
    
    private void viewMyComplaints() {
        // Create a dialog to show user's complaints
        JDialog complaintsDialog = new JDialog(this, "My Complaints", true);
        complaintsDialog.setSize(800, 500);
        complaintsDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Table for complaints
        String[] columnNames = {"ID", "Type", "Subject", "Priority", "Status", "Date"};
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
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
                    JOptionPane.showMessageDialog(complaintsDialog,
                        "Error loading complaints: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
        
        complaintsDialog.add(panel);
        complaintsDialog.setVisible(true);
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        addProfileField(panel, "Username:", passenger.getUsername(), gbc);
        gbc.gridy = 2;
        addProfileField(panel, "Full Name:", passenger.getFullName(), gbc);
        gbc.gridy = 3;
        addProfileField(panel, "Email:", passenger.getEmail(), gbc);
        gbc.gridy = 4;
        addProfileField(panel, "Phone:", passenger.getPhone(), gbc);
        gbc.gridy = 5;
        addProfileField(panel, "Status:", passenger.getStatus(), gbc);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(204, 0, 0));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        gbc.gridy = 6;
        gbc.gridx = 1;
        panel.add(logoutBtn, gbc);
        
        return panel;
    }
    
    private void addProfileField(JPanel panel, String label, String value, GridBagConstraints gbc) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        panel.add(labelComp, gbc);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(valueComp, gbc);
    }
    
    private void loadPassengerData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                int totalBookings = bookingDAO.getTotalBookingsCount(passenger.getUserId());
                double totalSpent = bookingDAO.getTotalAmountSpent(passenger.getUserId());
                
                passenger.setTotalBookings(totalBookings);
                passenger.setTotalAmountSpent(totalSpent);
                
                return null;
            }
            
            @Override
            protected void done() {
                // Update statistics on home panel after loading
                updateHomeStatistics();
            }
        };
        worker.execute();
    }
    
    private void updateHomeStatistics() {
        // This would update the stat cards on the home panel
        // Implementation depends on keeping references to those components
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}