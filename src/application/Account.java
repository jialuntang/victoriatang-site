package application;

import java.util.ArrayList;
import java.util.List;

import application.data.JsonDataManager;
import application.model.Transaction;

public class Account {
    private int id;
    private int userId;
    private String username;
    private String passwordHash;
    private double balance;
    private double hourlyWage;
    private List<Transaction> transactionHistory;
    private JsonDataManager dataManager;
    private boolean isUpdatingBalance = false;

    public Account(int id, String username, String passwordHash) {
        this.id = id;
        this.userId = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.balance = 0.0;
        this.hourlyWage = 0.0;
        this.transactionHistory = new ArrayList<>();
    }

    public void setDataManager(JsonDataManager dataManager) {
        this.dataManager = dataManager;
        loadTransactionHistory();
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double newBalance) {
        if (!isUpdatingBalance && dataManager != null) {
            try {
                isUpdatingBalance = true;
                this.balance = newBalance;
                dataManager.updateAccountBalance(this, newBalance);
            } finally {
                isUpdatingBalance = false;
            }
        } else {
            this.balance = newBalance;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public double getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(double hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public void addTransaction(double amount, String description) {
        try {
            Transaction transaction = new Transaction(0, id, 0, amount, description);
            transactionHistory.add(transaction);
            
            String type = amount > 0 ? "Deposit" : "Withdraw";
            dataManager.logTransaction(username, amount, type, description);
            
            updateBalance(amount);
        } catch (Exception e) {
            System.out.println("Error adding transaction: " + e.getMessage());
        }
    }

    private void updateBalance(double amount) {
        try {
            double newBalance = getBalance() + amount;
            setBalance(newBalance);
        } catch (Exception e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void loadTransactionHistory() {
        if (dataManager != null) {
            transactionHistory = dataManager.getRecentTransactions(this, 100);
        }
    }

    public double getTotalIncoming() {
        try {
            return dataManager.getTotalIncoming(this);
        } catch (Exception e) {
            System.out.println("Error getting total incoming: " + e.getMessage());
            return 0.0;
        }
    }

    public double getTotalOutgoing() {
        try {
            return dataManager.getTotalOutgoing(this);
        } catch (Exception e) {
            System.out.println("Error getting total outgoing: " + e.getMessage());
            return 0.0;
        }
    }

    @Override
    public String toString() {
        return String.format("User: %s, Balance: $%.2f", username, balance);
    }
}
