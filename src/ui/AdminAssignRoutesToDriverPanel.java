package ui;

import bl.*;
import dal.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * AdminAssignRoutesToDriverPanel - UI for Admin to assign routes to specific drivers
 */
public class AdminAssignRoutesToDriverPanel extends JPanel {
    
    private UserDAO userDAO;
    private RouteAssignmentDAO assignmentDAO;
    private TripDAO tripDAO;
    private BusDAO busDAO;
    
    private JComboBox<DriverItem> driverCombo;
    private JTable availableRoutesTable;
    private JTable assignedTripsTable;
    private DefaultTableModel availableRoutesModel;
    private DefaultTableModel assignedTripsModel;
    private JButton assignBtn;
    private JButton removeBtn;
    private JButton refreshBtn;
    
    private Driver selectedDriver;
    
    public AdminAssignRoutesToDriverPanel() {
        this.userDAO = new UserDAO();
        this.assignmentDAO = new RouteAssignmentDAO();
        this.tripDAO = new TripDAO();
        this.busDAO = new BusDAO();
        
        initializeUI();
        loadAvailableDrivers();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Assign Routes to Drivers");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        contentPanel.setBackground(Color.WHITE);
        
        // Left panel - Driver selection and available routes
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available Routes"));
        
        // Driver selection
        JPanel driverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        driverPanel.setBackground(Color.WHITE);
        driverPanel.add(new JLabel("Select Driver:"));
        
        driverCombo = new JComboBox<>();
        driverCombo.addActionListener(e -> onDriverSelected());
        driverCombo.setPreferredSize(new Dimension(300, 30));
        driverPanel.add(driverCombo);
        
        leftPanel.add(driverPanel, BorderLayout.NORTH);
        
        // Available routes table
        String[] availableColumns = {"Assignment ID", "Route", "From → To", "Bus", "Departure", "Arrival", "Days"};
        availableRoutesModel = new DefaultTableModel(availableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        availableRoutesTable = new JTable(availableRoutesModel);
        availableRoutesTable.setFont(new Font("Arial", Font.PLAIN, 11));
        availableRoutesTable.setRowHeight(22);
        availableRoutesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        availableRoutesTable.getTableHeader().setBackground(new Color(0, 102, 204));
        availableRoutesTable.getTableHeader().setForeground(Color.WHITE);
        availableRoutesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane availableScroll = new JScrollPane(availableRoutesTable);
        leftPanel.add(availableScroll, BorderLayout.CENTER);
        
        // Assign button
        JPanel assignBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        assignBtnPanel.setBackground(Color.WHITE);
        assignBtn = new JButton("Assign Selected Route ➜");
        assignBtn.setBackground(new Color(0, 153, 76));
        assignBtn.setForeground(Color.WHITE);
        assignBtn.setFont(new Font("Arial", Font.BOLD, 12));
        assignBtn.setPreferredSize(new Dimension(200, 40));
        assignBtn.addActionListener(e -> assignRouteToDriver());
        assignBtn.setEnabled(false);
        assignBtnPanel.add(assignBtn);
        leftPanel.add(assignBtnPanel, BorderLayout.SOUTH);
        
        // Right panel - Assigned trips
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Assigned Trips"));
        
        // Assigned trips table
        String[] assignedColumns = {"Trip ID", "Route", "Date", "Bus", "Status", "Start Time", "End Time"};
        assignedTripsModel = new DefaultTableModel(assignedColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        assignedTripsTable = new JTable(assignedTripsModel);
        assignedTripsTable.setFont(new Font("Arial", Font.PLAIN, 11));
        assignedTripsTable.setRowHeight(22);
        assignedTripsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        assignedTripsTable.getTableHeader().setBackground(new Color(255, 140, 0));
        assignedTripsTable.getTableHeader().setForeground(Color.WHITE);
        assignedTripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane assignedScroll = new JScrollPane(assignedTripsTable);
        rightPanel.add(assignedScroll, BorderLayout.CENTER);
        
        // Bottom buttons
        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomBtnPanel.setBackground(Color.WHITE);
        
        refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(0, 102, 204));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> loadAvailableDrivers());
        bottomBtnPanel.add(refreshBtn);
        
        removeBtn = new JButton("Remove Selected Trip");
        removeBtn.setBackground(new Color(204, 0, 0));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.addActionListener(e -> removeAssignedTrip());
        removeBtn.setEnabled(false);
        bottomBtnPanel.add(removeBtn);
        
        rightPanel.add(bottomBtnPanel, BorderLayout.SOUTH);
        
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void loadAvailableDrivers() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return userDAO.getUsersByRole("DRIVER");
            }
            
