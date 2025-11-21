package bl;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Booking entity class
 */
public class Booking {
    
    private int bookingId;
    private int passengerId;
    private String passengerName;
    private int assignmentId;
    private Date bookingDate;
    private int seatNumber;
    private String pickupStop;
    private String dropoffStop;
    private double fare;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields for display
    private String busNumber;
    private String routeName;
    private String departureTime;
    
    public Booking() {
    }
    
    public Booking(int passengerId, int assignmentId, Date bookingDate,
                   int seatNumber, String pickupStop, String dropoffStop, double fare) {
        this.passengerId = passengerId;
        this.assignmentId = assignmentId;
        this.bookingDate = bookingDate;
        this.seatNumber = seatNumber;
        this.pickupStop = pickupStop;
        this.dropoffStop = dropoffStop;
        this.fare = fare;
        this.status = "CONFIRMED";
    }
    
    // Business logic methods
    public boolean isActive() {
        return "CONFIRMED".equals(status);
    }
    
    public boolean canBeCancelled() {
        return "CONFIRMED".equals(status);
    }
    
    public void cancel() {
        this.status = "CANCELLED";
    }
    
    public void complete() {
        this.status = "COMPLETED";
    }
    
    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    
    public int getPassengerId() { return passengerId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }
    
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    
    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }
    
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
    
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    
    public String getPickupStop() { return pickupStop; }
    public void setPickupStop(String pickupStop) { this.pickupStop = pickupStop; }
    
    public String getDropoffStop() { return dropoffStop; }
    public void setDropoffStop(String dropoffStop) { this.dropoffStop = dropoffStop; }
    
    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    
    @Override
    public String toString() {
        return String.format("Booking[ID=%d, Seat=%d, Date=%s, Status=%s]",
                           bookingId, seatNumber, bookingDate, status);
    }
}