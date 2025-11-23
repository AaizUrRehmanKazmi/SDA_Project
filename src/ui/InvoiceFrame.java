package ui;

import bl.Booking;
import bl.Passenger;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class InvoiceFrame extends JFrame {

    private Booking booking;
    private Passenger passenger;

    public InvoiceFrame(Booking booking, Passenger passenger) {
        this.booking = booking;
        this.passenger = passenger;
        initComponents();
    }

    private void initComponents() {
        setTitle("Booking Invoice");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main container with padding
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createDetailsCard(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    // ✅ Modern Header
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("UZAIR TRANSPORT SYSTEM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(41, 128, 185));

        JLabel lblSub = new JLabel("Booking Invoice", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(120, 120, 120));

        JLabel lblInvoice = new JLabel("Invoice#: INV-" + booking.getBookingId(), SwingConstants.CENTER);
        lblInvoice.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panel.add(lblTitle);
        panel.add(lblSub);
        panel.add(lblInvoice);

        return panel;
    }

    // ✅ Card-style details section
    private JPanel createDetailsCard() {
        JPanel card = new JPanel(new GridLayout(7, 2, 10, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        addField(card, "Passenger:", passenger.getFullName());
        addField(card, "Route:", booking.getRouteName());
        addField(card, "Bus:", booking.getBusNumber());
        addField(card, "Seat:", String.valueOf(booking.getSeatNumber()));
        addField(card, "Fare:", "Rs. " + booking.getFare());
        addField(card, "Status:", booking.getStatus());

        String date = new SimpleDateFormat("dd/MM/yyyy").format(booking.getBookingDate());
        addField(card, "Travel Date:", date);

        return card;
    }

    private void addField(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(80, 80, 80));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(new Color(30, 30, 30));

        panel.add(lbl);
        panel.add(val);
    }

    // ✅ Professional footer with CTA button
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JTextArea footer = new JTextArea(
                "• Tickets are non-refundable once confirmed.\n" +
                "• Arrive 10 minutes before departure.\n" +
                "• Thank you for choosing Uzair Transport."
        );
        footer.setEditable(false);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setBackground(new Color(245, 247, 250));
        footer.setForeground(new Color(100, 100, 100));

        JButton printBtn = new JButton("Download / Print Invoice");
        printBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        printBtn.setForeground(Color.WHITE);
        printBtn.setBackground(new Color(41, 128, 185));
        printBtn.setFocusPainted(false);
        printBtn.setPreferredSize(new Dimension(200, 45));
        printBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Printing invoice..."));

        panel.add(footer, BorderLayout.CENTER);

        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(printBtn);

        panel.add(buttonWrapper, BorderLayout.SOUTH);

        return panel;
    }
}