package bl;

import util.Constants;

/**
 * Driver class extending User - demonstrates Inheritance
 */
public class Driver extends User {
    
    private String licenseNumber;
    private int yearsOfExperience;
    private int totalTripsCompleted;
    private double averageRating;
    private int assignedBusId;
    
    // Default constructor
    public Driver() {
        super();
        setRole(Constants.ROLE_DRIVER);
    }
    
    // Parameterized constructor
    public Driver(String username, String password, String fullName, 
                 String email, String phone) {
        super(username, password, fullName, email, phone, Constants.ROLE_DRIVER);
        this.totalTripsCompleted = 0;
        this.averageRating = 5.0;
    }
    
    // Implementing abstract methods (Polymorphism)
    @Override
    public String getUserRole() {
        return Constants.ROLE_DRIVER;
    }
    
    @Override
    public void displayDashboard() {
        System.out.println("Displaying Driver Dashboard for: " + getFullName());
    }
    
    // Driver-specific methods
    public boolean canStartTrip() {
        return getStatus().equals(Constants.STATUS_ACTIVE) && assignedBusId > 0;
    }
    
    public void completeTrip() {
        this.totalTripsCompleted++;
    }
    
    public void updateRating(double newRating) {
        if (totalTripsCompleted > 0) {
            this.averageRating = ((averageRating * (totalTripsCompleted - 1)) + newRating) 
                               / totalTripsCompleted;
        } else {
            this.averageRating = newRating;
        }
    }
    
    // Getters and Setters
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public int getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    
    public int getTotalTripsCompleted() {
        return totalTripsCompleted;
    }
    
    public void setTotalTripsCompleted(int totalTripsCompleted) {
        this.totalTripsCompleted = totalTripsCompleted;
    }
    
    public double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
    
    public int getAssignedBusId() {
        return assignedBusId;
    }
    
    public void setAssignedBusId(int assignedBusId) {
        this.assignedBusId = assignedBusId;
    }
    
    @Override
    public String toString() {
        return String.format("Driver[ID=%d, Name=%s, License=%s, Trips=%d, Rating=%.2f]",
                           getUserId(), getFullName(), licenseNumber, 
                           totalTripsCompleted, averageRating);
    }
}
