package application.data;

import application.model.*;
import application.Account;
import java.util.List;

public interface DataManager {
    // User operations
    boolean authenticateUser(String username, String password);
    User createUser(String username, String password);
    User getUserByUsername(String username);
    
    // Account operations
    Account createAccount(User user, double initialBalance, double hourlyWage);
    Account getAccountById(int accountId);
    void updateAccountBalance(Account account, double newBalance);
    void updateHourlyWage(Account account, double newWage);
    
    // Transaction operations
    Transaction createTransaction(Account sender, Account recipient, double amount, String description);
    List<Transaction> getPendingTransactions(Account account);
    List<Transaction> getRecentTransactions(Account account, int limit);
    void updateTransactionStatus(Transaction transaction, TransactionStatus status);
    
    // Financial operations
    double getTotalIncoming(Account account);
    double getTotalOutgoing(Account account);
} 