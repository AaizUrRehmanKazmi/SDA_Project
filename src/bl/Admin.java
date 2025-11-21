package bl;

import util.Constants;

/**
 * Admin class extending User - demonstrates Inheritance
 */
public class Admin extends User {
    
    private String department;
    private String accessLevel;
    private int complaintsHandled;
    
    // Default constructor
    public Admin() {
        super();
        setRole(Constants.ROLE_ADMIN);
    }
    
    // Parameterized constructor
    public Admin(String username, String password, String fullName, 
                String email, String phone) {
        super(username, password, fullName, email, phone, Constants.ROLE_ADMIN);
        this.department = "Management";
        this.accessLevel = "FULL";
        this.complaintsHandled = 0;
    }
    
    // Implementing abstract methods (Polymorphism)
    @Override
    public String getUserRole() {
        return Constants.ROLE_ADMIN;
    }
    
    @Override
    public void displayDashboard() {
        System.out.println("Displaying Admin Dashboard for: " + getFullName());
    }
    
    // Admin-specific methods
    public boolean hasFullAccess() {
        return accessLevel.equals("FULL") && getStatus().equals(Constants.STATUS_ACTIVE);
    }
    
    public void handleComplaint() {
        this.complaintsHandled++;
    }
    
    public boolean canManageUsers() {
        return hasFullAccess();
    }
    
    public boolean canGenerateReports() {
        return hasFullAccess();
    }
    
    public boolean canManageRoutes() {
        return hasFullAccess();
    }
    
    // Getters and Setters
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getAccessLevel() {
        return accessLevel;
    }
    
    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
    
    public int getComplaintsHandled() {
        return complaintsHandled;
    }
    
    public void setComplaintsHandled(int complaintsHandled) {
        this.complaintsHandled = complaintsHandled;
    }
    
    @Override
    public String toString() {
        return String.format("Admin[ID=%d, Name=%s, Department=%s, Access=%s, Complaints=%d]",
                           getUserId(), getFullName(), department, 
                           accessLevel, complaintsHandled);
    }
}