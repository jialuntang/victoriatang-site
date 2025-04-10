package application;

import java.util.ArrayList;
import java.util.List;

import application.data.JsonDataManager;
import application.model.Transaction;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Account {
    private final int id;
    private final int userId;
    private String username;
    private String passwordHash;
    private DoubleProperty balance;
    private DoubleProperty hourlyWage;
    private List<Transaction> transactionHistory;
    private JsonDataManager dataManager;
    private boolean isUpdatingBalance = false;

    public Account(int id, String username, String passwordHash) {
        this.id = id;
        this.userId = id; // Using account id as user id since we're merging
        this.username = username;
        this.passwordHash = passwordHash;
        this.balance = new SimpleDoubleProperty(0.0);
        this.hourlyWage = new SimpleDoubleProperty(0.0);
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
        return balance.get();
    }

    public void setBalance(double newBalance) {
        if (!isUpdatingBalance && dataManager != null) {
            try {
                isUpdatingBalance = true;
                this.balance.set(newBalance);
                dataManager.updateAccountBalance(this, newBalance);
            } finally {
                isUpdatingBalance = false;
            }
        } else {
            this.balance.set(newBalance);
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

    public DoubleProperty balanceProperty() {
        return balance;
    }

    public DoubleProperty hourlyWageProperty() {
        return hourlyWage;
    }

    public void addTransaction(double amount, String description) {
        try {
            // Create and add transaction to memory
            Transaction transaction = new Transaction(0, id, 0, amount, description);
            transactionHistory.add(transaction);
            
            // Log transaction in database
            String type = amount > 0 ? "Deposit" : "Withdraw";
            dataManager.logTransaction(username, amount, type, description);
            
            // Update balance
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
        loadTransactionHistory();
        return new ArrayList<>(transactionHistory); // Return a copy to prevent external modification
    }

    public void loadTransactionHistory() {
        try {
            List<Transaction> recentTransactions = dataManager.getRecentTransactions(id, 10);
            if (recentTransactions != null) {
                transactionHistory = recentTransactions;
            }
        } catch (Exception e) {
            System.out.println("Error loading transaction history: " + e.getMessage());
            if (transactionHistory == null) {
                transactionHistory = new ArrayList<>();
            }
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
        return String.format("User: %s, Balance: $%.2f", username, balance.get());
    }
}
