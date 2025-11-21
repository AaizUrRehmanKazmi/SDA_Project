package dal;

import bl.Bus;
import bl.Route;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BusDAO - Data Access Object for Bus operations
 */
public class BusDAO {
    
    private DatabaseConnection dbConnection;
    
    public BusDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // CREATE
    public boolean addBus(Bus bus) throws SQLException {
        String query = "INSERT INTO Buses (bus_number, bus_type, capacity, registration_number, status, driver_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, bus.getBusNumber());
            stmt.setString(2, bus.getBusType());
            stmt.setInt(3, bus.getCapacity());
            stmt.setString(4, bus.getRegistrationNumber());
            stmt.setString(5, bus.getStatus());
            stmt.setObject(6, bus.getDriverId() > 0 ? bus.getDriverId() : null);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    bus.setBusId(keys.getInt(1));
                }
                return true;
            }
            return false;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ
    public List<Bus> getAllBuses() throws SQLException {
        List<Bus> buses = new ArrayList<>();
        String query = "SELECT b.*, u.full_name as driver_name " +
                      "FROM Buses b LEFT JOIN Users u ON b.driver_id = u.user_id " +
                      "ORDER BY b.bus_number";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                buses.add(mapResultSetToBus(rs));
            }
            
            return buses;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    public Bus getBusById(int busId) throws SQLException {
        String query = "SELECT b.*, u.full_name as driver_name " +
                      "FROM Buses b LEFT JOIN Users u ON b.driver_id = u.user_id " +
                      "WHERE b.bus_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, busId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBus(rs);
            }
            return null;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    public List<Bus> getAvailableBuses() throws SQLException {
        String query = "SELECT b.*, u.full_name as driver_name " +
                      "FROM Buses b LEFT JOIN Users u ON b.driver_id = u.user_id " +
                      "WHERE b.status = 'AVAILABLE'";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            List<Bus> buses = new ArrayList<>();
            while (rs.next()) {
                buses.add(mapResultSetToBus(rs));
            }
            return buses;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE
    public boolean updateBus(Bus bus) throws SQLException {
        String query = "UPDATE Buses SET bus_type = ?, capacity = ?, status = ?, driver_id = ? " +
                      "WHERE bus_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            
            stmt.setString(1, bus.getBusType());
            stmt.setInt(2, bus.getCapacity());
            stmt.setString(3, bus.getStatus());
            stmt.setObject(4, bus.getDriverId() > 0 ? bus.getDriverId() : null);
            stmt.setInt(5, bus.getBusId());
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // DELETE
    public boolean deleteBus(int busId) throws SQLException {
        String query = "DELETE FROM Buses WHERE bus_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, busId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    private Bus mapResultSetToBus(ResultSet rs) throws SQLException {
        Bus bus = new Bus();
        bus.setBusId(rs.getInt("bus_id"));
        bus.setBusNumber(rs.getString("bus_number"));
        bus.setBusType(rs.getString("bus_type"));
        bus.setCapacity(rs.getInt("capacity"));
        bus.setRegistrationNumber(rs.getString("registration_number"));
        bus.setStatus(rs.getString("status"));
        bus.setDriverId(rs.getInt("driver_id"));
        bus.setDriverName(rs.getString("driver_name"));
        bus.setCreatedAt(rs.getTimestamp("created_at"));
        bus.setUpdatedAt(rs.getTimestamp("updated_at"));
        return bus;
    }
}

