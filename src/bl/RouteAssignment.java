package bl;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * RouteAssignment entity class
 * Represents bus-route schedule assignments
 */
public class RouteAssignment {
    
    private int assignmentId;
    private int busId;
    private int routeId;
    private Time departureTime;
    private Time arrivalTime;
    private String daysOfWeek;  // e.g., "MON,TUE,WED,THU,FRI"
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional display fields
    private String busNumber;
    private String routeName;
    private String routeOrigin;
    private String routeDestination;
    private double fare;
    
    public RouteAssignment() {
    }
    
    public RouteAssignment(int busId, int routeId, Time departureTime, 
                          Time arrivalTime, String daysOfWeek) {
        this.busId = busId;
        this.routeId = routeId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.daysOfWeek = daysOfWeek;
        this.status = "ACTIVE";
    }
    
    // Business logic methods
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    public boolean operatesOnDay(String day) {
        if (daysOfWeek == null) {
            return false;
        }
        return daysOfWeek.contains(day.toUpperCase());
    }
    
    public String[] getDaysArray() {
        if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
            return daysOfWeek.split(",");
        }
        return new String[0];
    }
    
    public int getDaysCount() {
        return getDaysArray().length;
    }
    
    public boolean isWeekdaySchedule() {
        String[] days = getDaysArray();
        return days.length == 5 && 
               operatesOnDay("MON") && operatesOnDay("TUE") && 
               operatesOnDay("WED") && operatesOnDay("THU") && 
               operatesOnDay("FRI");
    }
    
    public boolean isWeekendSchedule() {
        return operatesOnDay("SAT") || operatesOnDay("SUN");
    }
    
    public long getTripDurationMinutes() {
        if (departureTime != null && arrivalTime != null) {
            long diff = arrivalTime.getTime() - departureTime.getTime();
            return diff / (60 * 1000);
        }
        return 0;
    }
    
    // Getters and Setters
    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }
    
    public int getBusId() { return busId; }
    public void setBusId(int busId) { this.busId = busId; }
    
    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }
    
    public Time getDepartureTime() { return departureTime; }
    public void setDepartureTime(Time departureTime) { this.departureTime = departureTime; }
    
    public Time getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Time arrivalTime) { this.arrivalTime = arrivalTime; }
    
    public String getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(String daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    
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
    
    public String getRouteOrigin() { return routeOrigin; }
    public void setRouteOrigin(String routeOrigin) { this.routeOrigin = routeOrigin; }
    
    public String getRouteDestination() { return routeDestination; }
    public void setRouteDestination(String routeDestination) { 
        this.routeDestination = routeDestination; 
    }
    
    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }
    
    @Override
    public String toString() {
        return String.format("RouteAssignment[ID=%d, Bus=%d, Route=%d, Time=%s-%s, Days=%s]",
                           assignmentId, busId, routeId, departureTime, arrivalTime, daysOfWeek);
    }
}
