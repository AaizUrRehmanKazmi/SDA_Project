package bl;

import java.sql.Timestamp;

/**
 * Bus entity class - demonstrates Encapsulation
 */
public class Bus {
    
    private int busId;
    private String busNumber;
    private String busType;
    private int capacity;
    private String registrationNumber;
    private String status;
    private int driverId;
    private String driverName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public Bus() {
    }
    
    public Bus(String busNumber, String busType, int capacity, 
               String registrationNumber) {
        this.busNumber = busNumber;
        this.busType = busType;
        this.capacity = capacity;
        this.registrationNumber = registrationNumber;
        this.status = "AVAILABLE";
    }
    
    // Business logic methods
    public boolean isAvailable() {
        return "AVAILABLE".equals(status);
    }
    
    public boolean needsMaintenance() {
        return "MAINTENANCE".equals(status);
    }
    
    public boolean hasDriver() {
        return driverId > 0;
    }
    
    // Getters and Setters
    public int getBusId() { return busId; }
    public void setBusId(int busId) { this.busId = busId; }
    
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    
    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { 
        this.registrationNumber = registrationNumber; 
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }
    
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return String.format("Bus[ID=%d, Number=%s, Type=%s, Capacity=%d, Status=%s]",
                           busId, busNumber, busType, capacity, status);
    }
}
