package ui;

import bl.*;
import dal.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * RouteChangeRequestPanel - UI for passengers and drivers to request route changes
 */
public class RouteChangeRequestPanel extends JPanel {
    
    private int userId;
    private String userRole;
    private RouteChangeRequestDAO requestDAO;
    private RouteDAO routeDAO;
    private BookingDAO bookingDAO;
    private TripDAO tripDAO;
    private RouteAssignmentDAO assignmentDAO;
    private JTable requestsTable;
    private JButton submitRequestBtn;
    private JButton cancelRequestBtn;
    private DefaultTableModel tableModel;
    
    public RouteChangeRequestPanel(int userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
        this.requestDAO = new RouteChangeRequestDAO();
        this.routeDAO = new RouteDAO();
        this.bookingDAO = new BookingDAO();
        this.tripDAO = new TripDAO();
        this.assignmentDAO = new RouteAssignmentDAO();
        
        initializeUI();
        refreshRequests();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Route Change Requests");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        submitRequestBtn = new JButton("Submit Route Change Request");
        submitRequestBtn.setBackground(new Color(0, 153, 76));
        submitRequestBtn.setForeground(Color.WHITE);
        submitRequestBtn.setFont(new Font("Arial", Font.BOLD, 12));
        submitRequestBtn.addActionListener(e -> openSubmitRequestDialog());
        
        cancelRequestBtn = new JButton("Cancel Selected Request");
        cancelRequestBtn.setBackground(new Color(204, 0, 0));
        cancelRequestBtn.setForeground(Color.WHITE);
        cancelRequestBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelRequestBtn.addActionListener(e -> cancelSelectedRequest());
        
        buttonPanel.add(submitRequestBtn);
        buttonPanel.add(cancelRequestBtn);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Request ID", "Current Route", "Requested Route", "Reason", "Status", "Submitted Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        requestsTable = new JTable(tableModel);
        requestsTable.setColumnSelectionAllowed(false);
        requestsTable.setRowSelectionAllowed(true);
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        requestsTable.setRowHeight(25);
        requestsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        requestsTable.getTableHeader().setBackground(new Color(0, 102, 204));
        requestsTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void refreshRequests() {
        SwingWorker<List<RouteChangeRequest>, Void> worker = new SwingWorker<List<RouteChangeRequest>, Void>() {
            @Override
            protected List<RouteChangeRequest> doInBackground() throws Exception {
                return requestDAO.getAllRequestsByUser(userId);
            }
            
            @Override
            protected void done() {
                try {
                    List<RouteChangeRequest> requests = get();
                    tableModel.setRowCount(0);
                    
                    for (RouteChangeRequest req : requests) {
                        tableModel.addRow(new Object[]{
                            req.getRequestId(),
                            req.getCurrentRouteName(),
                            req.getRequestedRouteName(),
                            req.getReason(),
                            req.getStatus(),
                            req.getCreatedAt()
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RouteChangeRequestPanel.this, 
                        "Error loading requests: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void openSubmitRequestDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Submit Route Change Request", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        try {
            // Current Route
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Current Route:"), gbc);
            
            JComboBox<String> currentRouteCombo = new JComboBox<>();
            JComboBox<Integer> currentRouteIdCombo = new JComboBox<>();
            
            if ("PASSENGER".equals(userRole)) {
                // Load passenger's current booking routes
                List<Booking> bookings = bookingDAO.getBookingsByPassenger(userId);
                for (Booking booking : bookings) {
                    if ("CONFIRMED".equals(booking.getStatus())) {
                        currentRouteCombo.addItem(booking.getRouteName());
                        // Store the actual ROUTE ID from the assignment
                        currentRouteIdCombo.addItem(getRouteIdFromAssignment(booking.getAssignmentId()));
                    }
                }
            } else if ("DRIVER".equals(userRole)) {
                // Load driver's current trip routes
                List<Trip> trips = tripDAO.getTripsByDriver(userId);
                for (Trip trip : trips) {
                    if ("IN_PROGRESS".equals(trip.getStatus()) || "SCHEDULED".equals(trip.getStatus())) {
                        currentRouteCombo.addItem(trip.getRouteName());
                        // Store the actual ROUTE ID from the assignment
                        currentRouteIdCombo.addItem(getRouteIdFromAssignment(trip.getAssignmentId()));
                    }
                }
            }
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            panel.add(currentRouteCombo, gbc);
            gbc.gridwidth = 1;
            
            // Requested Route
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Requested Route:"), gbc);
            
            JComboBox<String> requestedRouteCombo = new JComboBox<>();
            JComboBox<Integer> requestedRouteIdCombo = new JComboBox<>();
            
            List<Route> allRoutes = routeDAO.getAllRoutes();
            for (Route route : allRoutes) {
                requestedRouteCombo.addItem(route.getRouteName());
                requestedRouteIdCombo.addItem(route.getRouteId());  // This is the correct route ID
            }
            
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            panel.add(requestedRouteCombo, gbc);
            gbc.gridwidth = 1;
            
            // Reason
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridheight = 1;
            panel.add(new JLabel("Reason:"), gbc);
            
            JTextArea reasonArea = new JTextArea(5, 40);
            reasonArea.setLineWrap(true);
            reasonArea.setWrapStyleWord(true);
            gbc.gridx = 1;
            gbc.gridwidth = 2;
            gbc.gridheight = 3;
            gbc.weighty = 1.0;
            panel.add(new JScrollPane(reasonArea), gbc);
            gbc.gridheight = 1;
            gbc.weighty = 0;
            
            // Buttons
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 3;
            gbc.anchor = GridBagConstraints.CENTER;
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton submitBtn = new JButton("Submit");
            submitBtn.setBackground(new Color(0, 153, 76));
            submitBtn.setForeground(Color.WHITE);
            submitBtn.setFont(new Font("Arial", Font.BOLD, 12));
            submitBtn.addActionListener(e -> {
                if (currentRouteCombo.getItemCount() == 0 || requestedRouteCombo.getItemCount() == 0) {
                    JOptionPane.showMessageDialog(dialog, "Please ensure routes are available for selection.");
                    return;
                }
                
                String reason = reasonArea.getText().trim();
                if (reason.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a reason for the route change.");
                    return;
                }
                
                Integer currentRouteId = (Integer) currentRouteIdCombo.getSelectedItem();
                Integer requestedRouteId = (Integer) requestedRouteIdCombo.getSelectedItem();
                
                if (currentRouteId == null || requestedRouteId == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select valid routes.");
                    return;
                }
                
                if (currentRouteId.equals(requestedRouteId)) {
                    JOptionPane.showMessageDialog(dialog, "Current route and requested route must be different.");
                    return;
                }
                
                // Create and submit request
                submitRouteChangeRequest(currentRouteId, requestedRouteId, reason, dialog);
            });
            
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setBackground(new Color(204, 0, 0));
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
            cancelBtn.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(submitBtn);
            buttonPanel.add(cancelBtn);
            panel.add(buttonPanel, gbc);
            
            dialog.add(panel);
            dialog.setVisible(true);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Error loading routes: " + ex.getMessage());
        }
    }
    
    /**
     * Get the Route ID from a Route Assignment ID
     */
    private int getRouteIdFromAssignment(int assignmentId) {
        try {
            RouteAssignment assignment = assignmentDAO.getAssignmentById(assignmentId);
            if (assignment != null) {
                return assignment.getRouteId();
            }
        } catch (SQLException e) {
            System.err.println("Error getting route from assignment: " + e.getMessage());
        }
        return 0;
    }
    
    private void submitRouteChangeRequest(int currentRouteId, int requestedRouteId, String reason, JDialog dialog) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Use the generic constructor with proper parameters
                RouteChangeRequest request = new RouteChangeRequest(
                    userId, 
                    userRole, 
                    0,  // relatedId (not needed, will be set properly in DAO)
                    currentRouteId,      // currentRouteId (now correct)
                    requestedRouteId,    // requestedRouteId (now correct)
                    reason
                );
                return requestDAO.addRouteChangeRequest(request);
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Route change request submitted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        refreshRequests();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            "Failed to submit route change request.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Error submitting request: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void cancelSelectedRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request to cancel");
            return;
        }
        
        int requestId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 4);
        
        if (!"PENDING".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only PENDING requests can be cancelled");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this request?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return requestDAO.cancelRequest(requestId);
                }
                
                @Override
                protected void done() {
                    try {
                        Boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(RouteChangeRequestPanel.this, 
                                "Request cancelled successfully",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshRequests();
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(RouteChangeRequestPanel.this, 
                            "Error cancelling request: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}