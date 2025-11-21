package ui;
import bl.*;
import dal.*;
import util.Constants;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * AdminDashboard - Main interface for admin users
 */
public class AdminDashboard extends JFrame {
    
    private Admin admin;
    private JTabbedPane tabbedPane;
    private UserDAO userDAO;
    private BusDAO busDAO;
    private RouteDAO routeDAO;
    private BookingDAO bookingDAO;
    private ComplaintDAO complaintDAO;
    private PaymentDAO paymentDAO;
    
    public AdminDashboard(Admin admin) {
        this.admin = admin;
        this.userDAO = new UserDAO();
        this.busDAO = new BusDAO();
        this.routeDAO = new RouteDAO();
        this.bookingDAO = new BookingDAO();
        this.complaintDAO = new ComplaintDAO();
        this.paymentDAO = new PaymentDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Admin Dashboard - " + admin.getFullName());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Users", createUsersPanel());
        tabbedPane.addTab("Buses", createBusesPanel());
        tabbedPane.addTab("Routes", createRoutesPanel());
        tabbedPane.addTab("Bookings", createBookingsPanel());
        tabbedPane.addTab("Complaints", createComplaintsPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        tabbedPane.addTab("Profile", createProfilePanel());
        
        add(tabbedPane);
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(0, 102, 204));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("Admin Control Panel");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        
        JLabel subtitleLabel = new JLabel("Welcome, " + admin.getFullName() + " - System Administrator");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(Color.WHITE);
        welcomePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        panel.add(welcomePanel, BorderLayout.NORTH);
        
        // Statistics grid
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        statsPanel.setBackground(Color.WHITE);
        
        statsPanel.add(createStatCard("Total Users", "Loading...", new Color(0, 102, 204)));
        statsPanel.add(createStatCard("Total Buses", "Loading...", new Color(0, 153, 76)));
        statsPanel.add(createStatCard("Active Routes", "Loading...", new Color(255, 153, 0)));
        statsPanel.add(createStatCard("Total Bookings", "Loading...", new Color(153, 0, 153)));
        statsPanel.add(createStatCard("Total Revenue", "Loading...", new Color(204, 0, 0)));
        statsPanel.add(createStatCard("Pending Complaints", "Loading...", new Color(102, 102, 102)));
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Load statistics
        loadDashboardStats(statsPanel);
        
