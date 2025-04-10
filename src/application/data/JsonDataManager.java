package application.data;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import application.Account;
import application.model.Transaction;
import application.model.TransactionStatus;
public class JsonDataManager implements DataManager {
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private final Map<Integer, Account> accounts = new ConcurrentHashMap<>();
    private final Map<Integer, List<Transaction>> transactions = new ConcurrentHashMap<>();
    @Override
    public boolean authenticateUser(String username, String password) {
        return accounts.values().stream()
            .anyMatch(account -> account.getUsername().equals(username) &&
                account.getPasswordHash().equals(hashPassword(password)));
    }

    @Override
    public Account createAccount(String username, String password) {
        int id = idCounter.getAndIncrement();
        Account account = new Account(id, username, hashPassword(password));
        accounts.put(id, account);
        return account;
    }

    @Override
    public Account getAccountByUsername(String username) {
        return accounts.values().stream()
            .filter(account -> account.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }


    public Account getAccountById(int accountId) {
        return accounts.get(accountId);
    }

    public void updateAccountBalance(Account account, double newBalance) {
        account.setBalance(newBalance);
    }


    @Override
    public Transaction createTransaction(Account sender, Account recipient, double amount, String description) {
        int id = idCounter.getAndIncrement();
        Transaction transaction = new Transaction(id, sender.getId(), recipient.getId(), amount, description);
        return transaction;
    }

    @Override
    public List<Transaction> getPendingTransactions(Account account) {
        // Transaction retrieval logic needs to be updated as per the new Account-based model
        return transactions.get(account.getId());
    }

    @Override
    public List<Transaction> getRecentTransactions(Account account, int limit) {
        // Transaction retrieval logic needs to be updated as per the new Account-based model
        List<Transaction> recentTransactions = transactions.get(account.getId());
        return recentTransactions;
    }

    @Override
    public void updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
    }


    @Override
    public boolean accountExists(String username) {
        return accounts.values().stream()
            .anyMatch(account -> account.getUsername().equals(username));
    }

    private String hashPassword(String password) {
        // Simple hash function for demonstration
        return String.valueOf(password.hashCode());
    }




}
