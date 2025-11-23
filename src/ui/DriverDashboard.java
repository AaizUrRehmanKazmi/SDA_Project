package ui;

import bl.*;
import dal.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * DriverDashboard - Complete professional interface with full backend integration
 */
public class DriverDashboard extends JFrame {
    
    // Professional Color Palette
    private static final Color PRIMARY_COLOR = new Color(230, 126, 34);      // Orange
    private static final Color SECONDARY_COLOR = new Color(243, 156, 18);    // Light Orange
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Green
    private static final Color DANGER_COLOR = new Color(231, 76, 60);        // Red
    private static final Color INFO_COLOR = new Color(52, 152, 219);         // Blue
    private static final Color DARK_BG = new Color(44, 62, 80);
    private static final Color LIGHT_BG = new Color(236, 240, 241);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    
    private Driver driver;
    private JTabbedPane tabbedPane;
    private TripDAO tripDAO;
    private BusDAO busDAO;
    private RouteAssignmentDAO assignmentDAO;
    
    // Statistics labels
    private JLabel totalTripsValue;
    private JLabel completedTripsValue;
    private JLabel ratingValue;
    
    // Active trip components
    private Trip currentTrip;
    private JLabel activeTripStatusLabel;
    private JPanel activeTripDetailsPanel;
    private JButton startTripBtn;
    private JButton updateLocationBtn;
    private JButton endTripBtn;
    private JTextArea tripInfoArea;
    private RouteDAO routeDAO;
    private Route currentRoute;
    
    public DriverDashboard(Driver driver) {
        this.driver = driver;
        this.tripDAO = new TripDAO();
        this.busDAO = new BusDAO();
        this.routeDAO = new RouteDAO();
        this.assignmentDAO = new RouteAssignmentDAO();
        initializeUI();
        loadDriverData();
        checkActiveTrip();
    }
    
