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
 * DriverDashboard - Main interface for driver users
 */
public class DriverDashboard extends JFrame {
    
    private Driver driver;
    private JTabbedPane tabbedPane;
    private TripDAO tripDAO;
    private Trip currentTrip;
    
 // Modern UI Colors
    private static final Color PRIMARY = new Color(255, 140, 0);
    private static final Color SUCCESS = new Color(46, 204, 113);
    private static final Color INFO = new Color(52, 152, 219);
    private static final Color DANGER = new Color(231, 76, 60);
    private static final Color CARD_BG = Color.WHITE;

    // Modern Button
 // Modern Button
    private JButton createModernButton(String text, Color color, int w, int h) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color c = getModel().isPressed() ? color.darker()
                        : getModel().isRollover() ? color.brighter()
                        : color;

                // draw rounded background
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();

                // now let JButton paint the text, focus, etc.
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(w, h));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.CENTER);

        return btn;
    }


    // Modern Table
    private JTable createStyledTable(DefaultTableModel model, Color lineColor) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(220,220,220));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setOpaque(true);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, lineColor));

        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        return table;
    }

    
    public DriverDashboard(Driver driver) {
        this.driver = driver;
        this.tripDAO = new TripDAO();
        initializeUI();
        loadDriverData();
    }
    
    private void initializeUI() {
        setTitle("Driver Dashboard - " + driver.getFullName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        tabbedPane.addTab("Home", createHomePanel());
        tabbedPane.addTab("Active Trip", createActiveTripPanel());
        tabbedPane.addTab("My Trips", createTripsHistoryPanel());
        tabbedPane.addTab("Profile", createProfilePanel());
        
        add(tabbedPane);
    }
    
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(255, 140, 0));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("Driver Portal - " + driver.getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        
        JLabel subtitleLabel = new JLabel("Manage your trips and routes");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(Color.WHITE);
        welcomePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        panel.add(welcomePanel, BorderLayout.NORTH);
        
        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        statsPanel.setBackground(Color.WHITE);
        
        JPanel totalTripsCard = createStatCard("Total Trips", "0", new Color(0, 153, 76));
        JPanel completedTripsCard = createStatCard("Completed", "0", new Color(0, 102, 204));
        JPanel ratingCard = createStatCard("Rating", "5.0", new Color(255, 153, 0));
        
        statsPanel.add(totalTripsCard);
        statsPanel.add(completedTripsCard);
        statsPanel.add(ratingCard);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        actionsPanel.setBackground(Color.WHITE);
        
        JButton startTripBtn = createModernButton("Start New Trip", SUCCESS, 180, 50);
        startTripBtn.addActionListener(e -> startNewTrip());
        
        JButton viewTripsBtn = createModernButton("View Trip History", INFO, 180, 50);
        viewTripsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        actionsPanel.add(startTripBtn);
        actionsPanel.add(viewTripsBtn);
        
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
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
        
        // Trip details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Trip Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel statusLabel = new JLabel("Status: No active trip");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        detailsPanel.add(statusLabel, gbc);
        
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton startBtn = createModernButton("Start Trip", SUCCESS, 140, 40);
        startBtn.setBackground(new Color(0, 153, 76));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFont(new Font("Arial", Font.BOLD, 14));
        startBtn.setPreferredSize(new Dimension(140, 40));
        startBtn.addActionListener(e -> startTrip());
        
        JButton updateLocationBtn = createModernButton("Update Location", INFO, 140, 40);
        updateLocationBtn.setBackground(new Color(0, 102, 204));
        updateLocationBtn.setForeground(Color.WHITE);
        updateLocationBtn.setFont(new Font("Arial", Font.BOLD, 14));
        updateLocationBtn.setPreferredSize(new Dimension(140, 40));
        updateLocationBtn.addActionListener(e -> updateLocation());
        
        JButton endBtn = createModernButton("End Trip", DANGER, 140, 40);
        endBtn.setBackground(new Color(204, 0, 0));
        endBtn.setForeground(Color.WHITE);
        endBtn.setFont(new Font("Arial", Font.BOLD, 14));
        endBtn.setPreferredSize(new Dimension(140, 40));
        endBtn.addActionListener(e -> endTrip());
        
        buttonPanel.add(startBtn);
        buttonPanel.add(updateLocationBtn);
        buttonPanel.add(endBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTripsHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Trip History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table Model
        String[] columnNames = {"Trip ID", "Date", "Route", "Bus", "Status", "Start Time", "End Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // ✅ Create Styled Table (NO EXTRA HEADER STYLING HERE)
        JTable tripsTable = createStyledTable(tableModel, PRIMARY);

        // ✅ Ensure white background so headers are visible
        tripsTable.setBackground(Color.WHITE);

        // ✅ ScrollPane with white viewport
        JScrollPane scrollPane = new JScrollPane(tripsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        // ✅ Refresh Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshBtn = createModernButton("Refresh", INFO, 120, 35);
        refreshBtn.addActionListener(e -> loadTrips(tableModel));
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // ✅ Load Data into Table
        loadTrips(tableModel);

        return panel;
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
        titleLabel.setForeground(new Color(255, 140, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        addProfileField(panel, "Username:", driver.getUsername(), gbc);
        gbc.gridy = 2;
        addProfileField(panel, "Full Name:", driver.getFullName(), gbc);
        gbc.gridy = 3;
        addProfileField(panel, "Email:", driver.getEmail(), gbc);
        gbc.gridy = 4;
        addProfileField(panel, "Phone:", driver.getPhone(), gbc);
        gbc.gridy = 5;
        addProfileField(panel, "License:", driver.getLicenseNumber(), gbc);
        gbc.gridy = 6;
        addProfileField(panel, "Rating:", String.format("%.2f", driver.getAverageRating()), gbc);
        
        JButton logoutBtn = createModernButton("Logout", DANGER, 150, 45);
        logoutBtn.setBackground(new Color(204, 0, 0));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        gbc.gridy = 7;
        gbc.gridx = 1;
        panel.add(logoutBtn, gbc);
        
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
        return button;
    }
    
    private void addProfileField(JPanel panel, String label, String value, GridBagConstraints gbc) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        panel.add(labelComp, gbc);
        
        JLabel valueComp = new JLabel(value != null ? value : "N/A");
        valueComp.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(valueComp, gbc);
    }
    
    private void loadDriverData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                int totalTrips = tripDAO.getCompletedTripsByDriver(driver.getUserId());
                driver.setTotalTripsCompleted(totalTrips);
                return null;
            }
        };
        worker.execute();
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
                    JOptionPane.showMessageDialog(DriverDashboard.this,
                        "Error loading trips: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void startNewTrip() {
        JOptionPane.showMessageDialog(this,
            "Trip creation functionality will be implemented here.\n" +
            "This will allow selecting a route assignment and starting a new trip.",
            "Start New Trip",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void startTrip() {
        if (currentTrip == null) {
            JOptionPane.showMessageDialog(this,
                "No trip selected. Please create a new trip first.",
                "No Trip",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (tripDAO.startTrip(currentTrip.getTripId())) {
                JOptionPane.showMessageDialog(this,
                    "Trip started successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                currentTrip.setStatus("IN_PROGRESS");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error starting trip: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateLocation() {
        JOptionPane.showMessageDialog(this,
            "Location update functionality.\n" +
            "GPS coordinates and current stop will be updated here.",
            "Update Location",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void endTrip() {
        if (currentTrip == null || !"IN_PROGRESS".equals(currentTrip.getStatus())) {
            JOptionPane.showMessageDialog(this,
                "No active trip to end.",
                "No Active Trip",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to end this trip?",
            "Confirm End Trip",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (tripDAO.endTrip(currentTrip.getTripId())) {
                    JOptionPane.showMessageDialog(this,
                        "Trip ended successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    currentTrip = null;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error ending trip: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
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