            @Override
            protected void done() {
                try {
                    List<User> drivers = get();
                    driverCombo.removeAllItems();
                    
                    if (drivers.isEmpty()) {
                        driverCombo.addItem(new DriverItem(null, "No drivers available"));
                        assignBtn.setEnabled(false);
                    } else {
                        for (User user : drivers) {
                            if (user instanceof Driver) {
                                Driver driver = (Driver) user;
                                driverCombo.addItem(new DriverItem(driver, 
                                    driver.getFullName() + " (" + driver.getUsername() + ")"));
                            }
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminAssignRoutesToDriverPanel.this,
                        "Error loading drivers: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void onDriverSelected() {
        availableRoutesModel.setRowCount(0);
        assignedTripsModel.setRowCount(0);
        
        DriverItem item = (DriverItem) driverCombo.getSelectedItem();
        if (item == null || item.getDriver() == null) {
            assignBtn.setEnabled(false);
            removeBtn.setEnabled(false);
            return;
        }
        
        selectedDriver = item.getDriver();
        assignBtn.setEnabled(true);
        
        // Load available routes
        loadAvailableRoutes();
        
        // Load assigned trips
        loadAssignedTrips();
    }
    
    private void loadAvailableRoutes() {
        SwingWorker<List<RouteAssignment>, Void> worker = new SwingWorker<List<RouteAssignment>, Void>() {
            @Override
            protected List<RouteAssignment> doInBackground() throws Exception {
                return assignmentDAO.getActiveAssignments();
            }
            
            @Override
            protected void done() {
                try {
                    List<RouteAssignment> assignments = get();
                    availableRoutesModel.setRowCount(0);
                    
                    for (RouteAssignment assignment : assignments) {
                        availableRoutesModel.addRow(new Object[]{
                            assignment.getAssignmentId(),
                            assignment.getRouteName(),
                            assignment.getRouteOrigin() + " → " + assignment.getRouteDestination(),
                            assignment.getBusNumber(),
                            assignment.getDepartureTime(),
                            assignment.getArrivalTime(),
                            assignment.getDaysOfWeek()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminAssignRoutesToDriverPanel.this,
                        "Error loading routes: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void loadAssignedTrips() {
        SwingWorker<List<Trip>, Void> worker = new SwingWorker<List<Trip>, Void>() {
            @Override
            protected List<Trip> doInBackground() throws Exception {
                return tripDAO.getTripsByDriver(selectedDriver.getUserId());
            }
            
            @Override
            protected void done() {
                try {
                    List<Trip> trips = get();
                    assignedTripsModel.setRowCount(0);
                    
                    for (Trip trip : trips) {
                        assignedTripsModel.addRow(new Object[]{
                            trip.getTripId(),
                            trip.getRouteName(),
                            trip.getTripDate(),
                            trip.getBusNumber(),
                            trip.getStatus(),
                            trip.getStartTime() != null ? trip.getStartTime().toString() : "Not Started",
                            trip.getEndTime() != null ? trip.getEndTime().toString() : "Not Ended"
                        });
                    }
                    
                    if (!trips.isEmpty()) {
                        removeBtn.setEnabled(true);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminAssignRoutesToDriverPanel.this,
                        "Error loading trips: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void assignRouteToDriver() {
        if (selectedDriver == null) {
            JOptionPane.showMessageDialog(this, "Please select a driver first.");
            return;
        }
        
        int selectedRow = availableRoutesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a route to assign.");
            return;
        }
        
        int assignmentId = (Integer) availableRoutesModel.getValueAt(selectedRow, 0);
        String route = (String) availableRoutesModel.getValueAt(selectedRow, 1);
        String routeInfo = (String) availableRoutesModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Assign route to driver?\n\n" +
                        "Driver: %s\n" +
                        "Route: %s\n" +
                        "Details: %s",
                selectedDriver.getFullName(),
                route,
                routeInfo),
            "Confirm Assignment",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Create trip for assigned route
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            Trip newTrip;
            
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Create new trip
                    java.util.Date utilDate = new java.util.Date();
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                    
                    newTrip = new Trip(assignmentId, selectedDriver.getUserId(), sqlDate);
                    newTrip.setStatus("SCHEDULED");
                    
                    return tripDAO.createTrip(newTrip);
                } catch (SQLException e) {
                    System.err.println("Error creating trip: " + e.getMessage());
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(AdminAssignRoutesToDriverPanel.this,
                            String.format("Route assigned successfully!\n\n" +
                                        "Trip ID: %d\n" +
                                        "Driver: %s\n" +
                                        "Route: %s\n" +
                                        "Status: SCHEDULED",
                                newTrip.getTripId(),
                                selectedDriver.getFullName(),
                                route),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        loadAssignedTrips();
                    } else {
                        JOptionPane.showMessageDialog(AdminAssignRoutesToDriverPanel.this,
                            "Failed to assign route. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminAssignRoutesToDriverPanel.this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void removeAssignedTrip() {
        int selectedRow = assignedTripsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a trip to remove.");
            return;
        }
        
        Object tripIdObj = assignedTripsModel.getValueAt(selectedRow, 0);
        if (tripIdObj == null) return;
        
        int tripId = (Integer) tripIdObj;
        String status = (String) assignedTripsModel.getValueAt(selectedRow, 4);
        
        if ("IN_PROGRESS".equals(status) || "COMPLETED".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "Cannot remove trips that are in progress or completed.",
                "Cannot Remove",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove this trip assignment?",
            "Confirm Removal",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    return tripDAO.cancelTrip(tripId);
                } catch (SQLException e) {
                    System.err.println("Error removing trip: " + e.getMessage());
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(AdminAssignRoutesToDriverPanel.this,
                            "Trip cancelled successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadAssignedTrips();
                    } else {
                        JOptionPane.showMessageDialog(AdminAssignRoutesToDriverPanel.this,
                            "Failed to cancel trip.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdminAssignRoutesToDriverPanel.this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Inner class for Driver display
     */
    private static class DriverItem {
        private Driver driver;
        private String displayText;
        
        public DriverItem(Driver driver, String displayText) {
            this.driver = driver;
            this.displayText = displayText;
        }
        
        public Driver getDriver() {
            return driver;
        }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
}