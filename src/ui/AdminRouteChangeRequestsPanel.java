package ui;

import bl.*;
import dal.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * AdminRouteChangeRequestsPanel - UI for admins to review and manage route change requests
 */
public class AdminRouteChangeRequestsPanel extends JPanel {
    
    private RouteChangeRequestDAO requestDAO;
    private JTable requestsTable;
    private JButton approveBtn;
    private JButton rejectBtn;
    private JButton refreshBtn;
    
    public AdminRouteChangeRequestsPanel() {
        this.requestDAO = new RouteChangeRequestDAO();
        initializeUI();
        refreshRequests();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Route Change Requests Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        approveBtn = new JButton("Approve Request");
        approveBtn.setBackground(new Color(0, 153, 76));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFont(new Font("Arial", Font.BOLD, 12));
        approveBtn.addActionListener(e -> approveSelectedRequest());
        
        rejectBtn = new JButton("Reject Request");
        rejectBtn.setBackground(new Color(204, 0, 0));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setFont(new Font("Arial", Font.BOLD, 12));
        rejectBtn.addActionListener(e -> rejectSelectedRequest());
        
        refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(0, 102, 204));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.addActionListener(e -> refreshRequests());
        
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Request ID", "User Name", "User Role", "Current Route", "Requested Route", "Reason", "Status", "Submitted Date"};
        requestsTable = new JTable();
        requestsTable.setColumnSelectionAllowed(false);
        requestsTable.setRowSelectionAllowed(true);
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void refreshRequests() {
        try {
            List<RouteChangeRequest> requests = requestDAO.getAllRequests();
            
            String[][] data = new String[requests.size()][8];
            for (int i = 0; i < requests.size(); i++) {
                RouteChangeRequest req = requests.get(i);
                data[i][0] = String.valueOf(req.getRequestId());
                data[i][1] = req.getUserName();
                data[i][2] = req.getUserRole();
                data[i][3] = req.getCurrentRouteName();
                data[i][4] = req.getRequestedRouteName();
                data[i][5] = req.getReason();
                data[i][6] = req.getStatus();
                data[i][7] = req.getCreatedAt().toString();
            }
            
            String[] columns = {"Request ID", "User Name", "User Role", "Current Route", "Requested Route", "Reason", "Status", "Submitted Date"};
            requestsTable.setModel(new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading requests: " + ex.getMessage());
        }
    }
    
    private void approveSelectedRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int requestId = Integer.parseInt((String) requestsTable.getValueAt(selectedRow, 0));
            String status = (String) requestsTable.getValueAt(selectedRow, 6);
            
            if (!"PENDING".equals(status)) {
                JOptionPane.showMessageDialog(this, "Only PENDING requests can be approved");
                return;
            }
            
            String notes = JOptionPane.showInputDialog(this, "Enter approval notes (optional):", "");
            
            try {
                requestDAO.approveRequest(requestId, notes != null ? notes : "");
                JOptionPane.showMessageDialog(this, "Request approved successfully");
                refreshRequests();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error approving request: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a request to approve");
        }
    }
    
    private void rejectSelectedRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int requestId = Integer.parseInt((String) requestsTable.getValueAt(selectedRow, 0));
            String status = (String) requestsTable.getValueAt(selectedRow, 6);
            
            if (!"PENDING".equals(status)) {
                JOptionPane.showMessageDialog(this, "Only PENDING requests can be rejected");
                return;
            }
            
            String notes = JOptionPane.showInputDialog(this, "Enter rejection reason:", "");
            
            if (notes != null) {
                try {
                    requestDAO.rejectRequest(requestId, notes);
                    JOptionPane.showMessageDialog(this, "Request rejected successfully");
                    refreshRequests();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error rejecting request: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a request to reject");
        }
    }
}