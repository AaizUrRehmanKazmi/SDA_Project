package dal;

import bl.Trip;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TripDAO - Data Access Object for Trip operations
 */
public class TripDAO {
    
    private DatabaseConnection dbConnection;
    
    public TripDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // CREATE - Create a new trip
    public boolean createTrip(Trip trip) throws SQLException {
        String query = "INSERT INTO Trips (assignment_id, driver_id, trip_date, status, passengers_count) " +
                      "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, trip.getAssignmentId());
            stmt.setInt(2, trip.getDriverId());
            stmt.setDate(3, trip.getTripDate());
            stmt.setString(4, trip.getStatus());
            stmt.setInt(5, trip.getPassengersCount());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    trip.setTripId(keys.getInt(1));
                }
                return true;
            }
            return false;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get trip by ID
    public Trip getTripById(int tripId) throws SQLException {
        String query = "SELECT t.*, u.full_name as driver_name, b.bus_number, r.route_name " +
                      "FROM Trips t " +
                      "JOIN Users u ON t.driver_id = u.user_id " +
                      "JOIN RouteAssignments ra ON t.assignment_id = ra.assignment_id " +
                      "JOIN Buses b ON ra.bus_id = b.bus_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "WHERE t.trip_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, tripId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTrip(rs);
            }
            return null;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get trips by driver
    public List<Trip> getTripsByDriver(int driverId) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT t.*, u.full_name as driver_name, b.bus_number, r.route_name " +
                      "FROM Trips t " +
                      "JOIN Users u ON t.driver_id = u.user_id " +
                      "JOIN RouteAssignments ra ON t.assignment_id = ra.assignment_id " +
                      "JOIN Buses b ON ra.bus_id = b.bus_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "WHERE t.driver_id = ? " +
                      "ORDER BY t.trip_date DESC, t.created_at DESC";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, driverId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                trips.add(mapResultSetToTrip(rs));
            }
            
            return trips;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get active trips
    public List<Trip> getActiveTrips() throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT t.*, u.full_name as driver_name, b.bus_number, r.route_name " +
                      "FROM Trips t " +
                      "JOIN Users u ON t.driver_id = u.user_id " +
                      "JOIN RouteAssignments ra ON t.assignment_id = ra.assignment_id " +
                      "JOIN Buses b ON ra.bus_id = b.bus_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "WHERE t.status = 'IN_PROGRESS' " +
                      "ORDER BY t.start_time DESC";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                trips.add(mapResultSetToTrip(rs));
            }
            
            return trips;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get trips by date
    public List<Trip> getTripsByDate(Date tripDate) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT t.*, u.full_name as driver_name, b.bus_number, r.route_name " +
                      "FROM Trips t " +
                      "JOIN Users u ON t.driver_id = u.user_id " +
                      "JOIN RouteAssignments ra ON t.assignment_id = ra.assignment_id " +
                      "JOIN Buses b ON ra.bus_id = b.bus_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "WHERE t.trip_date = ? " +
                      "ORDER BY t.start_time";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, tripDate);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                trips.add(mapResultSetToTrip(rs));
            }
            
            return trips;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Start trip
    public boolean startTrip(int tripId) throws SQLException {
        String query = "UPDATE Trips SET status = 'IN_PROGRESS', start_time = CURRENT_TIMESTAMP " +
                      "WHERE trip_id = ? AND status = 'SCHEDULED'";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, tripId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Update location
    public boolean updateTripLocation(int tripId, double latitude, double longitude, 
                                     String currentStop) throws SQLException {
        String query = "UPDATE Trips SET current_location_lat = ?, current_location_lng = ?, " +
                      "current_stop = ? WHERE trip_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, latitude);
            stmt.setDouble(2, longitude);
            stmt.setString(3, currentStop);
            stmt.setInt(4, tripId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - End trip
    public boolean endTrip(int tripId) throws SQLException {
        String query = "UPDATE Trips SET status = 'COMPLETED', end_time = CURRENT_TIMESTAMP " +
                      "WHERE trip_id = ? AND status = 'IN_PROGRESS'";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, tripId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Cancel trip
    public boolean cancelTrip(int tripId) throws SQLException {
        String query = "UPDATE Trips SET status = 'CANCELLED' " +
                      "WHERE trip_id = ? AND status IN ('SCHEDULED', 'IN_PROGRESS')";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, tripId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // Get trip statistics
    public int getTotalTripsCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Trips";
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
    
    // Get completed trips by driver
    public int getCompletedTripsByDriver(int driverId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Trips WHERE driver_id = ? AND status = 'COMPLETED'";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, driverId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    private Trip mapResultSetToTrip(ResultSet rs) throws SQLException {
        Trip trip = new Trip();
        trip.setTripId(rs.getInt("trip_id"));
        trip.setAssignmentId(rs.getInt("assignment_id"));
        trip.setDriverId(rs.getInt("driver_id"));
        trip.setTripDate(rs.getDate("trip_date"));
        trip.setStartTime(rs.getTimestamp("start_time"));
        trip.setEndTime(rs.getTimestamp("end_time"));
        trip.setCurrentLocationLat(rs.getDouble("current_location_lat"));
        trip.setCurrentLocationLng(rs.getDouble("current_location_lng"));
        trip.setCurrentStop(rs.getString("current_stop"));
        trip.setStatus(rs.getString("status"));
        trip.setPassengersCount(rs.getInt("passengers_count"));
        trip.setCreatedAt(rs.getTimestamp("created_at"));
        trip.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Set additional display fields if available
        try {
            trip.setDriverName(rs.getString("driver_name"));
            trip.setBusNumber(rs.getString("bus_number"));
            trip.setRouteName(rs.getString("route_name"));
        } catch (SQLException e) {
            // Columns don't exist, skip them
        }
        
        return trip;
    }
}
