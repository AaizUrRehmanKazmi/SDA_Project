package dal;

import bl.*;
import bl.Driver;
import util.Constants;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO - Data Access Object for User operations
 * Implements Information Expert and Creator GRASP patterns
 * Demonstrates Factory Pattern for user object creation
 */
public class UserDAO {
    
    private DatabaseConnection dbConnection;
    
    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Factory Method: Creates appropriate user object based on role
     * Demonstrates Factory Pattern
     */
    private User createUser(String role) {
        switch (role) {
            case Constants.ROLE_PASSENGER:
                return new Passenger();
            case Constants.ROLE_DRIVER:
                return new Driver();
            case Constants.ROLE_ADMIN:
                return new Admin();
            default:
                throw new IllegalArgumentException("Invalid user role: " + role);
        }
    }
    
    /**
     * Authenticate user - returns appropriate user object based on role
     */
    public User authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM Users WHERE username = ? AND password = ? AND status = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, Constants.STATUS_ACTIVE);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Factory pattern: create appropriate user type
                User user = createUser(rs.getString("role"));
                mapResultSetToUser(rs, user);
                return user;
            }
            
            return null; // Authentication failed
            
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Register new user (CREATE operation)
     */
    public boolean registerUser(User user) throws SQLException {
        String query = "INSERT INTO Users (username, password, full_name, email, phone, role, status) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getRole());
            stmt.setString(7, Constants.STATUS_ACTIVE);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    user.setUserId(keys.getInt(1));
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Get user by ID (READ operation)
     */
    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM Users WHERE user_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = createUser(rs.getString("role"));
                mapResultSetToUser(rs, user);
                return user;
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Get all users (READ operation)
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users ORDER BY created_at DESC";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                User user = createUser(rs.getString("role"));
                mapResultSetToUser(rs, user);
                users.add(user);
            }
            
            return users;
            
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(String role) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE role = ? ORDER BY full_name";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, role);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = createUser(rs.getString("role"));
                mapResultSetToUser(rs, user);
                users.add(user);
            }
            
            return users;
            
        } catch (SQLException e) {
            System.err.println("Error fetching users by role: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Update user (UPDATE operation)
     */
    public boolean updateUser(User user) throws SQLException {
        String query = "UPDATE Users SET full_name = ?, email = ?, phone = ?, status = ? " +
                      "WHERE user_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getStatus());
            stmt.setInt(5, user.getUserId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Delete user (DELETE operation)
     */
    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM Users WHERE user_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE username = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Check if email exists
     */
    public boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE email = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
    
    /**
     * Helper method to map ResultSet to User object
     */
    private void mapResultSetToUser(ResultSet rs, User user) throws SQLException {
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
    }
}
