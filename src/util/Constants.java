package util;

public class Constants {
    
    // Database Configuration
    public static final String DB_URL = "jdbc:mysql://localhost:3306/uzair_transport";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "SQL@0205"; 
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Connection Pool Settings
    public static final int MAX_POOL_SIZE = 20;
    public static final int MIN_POOL_SIZE = 5;
    
    // User Roles
    public static final String ROLE_PASSENGER = "PASSENGER";
    public static final String ROLE_DRIVER = "DRIVER";
    public static final String ROLE_ADMIN = "ADMIN";
    
    // User Status
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    
    // Bus Status
    public static final String BUS_AVAILABLE = "AVAILABLE";
    public static final String BUS_IN_SERVICE = "IN_SERVICE";
    public static final String BUS_MAINTENANCE = "MAINTENANCE";
    public static final String BUS_RETIRED = "RETIRED";
    
    // Booking Status
    public static final String BOOKING_CONFIRMED = "CONFIRMED";
    public static final String BOOKING_CANCELLED = "CANCELLED";
    public static final String BOOKING_COMPLETED = "COMPLETED";
    public static final String BOOKING_NO_SHOW = "NO_SHOW";
    
    // Payment Status
    public static final String PAYMENT_PENDING = "PENDING";
    public static final String PAYMENT_COMPLETED = "COMPLETED";
    public static final String PAYMENT_FAILED = "FAILED";
    public static final String PAYMENT_REFUNDED = "REFUNDED";
    
    // Trip Status
    public static final String TRIP_SCHEDULED = "SCHEDULED";
    public static final String TRIP_IN_PROGRESS = "IN_PROGRESS";
    public static final String TRIP_COMPLETED = "COMPLETED";
    public static final String TRIP_CANCELLED = "CANCELLED";
    
    // Complaint Status
    public static final String COMPLAINT_SUBMITTED = "SUBMITTED";
    public static final String COMPLAINT_UNDER_REVIEW = "UNDER_REVIEW";
    public static final String COMPLAINT_RESOLVED = "RESOLVED";
    public static final String COMPLAINT_CLOSED = "CLOSED";
    
    // UI Settings
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;
    public static final String APP_TITLE = "Uzair Transport System";
    
    // Validation Patterns
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String PHONE_PATTERN = "^03[0-9]{9}$";
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{4,20}$";
    
    // Error Messages
    public static final String ERROR_DB_CONNECTION = "Database connection failed. Please check your connection settings.";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid username or password.";
    public static final String ERROR_REQUIRED_FIELDS = "Please fill all required fields.";
    public static final String ERROR_INVALID_EMAIL = "Please enter a valid email address.";
    public static final String ERROR_INVALID_PHONE = "Phone number must be in format 03XXXXXXXXX.";
    public static final String ERROR_USERNAME_EXISTS = "Username already exists.";
    public static final String ERROR_EMAIL_EXISTS = "Email already exists.";
    
    // Success Messages
    public static final String SUCCESS_REGISTRATION = "Registration successful! You can now login.";
    public static final String SUCCESS_BOOKING = "Booking confirmed successfully!";
    public static final String SUCCESS_PAYMENT = "Payment processed successfully!";
    public static final String SUCCESS_UPDATE = "Updated successfully!";
    public static final String SUCCESS_DELETE = "Deleted successfully!";
    
    public static final String ROUTE_REQUEST_PENDING = "PENDING";
    public static final String ROUTE_REQUEST_APPROVED = "APPROVED";
    public static final String ROUTE_REQUEST_REJECTED = "REJECTED";
    public static final String ROUTE_REQUEST_CANCELLED = "CANCELLED";
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
}