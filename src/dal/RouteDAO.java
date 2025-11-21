package dal;

import bl.Bus;
import bl.Route;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class RouteDAO {
    
    private DatabaseConnection dbConnection;
    
    public RouteDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // CREATE
    public boolean addRoute(Route route) throws SQLException {
        String query = "INSERT INTO Routes (route_name, origin, destination, stops, distance_km, " +
                      "estimated_duration_minutes, fare, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, route.getRouteName());
            stmt.setString(2, route.getOrigin());
            stmt.setString(3, route.getDestination());
            stmt.setString(4, route.getStops());
            stmt.setDouble(5, route.getDistanceKm());
            stmt.setInt(6, route.getEstimatedDurationMinutes());
            stmt.setDouble(7, route.getFare());
            stmt.setString(8, route.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    route.setRouteId(keys.getInt(1));
                }
                return true;
            }
            return false;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ
    public List<Route> getAllRoutes() throws SQLException {
        List<Route> routes = new ArrayList<>();
        String query = "SELECT * FROM Routes ORDER BY route_name";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                routes.add(mapResultSetToRoute(rs));
            }
            
            return routes;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    public Route getRouteById(int routeId) throws SQLException {
        String query = "SELECT * FROM Routes WHERE route_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, routeId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToRoute(rs);
            }
            return null;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    public List<Route> getActiveRoutes() throws SQLException {
        List<Route> routes = new ArrayList<>();
        String query = "SELECT * FROM Routes WHERE status = 'ACTIVE' ORDER BY route_name";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                routes.add(mapResultSetToRoute(rs));
            }
            
            return routes;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE
    public boolean updateRoute(Route route) throws SQLException {
        String query = "UPDATE Routes SET route_name = ?, origin = ?, destination = ?, stops = ?, " +
                      "distance_km = ?, estimated_duration_minutes = ?, fare = ?, status = ? " +
                      "WHERE route_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            
            stmt.setString(1, route.getRouteName());
            stmt.setString(2, route.getOrigin());
            stmt.setString(3, route.getDestination());
            stmt.setString(4, route.getStops());
            stmt.setDouble(5, route.getDistanceKm());
            stmt.setInt(6, route.getEstimatedDurationMinutes());
            stmt.setDouble(7, route.getFare());
            stmt.setString(8, route.getStatus());
            stmt.setInt(9, route.getRouteId());
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // DELETE
    public boolean deleteRoute(int routeId) throws SQLException {
        String query = "DELETE FROM Routes WHERE route_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, routeId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    private Route mapResultSetToRoute(ResultSet rs) throws SQLException {
        Route route = new Route();
        route.setRouteId(rs.getInt("route_id"));
        route.setRouteName(rs.getString("route_name"));
        route.setOrigin(rs.getString("origin"));
        route.setDestination(rs.getString("destination"));
        route.setStops(rs.getString("stops"));
        route.setDistanceKm(rs.getDouble("distance_km"));
        route.setEstimatedDurationMinutes(rs.getInt("estimated_duration_minutes"));
        route.setFare(rs.getDouble("fare"));
        route.setStatus(rs.getString("status"));
        route.setCreatedAt(rs.getTimestamp("created_at"));
        route.setUpdatedAt(rs.getTimestamp("updated_at"));
        return route;
    }
}
