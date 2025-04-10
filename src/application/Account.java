package application;

import java.util.ArrayList;
import java.util.List;

import application.model.Transaction;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Account {
    private String username;
    private int IDnum; 
    private DoubleProperty balance;
    private DoubleProperty hourlyWage;
    private List<Transaction> transactionHistory;

    public Account(String username, int IDnum, double initialBalance, double initialHourlyWage) {
        this.username = username;
        this.IDnum = IDnum;
        this.balance = new SimpleDoubleProperty(initialBalance);
        this.hourlyWage = new SimpleDoubleProperty(initialHourlyWage);
        this.transactionHistory = new ArrayList<>();
        loadTransactionHistory();
    }

    public double getBalance() {
        return balance.get();
    }

    public void setBalance(double newBalance) {
        this.balance.set(newBalance);
        try {
            Database.updateAccountBalance(username, newBalance);
        } catch (Exception e) {
            System.out.println("Error updating balance in database: " + e.getMessage());
        }
    }

    public double getHourlyWage() {
        return hourlyWage.get();
    }

    public void setHourlyWage(double newHourlyWage) {
        this.hourlyWage.set(newHourlyWage);
        try {
            Database.updateHourlyWage(IDnum, newHourlyWage);
        } catch (Exception e) {
            System.out.println("Error updating hourly wage in database: " + e.getMessage());
        }
    }

    public String getUsername() {
        return username;
    }

    public int getIDnum() {
        return IDnum;
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
            Transaction transaction = new Transaction(username, amount, description);
            transactionHistory.add(transaction);
            
            // Log transaction in database
            Database.logTransaction(username, amount, description);
            
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

    private void loadTransactionHistory() {
        try {
            List<Transaction> recentTransactions = Database.getRecentTransactions(IDnum, 10);
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
            return Database.getTotalIncoming(IDnum);
        } catch (Exception e) {
            System.out.println("Error getting total incoming: " + e.getMessage());
            return 0.0;
        }
    }

    public double getTotalOutgoing() {
        try {
            return Database.getTotalOutgoing(IDnum);
        } catch (Exception e) {
            System.out.println("Error getting total outgoing: " + e.getMessage());
            return 0.0;
        }
    }
}
