package ui;

import bl.*;
import dal.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * StartNewTripPanel - UI for drivers to start new trips from available route assignments
 */
public class StartNewTripPanel extends JPanel {
    
    private Driver driver;
    private RouteAssignmentDAO assignmentDAO;
    private TripDAO tripDAO;
    private RouteDAO routeDAO;
    private BusDAO busDAO;
    
    private JComboBox<RouteAssignmentItem> assignmentCombo;
    private JSpinner dateSpinner;
    private JTable tripDetailsTable;
    private DefaultTableModel tripDetailsModel;
    private JButton selectTripBtn;
    private JButton refreshBtn;
    private JLabel routeDetailsLabel;
    
    private RouteAssignment selectedAssignment;
    
    public StartNewTripPanel(Driver driver) {
        this.driver = driver;
        this.assignmentDAO = new RouteAssignmentDAO();
        this.tripDAO = new TripDAO();
        this.routeDAO = new RouteDAO();
        this.busDAO = new BusDAO();
        
        initializeUI();
        loadAvailableAssignments();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Start New Trip");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);
        
        // Selection Panel
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Select Trip Details"));
        selectionPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Route Assignment Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        selectionPanel.add(new JLabel("Select Route Assignment:"), gbc);
        
        assignmentCombo = new JComboBox<>();
        assignmentCombo.addActionListener(e -> onAssignmentSelected());
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        selectionPanel.add(assignmentCombo, gbc);
        gbc.gridwidth = 1;
        
        // Date Selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        selectionPanel.add(new JLabel("Trip Date:"), gbc);
        
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new java.util.Date());
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        selectionPanel.add(dateSpinner, gbc);
        gbc.gridwidth = 1;
        
        // Route Details
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        routeDetailsLabel = new JLabel("<html>Select a route to view details</html>");
        routeDetailsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        routeDetailsLabel.setForeground(new Color(0, 102, 204));
        selectionPanel.add(routeDetailsLabel, gbc);
        
