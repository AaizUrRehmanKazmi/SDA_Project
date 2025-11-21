package bl;

import java.sql.Timestamp;
/**
 * Payment entity class
 */
public class Payment {
    
    private int paymentId;
    private int bookingId;
    private double amount;
    private String paymentMethod;
    private String transactionId;
    private String paymentStatus;
    private Timestamp paymentDate;
    
    public Payment() {
    }
    
    public Payment(int bookingId, double amount, String paymentMethod) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = generateTransactionId();
        this.paymentStatus = "PENDING";
    }
    
    // Business logic methods
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    public boolean isSuccessful() {
        return "COMPLETED".equals(paymentStatus);
    }
    
    public void processPayment() {
        this.paymentStatus = "COMPLETED";
        this.paymentDate = new Timestamp(System.currentTimeMillis());
    }
    
    public void failPayment() {
        this.paymentStatus = "FAILED";
    }
    
    public void refund() {
        if (isSuccessful()) {
            this.paymentStatus = "REFUNDED";
        }
    }
    
    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }
    
    @Override
    public String toString() {
        return String.format("Payment[ID=%d, Amount=%.2f, Method=%s, Status=%s]",
                           paymentId, amount, paymentMethod, paymentStatus);
    }
}
