package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class to manage all transactions of a user.
 */
public class Transaction {
    
    private String sender;       // Username of sender
    private String recipient;    // Username of recipient
    private double amount;       // Amount of money sent
    private String description;  // Description or reason for the transaction
    private String timestamp;    // Date and time of the transaction
    private int transactionId;   // Unique ID for the transaction
    private String status;       // Transaction status (e.g., Pending, Accepted)

    /**
     * Constructs a new Transaction object with the given details.
     *
     * @param sender      The sender's username.
     * @param recipient   The recipient's username.
     * @param amount      The transaction amount.
     * @param description A brief description of the transaction.
     */
    public Transaction(String sender, String recipient, double amount, String description) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status = "Pending"; // Default status is Pending
    }

    public Transaction(int transactionId, String sender, String recipient, double amount, String description) {
        this.transactionId = transactionId;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status = "Pending"; // Default status is Pending
    }

    /**
     * Gets the unique transaction ID.
     *
     * @return The transaction ID.
     */
    public int getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the unique transaction ID.
     *
     * @param transactionId The transaction ID to be set.
     */
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Gets the sender's username.
     *
     * @return The sender's username.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Gets the recipient's username.
     *
     * @return The recipient's username.
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Gets the transaction amount.
     *
     * @return The transaction amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Gets the description of the transaction.
     *
     * @return The transaction description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the timestamp of when the transaction occurred.
     *
     * @return The transaction timestamp.
     */
    public String getTimeStamp() {
        return timestamp;
    }

    /**
     * Gets the status of the transaction.
     *
     * @return The status of the transaction (e.g., Pending, Accepted).
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the transaction.
     *
     * @param status The new status of the transaction.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns a formatted string representation of the transaction.
     *
     * @return A string describing the transaction.
     */
    @Override
    public String toString() {
        return String.format("From: %s | Amount: $%.2f | %s | %s | Status: %s", sender, amount, description, timestamp, status);
    }

    /**
     * Determines the type of transaction based on the description.
     *
     * @return "Incoming" if money is received, "Outgoing" if money is sent, or "Unknown" otherwise.
     */
    public String getType() {
        String lowerDesc = description.toLowerCase();

        if (lowerDesc.contains("receive") || 
            lowerDesc.contains("deposit") || 
            lowerDesc.contains("pay tracker")) {
            return "Incoming";
        } else if (lowerDesc.contains("send") || 
                   lowerDesc.contains("withdraw") || 
                   lowerDesc.contains("withdrew") || 
                   lowerDesc.contains("sent to")) {
            return "Outgoing";
        }
        return "Unknown";
    }
}

