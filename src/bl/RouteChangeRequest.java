package bl;

import java.sql.Timestamp;

/**
 * RouteChangeRequest entity class - handles route change requests from passengers and drivers
 */
public class RouteChangeRequest {
    
    private int requestId;
    private int userId;
    private String userName;
    private String userRole; // PASSENGER or DRIVER
    private int bookingId; // For passengers (can be null for drivers)
    private int tripId; // For drivers (can be null for passengers)
    private int currentRouteId;
    private int requestedRouteId;
    private String currentRouteName;
    private String requestedRouteName;
    private String reason;
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
    private String adminNotes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp approvedAt;
    
    // Default constructor
    public RouteChangeRequest() {
    }
    
    // Constructor for Passenger route change request
    public RouteChangeRequest(int passengerId, int bookingId, int currentRouteId, 
                             int requestedRouteId, String reason) {
        this.userId = passengerId;
        this.userRole = "PASSENGER";
        this.bookingId = bookingId;
        this.tripId = 0; // Not used for passenger
        this.currentRouteId = currentRouteId;
        this.requestedRouteId = requestedRouteId;
        this.reason = reason;
        this.status = "PENDING";
    }
    
    // Constructor for Driver route change request (uses tripId instead of bookingId)
    public RouteChangeRequest(int driverId, int tripId, int currentRouteId, 
                             int requestedRouteId, String reason, boolean isDriver) {
        this.userId = driverId;
        this.userRole = "DRIVER";
        this.bookingId = 0; // Not used for driver
        this.tripId = tripId;
        this.currentRouteId = currentRouteId;
        this.requestedRouteId = requestedRouteId;
        this.reason = reason;
        this.status = "PENDING";
    }
    
    // Alternative: Generic constructor for both types
    public RouteChangeRequest(int userId, String userRole, int relatedId, 
                             int currentRouteId, int requestedRouteId, String reason) {
        this.userId = userId;
        this.userRole = userRole;
        this.currentRouteId = currentRouteId;
        this.requestedRouteId = requestedRouteId;
        this.reason = reason;
        this.status = "PENDING";
        
        if ("PASSENGER".equals(userRole)) {
            this.bookingId = relatedId;
            this.tripId = 0;
        } else if ("DRIVER".equals(userRole)) {
            this.bookingId = 0;
            this.tripId = relatedId;
        }
    }
    
    // Business logic methods
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    
    public void approve() {
        if (isPending()) {
            this.status = "APPROVED";
            this.approvedAt = new Timestamp(System.currentTimeMillis());
        }
    }
    
    public void reject(String notes) {
        if (isPending()) {
            this.status = "REJECTED";
            this.adminNotes = notes;
        }
    }
    
    public void cancel() {
        if (isPending()) {
            this.status = "CANCELLED";
        }
    }
    
    public boolean isApproved() {
        return "APPROVED".equals(status);
    }
    
    public boolean isRejected() {
        return "REJECTED".equals(status);
    }
    
    public boolean isPassengerRequest() {
        return "PASSENGER".equals(userRole);
    }
    
    public boolean isDriverRequest() {
        return "DRIVER".equals(userRole);
    }
    
    // Getters and Setters
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    
    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }
    
    public int getCurrentRouteId() { return currentRouteId; }
    public void setCurrentRouteId(int currentRouteId) { this.currentRouteId = currentRouteId; }
    
    public int getRequestedRouteId() { return requestedRouteId; }
    public void setRequestedRouteId(int requestedRouteId) { this.requestedRouteId = requestedRouteId; }
    
    public String getCurrentRouteName() { return currentRouteName; }
    public void setCurrentRouteName(String currentRouteName) { this.currentRouteName = currentRouteName; }
    
    public String getRequestedRouteName() { return requestedRouteName; }
    public void setRequestedRouteName(String requestedRouteName) { this.requestedRouteName = requestedRouteName; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public Timestamp getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Timestamp approvedAt) { this.approvedAt = approvedAt; }
    
    @Override
    public String toString() {
        return String.format("RouteChangeRequest[ID=%d, User=%s, Status=%s, From=%s, To=%s]",
                           requestId, userName, status, currentRouteName, requestedRouteName);
    }
}