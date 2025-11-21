package dal;

import bl.Complaint;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ComplaintDAO - Data Access Object for Complaint operations
 */
public class ComplaintDAO {
    
    private DatabaseConnection dbConnection;
    
    public ComplaintDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // CREATE - Submit complaint
    public boolean submitComplaint(Complaint complaint) throws SQLException {
        String query = "INSERT INTO Complaints (user_id, complaint_type, subject, description, " +
                      "priority, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, complaint.getUserId());
            stmt.setString(2, complaint.getComplaintType());
            stmt.setString(3, complaint.getSubject());
            stmt.setString(4, complaint.getDescription());
            stmt.setString(5, complaint.getPriority());
            stmt.setString(6, complaint.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    complaint.setComplaintId(keys.getInt(1));
                }
                return true;
            }
            return false;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get complaint by ID
    public Complaint getComplaintById(int complaintId) throws SQLException {
        String query = "SELECT c.*, u.full_name as user_name " +
                      "FROM Complaints c " +
                      "JOIN Users u ON c.user_id = u.user_id " +
                      "WHERE c.complaint_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, complaintId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToComplaint(rs);
            }
            return null;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get all complaints
    public List<Complaint> getAllComplaints() throws SQLException {
        List<Complaint> complaints = new ArrayList<>();
        String query = "SELECT c.*, u.full_name as user_name " +
                      "FROM Complaints c " +
                      "JOIN Users u ON c.user_id = u.user_id " +
                      "ORDER BY c.created_at DESC";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                complaints.add(mapResultSetToComplaint(rs));
            }
            
            return complaints;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get complaints by user
    public List<Complaint> getComplaintsByUser(int userId) throws SQLException {
        List<Complaint> complaints = new ArrayList<>();
        String query = "SELECT c.*, u.full_name as user_name " +
                      "FROM Complaints c " +
                      "JOIN Users u ON c.user_id = u.user_id " +
                      "WHERE c.user_id = ? " +
                      "ORDER BY c.created_at DESC";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                complaints.add(mapResultSetToComplaint(rs));
            }
            
            return complaints;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get complaints by status
    public List<Complaint> getComplaintsByStatus(String status) throws SQLException {
        List<Complaint> complaints = new ArrayList<>();
        String query = "SELECT c.*, u.full_name as user_name " +
                      "FROM Complaints c " +
                      "JOIN Users u ON c.user_id = u.user_id " +
                      "WHERE c.status = ? " +
                      "ORDER BY c.priority DESC, c.created_at DESC";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                complaints.add(mapResultSetToComplaint(rs));
            }
            
            return complaints;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get complaints by priority
    public List<Complaint> getComplaintsByPriority(String priority) throws SQLException {
        List<Complaint> complaints = new ArrayList<>();
        String query = "SELECT c.*, u.full_name as user_name " +
                      "FROM Complaints c " +
                      "JOIN Users u ON c.user_id = u.user_id " +
                      "WHERE c.priority = ? " +
                      "ORDER BY c.created_at DESC";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, priority);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                complaints.add(mapResultSetToComplaint(rs));
            }
            
            return complaints;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Update complaint status
    public boolean updateComplaintStatus(int complaintId, String status) throws SQLException {
        String query = "UPDATE Complaints SET status = ? WHERE complaint_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, complaintId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Resolve complaint
    public boolean resolveComplaint(int complaintId, String adminResponse) throws SQLException {
        String query = "UPDATE Complaints SET status = 'RESOLVED', admin_response = ?, " +
                      "resolved_at = CURRENT_TIMESTAMP WHERE complaint_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, adminResponse);
            stmt.setInt(2, complaintId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Close complaint
    public boolean closeComplaint(int complaintId) throws SQLException {
        return updateComplaintStatus(complaintId, "CLOSED");
    }
    
    // DELETE - Delete complaint
    public boolean deleteComplaint(int complaintId) throws SQLException {
        String query = "DELETE FROM Complaints WHERE complaint_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, complaintId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // Get complaint statistics
    public int getComplaintCountByStatus(String status) throws SQLException {
        String query = "SELECT COUNT(*) FROM Complaints WHERE status = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // Get pending complaints count
    public int getPendingComplaintsCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Complaints " +
                      "WHERE status IN ('SUBMITTED', 'UNDER_REVIEW')";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    private Complaint mapResultSetToComplaint(ResultSet rs) throws SQLException {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(rs.getInt("complaint_id"));
        complaint.setUserId(rs.getInt("user_id"));
        complaint.setUserName(rs.getString("user_name"));
        complaint.setComplaintType(rs.getString("complaint_type"));
        complaint.setSubject(rs.getString("subject"));
        complaint.setDescription(rs.getString("description"));
        complaint.setPriority(rs.getString("priority"));
        complaint.setStatus(rs.getString("status"));
        complaint.setAdminResponse(rs.getString("admin_response"));
        complaint.setCreatedAt(rs.getTimestamp("created_at"));
        complaint.setUpdatedAt(rs.getTimestamp("updated_at"));
        complaint.setResolvedAt(rs.getTimestamp("resolved_at"));
        return complaint;
    }
}
