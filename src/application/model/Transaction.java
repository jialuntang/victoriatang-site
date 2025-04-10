package application.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final int id;
    private final int senderId;
    private final int recipientId;
    private final double amount;
    private final String description;
    private final LocalDateTime timestamp;
    private TransactionStatus status;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(int id, int senderId, int recipientId, double amount, String description) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }

    public int getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(formatter);
    }

    @Override
    public String toString() {
        return String.format("%s: $%.2f - %s (%s)", 
            description, amount, getFormattedTimestamp());
    }
} 