package dal;

import bl.RouteChangeRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RouteChangeRequestDAO - Data access object for route change requests
 */
public class RouteChangeRequestDAO {
    
    private DatabaseConnection dbConnection;
    
    public RouteChangeRequestDAO() {
        // Get singleton instance of DatabaseConnection
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // CREATE - Add new route change request
    public boolean addRouteChangeRequest(RouteChangeRequest request) throws SQLException {
        String query = "INSERT INTO RouteChangeRequests " +
                      "(user_id, user_role, booking_id, trip_id, current_route_id, " +
                      "requested_route_id, reason, status, created_at) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, request.getUserId());
            stmt.setString(2, request.getUserRole());
            stmt.setObject(3, request.getBookingId() > 0 ? request.getBookingId() : null);
            stmt.setObject(4, request.getTripId() > 0 ? request.getTripId() : null);
            stmt.setInt(5, request.getCurrentRouteId());
            stmt.setInt(6, request.getRequestedRouteId());
            stmt.setString(7, request.getReason());
            stmt.setString(8, request.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    request.setRequestId(keys.getInt(1));
                }
                return true;
            }
            return false;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get request by ID
    public RouteChangeRequest getRequestById(int requestId) throws SQLException {
        String query = "SELECT rcr.*, u.full_name as user_name, cr.route_name as current_route_name, " +
                      "rr.route_name as requested_route_name " +
                      "FROM RouteChangeRequests rcr " +
                      "JOIN Users u ON rcr.user_id = u.user_id " +
                      "JOIN Routes cr ON rcr.current_route_id = cr.route_id " +
                      "JOIN Routes rr ON rcr.requested_route_id = rr.route_id " +
                      "WHERE rcr.request_id = ?";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, requestId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRequest(rs);
            }
            return null;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get pending requests by user
    public List<RouteChangeRequest> getPendingRequestsByUser(int userId) throws SQLException {
        List<RouteChangeRequest> requests = new ArrayList<>();
        String query = "SELECT rcr.*, u.full_name as user_name, cr.route_name as current_route_name, " +
                      "rr.route_name as requested_route_name " +
                      "FROM RouteChangeRequests rcr " +
                      "JOIN Users u ON rcr.user_id = u.user_id " +
                      "JOIN Routes cr ON rcr.current_route_id = cr.route_id " +
                      "JOIN Routes rr ON rcr.requested_route_id = rr.route_id " +
                      "WHERE rcr.user_id = ? AND rcr.status = 'PENDING' " +
                      "ORDER BY rcr.created_at DESC";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get all route change requests by user
    public List<RouteChangeRequest> getAllRequestsByUser(int userId) throws SQLException {
        List<RouteChangeRequest> requests = new ArrayList<>();
        String query = "SELECT rcr.*, u.full_name as user_name, cr.route_name as current_route_name, " +
                      "rr.route_name as requested_route_name " +
                      "FROM RouteChangeRequests rcr " +
                      "JOIN Users u ON rcr.user_id = u.user_id " +
                      "JOIN Routes cr ON rcr.current_route_id = cr.route_id " +
                      "JOIN Routes rr ON rcr.requested_route_id = rr.route_id " +
                      "WHERE rcr.user_id = ? " +
                      "ORDER BY rcr.created_at DESC";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get all pending requests (for Admin)
    public List<RouteChangeRequest> getAllPendingRequests() throws SQLException {
        List<RouteChangeRequest> requests = new ArrayList<>();
        String query = "SELECT rcr.*, u.full_name as user_name, cr.route_name as current_route_name, " +
                      "rr.route_name as requested_route_name " +
                      "FROM RouteChangeRequests rcr " +
                      "JOIN Users u ON rcr.user_id = u.user_id " +
                      "JOIN Routes cr ON rcr.current_route_id = cr.route_id " +
                      "JOIN Routes rr ON rcr.requested_route_id = rr.route_id " +
                      "WHERE rcr.status = 'PENDING' " +
                      "ORDER BY rcr.created_at DESC";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get all requests (for Admin)
    public List<RouteChangeRequest> getAllRequests() throws SQLException {
        List<RouteChangeRequest> requests = new ArrayList<>();
        String query = "SELECT rcr.*, u.full_name as user_name, cr.route_name as current_route_name, " +
                      "rr.route_name as requested_route_name " +
                      "FROM RouteChangeRequests rcr " +
                      "JOIN Users u ON rcr.user_id = u.user_id " +
                      "JOIN Routes cr ON rcr.current_route_id = cr.route_id " +
                      "JOIN Routes rr ON rcr.requested_route_id = rr.route_id " +
                      "ORDER BY rcr.created_at DESC";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get requests by status
    public List<RouteChangeRequest> getRequestsByStatus(String status) throws SQLException {
        List<RouteChangeRequest> requests = new ArrayList<>();
        String query = "SELECT rcr.*, u.full_name as user_name, cr.route_name as current_route_name, " +
                      "rr.route_name as requested_route_name " +
                      "FROM RouteChangeRequests rcr " +
                      "JOIN Users u ON rcr.user_id = u.user_id " +
                      "JOIN Routes cr ON rcr.current_route_id = cr.route_id " +
                      "JOIN Routes rr ON rcr.requested_route_id = rr.route_id " +
                      "WHERE rcr.status = ? " +
                      "ORDER BY rcr.created_at DESC";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToRequest(rs));
            }
            return requests;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Update request status
    public boolean updateRequestStatus(int requestId, String status) throws SQLException {
        String query = "UPDATE RouteChangeRequests SET status = ?, updated_at = NOW() WHERE request_id = ?";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Approve request with admin notes
    public boolean approveRequest(int requestId, String adminNotes) throws SQLException {
        String query = "UPDATE RouteChangeRequests SET status = 'APPROVED', " +
                      "admin_notes = ?, approved_at = NOW(), updated_at = NOW() WHERE request_id = ?";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, adminNotes);
            stmt.setInt(2, requestId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Reject request with admin notes
    public boolean rejectRequest(int requestId, String adminNotes) throws SQLException {
        String query = "UPDATE RouteChangeRequests SET status = 'REJECTED', " +
                      "admin_notes = ?, updated_at = NOW() WHERE request_id = ?";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, adminNotes);
            stmt.setInt(2, requestId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Cancel request
    public boolean cancelRequest(int requestId) throws SQLException {
        String query = "UPDATE RouteChangeRequests SET status = 'CANCELLED', updated_at = NOW() WHERE request_id = ?";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, requestId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // Helper method to map ResultSet to RouteChangeRequest
    private RouteChangeRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        RouteChangeRequest request = new RouteChangeRequest();
        request.setRequestId(rs.getInt("request_id"));
        request.setUserId(rs.getInt("user_id"));
        request.setUserName(rs.getString("user_name"));
        request.setUserRole(rs.getString("user_role"));
        request.setBookingId(rs.getInt("booking_id"));
        request.setTripId(rs.getInt("trip_id"));
        request.setCurrentRouteId(rs.getInt("current_route_id"));
        request.setRequestedRouteId(rs.getInt("requested_route_id"));
        request.setCurrentRouteName(rs.getString("current_route_name"));
        request.setRequestedRouteName(rs.getString("requested_route_name"));
        request.setReason(rs.getString("reason"));
        request.setStatus(rs.getString("status"));
        request.setAdminNotes(rs.getString("admin_notes"));
        request.setCreatedAt(rs.getTimestamp("created_at"));
        request.setUpdatedAt(rs.getTimestamp("updated_at"));
        request.setApprovedAt(rs.getTimestamp("approved_at"));
        return request;
    }
}