package bl;

import util.Constants;

/**
 * Passenger class extending User - demonstrates Inheritance
 */
public class Passenger extends User {
    
    private int totalBookings;
    private double totalAmountSpent;
    
    // Default constructor
    public Passenger() {
        super();
        setRole(Constants.ROLE_PASSENGER);
    }
    
    // Parameterized constructor
    public Passenger(String username, String password, String fullName, 
                    String email, String phone) {
        super(username, password, fullName, email, phone, Constants.ROLE_PASSENGER);
        this.totalBookings = 0;
        this.totalAmountSpent = 0.0;
    }
    
    // Implementing abstract methods (Polymorphism)
    @Override
    public String getUserRole() {
        return Constants.ROLE_PASSENGER;
    }
    
    @Override
    public void displayDashboard() {
        System.out.println("Displaying Passenger Dashboard for: " + getFullName());
    }
    
    // Passenger-specific methods
    public boolean canBookSeat() {
        return getStatus().equals(Constants.STATUS_ACTIVE);
    }
    
    public void incrementBookings() {
        this.totalBookings++;
    }
    
    public void addToTotalSpent(double amount) {
        this.totalAmountSpent += amount;
    }
    
    // Getters and Setters
    public int getTotalBookings() {
        return totalBookings;
    }
    
    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }
    
    public double getTotalAmountSpent() {
        return totalAmountSpent;
    }
    
    public void setTotalAmountSpent(double totalAmountSpent) {
        this.totalAmountSpent = totalAmountSpent;
    }
    
    @Override
    public String toString() {
        return String.format("Passenger[ID=%d, Name=%s, Bookings=%d, Spent=%.2f]",
                           getUserId(), getFullName(), totalBookings, totalAmountSpent);
    }
}
