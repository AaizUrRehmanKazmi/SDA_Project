package dal;

import bl.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingDAO - Data Access Object for Booking operations
 */
public class BookingDAO {
    
    private DatabaseConnection dbConnection;
    
    public BookingDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // CREATE - Make a booking
    public boolean createBooking(Booking booking) throws SQLException {
        String query = "INSERT INTO Bookings (passenger_id, assignment_id, booking_date, seat_number, " +
                      "pickup_stop, dropoff_stop, fare, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            
            // Check if seat is already booked
            if (isSeatBooked(conn, booking.getAssignmentId(), booking.getBookingDate(), 
                           booking.getSeatNumber())) {
                throw new SQLException("Seat " + booking.getSeatNumber() + " is already booked");
            }
            
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, booking.getPassengerId());
            stmt.setInt(2, booking.getAssignmentId());
            stmt.setDate(3, booking.getBookingDate());
            stmt.setInt(4, booking.getSeatNumber());
            stmt.setString(5, booking.getPickupStop());
            stmt.setString(6, booking.getDropoffStop());
            stmt.setDouble(7, booking.getFare());
            stmt.setString(8, "CONFIRMED");
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    booking.setBookingId(keys.getInt(1));
                }
                return true;
            }
            return false;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // Helper method to check if seat is already booked
    private boolean isSeatBooked(Connection conn, int assignmentId, Date bookingDate, 
                                int seatNumber) throws SQLException {
        String query = "SELECT COUNT(*) FROM Bookings " +
                      "WHERE assignment_id = ? AND booking_date = ? AND seat_number = ? " +
                      "AND status IN ('CONFIRMED', 'COMPLETED')";
        
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, assignmentId);
        stmt.setDate(2, bookingDate);
        stmt.setInt(3, seatNumber);
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }
    
    // Get booked seats for a specific assignment and date
    public List<Integer> getBookedSeats(int assignmentId, Date bookingDate) throws SQLException {
        List<Integer> bookedSeats = new ArrayList<>();
        String query = "SELECT seat_number FROM Bookings " +
                      "WHERE assignment_id = ? AND booking_date = ? " +
                      "AND status IN ('CONFIRMED', 'COMPLETED')";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, assignmentId);
            stmt.setDate(2, bookingDate);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookedSeats.add(rs.getInt("seat_number"));
            }
            
            return bookedSeats;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get bookings by passenger
    public List<Booking> getBookingsByPassenger(int passengerId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.*, ra.departure_time, r.route_name, bus.bus_number " +
                      "FROM Bookings b " +
                      "JOIN RouteAssignments ra ON b.assignment_id = ra.assignment_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "JOIN Buses bus ON ra.bus_id = bus.bus_id " +
                      "WHERE b.passenger_id = ? " +
                      "ORDER BY b.booking_date DESC, ra.departure_time DESC";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, passengerId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
            return bookings;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get all bookings
    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.*, u.full_name as passenger_name, ra.departure_time, " +
                      "r.route_name, bus.bus_number " +
                      "FROM Bookings b " +
                      "JOIN Users u ON b.passenger_id = u.user_id " +
                      "JOIN RouteAssignments ra ON b.assignment_id = ra.assignment_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "JOIN Buses bus ON ra.bus_id = bus.bus_id " +
                      "ORDER BY b.booking_date DESC";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
            
            return bookings;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get booking by ID
    public Booking getBookingById(int bookingId) throws SQLException {
        String query = "SELECT b.*, u.full_name as passenger_name, ra.departure_time, " +
                      "r.route_name, bus.bus_number " +
                      "FROM Bookings b " +
                      "JOIN Users u ON b.passenger_id = u.user_id " +
                      "JOIN RouteAssignments ra ON b.assignment_id = ra.assignment_id " +
                      "JOIN Routes r ON ra.route_id = r.route_id " +
                      "JOIN Buses bus ON ra.bus_id = bus.bus_id " +
                      "WHERE b.booking_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, bookingId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBooking(rs);
            }
            return null;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Cancel booking
    public boolean cancelBooking(int bookingId) throws SQLException {
        String query = "UPDATE Bookings SET status = 'CANCELLED' WHERE booking_id = ? AND status = 'CONFIRMED'";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, bookingId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Mark booking as completed
    public boolean completeBooking(int bookingId) throws SQLException {
        String query = "UPDATE Bookings SET status = 'COMPLETED' WHERE booking_id = ? AND status = 'CONFIRMED'";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, bookingId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // Get booking statistics for a passenger
    public int getTotalBookingsCount(int passengerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Bookings WHERE passenger_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, passengerId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // Get total amount spent by passenger
    public double getTotalAmountSpent(int passengerId) throws SQLException {
        String query = "SELECT COALESCE(SUM(fare), 0) FROM Bookings " +
                      "WHERE passenger_id = ? AND status IN ('CONFIRMED', 'COMPLETED')";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, passengerId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setPassengerId(rs.getInt("passenger_id"));
        
        // Check if passenger_name column exists
        try {
            booking.setPassengerName(rs.getString("passenger_name"));
        } catch (SQLException e) {
            // Column doesn't exist, skip it
        }
        
        booking.setAssignmentId(rs.getInt("assignment_id"));
        booking.setBookingDate(rs.getDate("booking_date"));
        booking.setSeatNumber(rs.getInt("seat_number"));
        booking.setPickupStop(rs.getString("pickup_stop"));
        booking.setDropoffStop(rs.getString("dropoff_stop"));
        booking.setFare(rs.getDouble("fare"));
        booking.setStatus(rs.getString("status"));
        booking.setCreatedAt(rs.getTimestamp("created_at"));
        booking.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Set additional display fields if available
        try {
            booking.setBusNumber(rs.getString("bus_number"));
            booking.setRouteName(rs.getString("route_name"));
            booking.setDepartureTime(rs.getString("departure_time"));
        } catch (SQLException e) {
            // Columns don't exist, skip them
        }
        
        return booking;
    }
}
