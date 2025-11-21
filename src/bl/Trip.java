package bl;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Trip entity class - represents active trips
 */
public class Trip {
    
    private int tripId;
    private int assignmentId;
    private int driverId;
    private String driverName;
    private Date tripDate;
    private Timestamp startTime;
    private Timestamp endTime;
    private double currentLocationLat;
    private double currentLocationLng;
    private String currentStop;
    private String status;
    private int passengersCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional display fields
    private String busNumber;
    private String routeName;
    
    public Trip() {
    }
    
    public Trip(int assignmentId, int driverId, Date tripDate) {
        this.assignmentId = assignmentId;
        this.driverId = driverId;
        this.tripDate = tripDate;
        this.status = "SCHEDULED";
        this.passengersCount = 0;
    }
    
    // Business logic methods
    public boolean canStart() {
        return "SCHEDULED".equals(status);
    }
    
    public void startTrip() {
        if (canStart()) {
            this.status = "IN_PROGRESS";
            this.startTime = new Timestamp(System.currentTimeMillis());
        }
    }
    
    public boolean isInProgress() {
        return "IN_PROGRESS".equals(status);
    }
    
    public void updateLocation(double latitude, double longitude, String stop) {
        if (isInProgress()) {
            this.currentLocationLat = latitude;
            this.currentLocationLng = longitude;
            this.currentStop = stop;
        }
    }
    
    public void endTrip() {
        if (isInProgress()) {
            this.status = "COMPLETED";
            this.endTime = new Timestamp(System.currentTimeMillis());
        }
    }
    
    public void cancelTrip() {
        if (!isCompleted()) {
            this.status = "CANCELLED";
        }
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public long getTripDurationMinutes() {
        if (startTime != null && endTime != null) {
            long diff = endTime.getTime() - startTime.getTime();
            return diff / (60 * 1000);
        }
        return 0;
    }
    
    // Getters and Setters
    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }
    
    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }
    
    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }
    
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    
    public Date getTripDate() { return tripDate; }
    public void setTripDate(Date tripDate) { this.tripDate = tripDate; }
    
    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }
    
    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }
    
    public double getCurrentLocationLat() { return currentLocationLat; }
    public void setCurrentLocationLat(double currentLocationLat) { 
        this.currentLocationLat = currentLocationLat; 
    }
    
    public double getCurrentLocationLng() { return currentLocationLng; }
    public void setCurrentLocationLng(double currentLocationLng) { 
        this.currentLocationLng = currentLocationLng; 
    }
    
    public String getCurrentStop() { return currentStop; }
    public void setCurrentStop(String currentStop) { this.currentStop = currentStop; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getPassengersCount() { return passengersCount; }
    public void setPassengersCount(int passengersCount) { this.passengersCount = passengersCount; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    
    @Override
    public String toString() {
        return String.format("Trip[ID=%d, Date=%s, Status=%s, Passengers=%d]",
                           tripId, tripDate, status, passengersCount);
    }
}
