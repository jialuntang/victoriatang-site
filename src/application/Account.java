package application;

import java.util.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import application.CreateAccountController;
import application.Database;

public class Account {

    private String username;
    private int IDnum; 
    private DoubleProperty balance;
    private DoubleProperty hourlyWage;
    private List<Transaction> transactionHistory = new ArrayList<>();

    public Account(String username, int IDnum, double initialBalance, double initialHourlyWage) {
        this.username = username;
        this.IDnum = IDnum;
        this.balance = new SimpleDoubleProperty(initialBalance);
        this.hourlyWage = new SimpleDoubleProperty(initialHourlyWage);
    }

    public double getBalance() {
        return balance.get();
    }

    public void setBalance(double newBalance) {
        this.balance.set(newBalance);
        Database.updateAccountBalance(IDnum, newBalance);  // Update the database as well
    }

    public double getHourlyWage() {
        return hourlyWage.get();
    }

    public void setHourlyWage(double newHourlyWage) {
        this.hourlyWage.set(newHourlyWage);
        Database.updateHourlyWage(IDnum, newHourlyWage);  // Update the hourly wage in the database
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

    // Transaction Methods

    public void addTransaction(String type, double amount, String recipient, String description) {
        Transaction transaction = new Transaction(this.username, recipient, amount, description);
        transactionHistory.add(transaction);
        Database.logTransaction(IDnum, amount, type, description);  // Log this transaction to the database
    }

    public List<Transaction> getTransactionHistory() {
        if (transactionHistory.isEmpty()) {
            transactionHistory = Database.getRecentTransactions(IDnum, 50);  // Load recent transactions from the database
        }
        return transactionHistory;
    }

    public void setTransactionHistory(List<Transaction> transactions) {
        this.transactionHistory.clear();
        this.transactionHistory.addAll(transactions);
    }

    // Financial Summary Methods

    public double getTotalIncoming() {
        return Database.getTotalIncoming(IDnum);  // Fetch total incoming transactions from the database
    }

    public double getTotalOutgoing() {
        return Database.getTotalOutgoing(IDnum);  // Fetch total outgoing transactions from the database
    }
}
