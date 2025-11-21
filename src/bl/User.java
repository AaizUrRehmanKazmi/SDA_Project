package bl;

import java.sql.Timestamp;

/**
 * Abstract User class demonstrating Abstraction and Encapsulation
 * Base class for all user types in the system
 */
public abstract class User {
    
    // Encapsulated fields (private with getters/setters)
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Default constructor
    public User() {
    }
    
    // Parameterized constructor
    public User(String username, String password, String fullName, 
                String email, String phone, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = "ACTIVE";
    }
    
    // Abstract methods to be implemented by subclasses (Polymorphism)
    public abstract String getUserRole();
    public abstract void displayDashboard();
    
    // Common method for all users
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    // Getters and Setters (Encapsulation)
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return String.format("User[ID=%d, Username=%s, Name=%s, Role=%s, Status=%s]",
                           userId, username, fullName, role, status);
    }
}
