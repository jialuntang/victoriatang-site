package application.models;

public class Account {
    private int id;
    private double balance;
    private double hourlyWage;
    private int ownerId; // Store only the ID reference

    public Account(int id, double balance, double hourlyWage, int ownerId) {
        this.id = id;
        this.balance = balance;
        this.hourlyWage = hourlyWage;
        this.ownerId = ownerId;
    }

    // Getters and setters
    public int getId() { return id; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public double getHourlyWage() { return hourlyWage; }
    public void setHourlyWage(double hourlyWage) { this.hourlyWage = hourlyWage; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
} 