    private void initializeUI() {
        setTitle("Uzair Transport - Driver Portal");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(LIGHT_BG);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(CARD_BG);
        tabbedPane.setForeground(TEXT_PRIMARY);
        
        tabbedPane.addTab("Home", createHomePanel());
        tabbedPane.addTab("Active Trip", createActiveTripPanel());
        tabbedPane.addTab("My Trips", createTripsHistoryPanel());
        tabbedPane.addTab("Scheduled Trips", createScheduledTripsPanel());
        tabbedPane.addTab("Route Changes", new RouteChangeRequestPanel(driver.getUserId(), "DRIVER"));
        tabbedPane.addTab("My Bus", createBusInfoPanel());
        tabbedPane.addTab("Profile", createProfilePanel());
        
        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        add(mainContainer);
    }
    private JPanel createScheduledTripsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Scheduled Trips - Assigned by Admin");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 153, 76));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table for scheduled trips
        String[] columnNames = {"Trip ID", "Date", "Route", "Bus", "Status", "Start Time", "End Time", "Action"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;  // Only "Action" column is editable
            }
        };
        
        JTable scheduledTripsTable = new JTable(tableModel);
        scheduledTripsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        scheduledTripsTable.setRowHeight(25);
        scheduledTripsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        scheduledTripsTable.getTableHeader().setBackground(new Color(0, 153, 76));
        scheduledTripsTable.getTableHeader().setForeground(Color.WHITE);
        
        // Custom button renderer for Action column
        scheduledTripsTable.getColumn("Action").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton btn = new JButton("Start");
            btn.setBackground(new Color(0, 102, 204));
            btn.setForeground(Color.WHITE);
            return btn;
        });
        
        JScrollPane scrollPane = new JScrollPane(scheduledTripsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadScheduledTrips(tableModel));
        
        JButton refreshManualBtn = new JButton("Load Assigned Trips");
        refreshManualBtn.setBackground(new Color(0, 153, 76));
        refreshManualBtn.setForeground(Color.WHITE);
        refreshManualBtn.addActionListener(e -> loadScheduledTrips(tableModel));
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(refreshManualBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load scheduled trips
        loadScheduledTrips(tableModel);
        
        return panel;
    }
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(LIGHT_BG);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 150));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JPanel welcomeSection = new JPanel(new GridLayout(2, 1, 0, 5));
        welcomeSection.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome, Driver " + driver.getFullName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Ready to hit the road • Uzair Transport System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        welcomeSection.add(welcomeLabel);
        welcomeSection.add(subtitleLabel);
        headerPanel.add(welcomeSection, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(LIGHT_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Statistics cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        statsPanel.setOpaque(false);
        
        JPanel totalTripsCard = createModernStatCard("Total Trips", "0", "🚌", INFO_COLOR);
        totalTripsValue = (JLabel) ((JPanel) totalTripsCard.getComponent(0)).getComponent(1);
        
        JPanel completedCard = createModernStatCard("Completed", "0", "✓", SUCCESS_COLOR);
        completedTripsValue = (JLabel) ((JPanel) completedCard.getComponent(0)).getComponent(1);
        
        JPanel ratingCard = createModernStatCard("Rating", "5.0", "⭐", PRIMARY_COLOR);
        ratingValue = (JLabel) ((JPanel) ratingCard.getComponent(0)).getComponent(1);
        
        statsPanel.add(totalTripsCard);
        statsPanel.add(completedCard);
        statsPanel.add(ratingCard);
        
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Quick actions
        JPanel actionsSection = new JPanel(new BorderLayout(0, 15));
        actionsSection.setOpaque(false);
        
        JLabel actionsTitle = new JLabel("Quick Actions");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        actionsTitle.setForeground(TEXT_PRIMARY);
        actionsSection.add(actionsTitle, BorderLayout.NORTH);
        
        JPanel actionsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        actionsPanel.setOpaque(false);
        
        JButton startTripBtnHome = createModernActionButton("🚀 Start New Trip", 
            "Begin your journey", SUCCESS_COLOR);
        startTripBtnHome.addActionListener(e -> {
            tabbedPane.setSelectedIndex(1);
            showStartTripDialog();
        });
        
        JButton viewTripsBtn = createModernActionButton("📋 View Trips", 
            "Check trip history", INFO_COLOR);
        viewTripsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        JButton myBusBtn = createModernActionButton("🚍 My Bus", 
            "Bus information", PRIMARY_COLOR);
        myBusBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        
        actionsPanel.add(startTripBtnHome);
        actionsPanel.add(viewTripsBtn);
        actionsPanel.add(myBusBtn);
        
        actionsSection.add(actionsPanel, BorderLayout.CENTER);
        contentPanel.add(actionsSection, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActiveTripPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Active Trip Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 140, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Main content panel with two sections
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        contentPanel.setBackground(Color.WHITE);
        
        // ===== LEFT PANEL: SCHEDULED TRIPS TABLE =====
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Scheduled Trips - Click to Select"));
        
        String[] tableColumns = {"Trip ID", "Route", "Date", "Bus", "Status", "Action"};
        DefaultTableModel tableModel = new DefaultTableModel(tableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tripsTable = new JTable(tableModel);
        tripsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        tripsTable.setRowHeight(28);
        tripsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tripsTable.getTableHeader().setBackground(new Color(255, 140, 0));
        tripsTable.getTableHeader().setForeground(Color.WHITE);
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JButton selectTripBtn = new JButton("Select Trip from List");
        // Add mouse listener to select trip when row is clicked
        tripsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tripsTable.getSelectedRow();
                if (row >= 0) {
                    // Optional: Highlight row / preview information
                    onTripSelected(row, tableModel);

                    
                    selectTripBtn.setEnabled(true);
                }
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(tripsTable);
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Refresh button for table
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableButtonPanel.setBackground(Color.WHITE);
        
        JButton refreshTableBtn = new JButton("Refresh List");
        refreshTableBtn.setBackground(new Color(0, 102, 204));
        refreshTableBtn.setForeground(Color.WHITE);
        refreshTableBtn.addActionListener(e -> loadScheduledTripsForTable(tableModel));
        tableButtonPanel.add(refreshTableBtn);
        leftPanel.add(tableButtonPanel, BorderLayout.SOUTH);
        
        // ===== RIGHT PANEL: TRIP DETAILS & ACTIONS =====
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Trip Details"));
        
        // Trip details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Trip ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(new JLabel("Trip ID:"), gbc);
        JLabel tripIdLabel = new JLabel("Not Selected");
        tripIdLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 1;
        detailsPanel.add(tripIdLabel, gbc);
        
        // Route Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsPanel.add(new JLabel("Route:"), gbc);
        JLabel routeLabel = new JLabel("N/A");
        routeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        routeLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 1;
        detailsPanel.add(routeLabel, gbc);
        
        // Trip Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        detailsPanel.add(new JLabel("Trip Date:"), gbc);
        JLabel dateLabel = new JLabel("N/A");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        detailsPanel.add(dateLabel, gbc);
        
        // Bus Number
        gbc.gridx = 0;
        gbc.gridy = 3;
        detailsPanel.add(new JLabel("Bus:"), gbc);
        JLabel busLabel = new JLabel("N/A");
        busLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        detailsPanel.add(busLabel, gbc);
        
        // Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        detailsPanel.add(new JLabel("Status:"), gbc);
        JLabel statusLabel = new JLabel("No Trip Selected");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(new Color(255, 140, 0));
        gbc.gridx = 1;
        detailsPanel.add(statusLabel, gbc);
        
        // Start Time
        gbc.gridx = 0;
        gbc.gridy = 5;
        detailsPanel.add(new JLabel("Start Time:"), gbc);
        JLabel startTimeLabel = new JLabel("Not Started");
        startTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        detailsPanel.add(startTimeLabel, gbc);
        
        // End Time
        gbc.gridx = 0;
        gbc.gridy = 6;
        detailsPanel.add(new JLabel("End Time:"), gbc);
        JLabel endTimeLabel = new JLabel("Not Ended");
        endTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        detailsPanel.add(endTimeLabel, gbc);
        
        rightPanel.add(detailsPanel, BorderLayout.NORTH);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        actionPanel.setBackground(Color.WHITE);
        JButton endBtn = new JButton("End Trip");
        JButton startBtn = new JButton("Start Trip");
        
        selectTripBtn.setBackground(new Color(0, 153, 76));
        selectTripBtn.setForeground(Color.WHITE);
        selectTripBtn.setFont(new Font("Arial", Font.BOLD, 13));
        selectTripBtn.setPreferredSize(new Dimension(160, 45));
        selectTripBtn.setEnabled(false);
        selectTripBtn.addActionListener(e -> selectTripAction(tripsTable, tableModel, 
            tripIdLabel, routeLabel, dateLabel, busLabel, statusLabel, startTimeLabel, endTimeLabel,startBtn,endBtn));
        
        startBtn.setBackground(new Color(0, 153, 76));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFont(new Font("Arial", Font.BOLD, 13));
        startBtn.setPreferredSize(new Dimension(120, 45));
        startBtn.setEnabled(false);
        startBtn.addActionListener(e -> startTripAction(tripIdLabel, statusLabel, startTimeLabel, selectTripBtn, startBtn, endBtn, 
            routeLabel, dateLabel, busLabel, endTimeLabel, tableModel));
        
        
        endBtn.setBackground(new Color(204, 0, 0));
        endBtn.setForeground(Color.WHITE);
        endBtn.setFont(new Font("Arial", Font.BOLD, 13));
        endBtn.setPreferredSize(new Dimension(120, 45));
        endBtn.setEnabled(false);
        endBtn.addActionListener(e -> endTripAction(tripIdLabel, statusLabel, endTimeLabel, selectTripBtn, startBtn, endBtn, tableModel));
        
        actionPanel.add(selectTripBtn);
        actionPanel.add(startBtn);
        actionPanel.add(endBtn);
        
        rightPanel.add(actionPanel, BorderLayout.CENTER);
        
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Load scheduled trips on panel creation
        loadScheduledTripsForTable(tableModel);
        
        return panel;
    }
    private void loadScheduledTripsForTable(DefaultTableModel tableModel) {
        SwingWorker<java.util.List<Trip>, Void> worker = new SwingWorker<java.util.List<Trip>, Void>() {
            @Override
            protected java.util.List<Trip> doInBackground() throws Exception {
                java.util.List<Trip> allTrips = tripDAO.getTripsByDriver(driver.getUserId());
                java.util.List<Trip> scheduledTrips = new java.util.ArrayList<>();
                
                // Include SCHEDULED and IN_PROGRESS trips
                for (Trip trip : allTrips) {
                    if ("SCHEDULED".equals(trip.getStatus()) || "IN_PROGRESS".equals(trip.getStatus())) {
                        scheduledTrips.add(trip);
                    }
                }
                return scheduledTrips;
            }
            
            @Override
            protected void done() {
                try {
                    java.util.List<Trip> trips = get();
                    tableModel.setRowCount(0);
                    
                    for (Trip trip : trips) {
                        tableModel.addRow(new Object[]{
                            trip.getTripId(),
                            trip.getRouteName() != null ? trip.getRouteName() : "N/A",
                            trip.getTripDate(),
                            trip.getBusNumber() != null ? trip.getBusNumber() : "N/A",
                            trip.getStatus(),
                            trip.getStatus()
                        });
                    }
                    
                    if (trips.isEmpty()) {
                        JOptionPane.showMessageDialog(DriverDashboard.this,
                            "No scheduled trips assigned to you.\nWait for Admin to assign routes.",
                            "No Scheduled Trips",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DriverDashboard.this,
                        "Error loading scheduled trips: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // NEW METHOD: Handle trip selection
    private void onTripSelected(int row, DefaultTableModel tableModel) {
        // This is called when a row is clicked
    }

    // NEW METHOD: Select trip from table
    private void selectTripAction(
            JTable tripsTable, DefaultTableModel tableModel,
            JLabel tripIdLabel, JLabel routeLabel, JLabel dateLabel,
            JLabel busLabel, JLabel statusLabel,
            JLabel startTimeLabel, JLabel endTimeLabel,
            JButton startTripBtn, JButton endTripBtn
    ) {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a trip first!");
            return;
        }

        int tripId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            this.currentTrip = tripDAO.getTripById(tripId);
            if (currentTrip != null) {

                tripIdLabel.setText("Trip ID: " + currentTrip.getTripId());
                routeLabel.setText("Route: " + currentTrip.getRouteName());
                dateLabel.setText("Date: " + currentTrip.getTripDate());
                busLabel.setText("Bus: " + currentTrip.getBusNumber());
                statusLabel.setText("Status: " + currentTrip.getStatus());
                startTimeLabel.setText("Start Time: " + currentTrip.getStartTime());
                endTimeLabel.setText("End Time: " + currentTrip.getEndTime());

                // 🔥 Enable / Disable buttons based on status
                String status = currentTrip.getStatus();

                if ("SCHEDULED".equals(status)) {
                    startTripBtn.setEnabled(true);
                    endTripBtn.setEnabled(false);
                }
                else if ("IN_PROGRESS".equals(status)) {
                    startTripBtn.setEnabled(false);
                    endTripBtn.setEnabled(true);
                }
                else {
                    // COMPLETED, CANCELLED, etc.
                    startTripBtn.setEnabled(false);
                    endTripBtn.setEnabled(false);
                }

                JOptionPane.showMessageDialog(null, "Trip selected successfully!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load trip details!");
        }
    }


    // NEW METHOD: Start trip action
    private void startTripAction(JLabel tripIdLabel, JLabel statusLabel, JLabel startTimeLabel,
                                JButton selectTripBtn, JButton startBtn, JButton endBtn,
                                JLabel routeLabel, JLabel dateLabel, JLabel busLabel,
                                JLabel endTimeLabel, DefaultTableModel tableModel) {
        if (currentTrip == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a trip first.",
                "No Trip Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!"SCHEDULED".equals(currentTrip.getStatus())) {
            JOptionPane.showMessageDialog(this,
                "Only SCHEDULED trips can be started.",
                "Cannot Start",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Start Trip?\n\n" +
            "Trip ID: " + currentTrip.getTripId() +
            "\nRoute: " + currentTrip.getRouteName() +
            "\nBus: " + currentTrip.getBusNumber(),
            "Confirm Trip Start",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (tripDAO.startTrip(currentTrip.getTripId())) {
                    currentTrip.setStatus("IN_PROGRESS");
                    statusLabel.setText("IN_PROGRESS");
                    statusLabel.setForeground(new Color(255, 140, 0));
                    startTimeLabel.setText(new java.sql.Timestamp(System.currentTimeMillis()).toString());
                    
                    startBtn.setEnabled(false);
                    endBtn.setEnabled(true);
                    
                    JOptionPane.showMessageDialog(this,
                        "Trip started successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadScheduledTripsForTable(tableModel);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error starting trip: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // NEW METHOD: End trip action
    private void endTripAction(JLabel tripIdLabel, JLabel statusLabel, JLabel endTimeLabel,
                              JButton selectTripBtn, JButton startBtn, JButton endBtn,
                              DefaultTableModel tableModel) {
        if (currentTrip == null || !"IN_PROGRESS".equals(currentTrip.getStatus())) {
            JOptionPane.showMessageDialog(this,
                "No active trip to end.",
                "No Active Trip",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "End Trip?\n\n" +
            "Trip ID: " + currentTrip.getTripId() +
            "\nRoute: " + currentTrip.getRouteName(),
            "Confirm Trip End",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (tripDAO.endTrip(currentTrip.getTripId())) {
                    currentTrip.setStatus("COMPLETED");
                    statusLabel.setText("COMPLETED");
                    statusLabel.setForeground(new Color(0, 153, 76));
                    endTimeLabel.setText(new java.sql.Timestamp(System.currentTimeMillis()).toString());
                    
                    endBtn.setEnabled(false);
                    startBtn.setEnabled(false);
                    
                    JOptionPane.showMessageDialog(this,
                        "Trip ended successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadScheduledTripsForTable(tableModel);
                    currentTrip = null;
                    tripIdLabel.setText("Not Selected");
                    statusLabel.setText("No Trip Selected");
                    statusLabel.setForeground(new Color(255, 140, 0));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error ending trip: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void showStartTripDialog() {
        JDialog dialog = new JDialog(this, "Start New Trip", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(CARD_BG);
        
        JLabel titleLabel = new JLabel("Select Route Assignment to Start");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Get driver's assigned routes
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(CARD_BG);
        
        String[] columnNames = {"ID", "Route", "Bus", "Departure", "Days"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable assignmentsTable = new JTable(model);
        styleTable(assignmentsTable);
        JScrollPane scrollPane = new JScrollPane(assignmentsTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Load assignments
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Get bus assigned to driver
                Bus driverBus = busDAO.getBusById(driver.getAssignedBusId());
                if (driverBus != null) {
                    List<RouteAssignment> assignments = assignmentDAO.getAssignmentsByBus(driverBus.getBusId());
                    for (RouteAssignment ra : assignments) {
                        model.addRow(new Object[]{
                            ra.getAssignmentId(),
                            ra.getRouteName(),
                            ra.getBusNumber(),
                            ra.getDepartureTime(),
                            ra.getDaysOfWeek()
                        });
                    }
                }
                return null;
            }
        };
        worker.execute();
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton startBtn = createStyledButton("Start Selected Trip", SUCCESS_COLOR, 180, 45);
        startBtn.addActionListener(e -> {
            int selectedRow = assignmentsTable.getSelectedRow();
            if (selectedRow == -1) {
                showMessage("Please select a route assignment", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int assignmentId = (int) model.getValueAt(selectedRow, 0);
            startNewTrip();
            dialog.dispose();
        });
        
        JButton cancelBtn = createStyledButton("Cancel", TEXT_SECONDARY, 120, 45);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(startBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    private void loadScheduledTrips(DefaultTableModel tableModel) {
        SwingWorker<List<Trip>, Void> worker = new SwingWorker<List<Trip>, Void>() {
            @Override
            protected List<Trip> doInBackground() throws Exception {
                List<Trip> allTrips = tripDAO.getTripsByDriver(driver.getUserId());
                java.util.List<Trip> scheduledTrips = new java.util.ArrayList<>();
                
                // Filter only SCHEDULED trips
                for (Trip trip : allTrips) {
                    if ("SCHEDULED".equals(trip.getStatus())) {
                        scheduledTrips.add(trip);
                    }
                }
                return scheduledTrips;
            }
            
            @Override
            protected void done() {
                try {
                    List<Trip> trips = get();
                    tableModel.setRowCount(0);
                    
                    if (trips.isEmpty()) {
                        JOptionPane.showMessageDialog(DriverDashboard.this,
                            "No scheduled trips assigned to you.\n\n" +
                            "Wait for Admin to assign routes.",
                            "No Scheduled Trips",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    for (Trip trip : trips) {
                        tableModel.addRow(new Object[]{
                            trip.getTripId(),
                            trip.getTripDate(),
                            trip.getRouteName(),
                            trip.getBusNumber(),
                            trip.getStatus(),
                            trip.getStartTime() != null ? trip.getStartTime() : "Not Started",
                            trip.getEndTime() != null ? trip.getEndTime() : "Not Ended",
                            "Start"  // Action button
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DriverDashboard.this,
                        "Error loading scheduled trips: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // NEW METHOD - Handle trip start from scheduled list
    private void startScheduledTrip(int tripId, DefaultTableModel tableModel) {
        try {
            if (tripDAO.startTrip(tripId)) {
                JOptionPane.showMessageDialog(this,
                    "Trip started successfully!\n" +
                    "Trip ID: " + tripId,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadScheduledTrips(tableModel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error starting trip: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
 // REPLACE ONLY THIS METHOD in DriverDashboard.java
    private void startNewTrip() {
        // Create and show the Start New Trip dialog
        JDialog tripDialog = new JDialog(this, "Start New Trip", true);
        tripDialog.setSize(900, 700);
        tripDialog.setLocationRelativeTo(this);
        
        // Create the panel
        StartNewTripPanel startTripPanel = new StartNewTripPanel(driver);
        tripDialog.add(startTripPanel);
        
        tripDialog.setVisible(true);
    }
    
    private void checkActiveTrip() {
        SwingWorker<Trip, Void> worker = new SwingWorker<Trip, Void>() {
            @Override
            protected Trip doInBackground() throws Exception {
                List<Trip> trips = tripDAO.getTripsByDriver(driver.getUserId());
                for (Trip trip : trips) {
                    if ("IN_PROGRESS".equals(trip.getStatus())) {
                        return trip;
                    }
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    currentTrip = get();
                    updateActiveTripUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void updateActiveTripUI() {
        if (currentTrip != null && "IN_PROGRESS".equals(currentTrip.getStatus())) {
            activeTripStatusLabel.setText("🚌 Trip In Progress");
            activeTripStatusLabel.setForeground(SUCCESS_COLOR);
            
            String tripInfo = String.format(
                "Trip ID: %d\n" +
                "Route: %s\n" +
                "Bus: %s\n" +
                "Date: %s\n" +
                "Start Time: %s\n" +
                "Status: IN PROGRESS\n" +
                "Passengers: %d\n\n" +
                "Current Location: %s\n" +
                "GPS: %.6f, %.6f",
                currentTrip.getTripId(),
                currentTrip.getRouteName(),
                currentTrip.getBusNumber(),
                currentTrip.getTripDate(),
                currentTrip.getStartTime(),
                currentTrip.getPassengersCount(),
                currentTrip.getCurrentStop() != null ? currentTrip.getCurrentStop() : "Not updated",
                currentTrip.getCurrentLocationLat(),
                currentTrip.getCurrentLocationLng()
            );
            
            tripInfoArea.setText(tripInfo);
            activeTripDetailsPanel.setVisible(true);
            
            startTripBtn.setEnabled(false);
            updateLocationBtn.setEnabled(true);
            endTripBtn.setEnabled(true);
        } else {
            activeTripStatusLabel.setText("No active trip");
            activeTripStatusLabel.setForeground(TEXT_SECONDARY);
            activeTripDetailsPanel.setVisible(false);
            
            startTripBtn.setEnabled(true);
            updateLocationBtn.setEnabled(false);
            endTripBtn.setEnabled(false);
        }
    }
    
    private void updateTripLocation() {
        JDialog dialog = new JDialog(this, "Update Trip Location", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Update Location Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, gbc);
        
        // Latitude
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Latitude:"), gbc);
        
        JTextField latField = new JTextField(String.valueOf(currentTrip.getCurrentLocationLat()));
        gbc.gridx = 1;
        panel.add(latField, gbc);
        
        // Longitude
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Longitude:"), gbc);
        
        JTextField lngField = new JTextField(String.valueOf(currentTrip.getCurrentLocationLng()));
        gbc.gridx = 1;
        panel.add(lngField, gbc);
        
        // Current Stop
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Current Stop:"), gbc);
        
        JTextField stopField = new JTextField(currentTrip.getCurrentStop());
        gbc.gridx = 1;
        panel.add(stopField, gbc);
        
        // Auto-generate button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton autoGenBtn = createStyledButton("🎲 Generate Random GPS", INFO_COLOR, 250, 40);
        autoGenBtn.addActionListener(e -> {
            // Generate random GPS coordinates (simulation)
            double baseLat = 33.6844;  // Rawalpindi base
            double baseLng = 73.0479;
            double lat = baseLat + (Math.random() - 0.5) * 0.1;
            double lng = baseLng + (Math.random() - 0.5) * 0.1;
            latField.setText(String.format("%.6f", lat));
            lngField.setText(String.format("%.6f", lng));
        });
        panel.add(autoGenBtn, gbc);
        
        // Buttons
        gbc.gridy = 5;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton updateBtn = createStyledButton("Update", SUCCESS_COLOR, 120, 40);
        updateBtn.addActionListener(e -> {
            try {
                double lat = Double.parseDouble(latField.getText());
                double lng = Double.parseDouble(lngField.getText());
                String stop = stopField.getText();
                
                if (tripDAO.updateTripLocation(currentTrip.getTripId(), lat, lng, stop)) {
                    showMessage("Location updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    currentTrip.setCurrentLocationLat(lat);
                    currentTrip.setCurrentLocationLng(lng);
                    currentTrip.setCurrentStop(stop);
                    updateActiveTripUI();
                    dialog.dispose();
                }
            } catch (Exception ex) {
                showMessage("Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelBtn = createStyledButton("Cancel", TEXT_SECONDARY, 120, 40);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void endTrip() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to end this trip?\n\nTrip ID: " + currentTrip.getTripId(),
            "Confirm End Trip",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return tripDAO.endTrip(currentTrip.getTripId());
                }
                
                @Override
                protected void done() {
                    try {
                        Boolean success = get();
                        if (success) {
                            showMessage(
                                "Trip ended successfully!\n\nThank you for your safe driving!",
                                "Trip Completed",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            driver.completeTrip();
                            currentTrip = null;
                            updateActiveTripUI();
                            loadDriverData();
                        } else {
                            showMessage("Failed to end trip", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        showMessage("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            };
            worker.execute();
        }
    }
    
    private JPanel createTripsHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(LIGHT_BG);
        
        JPanel headerPanel = createSectionHeader("📋 Trip History", 
            "Your completed journeys", INFO_COLOR);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(LIGHT_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(CARD_BG);
        tableContainer.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1));
        
        String[] columnNames = {"Trip ID", "Date", "Route", "Bus", "Status", "Start Time", "End Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tripsTable = new JTable(tableModel);
        styleTable(tripsTable);
        
        JScrollPane scrollPane = new JScrollPane(tripsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BG);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(tableContainer, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton refreshBtn = createStyledButton("🔄 Refresh", INFO_COLOR, 120, 40);
        refreshBtn.addActionListener(e -> loadTrips(tableModel));
        buttonPanel.add(refreshBtn);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        loadTrips(tableModel);
        
        return panel;
    }
    
    private void loadTrips(DefaultTableModel tableModel) {
        SwingWorker<List<Trip>, Void> worker = new SwingWorker<List<Trip>, Void>() {
            @Override
            protected List<Trip> doInBackground() throws Exception {
                return tripDAO.getTripsByDriver(driver.getUserId());
            }
            
            @Override
            protected void done() {
                try {
                    List<Trip> trips = get();
                    tableModel.setRowCount(0);
                    
                    for (Trip trip : trips) {
                        tableModel.addRow(new Object[]{
                            trip.getTripId(),
                            trip.getTripDate(),
                            trip.getRouteName(),
                            trip.getBusNumber(),
                            trip.getStatus(),
                            trip.getStartTime(),
                            trip.getEndTime()
                        });
                    }
                } catch (Exception e) {
                    showMessage("Error loading trips: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private JPanel createBusInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(LIGHT_BG);
        
        JPanel headerPanel = createSectionHeader("🚍 My Bus Information", 
            "Details about your assigned bus", PRIMARY_COLOR);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(LIGHT_BG);
        
        JPanel busCard = new JPanel(new GridBagLayout());
        busCard.setBackground(CARD_BG);
        busCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Load bus info
        SwingWorker<Bus, Void> worker = new SwingWorker<Bus, Void>() {
            @Override
            protected Bus doInBackground() throws Exception {
                if (driver.getAssignedBusId() > 0) {
                    return busDAO.getBusById(driver.getAssignedBusId());
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    Bus bus = get();
                    if (bus != null) {
                        addModernProfileField(busCard, "Bus Number", bus.getBusNumber(), gbc, 0);
                        addModernProfileField(busCard, "Bus Type", bus.getBusType(), gbc, 1);
                        addModernProfileField(busCard, "Capacity", String.valueOf(bus.getCapacity()), gbc, 2);
                        addModernProfileField(busCard, "Registration", bus.getRegistrationNumber(), gbc, 3);
                        addModernProfileField(busCard, "Status", bus.getStatus(), gbc, 4);
                    } else {
                        gbc.gridx = 0;
                        gbc.gridy = 0;
                        gbc.gridwidth = 2;
                        JLabel noDataLabel = new JLabel("⚠️ No bus assigned yet");
                        noDataLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                        noDataLabel.setForeground(TEXT_SECONDARY);
                        busCard.add(noDataLabel, gbc);
                    }
                } catch (Exception e) {
                    showMessage("Error loading bus info: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
        
        contentPanel.add(busCard);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(LIGHT_BG);
        
        JPanel headerPanel = createSectionHeader("👤 My Profile", 
            "Your account information", SECONDARY_COLOR);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(LIGHT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        
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
        
        addModernProfileField(profileCard, "Username", driver.getUsername(), cardGbc, 0);
        addModernProfileField(profileCard, "Full Name", driver.getFullName(), cardGbc, 1);
        addModernProfileField(profileCard, "Email", driver.getEmail(), cardGbc, 2);
        addModernProfileField(profileCard, "Phone", driver.getPhone(), cardGbc, 3);
        addModernProfileField(profileCard, "License Number", 
            driver.getLicenseNumber() != null ? driver.getLicenseNumber() : "N/A", cardGbc, 4);
        addModernProfileField(profileCard, "Status", driver.getStatus(), cardGbc, 5);
        addModernProfileField(profileCard, "Total Trips", String.valueOf(driver.getTotalTripsCompleted()), cardGbc, 6);
        addModernProfileField(profileCard, "Rating", String.format("%.2f ⭐", driver.getAverageRating()), cardGbc, 7);
        
        // Logout button
        cardGbc.gridy = 8;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(25, 12, 12, 12);
        
        JButton logoutBtn = createStyledButton("🚪 Logout", DANGER_COLOR, 200, 45);
        logoutBtn.addActionListener(e -> logout());
        profileCard.add(logoutBtn, cardGbc);
        
        contentPanel.add(profileCard);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Helper methods for UI components
    
    private JPanel createModernStatCard(String title, String value, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 30), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
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
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(new Color(accentColor.getRed(), accentColor.getGreen(), 
                                         accentColor.getBlue(), 50));
        
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);
        
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
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
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
        table.setSelectionBackground(new Color(230, 126, 34, 50));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(0, 0, 0, 10));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        }
        
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
    
    private void loadDriverData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            int totalTrips;
            int completedTrips;
            
            @Override
            protected Void doInBackground() throws Exception {
                totalTrips = tripDAO.getCompletedTripsByDriver(driver.getUserId());
                
                List<Trip> allTrips = tripDAO.getTripsByDriver(driver.getUserId());
                completedTrips = (int) allTrips.stream()
                    .filter(t -> "COMPLETED".equals(t.getStatus()))
                    .count();
                
                driver.setTotalTripsCompleted(totalTrips);
                
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
        if (totalTripsValue != null) {
            totalTripsValue.setText(String.valueOf(driver.getTotalTripsCompleted()));
        }
        if (completedTripsValue != null) {
            completedTripsValue.setText(String.valueOf(driver.getTotalTripsCompleted()));
        }
        if (ratingValue != null) {
            ratingValue.setText(String.format("%.1f", driver.getAverageRating()));
        }
    }
    
    private void showMessage(String message, String title, int messageType) {
        UIManager.put("OptionPane.background", CARD_BG);
        UIManager.put("Panel.background", CARD_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        
        JOptionPane.showMessageDialog(this, message, title, messageType);
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