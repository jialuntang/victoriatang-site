package application.data;

import java.util.List;

import application.Account;
import application.model.Transaction;
import application.model.TransactionStatus;

public interface DataManager {
    // Account operations
    boolean authenticateUser(String username, String password);
    Account createAccount(String username, String password);
    
    // Transaction operations
    Transaction createTransaction(Account sender, Account recipient, double amount, String description);
    List<Transaction> getPendingTransactions(Account account);
    List<Transaction> getRecentTransactions(Account account, int limit);
    void updateTransactionStatus(Transaction transaction, TransactionStatus status);
    
    // Financial operations
    double getTotalIncoming(Account account);
    double getTotalOutgoing(Account account);
    void updateAccountBalance(Account account, double newBalance);
    List<Transaction> getRecentTransactions(int accountId, int limit);
    void addAccount(String username, String password);
    boolean accountExists(String username);
    boolean validateAccount(String username, String password);
    Account getAccountByUsername(String username);
    double getAccountBalance(String username);
    void logTransaction(String username, double amount, String type, String description);
    

} 