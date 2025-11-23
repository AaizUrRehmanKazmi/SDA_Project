package dal;

import bl.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PaymentDAO - Data Access Object for Payment operations
 */
public class PaymentDAO {
    
    private DatabaseConnection dbConnection;
    
    public PaymentDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // CREATE - Process payment
    public boolean processPayment(Payment payment) throws SQLException {
        String query = "INSERT INTO Payments (booking_id, amount, payment_method, " +
                      "transaction_id, payment_status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, payment.getBookingId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getTransactionId());
            stmt.setString(5, payment.getPaymentStatus());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    payment.setPaymentId(keys.getInt(1));
                }
                return true;
            }
            return false;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get payment by ID
    public Payment getPaymentById(int paymentId) throws SQLException {
        String query = "SELECT * FROM Payments WHERE payment_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, paymentId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPayment(rs);
            }
            return null;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get payment by booking ID
    public Payment getPaymentByBookingId(int bookingId) throws SQLException {
        String query = "SELECT * FROM Payments WHERE booking_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, bookingId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPayment(rs);
            }
            return null;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get all payments
    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM Payments ORDER BY payment_date DESC";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            
            return payments;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // READ - Get payments by status
    public List<Payment> getPaymentsByStatus(String status) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM Payments WHERE payment_status = ? ORDER BY payment_date DESC";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            
            return payments;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // UPDATE - Update payment status
    public boolean updatePaymentStatus(int paymentId, String status) throws SQLException {
        String query = "UPDATE Payments SET payment_status = ? WHERE payment_id = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            stmt.setInt(2, paymentId);
            
            return stmt.executeUpdate() > 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // Process refund
    public boolean processRefund(int paymentId) throws SQLException {
        return updatePaymentStatus(paymentId, "REFUNDED");
    }
    
    // Get total revenue
    public double getTotalRevenue() throws SQLException {
        String query = "SELECT COALESCE(SUM(amount), 0) FROM Payments " +
                      "WHERE payment_status = 'COMPLETED'";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
 // Get today's revenue
    public double getDailyRevenue() throws SQLException {
        String query = "SELECT COALESCE(SUM(amount), 0) FROM Payments " +
                "WHERE payment_status = 'COMPLETED' " +
                "AND DATE(payment_date) = CURDATE()";

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            return rs.next() ? rs.getDouble(1) : 0.0;
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }

    // Get revenue for current month
    public double getMonthlyRevenue() throws SQLException {
        String query = "SELECT COALESCE(SUM(amount), 0) FROM Payments " +
                "WHERE payment_status = 'COMPLETED' " +
                "AND MONTH(payment_date) = MONTH(CURDATE()) " +
                "AND YEAR(payment_date) = YEAR(CURDATE())";

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            return rs.next() ? rs.getDouble(1) : 0.0;
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }

    // Get revenue for current year
    public double getYearlyRevenue() throws SQLException {
        String query = "SELECT COALESCE(SUM(amount), 0) FROM Payments " +
                "WHERE payment_status = 'COMPLETED' " +
                "AND YEAR(payment_date) = YEAR(CURDATE())";

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            return rs.next() ? rs.getDouble(1) : 0.0;
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }

    // Get revenue grouped by route
    public List<Object[]> getRevenueByRoute() throws SQLException {
        List<Object[]> list = new ArrayList<>();

        String query = "SELECT r.route_name, COALESCE(SUM(p.amount), 0) " +
                "FROM Payments p " +
                "JOIN Bookings b ON p.booking_id = b.booking_id " +
                "JOIN Routes r ON b.route_id = r.route_id " +
                "WHERE p.payment_status = 'COMPLETED' " +
                "GROUP BY r.route_name " +
                "ORDER BY SUM(p.amount) DESC";

        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                list.add(new Object[]{rs.getString(1), rs.getDouble(2)});
            }
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }

        return list;
    }

    
    // Get revenue by date range
    public double getRevenueByDateRange(Date startDate, Date endDate) throws SQLException {
        String query = "SELECT COALESCE(SUM(amount), 0) FROM Payments " +
                      "WHERE payment_status = 'COMPLETED' " +
                      "AND payment_date BETWEEN ? AND ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    // Get payment statistics
    public int getPaymentCountByStatus(String status) throws SQLException {
        String query = "SELECT COUNT(*) FROM Payments WHERE payment_status = ?";
        Connection conn = null;
        
        try {
            conn = dbConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, status);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } finally {
            if (conn != null) dbConnection.releaseConnection(conn);
        }
    }
    
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setBookingId(rs.getInt("booking_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setTransactionId(rs.getString("transaction_id"));
        payment.setPaymentStatus(rs.getString("payment_status"));
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        return payment;
    }
}
