package bl;
import java.sql.Timestamp;
/**
 * Route entity class - demonstrates Encapsulation
 */
public class Route {
    
    private int routeId;
    private String routeName;
    private String origin;
    private String destination;
    private String stops; // Comma-separated
    private double distanceKm;
    private int estimatedDurationMinutes;
    private double fare;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public Route() {
    }
    
    public Route(String routeName, String origin, String destination, 
                 String stops, double distanceKm, int estimatedDurationMinutes, 
                 double fare) {
        this.routeName = routeName;
        this.origin = origin;
        this.destination = destination;
        this.stops = stops;
        this.distanceKm = distanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.fare = fare;
        this.status = "ACTIVE";
    }
    
    // Business logic methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public String[] getStopsList() {
        if (stops != null && !stops.isEmpty()) {
            return stops.split(",");
        }
        return new String[0];
    }
    
    public int getNumberOfStops() {
        return getStopsList().length;
    }
    
    public double calculateFarePerKm() {
        return distanceKm > 0 ? fare / distanceKm : 0;
    }
    
    // Getters and Setters
    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }
    
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    
    public String getStops() { return stops; }
    public void setStops(String stops) { this.stops = stops; }
    
    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    
    public int getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) { 
        this.estimatedDurationMinutes = estimatedDurationMinutes; 
    }
    
    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return String.format("Route[ID=%d, Name=%s, %s to %s, Fare=%.2f]",
                           routeId, routeName, origin, destination, fare);
    }
}
