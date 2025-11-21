package dal;


import util.Constants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseConnection implements Singleton Pattern for database connectivity
 * Provides connection pooling for better performance
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private List<Connection> connectionPool;
    private List<Connection> usedConnections;
    private static final int INITIAL_POOL_SIZE = 10;
    
    // Private constructor for Singleton pattern
    private DatabaseConnection() {
        connectionPool = new ArrayList<>();
        usedConnections = new ArrayList<>();
        
        try {
            // Load MySQL JDBC Driver
            Class.forName(Constants.DB_DRIVER);
            
            // Initialize connection pool
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                connectionPool.add(createConnection());
            }
            
            System.out.println("Database connection pool initialized with " + 
                             INITIAL_POOL_SIZE + " connections");
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get singleton instance of DatabaseConnection
     * Thread-safe implementation
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Creates a new database connection
     */
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
            Constants.DB_URL,
            Constants.DB_USER,
            Constants.DB_PASSWORD
        );
    }
    
    /**
     * Get a connection from the pool
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < Constants.MAX_POOL_SIZE) {
                connectionPool.add(createConnection());
            } else {
                throw new SQLException("Maximum pool size reached, no available connections!");
            }
        }
        
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        
        // Check if connection is still valid
        if (!connection.isValid(3)) {
            connection = createConnection();
        }
        
        usedConnections.add(connection);
        return connection;
    }
    
    /**
     * Return a connection to the pool
     */
    public synchronized boolean releaseConnection(Connection connection) {
        if (connection == null) {
            return false;
        }
        
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }
    
    /**
     * Test database connectivity
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(3);
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close all connections in the pool
     */
    public synchronized void closeAllConnections() {
        try {
            for (Connection conn : connectionPool) {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            }
            
            for (Connection conn : usedConnections) {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            }
            
            connectionPool.clear();
            usedConnections.clear();
            
            System.out.println("All database connections closed");
            
        } catch (SQLException e) {
            System.err.println("Error closing connections: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get current pool statistics
     */
    public String getPoolStats() {
        return String.format("Pool Stats - Available: %d, In Use: %d, Total: %d",
                           connectionPool.size(),
                           usedConnections.size(),
                           connectionPool.size() + usedConnections.size());
    }
}

