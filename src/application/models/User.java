package application.models;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private int accountId; // Store only the ID reference

    public User(int id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.accountId = -1; // No account initially
    }

    // Getters and setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
} 