        return panel;
    }
    
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"ID", "Username", "Full Name", "Email", "Phone", "Role", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable usersTable = new JTable(tableModel);
        usersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        usersTable.setRowHeight(25);
        usersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        usersTable.getTableHeader().setBackground(new Color(0, 102, 204));
        usersTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadUsers(tableModel));
        
        JButton addBtn = new JButton("Add User");
        addBtn.setBackground(new Color(0, 153, 76));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> addUser(tableModel));
        
        JButton editBtn = new JButton("Edit User");
        editBtn.setBackground(new Color(255, 153, 0));
        editBtn.setForeground(Color.WHITE);
        editBtn.addActionListener(e -> editUser(usersTable, tableModel));
        
        JButton deleteBtn = new JButton("Delete User");
        deleteBtn.setBackground(new Color(204, 0, 0));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> deleteUser(usersTable, tableModel));
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load users
        loadUsers(tableModel);
        
        return panel;
    }
    
    private JPanel createBusesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Bus Fleet Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 153, 76));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"Bus ID", "Bus Number", "Type", "Capacity", "Registration", "Driver", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable busesTable = new JTable(tableModel);
        busesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        busesTable.setRowHeight(25);
        busesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        busesTable.getTableHeader().setBackground(new Color(0, 153, 76));
        busesTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(busesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadBuses(tableModel));
        
        JButton addBtn = new JButton("Add Bus");
        addBtn.setBackground(new Color(0, 153, 76));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> addBus(tableModel));
        
        JButton editBtn = new JButton("Edit Bus");
        editBtn.setBackground(new Color(255, 153, 0));
        editBtn.setForeground(Color.WHITE);
        
        JButton deleteBtn = new JButton("Delete Bus");
        deleteBtn.setBackground(new Color(204, 0, 0));
        deleteBtn.setForeground(Color.WHITE);
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load buses
        loadBuses(tableModel);
        
        return panel;
    }
    
    private JPanel createRoutesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Route Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 153, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"Route ID", "Route Name", "Origin", "Destination", "Distance (km)", "Duration (min)", "Fare", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable routesTable = new JTable(tableModel);
        routesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        routesTable.setRowHeight(25);
        routesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        routesTable.getTableHeader().setBackground(new Color(255, 153, 0));
        routesTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(routesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadRoutes(tableModel));
        
        JButton addBtn = new JButton("Add Route");
        addBtn.setBackground(new Color(0, 153, 76));
        addBtn.setForeground(Color.WHITE);
        
        JButton editBtn = new JButton("Edit Route");
        editBtn.setBackground(new Color(255, 153, 0));
        editBtn.setForeground(Color.WHITE);
        
        JButton deleteBtn = new JButton("Delete Route");
        deleteBtn.setBackground(new Color(204, 0, 0));
        deleteBtn.setForeground(Color.WHITE);
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load routes
        loadRoutes(tableModel);
        
        return panel;
    }
    
    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Booking Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(153, 0, 153));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"Booking ID", "Passenger", "Date", "Route", "Bus", "Seat", "Fare", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable bookingsTable = new JTable(tableModel);
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        bookingsTable.setRowHeight(25);
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        bookingsTable.getTableHeader().setBackground(new Color(153, 0, 153));
        bookingsTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadAllBookings(tableModel));
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load bookings
        loadAllBookings(tableModel);
        
        return panel;
    }
    
    private JPanel createComplaintsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Complaint Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(204, 0, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"ID", "User", "Type", "Subject", "Priority", "Status", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable complaintsTable = new JTable(tableModel);
        complaintsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        complaintsTable.setRowHeight(25);
        complaintsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        complaintsTable.getTableHeader().setBackground(new Color(204, 0, 0));
        complaintsTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(complaintsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadComplaints(tableModel));
        
        JButton viewBtn = new JButton("View Details");
        viewBtn.setBackground(new Color(0, 102, 204));
        viewBtn.setForeground(Color.WHITE);
        viewBtn.addActionListener(e -> viewComplaintDetails(complaintsTable, tableModel));
        
        JButton resolveBtn = new JButton("Resolve");
        resolveBtn.setBackground(new Color(0, 153, 76));
        resolveBtn.setForeground(Color.WHITE);
        resolveBtn.addActionListener(e -> resolveComplaint(complaintsTable, tableModel));
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(resolveBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load complaints
        loadComplaints(tableModel);
        
        return panel;
    }
    
    private void viewComplaintDetails(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a complaint to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int complaintId = (int) tableModel.getValueAt(selectedRow, 0);
        
        SwingWorker<Complaint, Void> worker = new SwingWorker<Complaint, Void>() {
            @Override
            protected Complaint doInBackground() throws Exception {
                return complaintDAO.getComplaintById(complaintId);
            }
            
            @Override
            protected void done() {
                try {
                    Complaint complaint = get();
                    if (complaint != null) {
                        showComplaintDetailsDialog(complaint);
                    }
                } catch (Exception e) {
                    showError("Error loading complaint details: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void showComplaintDetailsDialog(Complaint complaint) {
        JDialog dialog = new JDialog(this, "Complaint Details", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        addDetailField(detailsPanel, "Complaint ID:", String.valueOf(complaint.getComplaintId()), gbc, 0);
        addDetailField(detailsPanel, "User:", complaint.getUserName(), gbc, 1);
        addDetailField(detailsPanel, "Type:", complaint.getComplaintType(), gbc, 2);
        addDetailField(detailsPanel, "Priority:", complaint.getPriority(), gbc, 3);
        addDetailField(detailsPanel, "Status:", complaint.getStatus(), gbc, 4);
        addDetailField(detailsPanel, "Date:", complaint.getCreatedAt().toString(), gbc, 5);
        addDetailField(detailsPanel, "Subject:", complaint.getSubject(), gbc, 6);
        
        panel.add(detailsPanel, BorderLayout.NORTH);
        
        // Description
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.setBackground(Color.WHITE);
        descPanel.setBorder(BorderFactory.createTitledBorder("Description"));
        
        JTextArea descArea = new JTextArea(complaint.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(550, 150));
        descPanel.add(descScroll);
        
        panel.add(descPanel, BorderLayout.CENTER);
        
        // Response if available
        if (complaint.getAdminResponse() != null) {
            JPanel responsePanel = new JPanel(new BorderLayout(5, 5));
            responsePanel.setBackground(Color.WHITE);
            responsePanel.setBorder(BorderFactory.createTitledBorder("Admin Response"));
            
            JTextArea responseArea = new JTextArea(complaint.getAdminResponse());
            responseArea.setEditable(false);
            responseArea.setLineWrap(true);
            responseArea.setWrapStyleWord(true);
            responseArea.setFont(new Font("Arial", Font.PLAIN, 13));
            JScrollPane responseScroll = new JScrollPane(responseArea);
            responseScroll.setPreferredSize(new Dimension(550, 100));
            responsePanel.add(responseScroll);
            
            panel.add(responsePanel, BorderLayout.SOUTH);
        }
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void addDetailField(JPanel panel, String label, String value, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(val, gbc);
    }
    
    private void resolveComplaint(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a complaint to resolve.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int complaintId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        
        if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
            JOptionPane.showMessageDialog(this,
                "This complaint has already been resolved.",
                "Already Resolved",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Ask for response
        JTextArea responseArea = new JTextArea(5, 30);
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(responseArea);
        
        int option = JOptionPane.showConfirmDialog(this,
            new Object[]{"Enter your response:", scrollPane},
            "Resolve Complaint",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        
        if (option == JOptionPane.OK_OPTION) {
            String response = responseArea.getText().trim();
            
            if (response.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a response.",
                    "Response Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return complaintDAO.resolveComplaint(complaintId, response);
                }
                
                @Override
                protected void done() {
                    try {
                        Boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(AdminDashboard.this,
                                "Complaint resolved successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                            loadComplaints(tableModel);
                        } else {
                            showError("Failed to resolve complaint.");
                        }
                    } catch (Exception e) {
                        showError("Error resolving complaint: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Revenue Report
        JButton revenueBtn = createReportButton("Revenue Report", 
            "Generate detailed revenue analysis", new Color(0, 153, 76));
        revenueBtn.addActionListener(e -> generateRevenueReport());
        
        // Passenger Report
        JButton passengerBtn = createReportButton("Passenger Statistics", 
            "Analyze passenger bookings and trends", new Color(0, 102, 204));
        
        // Bus Utilization
        JButton busUtilBtn = createReportButton("Bus Utilization", 
            "Bus fleet performance metrics", new Color(255, 153, 0));
        
        // Route Performance
        JButton routeBtn = createReportButton("Route Performance", 
            "Route-wise statistics and analysis", new Color(153, 0, 153));
        
        // Driver Performance
        JButton driverBtn = createReportButton("Driver Performance", 
            "Driver trip statistics and ratings", new Color(102, 102, 102));
        
        // Complaint Summary
        JButton complaintBtn = createReportButton("Complaint Summary", 
            "Complaint resolution statistics", new Color(204, 0, 0));
        
        panel.add(revenueBtn);
        panel.add(passengerBtn);
        panel.add(busUtilBtn);
        panel.add(routeBtn);
        panel.add(driverBtn);
        panel.add(complaintBtn);
        
        return panel;
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel titleLabel = new JLabel("Admin Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        addProfileField(panel, "Username:", admin.getUsername(), gbc);
        gbc.gridy = 2;
        addProfileField(panel, "Full Name:", admin.getFullName(), gbc);
        gbc.gridy = 3;
        addProfileField(panel, "Email:", admin.getEmail(), gbc);
        gbc.gridy = 4;
        addProfileField(panel, "Phone:", admin.getPhone(), gbc);
        gbc.gridy = 5;
        addProfileField(panel, "Department:", admin.getDepartment(), gbc);
        gbc.gridy = 6;
        addProfileField(panel, "Access Level:", admin.getAccessLevel(), gbc);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(204, 0, 0));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        gbc.gridy = 7;
        gbc.gridx = 1;
        panel.add(logoutBtn, gbc);
        
        return panel;
    }
    
    // Helper methods
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JButton createReportButton(String title, String description, Color color) {
        JButton button = new JButton("<html><center>" + title + "<br><small>" + description + "</small></center></html>");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
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
    
    // Data loading methods
    private void loadDashboardStats(JPanel statsPanel) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // This would load actual statistics from database
                return null;
            }
        };
        worker.execute();
    }
    
    private void loadUsers(DefaultTableModel tableModel) {
        SwingWorker<List<User>, Void> worker = new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return userDAO.getAllUsers();
            }
            
            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    tableModel.setRowCount(0);
                    
                    for (User user : users) {
                        tableModel.addRow(new Object[]{
                            user.getUserId(),
                            user.getUsername(),
                            user.getFullName(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getRole(),
                            user.getStatus()
                        });
                    }
                } catch (Exception e) {
                    showError("Error loading users: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void loadBuses(DefaultTableModel tableModel) {
        SwingWorker<List<Bus>, Void> worker = new SwingWorker<List<Bus>, Void>() {
            @Override
            protected List<Bus> doInBackground() throws Exception {
                return busDAO.getAllBuses();
            }
            
            @Override
            protected void done() {
                try {
                    List<Bus> buses = get();
                    tableModel.setRowCount(0);
                    
                    for (Bus bus : buses) {
                        tableModel.addRow(new Object[]{
                            bus.getBusId(),
                            bus.getBusNumber(),
                            bus.getBusType(),
                            bus.getCapacity(),
                            bus.getRegistrationNumber(),
                            bus.getDriverName() != null ? bus.getDriverName() : "Unassigned",
                            bus.getStatus()
                        });
                    }
                } catch (Exception e) {
                    showError("Error loading buses: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void loadRoutes(DefaultTableModel tableModel) {
        SwingWorker<List<Route>, Void> worker = new SwingWorker<List<Route>, Void>() {
            @Override
            protected List<Route> doInBackground() throws Exception {
                return routeDAO.getAllRoutes();
            }
            
            @Override
            protected void done() {
                try {
                    List<Route> routes = get();
                    tableModel.setRowCount(0);
                    
                    for (Route route : routes) {
                        tableModel.addRow(new Object[]{
                            route.getRouteId(),
                            route.getRouteName(),
                            route.getOrigin(),
                            route.getDestination(),
                            route.getDistanceKm(),
                            route.getEstimatedDurationMinutes(),
                            String.format("Rs. %.2f", route.getFare()),
                            route.getStatus()
                        });
                    }
                } catch (Exception e) {
                    showError("Error loading routes: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void loadAllBookings(DefaultTableModel tableModel) {
        SwingWorker<List<Booking>, Void> worker = new SwingWorker<List<Booking>, Void>() {
            @Override
            protected List<Booking> doInBackground() throws Exception {
                return bookingDAO.getAllBookings();
            }
            
            @Override
            protected void done() {
                try {
                    List<Booking> bookings = get();
                    tableModel.setRowCount(0);
                    
                    for (Booking booking : bookings) {
                        tableModel.addRow(new Object[]{
                            booking.getBookingId(),
                            booking.getPassengerName(),
                            booking.getBookingDate(),
                            booking.getRouteName(),
                            booking.getBusNumber(),
                            booking.getSeatNumber(),
                            String.format("Rs. %.2f", booking.getFare()),
                            booking.getStatus()
                        });
                    }
                } catch (Exception e) {
                    showError("Error loading bookings: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void loadComplaints(DefaultTableModel tableModel) {
        SwingWorker<List<Complaint>, Void> worker = new SwingWorker<List<Complaint>, Void>() {
            @Override
            protected List<Complaint> doInBackground() throws Exception {
                return complaintDAO.getAllComplaints();
            }
            
            @Override
            protected void done() {
                try {
                    List<Complaint> complaints = get();
                    tableModel.setRowCount(0);
                    
                    for (Complaint complaint : complaints) {
                        tableModel.addRow(new Object[]{
                            complaint.getComplaintId(),
                            complaint.getUserName(),
                            complaint.getComplaintType(),
                            complaint.getSubject(),
                            complaint.getPriority(),
                            complaint.getStatus(),
                            complaint.getCreatedAt()
                        });
                    }
                } catch (Exception e) {
                    showError("Error loading complaints: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    // Action methods
    private void addUser(DefaultTableModel tableModel) {
        JOptionPane.showMessageDialog(this,
            "Add User form will be implemented here.",
            "Add User",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void editUser(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this,
            "Edit User form will be implemented here.",
            "Edit User",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteUser(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this user?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (userDAO.deleteUser(userId)) {
                    JOptionPane.showMessageDialog(this,
                        "User deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadUsers(tableModel);
                }
            } catch (SQLException e) {
                showError("Error deleting user: " + e.getMessage());
            }
        }
    }
    
    private void addBus(DefaultTableModel tableModel) {
        JOptionPane.showMessageDialog(this,
            "Add Bus form will be implemented here.",
            "Add Bus",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generateRevenueReport() {
        SwingWorker<Double, Void> worker = new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                return paymentDAO.getTotalRevenue();
            }
            
            @Override
            protected void done() {
                try {
                    Double revenue = get();
                    JOptionPane.showMessageDialog(AdminDashboard.this,
                        String.format("Total Revenue: Rs. %.2f\n\n" +
                                    "Detailed revenue report generation\n" +
                                    "would be implemented here with:\n" +
                                    "- Daily/Monthly/Yearly breakdown\n" +
                                    "- Revenue by route\n" +
                                    "- Export to PDF/Excel", revenue),
                        "Revenue Report",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    showError("Error generating report: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
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