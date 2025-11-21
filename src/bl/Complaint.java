package bl;

import java.sql.Timestamp;

/**
 * Complaint entity class for feedback and complaint management
 */
public class Complaint {
    
    private int complaintId;
    private int userId;
    private String userName;
    private String complaintType;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private String adminResponse;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp resolvedAt;
    
    public Complaint() {
    }
    
    public Complaint(int userId, String complaintType, String subject, 
                    String description, String priority) {
        this.userId = userId;
        this.complaintType = complaintType;
        this.subject = subject;
        this.description = description;
        this.priority = priority;
        this.status = "SUBMITTED";
    }
    
    // Business logic methods
    public boolean isOpen() {
        return "SUBMITTED".equals(status) || "UNDER_REVIEW".equals(status);
    }
    
    public boolean canBeResolved() {
        return isOpen();
    }
    
    public void markUnderReview() {
        if ("SUBMITTED".equals(status)) {
            this.status = "UNDER_REVIEW";
        }
    }
    
    public void resolve(String response) {
        if (canBeResolved()) {
            this.status = "RESOLVED";
            this.adminResponse = response;
            this.resolvedAt = new Timestamp(System.currentTimeMillis());
        }
    }
    
    public void close() {
        if ("RESOLVED".equals(status)) {
            this.status = "CLOSED";
        }
    }
    
    public boolean isResolved() {
        return "RESOLVED".equals(status) || "CLOSED".equals(status);
    }
    
    public boolean isHighPriority() {
        return "HIGH".equals(priority) || "CRITICAL".equals(priority);
    }
    
    public long getDaysSinceSubmitted() {
        if (createdAt != null) {
            long diff = System.currentTimeMillis() - createdAt.getTime();
            return diff / (1000 * 60 * 60 * 24);
        }
        return 0;
    }
    
    // Getters and Setters
    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getComplaintType() { return complaintType; }
    public void setComplaintType(String complaintType) { this.complaintType = complaintType; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public Timestamp getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Timestamp resolvedAt) { this.resolvedAt = resolvedAt; }
    
    @Override
    public String toString() {
        return String.format("Complaint[ID=%d, Type=%s, Priority=%s, Status=%s]",
                           complaintId, complaintType, priority, status);
    }
}
