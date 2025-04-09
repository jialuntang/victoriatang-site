package application.models;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private int senderId;
    private int recipientId;
    private double amount;
    private String description;
    private TransactionStatus status;
    private LocalDateTime timestamp;

    public Transaction(int id, int senderId, int recipientId, double amount, String description) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.description = description;
        this.status = TransactionStatus.PENDING;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public int getId() { return id; }
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public int getRecipientId() { return recipientId; }
    public void setRecipientId(int recipientId) { this.recipientId = recipientId; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
} 