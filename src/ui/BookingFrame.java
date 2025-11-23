package ui;

import bl.*;
import dal.*;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * BookingFrame - Complete seat reservation functionality
 */
public class BookingFrame extends JFrame {
    
    private Passenger passenger;
    private JComboBox<RouteItem> routeComboBox;
    private JSpinner dateSpinner;
    private JPanel seatPanel;
    private JButton confirmButton;
    private JButton cancelButton;
    private JLabel fareLabel;
    private JComboBox<String> pickupStopCombo;
    private JComboBox<String> dropoffStopCombo;
    
    private RouteAssignmentDAO assignmentDAO;
    private BookingDAO bookingDAO;
    private PaymentDAO paymentDAO;
    
    private RouteAssignment selectedAssignment;
    private int selectedSeat = -1;
    private List<Integer> bookedSeats;
    
    public BookingFrame(Passenger passenger) {
        this.passenger = passenger;
        this.assignmentDAO = new RouteAssignmentDAO();
        this.bookingDAO = new BookingDAO();
        this.paymentDAO = new PaymentDAO();
        initializeUI();
        loadRoutes();
    }
    
    private void initializeUI() {
        setTitle("Book Your Seat");
        setSize(700, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Reserve Your Seat");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Route Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Route:"), gbc);
        
        routeComboBox = new JComboBox<>();
        routeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        routeComboBox.addActionListener(e -> onRouteSelected());
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(routeComboBox, gbc);
        
