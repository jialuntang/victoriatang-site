package application.data;

import application.model.*;
import application.Account;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class JsonDataManager implements DataManager {
    private final AtomicInteger userIdCounter = new AtomicInteger(1);
    private final AtomicInteger accountIdCounter = new AtomicInteger(1);
    private final AtomicInteger transactionIdCounter = new AtomicInteger(1);

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, Account> accounts = new ConcurrentHashMap<>();
    private final Map<Integer, Transaction> transactions = new ConcurrentHashMap<>();

    public JsonDataManager() {
        // No need to load data since we're keeping everything in memory
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        return users.values().stream()
                .anyMatch(user -> user.getUsername().equals(username) && 
                                user.getPasswordHash().equals(hashPassword(password)));
    }

    @Override
    public User createUser(String username, String password) {
        int id = userIdCounter.getAndIncrement();
        User user = new User(id, username, hashPassword(password));
        users.put(id, user);
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Account createAccount(User user, double initialBalance, double hourlyWage) {
        int id = accountIdCounter.getAndIncrement();
        Account account = new Account(id, initialBalance, hourlyWage, user.getId());
        accounts.put(id, account);
        user.setAccountId(id);
        return account;
    }

    @Override
    public Account getAccountById(int accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void updateAccountBalance(Account account, double newBalance) {
        account.setBalance(newBalance);
    }

    @Override
    public void updateHourlyWage(Account account, double newWage) {
        account.setHourlyWage(newWage);
    }

    @Override
    public Transaction createTransaction(Account sender, Account recipient, double amount, String description) {
        int id = transactionIdCounter.getAndIncrement();
        Transaction transaction = new Transaction(id, sender.getId(), recipient.getId(), amount, description);
        transactions.put(id, transaction);
        return transaction;
    }

    @Override
    public List<Transaction> getPendingTransactions(Account account) {
        int accountId = account.getId();
        return transactions.values().stream()
                .filter(t -> t.getStatus() == TransactionStatus.PENDING &&
                           (t.getSenderId() == accountId || 
                            t.getRecipientId() == accountId))
                .toList();
    }

    @Override
    public List<Transaction> getRecentTransactions(Account account, int limit) {
        int accountId = account.getId();
        return transactions.values().stream()
                .filter(t -> t.getSenderId() == accountId || 
                           t.getRecipientId() == accountId)
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .limit(limit)
                .toList();
    }

    @Override
    public void updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
    }

    @Override
    public double getTotalIncoming(Account account) {
        int accountId = account.getId();
        return transactions.values().stream()
                .filter(t -> t.getRecipientId() == accountId && 
                           t.getStatus() == TransactionStatus.COMPLETED)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    @Override
    public double getTotalOutgoing(Account account) {
        int accountId = account.getId();
        return transactions.values().stream()
                .filter(t -> t.getSenderId() == accountId && 
                           t.getStatus() == TransactionStatus.COMPLETED)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private String hashPassword(String password) {
        // In a real application, use a proper password hashing algorithm like bcrypt
        return Integer.toString(password.hashCode());
    }
}
