package application.model;

import java.util.ArrayList;
import java.util.List;

import application.model.Transaction;

public class User {
    private final int id;
    private final String username;
    private final String passwordHash;
    private int accountId;
    private double balance;
    private List<Transaction> transactions;

    public User(int id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        balance += transaction.getAmount();
    }

    @Override
    public String toString() {
        return String.format("User: %s, Balance: $%.2f", username, balance);
    }
} 