package dal;

import bl.RouteAssignment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RouteAssignmentDAO - Data Access Object for RouteAssignment operations
 */
public class RouteAssignmentDAO {
    
    private DatabaseConnection dbConnection;
    
    public RouteAssignmentDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // CREATE
    public boolean addRouteAssignment(RouteAssignment assignment) throws SQLException {
        String query = "INSERT INTO RouteAssignments (bus_id, route_id, departure_time, " +
                      "arrival_time, days_of_week, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, assignment.getBusId());
            stmt.setInt(2, assignment.getRouteId());
            stmt.setTime(3, assignment.getDepartureTime());
            stmt.setTime(4, assignment.getArrivalTime());
            stmt.setString(5, assignment.getDaysOfWeek());
            stmt.setString(6, assignment.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    assignment.setAssignmentId(keys.getInt(1));
                }
                return true;
            }
            return false;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get all assignments
    public List<RouteAssignment> getAllAssignments() throws SQLException {
        List<RouteAssignment> assignments = new ArrayList<>();
        String query = "SELECT ra.*, b.bus_number, r.route_name, r.origin, r.destination, r.fare " +
                      "FROM RouteAssignments ra " +
                      "JOIN Buses b ON ra.bus_id = b.bus_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "ORDER BY ra.departure_time";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                assignments.add(mapResultSetToAssignment(rs));
            }
            
            return assignments;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get assignment by ID
    public RouteAssignment getAssignmentById(int assignmentId) throws SQLException {
        String query = "SELECT ra.*, b.bus_number, r.route_name, r.origin, r.destination, r.fare " +
                      "FROM RouteAssignments ra " +
                      "JOIN Buses b ON ra.bus_id = b.bus_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "WHERE ra.assignment_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, assignmentId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAssignment(rs);
            }
            return null;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get assignments by bus
    public List<RouteAssignment> getAssignmentsByBus(int busId) throws SQLException {
        List<RouteAssignment> assignments = new ArrayList<>();
        String query = "SELECT ra.*, b.bus_number, r.route_name, r.origin, r.destination, r.fare " +
                      "FROM RouteAssignments ra " +
                      "JOIN Buses b ON ra.bus_id = b.bus_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "WHERE ra.bus_id = ? AND ra.status = 'ACTIVE' " +
                      "ORDER BY ra.departure_time";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, busId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                assignments.add(mapResultSetToAssignment(rs));
            }
            
            return assignments;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get assignments by route
    public List<RouteAssignment> getAssignmentsByRoute(int routeId) throws SQLException {
        List<RouteAssignment> assignments = new ArrayList<>();
        String query = "SELECT ra.*, b.bus_number, r.route_name, r.origin, r.destination, r.fare " +
                      "FROM RouteAssignments ra " +
                      "JOIN Buses b ON ra.bus_id = b.bus_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "WHERE ra.route_id = ? AND ra.status = 'ACTIVE' " +
                      "ORDER BY ra.departure_time";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, routeId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                assignments.add(mapResultSetToAssignment(rs));
            }
            
            return assignments;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get active assignments
    public List<RouteAssignment> getActiveAssignments() throws SQLException {
        List<RouteAssignment> assignments = new ArrayList<>();
        String query = "SELECT ra.*, b.bus_number, r.route_name, r.origin, r.destination, r.fare " +
                      "FROM RouteAssignments ra " +
                      "JOIN Buses b ON ra.bus_id = b.bus_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "WHERE ra.status = 'ACTIVE' " +
                      "ORDER BY r.route_name, ra.departure_time";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                assignments.add(mapResultSetToAssignment(rs));
            }
            
            return assignments;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE
    public boolean updateRouteAssignment(RouteAssignment assignment) throws SQLException {
        String query = "UPDATE RouteAssignments SET bus_id = ?, route_id = ?, " +
                      "departure_time = ?, arrival_time = ?, days_of_week = ?, status = ? " +
                      "WHERE assignment_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            
            stmt.setInt(1, assignment.getBusId());
            stmt.setInt(2, assignment.getRouteId());
            stmt.setTime(3, assignment.getDepartureTime());
            stmt.setTime(4, assignment.getArrivalTime());
            stmt.setString(5, assignment.getDaysOfWeek());
            stmt.setString(6, assignment.getStatus());
            stmt.setInt(7, assignment.getAssignmentId());
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // DELETE
    public boolean deleteRouteAssignment(int assignmentId) throws SQLException {
        String query = "DELETE FROM RouteAssignments WHERE assignment_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, assignmentId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // Cancel assignment (soft delete)
    public boolean cancelAssignment(int assignmentId) throws SQLException {
        String query = "UPDATE RouteAssignments SET status = 'CANCELLED' WHERE assignment_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, assignmentId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // Check if bus is available for assignment
    public boolean isBusAvailableForTime(int busId, Time departureTime, Time arrivalTime, 
                                        String daysOfWeek, int excludeAssignmentId) throws SQLException {
        String query = "SELECT COUNT(*) FROM RouteAssignments " +
                      "WHERE bus_id = ? AND status = 'ACTIVE' " +
                      "AND assignment_id != ? " +
                      "AND (" +
                      "  (departure_time <= ? AND arrival_time >= ?) OR " +
                      "  (departure_time <= ? AND arrival_time >= ?) OR " +
                      "  (departure_time >= ? AND arrival_time <= ?)" +
                      ")";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, busId);
            stmt.setInt(2, excludeAssignmentId);
            stmt.setTime(3, departureTime);
            stmt.setTime(4, departureTime);
            stmt.setTime(5, arrivalTime);
            stmt.setTime(6, arrivalTime);
            stmt.setTime(7, departureTime);
            stmt.setTime(8, arrivalTime);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    private RouteAssignment mapResultSetToAssignment(ResultSet rs) throws SQLException {
        RouteAssignment assignment = new RouteAssignment();
        assignment.setAssignmentId(rs.getInt("assignment_id"));
        assignment.setBusId(rs.getInt("bus_id"));
        assignment.setRouteId(rs.getInt("route_id"));
        assignment.setDepartureTime(rs.getTime("departure_time"));
        assignment.setArrivalTime(rs.getTime("arrival_time"));
        assignment.setDaysOfWeek(rs.getString("days_of_week"));
        assignment.setStatus(rs.getString("status"));
        assignment.setCreatedAt(rs.getTimestamp("created_at"));
        assignment.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Set additional display fields if available
        try {
            assignment.setBusNumber(rs.getString("bus_number"));
            assignment.setRouteName(rs.getString("route_name"));
            assignment.setRouteOrigin(rs.getString("origin"));
            assignment.setRouteDestination(rs.getString("destination"));
            assignment.setFare(rs.getDouble("fare"));
        } catch (SQLException e) {
            // Columns don't exist, skip them
        }
        
        return assignment;
    }
}