        // Refresh Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        refreshBtn = new JButton("Refresh Routes");
        refreshBtn.setBackground(new Color(0, 102, 204));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> loadAvailableAssignments());
        selectionPanel.add(refreshBtn, gbc);
        
        // Select Trip Button
        gbc.gridx = 2;
        selectTripBtn = new JButton("Start Selected Trip");
        selectTripBtn.setBackground(new Color(0, 153, 76));
        selectTripBtn.setForeground(Color.WHITE);
        selectTripBtn.setFont(new Font("Arial", Font.BOLD, 12));
        selectTripBtn.addActionListener(e -> startSelectedTrip());
        selectTripBtn.setEnabled(false);
        selectionPanel.add(selectTripBtn, gbc);
        
        add(selectionPanel, BorderLayout.NORTH);
        
        // Trip Details Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Trip Information"));
        tablePanel.setBackground(Color.WHITE);
        
        String[] columns = {"Property", "Value"};
        tripDetailsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tripDetailsTable = new JTable(tripDetailsModel);
        tripDetailsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        tripDetailsTable.setRowHeight(25);
        tripDetailsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tripDetailsTable.getTableHeader().setBackground(new Color(0, 102, 204));
        tripDetailsTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(tripDetailsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
    }
    
    private void loadAvailableAssignments() {
        SwingWorker<List<RouteAssignment>, Void> worker = new SwingWorker<List<RouteAssignment>, Void>() {
            @Override
            protected List<RouteAssignment> doInBackground() throws Exception {
                
                // Get the bus assigned to this driver
                Bus driverBus = busDAO.getBusById(driver.getAssignedBusId());
                
                if (driverBus == null) {
                    throw new Exception("No bus assigned to driver.");
                }
                
                // Load only assignments related to that bus
                return assignmentDAO.getAssignmentsByBus(driverBus.getBusId());
            }
            
            @Override
            protected void done() {
                try {
                    List<RouteAssignment> assignments = get();
                    assignmentCombo.removeAllItems();
                    
                    if (assignments.isEmpty()) {
                        assignmentCombo.addItem(new RouteAssignmentItem(
                            null, "No active routes assigned to you"
                        ));
                        selectTripBtn.setEnabled(false);
                    } else {
                        for (RouteAssignment assignment : assignments) {
                            assignmentCombo.addItem(new RouteAssignmentItem(
                                assignment,
                                String.format("%s (%s → %s) at %s",
                                    assignment.getRouteName(),
                                    assignment.getRouteOrigin(),
                                    assignment.getRouteDestination(),
                                    assignment.getDepartureTime())
                            ));
                        }
                        selectTripBtn.setEnabled(true);
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StartNewTripPanel.this,
                        "Error loading routes: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    
    private void onAssignmentSelected() {
        tripDetailsModel.setRowCount(0);
        
        RouteAssignmentItem item = (RouteAssignmentItem) assignmentCombo.getSelectedItem();
        if (item == null || item.getAssignment() == null) {
            routeDetailsLabel.setText("<html>No route selected</html>");
            selectTripBtn.setEnabled(false);
            return;
        }
        
        selectedAssignment = item.getAssignment();
        
        // Display route details
        routeDetailsLabel.setText(String.format(
            "<html><b>Route:</b> %s | <b>From:</b> %s | <b>To:</b> %s | <b>Fare:</b> Rs. %.2f | <b>Days:</b> %s</html>",
            selectedAssignment.getRouteName(),
            selectedAssignment.getRouteOrigin(),
            selectedAssignment.getRouteDestination(),
            selectedAssignment.getFare(),
            selectedAssignment.getDaysOfWeek()
        ));
        
        // Populate trip details table
        tripDetailsModel.addRow(new Object[]{"Route Name", selectedAssignment.getRouteName()});
        tripDetailsModel.addRow(new Object[]{"Bus Number", selectedAssignment.getBusNumber()});
        tripDetailsModel.addRow(new Object[]{"Origin", selectedAssignment.getRouteOrigin()});
        tripDetailsModel.addRow(new Object[]{"Destination", selectedAssignment.getRouteDestination()});
        tripDetailsModel.addRow(new Object[]{"Departure Time", selectedAssignment.getDepartureTime()});
        tripDetailsModel.addRow(new Object[]{"Arrival Time", selectedAssignment.getArrivalTime()});
        tripDetailsModel.addRow(new Object[]{"Fare", "Rs. " + String.format("%.2f", selectedAssignment.getFare())});
        tripDetailsModel.addRow(new Object[]{"Operating Days", selectedAssignment.getDaysOfWeek()});
        tripDetailsModel.addRow(new Object[]{"Trip Date", dateSpinner.getValue().toString()});
        tripDetailsModel.addRow(new Object[]{"Status", "SCHEDULED"});
        
        selectTripBtn.setEnabled(true);
    }
    
    private void startSelectedTrip() {
        if (selectedAssignment == null) {
            JOptionPane.showMessageDialog(this, "Please select a route assignment.");
            return;
        }
        
        java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        
        // Create confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Start Trip?\n\n" +
                        "Route: %s\n" +
                        "From: %s to %s\n" +
                        "Bus: %s\n" +
                        "Date: %s\n" +
                        "Departure: %s",
                selectedAssignment.getRouteName(),
                selectedAssignment.getRouteOrigin(),
                selectedAssignment.getRouteDestination(),
                selectedAssignment.getBusNumber(),
                sqlDate,
                selectedAssignment.getDepartureTime()),
            "Confirm Trip Start",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Create and save trip
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            Trip newTrip;
            
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Create new trip object
                    newTrip = new Trip(
                        selectedAssignment.getAssignmentId(),
                        driver.getUserId(),
                        sqlDate
                    );
                    newTrip.setStatus("SCHEDULED");
                    
                    // Save trip to database
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
                        JOptionPane.showMessageDialog(StartNewTripPanel.this,
                            String.format("Trip created successfully!\n\n" +
                                        "Trip ID: %d\n" +
                                        "Route: %s\n" +
                                        "Status: SCHEDULED\n\n" +
                                        "You can now start the trip from the Active Trip panel.",
                                        newTrip.getTripId(),
                                        selectedAssignment.getRouteName()),
                            "Trip Created",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Reset form
                        assignmentCombo.setSelectedIndex(0);
                        dateSpinner.setValue(new java.util.Date());
                        tripDetailsModel.setRowCount(0);
                        
                    } else {
                        JOptionPane.showMessageDialog(StartNewTripPanel.this,
                            "Failed to create trip. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StartNewTripPanel.this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Inner class to hold RouteAssignment with custom display string
     */
    private static class RouteAssignmentItem {
        private RouteAssignment assignment;
        private String displayText;
        
        public RouteAssignmentItem(RouteAssignment assignment, String displayText) {
            this.assignment = assignment;
            this.displayText = displayText;
        }
        
        public RouteAssignment getAssignment() {
            return assignment;
        }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
}