        // Date Selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Travel Date:"), gbc);
        
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new java.util.Date());
        dateSpinner.addChangeListener(e -> onDateChanged());
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(dateSpinner, gbc);
        
        // Pickup Stop
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Pickup Stop:"), gbc);
        
        pickupStopCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(pickupStopCombo, gbc);
        
        // Dropoff Stop
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Dropoff Stop:"), gbc);
        
        dropoffStopCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(dropoffStopCombo, gbc);
        
        // Fare Display
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Fare:"), gbc);
        
        fareLabel = new JLabel("Rs. 0.00");
        fareLabel.setFont(new Font("Arial", Font.BOLD, 16));
        fareLabel.setForeground(new Color(0, 153, 76));
        gbc.gridx = 1;
        formPanel.add(fareLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        
        // Seat Selection Panel
        JPanel seatContainer = new JPanel(new BorderLayout());
        seatContainer.setBackground(Color.WHITE);
        seatContainer.setBorder(BorderFactory.createTitledBorder("Select Your Seat"));
        
        seatPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        seatPanel.setBackground(Color.WHITE);
        JScrollPane seatScroll = new JScrollPane(seatPanel);
        seatScroll.setPreferredSize(new Dimension(600, 300));
        seatContainer.add(seatScroll, BorderLayout.CENTER);
        
        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.add(createLegendItem("Available", new Color(0, 153, 76)));
        legendPanel.add(createLegendItem("Booked", Color.RED));
        legendPanel.add(createLegendItem("Selected", new Color(0, 102, 204)));
        seatContainer.add(legendPanel, BorderLayout.SOUTH);
        
        mainPanel.add(seatContainer, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        confirmButton = new JButton("Confirm Booking");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setBackground(new Color(0, 153, 76));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setPreferredSize(new Dimension(160, 40));
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> confirmBooking());
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(new Color(204, 0, 0));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(colorBox);
        panel.add(label);
        
        return panel;
    }
    
    private void loadRoutes() {
        SwingWorker<List<RouteAssignment>, Void> worker = new SwingWorker<List<RouteAssignment>, Void>() {
            @Override
            protected List<RouteAssignment> doInBackground() throws Exception {
                return assignmentDAO.getActiveAssignments();
            }
            
            @Override
            protected void done() {
                try {
                    List<RouteAssignment> assignments = get();
                    routeComboBox.removeAllItems();
                    
                    for (RouteAssignment assignment : assignments) {
                        routeComboBox.addItem(new RouteItem(assignment));
                    }
                    
                    if (routeComboBox.getItemCount() > 0) {
                        routeComboBox.setSelectedIndex(0);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BookingFrame.this,
                        "Error loading routes: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void onRouteSelected() {
        RouteItem item = (RouteItem) routeComboBox.getSelectedItem();
        if (item != null) {
            selectedAssignment = item.getAssignment();
            fareLabel.setText(String.format("Rs. %.2f", selectedAssignment.getFare()));
            
            // Load stops
            String[] stops = selectedAssignment.getRouteOrigin().split(",");
            pickupStopCombo.removeAllItems();
            dropoffStopCombo.removeAllItems();
            
            // Add stops from route
            if (selectedAssignment.getRouteName() != null) {
                pickupStopCombo.addItem(selectedAssignment.getRouteOrigin());
                dropoffStopCombo.addItem(selectedAssignment.getRouteDestination());
            }
            
            loadSeats();
        }
    }
    
    private void onDateChanged() {
        if (selectedAssignment != null) {
            loadSeats();
        }
    }
    
    private void loadSeats() {
        if (selectedAssignment == null) return;
        
        seatPanel.removeAll();
        selectedSeat = -1;
        confirmButton.setEnabled(false);
        
        java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
        Date sqlDate = new Date(utilDate.getTime());
        
        SwingWorker<List<Integer>, Void> worker = new SwingWorker<List<Integer>, Void>() {
            @Override
            protected List<Integer> doInBackground() throws Exception {
                return bookingDAO.getBookedSeats(selectedAssignment.getAssignmentId(), sqlDate);
            }
            
            @Override
            protected void done() {
                try {
                    bookedSeats = get();
                    createSeatButtons();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BookingFrame.this,
                        "Error loading seats: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void createSeatButtons() {
        // Assume bus capacity of 40 seats (can be fetched from bus details)
        int capacity = 40;
        
        for (int i = 1; i <= capacity; i++) {
            final int seatNumber = i;
            JButton seatButton = new JButton(String.valueOf(seatNumber));
            seatButton.setFont(new Font("Arial", Font.BOLD, 14));
            seatButton.setFocusPainted(false);
            seatButton.setPreferredSize(new Dimension(60, 60));
            
            if (bookedSeats.contains(seatNumber)) {
                // Booked seat
                seatButton.setBackground(Color.RED);
                seatButton.setForeground(Color.WHITE);
                seatButton.setEnabled(false);
            } else {
                // Available seat
                seatButton.setBackground(new Color(0, 153, 76));
                seatButton.setForeground(Color.WHITE);
                seatButton.addActionListener(e -> selectSeat(seatNumber, seatButton));
            }
            
            seatPanel.add(seatButton);
        }
        
        seatPanel.revalidate();
        seatPanel.repaint();
    }
    
    private void selectSeat(int seatNumber, JButton button) {
        // Reset previous selection
        if (selectedSeat != -1) {
            for (Component comp : seatPanel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton btn = (JButton) comp;
                    if (btn.getText().equals(String.valueOf(selectedSeat)) && btn.isEnabled()) {
                        btn.setBackground(new Color(0, 153, 76));
                    }
                }
            }
        }
        
        // Select new seat
        selectedSeat = seatNumber;
        button.setBackground(new Color(0, 102, 204));
        confirmButton.setEnabled(true);
    }
    
    private void confirmBooking() {
        if (selectedSeat == -1 || selectedAssignment == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a seat first.",
                "No Seat Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String pickup = (String) pickupStopCombo.getSelectedItem();
        String dropoff = (String) dropoffStopCombo.getSelectedItem();
        
        if (pickup == null || dropoff == null) {
            JOptionPane.showMessageDialog(this,
                "Please select pickup and dropoff stops.",
                "Incomplete Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Confirm booking?\n\nRoute: %s\nSeat: %d\nFare: Rs. %.2f\nDate: %s",
                selectedAssignment.getRouteName(),
                selectedSeat,
                selectedAssignment.getFare(),
                dateSpinner.getValue()),
            "Confirm Booking",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Process booking
        confirmButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            Booking newBooking;
            
            @Override
            protected Boolean doInBackground() throws Exception {
                java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
                Date sqlDate = new Date(utilDate.getTime());
                
                // Create booking
                newBooking = new Booking(
                    passenger.getUserId(),
                    selectedAssignment.getAssignmentId(),
                    sqlDate,
                    selectedSeat,
                    pickup,
                    dropoff,
                    selectedAssignment.getFare()
                );
                
                boolean bookingCreated = bookingDAO.createBooking(newBooking);
                
                if (bookingCreated) {
                    // Process payment
                    Payment payment = new Payment(
                        newBooking.getBookingId(),
                        newBooking.getFare(),
                        "ONLINE"
                    );
                    payment.processPayment();
                    return paymentDAO.processPayment(payment);
                }
                
                return false;
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    
                    if (success) {

                        // ✅ Open Invoice Window with booking & passenger info
                        InvoiceFrame invoice = new InvoiceFrame(newBooking, passenger);

                        // ✅ Show invoice
                        invoice.setVisible(true);

                        // ✅ Close booking window
                        dispose();
                  

                    } else {
                        JOptionPane.showMessageDialog(BookingFrame.this,
                            "Booking failed. Please try again.",
                            "Booking Failed",
                            JOptionPane.ERROR_MESSAGE);
                        confirmButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(BookingFrame.this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    confirmButton.setEnabled(true);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        
        worker.execute();
    }
    
    // Inner class to hold route assignment with custom toString
    private static class RouteItem {
        private RouteAssignment assignment;
        
        public RouteItem(RouteAssignment assignment) {
            this.assignment = assignment;
        }
        
        public RouteAssignment getAssignment() {
            return assignment;
        }
        
        @Override
        public String toString() {
            return String.format("%s (%s → %s) - %s",
                assignment.getRouteName(),
                assignment.getRouteOrigin(),
                assignment.getRouteDestination(),
                assignment.getDepartureTime());
        }
    }
}