package ui;

import bl.*;
import dal.*;
import util.Constants;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;



/**
 * AdminDashboard - Main interface for admin users
 */
public class AdminDashboard extends JFrame {

    // Theme constants
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_LABEL_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_LABEL_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);

    // Professional color palette (aligned with PassengerDashboard)
    private static final Color COLOR_PRIMARY   = new Color(41, 128, 185);   // Modern blue
    private static final Color COLOR_SECONDARY = new Color(52, 152, 219);   // Light blue
    private static final Color COLOR_SUCCESS   = new Color(46, 204, 113);   // Green
    private static final Color COLOR_WARNING   = new Color(230, 126, 34);   // Orange
    private static final Color COLOR_DANGER    = new Color(231, 76, 60);    // Red
    private static final Color COLOR_PURPLE    = new Color(155, 89, 182);   // Soft purple
    private static final Color COLOR_MUTED     = new Color(127, 140, 141);  // Muted gray

    private static final Color COLOR_DARK_BG   = new Color(44, 62, 80);
    private static final Color COLOR_LIGHT_BG  = new Color(236, 240, 241);
    private static final Color COLOR_CARD_BG   = Color.WHITE;

    // Main background for screens
    private static final Color COLOR_BG        = COLOR_LIGHT_BG;

    // Text colors similar to PassengerDashboard
    private static final Color TEXT_PRIMARY    = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY  = new Color(127, 140, 141);


    private Admin admin;
    private JTabbedPane tabbedPane;

    private UserDAO userDAO;
    private BusDAO busDAO;
    private RouteDAO routeDAO;
    private BookingDAO bookingDAO;
    private ComplaintDAO complaintDAO;
    private PaymentDAO paymentDAO;

    // Dashboard stat labels
    private JLabel lblTotalUsers;
    private JLabel lblTotalBuses;
    private JLabel lblActiveRoutes;
    private JLabel lblTotalBookings;
    private JLabel lblTotalRevenue;
    private JLabel lblPendingComplaints;

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
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Modern Header Bar
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setBackground(new Color(0, 102, 204));
        headerBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel appTitle = new JLabel("Admin Dashboard");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitle.setForeground(Color.WHITE);

        JLabel adminLabel = new JLabel("Logged in as: " + admin.getFullName() + " (" + admin.getUsername() + ")");
        adminLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        adminLabel.setForeground(Color.WHITE);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);
        rightHeader.add(adminLabel);

     // Modern Rounded Logout Button (Header)
        JButton logoutTopBtn = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2.setColor(new Color(220, 53, 69)); // Red tone
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                super.paintComponent(g);
                g2.dispose();
            }
        };

        logoutTopBtn.setContentAreaFilled(false);
        logoutTopBtn.setBorderPainted(false);
        logoutTopBtn.setFocusPainted(false);
        logoutTopBtn.setForeground(Color.WHITE);
        logoutTopBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutTopBtn.setPreferredSize(new Dimension(100, 32));
        logoutTopBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutTopBtn.addActionListener(e -> logout());
        logoutTopBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutTopBtn.setBackground(new Color(200, 40, 55));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutTopBtn.setBackground(new Color(220, 53, 69));
            }
        });

        
        logoutTopBtn.setPreferredSize(new Dimension(100, 30));
        logoutTopBtn.addActionListener(e -> logout());
        rightHeader.add(logoutTopBtn);

        headerBar.add(appTitle, BorderLayout.WEST);
        headerBar.add(rightHeader, BorderLayout.EAST);

        add(headerBar, BorderLayout.NORTH);

        // Clean Flat Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setBorder(null);

        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Users", createUsersPanel());
        tabbedPane.addTab("Buses", createBusesPanel());
        tabbedPane.addTab("Routes", createRoutesPanel());
        tabbedPane.addTab("Bookings", createBookingsPanel());
        tabbedPane.addTab("Assign Routes", new AdminAssignRoutesToDriverPanel());
        tabbedPane.addTab("Route Changes", new AdminRouteChangeRequestsPanel());
        tabbedPane.addTab("Complaints", createComplaintsPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        tabbedPane.addTab("Profile", createProfilePanel());

        add(tabbedPane, BorderLayout.CENTER);
    }


    /* ========================= DASHBOARD ========================= */

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(COLOR_BG);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Admin Control Panel");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Welcome, " + admin.getFullName() + " - System Administrator");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.WHITE);

        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Stats grid
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(COLOR_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        lblTotalUsers = new JLabel("Loading...");
        lblTotalBuses = new JLabel("Loading...");
        lblActiveRoutes = new JLabel("Loading...");
        lblTotalBookings = new JLabel("Loading...");
        lblTotalRevenue = new JLabel("Loading...");
        lblPendingComplaints = new JLabel("Loading...");

        statsPanel.add(createStatCard("Total Users", lblTotalUsers));
        statsPanel.add(createStatCard("Total Buses", lblTotalBuses));
        statsPanel.add(createStatCard("Active Routes", lblActiveRoutes));
        statsPanel.add(createStatCard("Total Bookings", lblTotalBookings));
        statsPanel.add(createStatCard("Total Revenue", lblTotalRevenue));
        statsPanel.add(createStatCard("Pending Complaints", lblPendingComplaints));

        panel.add(statsPanel, BorderLayout.CENTER);

        loadDashboardStats();

        return panel;
    }

    /* ========================= USERS ========================= */

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(COLOR_BG);

        String[] columnNames = {"ID", "Username", "Full Name", "Email", "Phone", "Role", "Status"};
        DefaultTableModel tableModel = createTableModel(columnNames);
        JTable usersTable = createStyledTable(tableModel, COLOR_PRIMARY);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        usersTable.setRowSorter(sorter);

        // Header with title + search
        JPanel headerPanel = createSectionHeaderWithSearch("User Management", COLOR_PRIMARY, sorter, "Search user...");
        panel.add(headerPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = createButton("Refresh", null);
        refreshBtn.setToolTipText("Reload all users");
        refreshBtn.addActionListener(e -> loadUsers(tableModel));

        JButton addBtn = createButton("Add User", COLOR_SUCCESS);
        addBtn.setToolTipText("Create a new user");
        addBtn.addActionListener(e -> addUser(tableModel));

        JButton editBtn = createButton("Edit User", COLOR_WARNING);
        editBtn.setToolTipText("Edit selected user");
        editBtn.addActionListener(e -> editUser(usersTable, tableModel));

        JButton deleteBtn = createButton("Delete User", COLOR_DANGER);
        deleteBtn.setToolTipText("Delete selected user");
        deleteBtn.addActionListener(e -> deleteUser(usersTable, tableModel));

        JPanel buttonPanel = createButtonBar(refreshBtn, addBtn, editBtn, deleteBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadUsers(tableModel);

        return panel;
    }

    /* ========================= BUSES ========================= */

    private JPanel createBusesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(COLOR_BG);

        String[] columnNames = {"Bus ID", "Bus Number", "Type", "Capacity", "Registration", "Driver", "Status"};
        DefaultTableModel tableModel = createTableModel(columnNames);
        JTable busesTable = createStyledTable(tableModel, COLOR_SUCCESS);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        busesTable.setRowSorter(sorter);

        JPanel headerPanel = createSectionHeaderWithSearch("Bus Fleet Management", COLOR_SUCCESS, sorter, "Search buses...");
        panel.add(headerPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(busesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = createButton("Refresh", null);
        refreshBtn.addActionListener(e -> loadBuses(tableModel));

        JButton addBtn = createButton("Add Bus", COLOR_SUCCESS);
        addBtn.addActionListener(e -> addBus(tableModel));

        JButton editBtn = createButton("Edit Bus", COLOR_WARNING);
        editBtn.addActionListener(e -> editBus(busesTable, tableModel));

        JButton deleteBtn = createButton("Delete Bus", COLOR_DANGER);
        deleteBtn.addActionListener(e -> deleteBus(busesTable, tableModel));

        JPanel buttonPanel = createButtonBar(refreshBtn, addBtn, editBtn, deleteBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadBuses(tableModel);

        return panel;
    }

    /* ========================= ROUTES ========================= */

    private JPanel createRoutesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(COLOR_BG);

        String[] columnNames = {
                "Route ID", "Route Name", "Origin", "Destination",
                "Distance (km)", "Duration (min)", "Fare", "Status"
        };
        DefaultTableModel tableModel = createTableModel(columnNames);
        JTable routesTable = createStyledTable(tableModel, COLOR_WARNING);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        routesTable.setRowSorter(sorter);

        JPanel headerPanel = createSectionHeaderWithSearch("Route Management", COLOR_WARNING, sorter, "Search routes...");
        panel.add(headerPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(routesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = createButton("Refresh", null);
        refreshBtn.addActionListener(e -> loadRoutes(tableModel));

        JButton addBtn = createButton("Add Route", COLOR_SUCCESS);
        addBtn.addActionListener(e -> addRoute(tableModel));

        JButton editBtn = createButton("Edit Route", COLOR_WARNING);
        editBtn.addActionListener(e -> editRoute(routesTable, tableModel));

        JButton deleteBtn = createButton("Delete Route", COLOR_DANGER);
        deleteBtn.addActionListener(e -> deleteRoute(routesTable, tableModel));

        JPanel buttonPanel = createButtonBar(refreshBtn, addBtn, editBtn, deleteBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadRoutes(tableModel);

        return panel;
    }

    /* ========================= BOOKINGS ========================= */

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(COLOR_BG);

        String[] columnNames = {
                "Booking ID", "Passenger", "Date", "Route",
                "Bus", "Seat", "Fare", "Status"
        };
        DefaultTableModel tableModel = createTableModel(columnNames);
        JTable bookingsTable = createStyledTable(tableModel, COLOR_PURPLE);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        bookingsTable.setRowSorter(sorter);

        JPanel headerPanel = createSectionHeaderWithSearch("Booking Management", COLOR_PURPLE, sorter, "Search bookings...");
        panel.add(headerPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = createButton("Refresh", null);
        refreshBtn.addActionListener(e -> loadAllBookings(tableModel));

        JButton detailsBtn = createButton("View Details", COLOR_PRIMARY);
        detailsBtn.addActionListener(e -> viewBookingDetails(bookingsTable, tableModel));

        JPanel buttonPanel = createButtonBar(refreshBtn, detailsBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadAllBookings(tableModel);

        return panel;
    }

    /* ========================= COMPLAINTS ========================= */

    private JPanel createComplaintsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(COLOR_BG);

        String[] columnNames = {"ID", "User", "Type", "Subject", "Priority", "Status", "Date"};
        DefaultTableModel tableModel = createTableModel(columnNames);
        JTable complaintsTable = createStyledTable(tableModel, COLOR_DANGER);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        complaintsTable.setRowSorter(sorter);

        JPanel headerPanel = createSectionHeaderWithSearch("Complaint Management", COLOR_DANGER, sorter, "Search complaints...");
        panel.add(headerPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(complaintsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = createButton("Refresh", null);
        refreshBtn.addActionListener(e -> loadComplaints(tableModel));

        JButton viewBtn = createButton("View Details", COLOR_PRIMARY);
        viewBtn.addActionListener(e -> viewComplaintDetails(complaintsTable, tableModel));

        JButton resolveBtn = createButton("Resolve", COLOR_SUCCESS);
        resolveBtn.addActionListener(e -> resolveComplaint(complaintsTable, tableModel));

        JPanel buttonPanel = createButtonBar(refreshBtn, viewBtn, resolveBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadComplaints(tableModel);

        return panel;
    }

    private void viewComplaintDetails(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a complaint to view.");
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
        panel.setBackground(COLOR_BG);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(COLOR_BG);
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

        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.setBackground(COLOR_BG);
        descPanel.setBorder(BorderFactory.createTitledBorder("Description"));

        JTextArea descArea = new JTextArea(complaint.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(FONT_TABLE);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(550, 150));
        descPanel.add(descScroll);

        panel.add(descPanel, BorderLayout.CENTER);

        if (complaint.getAdminResponse() != null && !complaint.getAdminResponse().trim().isEmpty()) {
            JPanel responsePanel = new JPanel(new BorderLayout(5, 5));
            responsePanel.setBackground(COLOR_BG);
            responsePanel.setBorder(BorderFactory.createTitledBorder("Admin Response"));

            JTextArea responseArea = new JTextArea(complaint.getAdminResponse());
            responseArea.setEditable(false);
            responseArea.setLineWrap(true);
            responseArea.setWrapStyleWord(true);
            responseArea.setFont(FONT_TABLE);
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
        lbl.setFont(FONT_LABEL_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel val = new JLabel(value);
        val.setFont(FONT_LABEL_NORMAL);
        panel.add(val, gbc);
    }

    private void resolveComplaint(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a complaint to resolve.");
            return;
        }

        int complaintId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 5);

        if ("RESOLVED".equalsIgnoreCase(status) || "CLOSED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                    "This complaint has already been resolved.",
                    "Already Resolved",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

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
                showWarning("Please enter a response.");
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

    /* ========================= REPORTS ========================= */

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(COLOR_BG);

        JLabel titleLabel = createSectionTitle("Reports & Analytics", COLOR_PRIMARY);

        JLabel subtitle = new JLabel("Quick overview of system performance and KPIs");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(COLOR_MUTED);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BG);
        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        panel.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setBackground(COLOR_BG);
        JPanel revenueCard = createReportButton("Revenue Report",
                "Revenue totals & breakdown", COLOR_SUCCESS);
        revenueCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showRevenueReport();
            }
        });

        JPanel passengerCard = createReportButton("Passenger Statistics",
                "Bookings, unique passengers, trends", COLOR_PRIMARY);
        passengerCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPassengerStats();
            }
        });

        JPanel busUtilCard = createReportButton("Bus Utilization",
                "Trips & load per bus", COLOR_WARNING);
        busUtilCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showBusUtilization();
            }
        });

        JPanel routeCard = createReportButton("Route Performance",
                "Top routes by bookings", COLOR_PURPLE);
        routeCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showRoutePerformance();
            }
        });

        JPanel driverCard = createReportButton("Driver Performance",
                "Trips & buses per driver", COLOR_MUTED);
        driverCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDriverPerformance();
            }
        });

        JPanel complaintCard = createReportButton("Complaint Summary",
                "Open vs resolved complaints", COLOR_DANGER);
        complaintCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showComplaintSummary();
            }
        });

        grid.add(revenueCard);
        grid.add(passengerCard);
        grid.add(busUtilCard);
        grid.add(routeCard);
        grid.add(driverCard);
        grid.add(complaintCard);


        panel.add(grid, BorderLayout.CENTER);

        return panel;
    }

    private void showRevenueReport() {
        JDialog dialog = new JDialog(this, "Revenue Report", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel("Revenue Report", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(COLOR_SUCCESS);

        dialog.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Category", "Amount (Rs.)"}, 0
        );
        JTable table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton exportExcelBtn = createButton("Export to Excel", COLOR_PRIMARY);
        JButton exportPdfBtn = createButton("Export to PDF", COLOR_DANGER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.add(exportExcelBtn);
        btnPanel.add(exportPdfBtn);

        dialog.add(btnPanel, BorderLayout.SOUTH);

        // Load Data
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            double totalRevenue;
            double dailyRevenue;
            double monthlyRevenue;
            double yearlyRevenue;
            List<Object[]> routeRevenue;

            @Override
            protected Void doInBackground() throws Exception {
                totalRevenue = paymentDAO.getTotalRevenue();
                dailyRevenue = paymentDAO.getDailyRevenue();
                monthlyRevenue = paymentDAO.getMonthlyRevenue();
                yearlyRevenue = paymentDAO.getYearlyRevenue();
                routeRevenue = paymentDAO.getRevenueByRoute();
                return null;
            }

            @Override
            protected void done() {
                try {
                    model.addRow(new Object[]{"Total Revenue", totalRevenue});
                    model.addRow(new Object[]{"Today's Revenue", dailyRevenue});
                    model.addRow(new Object[]{"This Month", monthlyRevenue});
                    model.addRow(new Object[]{"This Year", yearlyRevenue});
                    model.addRow(new Object[]{"--- Revenue By Route ---", ""});

                    for (Object[] r : routeRevenue) {
                        model.addRow(new Object[]{r[0], r[1]}); // routeName, amount
                    }
                } catch (Exception ex) {
                    showError("Error loading revenue report: " + ex.getMessage());
                }
            }
        };
        worker.execute();

        // Export Excel
        exportExcelBtn.addActionListener(e -> exportRevenueExcel(model));

        // Export PDF
        //exportPdfBtn.addActionListener(e -> exportRevenuePDF(model));

        dialog.setVisible(true);
    }


    private void showPassengerStats() {
        JDialog dialog = new JDialog(this, "Passenger Statistics", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Passenger Statistics", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(COLOR_PRIMARY);
        dialog.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Passenger Name"}, 0
        );
        JTable table = new JTable(model);
        table.setRowHeight(25);

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        SwingWorker<List<Booking>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Booking> doInBackground() throws Exception {
                return bookingDAO.getAllBookings();
            }

            @Override
            protected void done() {
                try {
                    List<Booking> bookings = get();
                    Set<String> passengers = bookings.stream()
                            .map(Booking::getPassengerName)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    passengers.forEach(p -> model.addRow(new Object[]{p}));

                    if (passengers.isEmpty()) {
                        model.addRow(new Object[]{"No passenger data available"});
                    }

                } catch (Exception e) {
                    showError("Error generating passenger statistics: " + e.getMessage());
                }
            }
        };

        worker.execute();
        dialog.setVisible(true);
    }


    private void showBusUtilization() {
        JDialog dialog = new JDialog(this, "Bus Utilization", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Bus Utilization", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(COLOR_WARNING);
        dialog.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Bus Number", "Bookings"}, 0
        );
        JTable table = new JTable(model);
        table.setRowHeight(25);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        SwingWorker<List<Booking>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Booking> doInBackground() throws Exception {
                return bookingDAO.getAllBookings();
            }

            @Override
            protected void done() {
                try {
                    List<Booking> bookings = get();
                    Map<String, Long> busTrips = bookings.stream()
                            .filter(b -> b.getBusNumber() != null)
                            .collect(Collectors.groupingBy(Booking::getBusNumber, Collectors.counting()));

                    busTrips.forEach((bus, count) -> model.addRow(new Object[]{bus, count}));

                    if (busTrips.isEmpty()) {
                        model.addRow(new Object[]{"No bus data available", ""});
                    }

                } catch (Exception e) {
                    showError("Error generating bus utilization report: " + e.getMessage());
                }
            }
        };

        worker.execute();
        dialog.setVisible(true);
    }


    private void showRoutePerformance() {
        JDialog dialog = new JDialog(this, "Route Performance", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Route Performance", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(COLOR_PURPLE);
        dialog.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Route", "Bookings"}, 0
        );
        JTable table = new JTable(model);
        table.setRowHeight(25);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        SwingWorker<List<Booking>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Booking> doInBackground() throws Exception {
                return bookingDAO.getAllBookings();
            }

            @Override
            protected void done() {
                try {
                    List<Booking> bookings = get();
                    Map<String, Long> routeMap = bookings.stream()
                            .filter(b -> b.getRouteName() != null)
                            .collect(Collectors.groupingBy(Booking::getRouteName, Collectors.counting()));

                    List<Map.Entry<String, Long>> sorted = new ArrayList<>(routeMap.entrySet());
                    sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

                    sorted.forEach(e -> model.addRow(new Object[]{e.getKey(), e.getValue()}));

                    if (sorted.isEmpty()) {
                        model.addRow(new Object[]{"No route data available", ""});
                    }

                } catch (Exception e) {
                    showError("Error generating route performance report: " + e.getMessage());
                }
            }
        };

        worker.execute();
        dialog.setVisible(true);
    }


    private void showDriverPerformance() {
        JDialog dialog = new JDialog(this, "Driver Performance", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Driver Performance", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(COLOR_MUTED);
        dialog.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Driver", "Assigned Buses"}, 0
        );
        JTable table = new JTable(model);
        table.setRowHeight(25);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        SwingWorker<List<Bus>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Bus> doInBackground() throws Exception {
                return busDAO.getAllBuses();
            }

            @Override
            protected void done() {
                try {
                    List<Bus> buses = get();
                    Map<String, Long> driverMap = buses.stream()
                            .filter(b -> b.getDriverName() != null)
                            .collect(Collectors.groupingBy(Bus::getDriverName, Collectors.counting()));

                    driverMap.forEach((driver, count) -> model.addRow(new Object[]{driver, count}));

                    if (driverMap.isEmpty()) {
                        model.addRow(new Object[]{"No driver data available", ""});
                    }

                } catch (Exception e) {
                    showError("Error generating driver performance report: " + e.getMessage());
                }
            }
        };

        worker.execute();
        dialog.setVisible(true);
    }


    private void showComplaintSummary() {
        JDialog dialog = new JDialog(this, "Complaint Summary", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Complaint Summary", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(COLOR_DANGER);
        dialog.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Metric", "Count"}, 0
        );
        JTable table = new JTable(model);
        table.setRowHeight(25);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        SwingWorker<List<Complaint>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Complaint> doInBackground() throws Exception {
                return complaintDAO.getAllComplaints();
            }

            @Override
            protected void done() {
                try {
                    List<Complaint> list = get();
                    long total = list.size();
                    long open = list.stream()
                            .filter(c -> !"RESOLVED".equalsIgnoreCase(c.getStatus())
                                    && !"CLOSED".equalsIgnoreCase(c.getStatus()))
                            .count();
                    long resolved = total - open;

                    model.addRow(new Object[]{"Total Complaints", total});
                    model.addRow(new Object[]{"Open / Pending", open});
                    model.addRow(new Object[]{"Resolved / Closed", resolved});

                    if (total == 0) {
                        model.addRow(new Object[]{"No complaint data available", ""});
                    }

                } catch (Exception e) {
                    showError("Error generating complaint summary: " + e.getMessage());
                }
            }
        };

        worker.execute();
        dialog.setVisible(true);
    }


    /* ========================= PROFILE ========================= */

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // TITLE
        JLabel titleLabel = new JLabel("👤 Admin Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_PRIMARY);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        addProfileField(card, "Username:", admin.getUsername(), gbc, 1);
        addProfileField(card, "Full Name:", admin.getFullName(), gbc, 2);
        addProfileField(card, "Email:", admin.getEmail(), gbc, 3);
        addProfileField(card, "Phone:", admin.getPhone(), gbc, 4);
        addProfileField(card, "Department:", admin.getDepartment(), gbc, 5);
        addProfileField(card, "Access Level:", admin.getAccessLevel(), gbc, 6);

     // LOGOUT BUTTON (Modern Rounded Style)
        JButton logoutBtn = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Button background
                g2.setColor(new Color(220, 53, 69)); // Red
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                super.paintComponent(g);
                g2.dispose();
            }
        };
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setPreferredSize(new Dimension(130, 40));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        logoutBtn.addActionListener(e -> logout());

        // Add to grid
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(logoutBtn, gbc);


        panel.add(card);

        return panel;
    }

    private void addProfileField(JPanel card, String label, String value, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(lbl, gbc);

        gbc.gridx = 1;
        JLabel val = new JLabel(value != null ? value : "-");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(val, gbc);
    }


    private void addProfileField(JPanel panel, String label, String value, GridBagConstraints gbc) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(FONT_LABEL_BOLD);
        gbc.gridx = 0;
        panel.add(labelComp, gbc);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(FONT_LABEL_NORMAL);
        gbc.gridx = 1;
        panel.add(valueComp, gbc);
    }

    /* ========================= DATA LOADING ========================= */

    private void loadDashboardStats() {
        SwingWorker<DashboardStats, Void> worker = new SwingWorker<DashboardStats, Void>() {
            @Override
            protected DashboardStats doInBackground() throws Exception {
                DashboardStats stats = new DashboardStats();
                stats.totalUsers = userDAO.getAllUsers().size();
                stats.totalBuses = busDAO.getAllBuses().size();
                stats.totalRoutes = routeDAO.getAllRoutes().size();
                stats.totalBookings = bookingDAO.getAllBookings().size();
                stats.totalRevenue = paymentDAO.getTotalRevenue();
                stats.pendingComplaints = (int) complaintDAO.getAllComplaints().stream()
                        .filter(c -> !"RESOLVED".equalsIgnoreCase(c.getStatus())
                                && !"CLOSED".equalsIgnoreCase(c.getStatus()))
                        .count();
                return stats;
            }

            @Override
            protected void done() {
                try {
                    DashboardStats stats = get();
                    lblTotalUsers.setText(String.valueOf(stats.totalUsers));
                    lblTotalBuses.setText(String.valueOf(stats.totalBuses));
                    lblActiveRoutes.setText(String.valueOf(stats.totalRoutes));
                    lblTotalBookings.setText(String.valueOf(stats.totalBookings));
                    lblTotalRevenue.setText(String.format("Rs. %.2f", stats.totalRevenue));
                    lblPendingComplaints.setText(String.valueOf(stats.pendingComplaints));
                } catch (Exception e) {
                    showError("Error loading dashboard statistics: " + e.getMessage());
                }
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

    /* ========================= ACTION METHODS ========================= */

    private void addUser(DefaultTableModel tableModel) {
        JDialog dialog = new JDialog(this, "Add User", true);
        dialog.setSize(400, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtUsername = new JTextField();
        JTextField txtFullName = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPhone = new JTextField();
        JPasswordField txtPassword = new JPasswordField();

        JComboBox<String> cbRole = new JComboBox<>(new String[]{
                Constants.ROLE_ADMIN,
                Constants.ROLE_DRIVER,
                Constants.ROLE_PASSENGER
        });

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtUsername, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtFullName, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtEmail, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtPhone, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtPassword, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        dialog.add(cbRole, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = createButton("Save", COLOR_SUCCESS);
        JButton cancelBtn = createButton("Cancel", COLOR_DANGER);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        row++; gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        dialog.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String fullName = txtFullName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String password = new String(txtPassword.getPassword());
            String role = (String) cbRole.getSelectedItem();

            if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showWarning("Please fill all required fields.");
                return;
            }

            // ✅ CREATE THE CORRECT USER TYPE
            User user;
            switch (role) {
                case Constants.ROLE_ADMIN:
                    user = new Admin(username, password, fullName, email, phone);
                    break;
                case Constants.ROLE_DRIVER:
                    user = new Driver(username, password, fullName, email, phone);
                    break;
                case Constants.ROLE_PASSENGER:
                    user = new Passenger(username, password, fullName, email, phone);
                    break;
                default:
                    showError("Invalid role");
                    return;
            }

            try {
                // ✅ CALL DAO
                boolean success = userDAO.registerUser(user);

                if (success) {
                    JOptionPane.showMessageDialog(this, "User added successfully!");
                    loadUsers(tableModel);
                    dialog.dispose();
                } else {
                    showError("Failed to add user.");
                }

            } catch (Exception ex) {
                showError("Error adding user: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }


    private void editUser(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a user to edit.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);

        // Load full user from DAO (safer than from table)
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.getUserById(userId);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user == null) {
                        showError("User not found.");
                        return;
                    }
                    showEditUserDialog(user, tableModel);
                } catch (Exception e) {
                    showError("Error loading user: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showEditUserDialog(User user, DefaultTableModel tableModel) {
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(400, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtUsername = new JTextField(user.getUsername());
        JTextField txtFullName = new JTextField(user.getFullName());
        JTextField txtEmail = new JTextField(user.getEmail());
        JTextField txtPhone = new JTextField(user.getPhone());
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"USER", "ADMIN"});
        cbRole.setSelectedItem(user.getRole());
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        cbStatus.setSelectedItem(user.getStatus());

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtUsername, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtFullName, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtEmail, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtPhone, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        dialog.add(cbRole, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        dialog.add(cbStatus, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = createButton("Save Changes", COLOR_SUCCESS);
        JButton cancelBtn = createButton("Cancel", COLOR_DANGER);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        row++; gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        dialog.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String fullName = txtFullName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String role = (String) cbRole.getSelectedItem();
            String status = (String) cbStatus.getSelectedItem();

            if (username.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                showWarning("Please fill in all required fields (username, full name, email).");
                return;
            }

            try {
                user.setUsername(username);
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhone(phone);
                user.setRole(role);
                user.setStatus(status);

                // TODO: adjust to your DAO method
                userDAO.updateUser(user);

                JOptionPane.showMessageDialog(this,
                        "User updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadUsers(tableModel);
                dialog.dispose();
            } catch (Exception ex) {
                showError("Error updating user: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteUser(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a user to delete.");
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
        JDialog dialog = new JDialog(this, "Add Bus", true);
        dialog.setSize(400, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtBusNumber = new JTextField();
        JTextField txtType = new JTextField();
        JTextField txtCapacity = new JTextField();
        JTextField txtReg = new JTextField();
        JTextField txtDriver = new JTextField();
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "MAINTENANCE"});

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Bus Number:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtBusNumber, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtType, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtCapacity, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Registration #:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtReg, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Driver:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtDriver, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        dialog.add(cbStatus, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = createButton("Save", COLOR_SUCCESS);
        JButton cancelBtn = createButton("Cancel", COLOR_DANGER);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        row++; gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        dialog.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            String busNumber = txtBusNumber.getText().trim();
            String type = txtType.getText().trim();
            String capStr = txtCapacity.getText().trim();
            String reg = txtReg.getText().trim();
            String driver = txtDriver.getText().trim();
            String status = (String) cbStatus.getSelectedItem();

            if (busNumber.isEmpty() || type.isEmpty() || capStr.isEmpty() || reg.isEmpty()) {
                showWarning("Please fill in all required fields.");
                return;
            }

            try {
                int capacity = Integer.parseInt(capStr);

                // TODO: adjust to your actual Bus class and DAO
                Bus bus = new Bus();
                bus.setBusNumber(busNumber);
                bus.setBusType(type);
                bus.setCapacity(capacity);
                bus.setRegistrationNumber(reg);
                bus.setDriverName(driver.isEmpty() ? null : driver);
                bus.setStatus(status);

                busDAO.addBus(bus);

                JOptionPane.showMessageDialog(this,
                        "Bus added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadBuses(tableModel);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                showWarning("Capacity must be a valid number.");
            } catch (Exception ex) {
                showError("Error adding bus: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void editBus(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a bus to edit.");
            return;
        }
        int busId = (int) tableModel.getValueAt(selectedRow, 0);

        SwingWorker<Bus, Void> worker = new SwingWorker<Bus, Void>() {
            @Override
            protected Bus doInBackground() throws Exception {
                return busDAO.getBusById(busId);
            }

            @Override
            protected void done() {
                try {
                    Bus bus = get();
                    if (bus == null) {
                        showError("Bus not found.");
                        return;
                    }
                    showEditBusDialog(bus, tableModel);
                } catch (Exception e) {
                    showError("Error loading bus: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showEditBusDialog(Bus bus, DefaultTableModel tableModel) {
        JDialog dialog = new JDialog(this, "Edit Bus", true);
        dialog.setSize(400, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtBusNumber = new JTextField(bus.getBusNumber());
        JTextField txtType = new JTextField(bus.getBusType());
        JTextField txtCapacity = new JTextField(String.valueOf(bus.getCapacity()));
        JTextField txtReg = new JTextField(bus.getRegistrationNumber());
        JTextField txtDriver = new JTextField(bus.getDriverName() != null ? bus.getDriverName() : "");
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "MAINTENANCE"});
        cbStatus.setSelectedItem(bus.getStatus());

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Bus Number:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtBusNumber, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtType, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtCapacity, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Registration #:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtReg, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Driver:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtDriver, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        dialog.add(cbStatus, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = createButton("Save Changes", COLOR_SUCCESS);
        JButton cancelBtn = createButton("Cancel", COLOR_DANGER);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        row++; gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        dialog.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            String busNumber = txtBusNumber.getText().trim();
            String type = txtType.getText().trim();
            String capStr = txtCapacity.getText().trim();
            String reg = txtReg.getText().trim();
            String driver = txtDriver.getText().trim();
            String status = (String) cbStatus.getSelectedItem();

            if (busNumber.isEmpty() || type.isEmpty() || capStr.isEmpty() || reg.isEmpty()) {
                showWarning("Please fill in all required fields.");
                return;
            }

            try {
                int capacity = Integer.parseInt(capStr);

                bus.setBusNumber(busNumber);
                bus.setBusType(type);
                bus.setCapacity(capacity);
                bus.setRegistrationNumber(reg);
                bus.setDriverName(driver.isEmpty() ? null : driver);
                bus.setStatus(status);

                busDAO.updateBus(bus);

                JOptionPane.showMessageDialog(this,
                        "Bus updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadBuses(tableModel);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                showWarning("Capacity must be a valid number.");
            } catch (Exception ex) {
                showError("Error updating bus: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteBus(JTable busesTable, DefaultTableModel tableModel) {
        int selectedRow = busesTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a bus to delete.");
            return;
        }
        int busId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this bus?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                busDAO.deleteBus(busId);
                JOptionPane.showMessageDialog(this,
                        "Bus deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadBuses(tableModel);
            } catch (Exception ex) {
                showError("Error deleting bus: " + ex.getMessage());
            }
        }
    }

    private void addRoute(DefaultTableModel tableModel) {
        JDialog dialog = new JDialog(this, "Add Route", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtName = new JTextField();
        JTextField txtOrigin = new JTextField();
        JTextField txtDestination = new JTextField();
        JTextField txtDistance = new JTextField();
        JTextField txtDuration = new JTextField();
        JTextField txtFare = new JTextField();
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Route Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtName, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtOrigin, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtDestination, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Distance (km):"), gbc);
        gbc.gridx = 1;
        dialog.add(txtDistance, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Duration (min):"), gbc);
        gbc.gridx = 1;
        dialog.add(txtDuration, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Fare:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtFare, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        dialog.add(cbStatus, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = createButton("Save", COLOR_SUCCESS);
        JButton cancelBtn = createButton("Cancel", COLOR_DANGER);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        row++; gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        dialog.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            String name = txtName.getText().trim();
            String origin = txtOrigin.getText().trim();
            String destination = txtDestination.getText().trim();
            String distStr = txtDistance.getText().trim();
            String durStr = txtDuration.getText().trim();
            String fareStr = txtFare.getText().trim();
            String status = (String) cbStatus.getSelectedItem();

            if (name.isEmpty() || origin.isEmpty() || destination.isEmpty()
                    || distStr.isEmpty() || durStr.isEmpty() || fareStr.isEmpty()) {
                showWarning("Please fill in all fields.");
                return;
            }

            try {
                double distance = Double.parseDouble(distStr);
                int duration = Integer.parseInt(durStr);
                double fare = Double.parseDouble(fareStr);

                Route route = new Route();
                route.setRouteName(name);
                route.setOrigin(origin);
                route.setDestination(destination);
                route.setDistanceKm(distance);
                route.setEstimatedDurationMinutes(duration);
                route.setFare(fare);
                route.setStatus(status);

                routeDAO.addRoute(route);

                JOptionPane.showMessageDialog(this,
                        "Route added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadRoutes(tableModel);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                showWarning("Distance, duration, and fare must be valid numbers.");
            } catch (Exception ex) {
                showError("Error adding route: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void editRoute(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a route to edit.");
            return;
        }

        int routeId = (int) tableModel.getValueAt(selectedRow, 0);

        SwingWorker<Route, Void> worker = new SwingWorker<Route, Void>() {
            @Override
            protected Route doInBackground() throws Exception {
                return routeDAO.getRouteById(routeId);
            }

            @Override
            protected void done() {
                try {
                    Route route = get();
                    if (route == null) {
                        showError("Route not found.");
                        return;
                    }
                    showEditRouteDialog(route, tableModel);
                } catch (Exception e) {
                    showError("Error loading route: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showEditRouteDialog(Route route, DefaultTableModel tableModel) {
        JDialog dialog = new JDialog(this, "Edit Route", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtName = new JTextField(route.getRouteName());
        JTextField txtOrigin = new JTextField(route.getOrigin());
        JTextField txtDestination = new JTextField(route.getDestination());
        JTextField txtDistance = new JTextField(String.valueOf(route.getDistanceKm()));
        JTextField txtDuration = new JTextField(String.valueOf(route.getEstimatedDurationMinutes()));
        JTextField txtFare = new JTextField(String.valueOf(route.getFare()));
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        cbStatus.setSelectedItem(route.getStatus());

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Route Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtName, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtOrigin, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtDestination, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Distance (km):"), gbc);
        gbc.gridx = 1;
        dialog.add(txtDistance, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Duration (min):"), gbc);
        gbc.gridx = 1;
        dialog.add(txtDuration, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Fare:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtFare, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        dialog.add(cbStatus, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = createButton("Save Changes", COLOR_SUCCESS);
        JButton cancelBtn = createButton("Cancel", COLOR_DANGER);
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        row++; gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        dialog.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            String name = txtName.getText().trim();
            String origin = txtOrigin.getText().trim();
            String destination = txtDestination.getText().trim();
            String distStr = txtDistance.getText().trim();
            String durStr = txtDuration.getText().trim();
            String fareStr = txtFare.getText().trim();
            String status = (String) cbStatus.getSelectedItem();

            if (name.isEmpty() || origin.isEmpty() || destination.isEmpty()
                    || distStr.isEmpty() || durStr.isEmpty() || fareStr.isEmpty()) {
                showWarning("Please fill in all fields.");
                return;
            }

            try {
                double distance = Double.parseDouble(distStr);
                int duration = Integer.parseInt(durStr);
                double fare = Double.parseDouble(fareStr);

                route.setRouteName(name);
                route.setOrigin(origin);
                route.setDestination(destination);
                route.setDistanceKm(distance);
                route.setEstimatedDurationMinutes(duration);
                route.setFare(fare);
                route.setStatus(status);

                routeDAO.updateRoute(route);

                JOptionPane.showMessageDialog(this,
                        "Route updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadRoutes(tableModel);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                showWarning("Distance, duration, and fare must be valid numbers.");
            } catch (Exception ex) {
                showError("Error updating route: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteRoute(JTable routesTable, DefaultTableModel tableModel) {
        int selectedRow = routesTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a route to delete.");
            return;
        }
        int routeId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this route?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                routeDAO.deleteRoute(routeId);
                JOptionPane.showMessageDialog(this,
                        "Route deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadRoutes(tableModel);
            } catch (Exception ex) {
                showError("Error deleting route: " + ex.getMessage());
            }
        }
    }

    private void viewBookingDetails(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a booking to view.");
            return;
        }

        int bookingId = (int) model.getValueAt(selectedRow, 0);

        SwingWorker<Booking, Void> worker = new SwingWorker<Booking, Void>() {
            @Override
            protected Booking doInBackground() throws Exception {
                return bookingDAO.getBookingById(bookingId);
            }

            @Override
            protected void done() {
                try {
                    Booking booking = get();
                    if (booking == null) {
                        showError("Booking not found.");
                        return;
                    }
                    showBookingDetailsDialog(booking);
                } catch (Exception e) {
                    showError("Error loading booking details: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showBookingDetailsDialog(Booking booking) {
        JDialog dialog = new JDialog(this, "Booking Details", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(COLOR_BG);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        detailsPanel.add(new JLabel("Booking ID:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.valueOf(booking.getBookingId())), gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        detailsPanel.add(new JLabel("Passenger:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(booking.getPassengerName()), gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        detailsPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.valueOf(booking.getBookingDate())), gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        detailsPanel.add(new JLabel("Route:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(booking.getRouteName()), gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        detailsPanel.add(new JLabel("Bus:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(booking.getBusNumber()), gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        detailsPanel.add(new JLabel("Seat:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.valueOf(booking.getSeatNumber())), gbc);


        row++; gbc.gridx = 0; gbc.gridy = row;
        detailsPanel.add(new JLabel("Fare:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.format("Rs. %.2f", booking.getFare())), gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        detailsPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(booking.getStatus()), gbc);

        dialog.add(detailsPanel, BorderLayout.CENTER);

        JButton closeBtn = createButton("Close", COLOR_PRIMARY);
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(closeBtn);
        btnPanel.setBackground(COLOR_BG);

        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
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

    /* ========================= UI HELPERS ========================= */

    private JLabel createSectionTitle(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(color);
        return label;
    }

    private JPanel createSectionHeaderWithSearch(String title, Color color,
                                                 TableRowSorter<DefaultTableModel> sorter,
                                                 String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);

        JLabel label = createSectionTitle(title, color);
        panel.add(label, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setBackground(COLOR_BG);
        JTextField searchField = new JTextField(18);
        searchField.setToolTipText(placeholder);
        searchField.putClientProperty("JTextField.placeholderText", placeholder);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String text = searchField.getText();
                if (text == null || text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text.trim()));
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        panel.add(searchPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(240, 248, 255),
                        getWidth(), getHeight(), new Color(220, 230, 245)
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(250, 140));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(90, 90, 90));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(new Color(20, 20, 20));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }



    private DefaultTableModel createTableModel(String[] columnNames) {
        return new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createStyledTable(DefaultTableModel model, Color headerColor) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                } else {
                    c.setBackground(new Color(200, 220, 255));
                }
                c.setForeground(Color.BLACK);
                return c;
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(Color.WHITE);  // ✅ Bright visible background
        header.setForeground(Color.BLACK);
        header.setOpaque(true);

        // ✅ Add a visible bottom border line
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, headerColor));

        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        return table;
    }





    private JButton createButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ✅ Force true background color
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorderPainted(false);

        // ✅ Apply the chosen color
        button.setBackground(bg != null ? bg : new Color(80, 80, 80));

        // ✅ Better padding
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        return button;
    }


    private JPanel createButtonBar(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(COLOR_BG);

        for (JButton btn : buttons) {
            btn.setPreferredSize(new Dimension(130, 35));
            panel.add(btn);
        }
        return panel;
    }

    private JPanel createReportButton(String title, String description, Color color) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
        };

        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // TITLE
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(color);

        // DESCRIPTION
        JLabel lblDesc = new JLabel(description, SwingConstants.CENTER);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(80, 80, 80));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(lblTitle);
        textPanel.add(lblDesc);

        card.add(textPanel, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(245, 245, 245));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }



    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    /* ========================= INNER TYPES ========================= */

    private static class DashboardStats {
        int totalUsers;
        int totalBuses;
        int totalRoutes;
        int totalBookings;
        double totalRevenue;
        int pendingComplaints;
    }
    
    private void exportRevenueExcel(DefaultTableModel model) {
        try {
            File file = new File("RevenueReport.csv");
            FileWriter fw = new FileWriter(file);

            // Write header
            fw.write("Category,Amount\n");

            // Write rows
            for (int i = 0; i < model.getRowCount(); i++) {
                fw.write(model.getValueAt(i, 0) + "," + model.getValueAt(i, 1) + "\n");
            }

            fw.close();

            JOptionPane.showMessageDialog(this,
                    "Exported successfully:\n" + file.getAbsolutePath(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            showError("Export failed: " + e.getMessage());
        }
    }


}