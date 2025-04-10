package application.data;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import application.Account;
import application.model.Transaction;
import application.model.TransactionStatus;
public class JsonDataManager implements DataManager {
    private final AtomicInteger accountIdCounter = new AtomicInteger(1);
    private final Map<Integer, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public boolean authenticateUser(String username, String password) {
        return accounts.values().stream()
            .anyMatch(account -> account.getUsername().equals(username) &&
                account.getPasswordHash().equals(hashPassword(password)));
    }

    @Override
    public Account createUser(String username, String password) {
        int id = accountIdCounter.getAndIncrement();
        Account account = new Account(id, username, hashPassword(password), 0.0, 0.0);
        accounts.put(id, account);
        return account;
    }

    @Override
    public Account getUserByUsername(String username) {
        return accounts.values().stream()
            .filter(account -> account.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Account createAccount(Account user, double initialBalance, double hourlyWage) {
        // Since we've merged User and Account, we just update the existing account
        user.setBalance(initialBalance);
        return user;
    }

    public Account getAccountById(int accountId) {
        return accounts.get(accountId);
    }

    public void updateAccountBalance(Account account, double newBalance) {
        account.setBalance(newBalance);
    }


    @Override
    public Transaction createTransaction(Account sender, Account recipient, double amount, String description) {
        // Transaction creation logic needs to be updated as per the new Account-based model
        throw new UnsupportedOperationException("Transaction creation not supported in the new model");
    }

    @Override
    public List<Transaction> getPendingTransactions(Account account) {
        // Transaction retrieval logic needs to be updated as per the new Account-based model
        throw new UnsupportedOperationException("Pending transactions retrieval not supported in the new model");
    }

    @Override
    public List<Transaction> getRecentTransactions(Account account, int limit) {
        // Transaction retrieval logic needs to be updated as per the new Account-based model
        throw new UnsupportedOperationException("Recent transactions retrieval not supported in the new model");
    }

    @Override
    public void updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        // Transaction status update logic needs to be updated as per the new Account-based model
        throw new UnsupportedOperationException("Transaction status update not supported in the new model");
    }

    @Override
    public double getTotalIncoming(Account account) {
        // Total incoming logic needs to be updated as per the new Account-based model
        throw new UnsupportedOperationException("Total incoming retrieval not supported in the new model");
    }

    @Override
    public double getTotalOutgoing(Account account) {
        // Total outgoing logic needs to be updated as per the new Account-based model
        throw new UnsupportedOperationException("Total outgoing retrieval not supported in the new model");
    }

    private String hashPassword(String password) {
        // Simple hash function for demonstration
        return String.valueOf(password.hashCode());
    }
}
