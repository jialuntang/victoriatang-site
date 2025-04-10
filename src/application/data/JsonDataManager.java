package application.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import application.Account;
import application.model.Transaction;
import application.model.TransactionStatus;

public class JsonDataManager implements DataManager {
    private final AtomicInteger idCounter = new AtomicInteger(1);
    private final Map<Integer, Account> accounts = new ConcurrentHashMap<>();
    private final Map<Integer, List<Transaction>> transactions = new ConcurrentHashMap<>();
    private static final String USER_FILE_PATH = System.getProperty("user.dir") + "/user_data.txt";
    private static final String ACCOUNT_FILE_PATH = System.getProperty("user.dir") + "/account_data.txt";
    private static final String TRANSACTION_FILE_PATH = System.getProperty("user.dir") + "/transaction_data.txt";

    public JsonDataManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        File userFile = new File(USER_FILE_PATH);
        File accountFile = new File(ACCOUNT_FILE_PATH);
        File transactionFile = new File(TRANSACTION_FILE_PATH);
        
        try {
            if (!userFile.exists()) userFile.createNewFile();
            if (!accountFile.exists()) accountFile.createNewFile();
            if (!transactionFile.exists()) transactionFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating database files: " + e.getMessage());
        }
    }

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
        // Find and update the account in accounts list
        for (Account acc : accounts.values()) {
            if (acc.getId() == account.getId()) {
                acc.balanceProperty().set(newBalance);
                break;
            }
        }
        // Save changes to file
        saveAccounts();
    }

    private void saveAccounts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE_PATH))) {
            for (Account account : accounts.values()) {
                String accountRecord = String.format("%d,%s,%s,%.2f\n",
                    account.getId(),
                    account.getUsername(),
                    account.getPasswordHash(),
                    account.getBalance());
                writer.write(accountRecord);
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    @Override
    public Transaction createTransaction(Account sender, Account recipient, double amount, String description) {
        int id = idCounter.getAndIncrement();
        Transaction transaction = new Transaction(id, sender.getId(), recipient.getId(), amount, description);
        return transaction;
    }

    @Override
    public List<Transaction> getPendingTransactions(Account account) {
        return transactions.get(account.getId());
    }

    @Override
    public List<Transaction> getRecentTransactions(Account account, int limit) {
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
        return String.valueOf(password.hashCode());
    }

    @Override
    public void addAccount(String username, String password) {
        if (!accountExists(username)) {
            createAccount(username, password);
        }
    }

    @Override
    public boolean validateAccount(String username, String password) {
        return authenticateUser(username, password);
    }

    @Override
    public double getAccountBalance(String username) {
        Account account = getAccountByUsername(username);
        return account != null ? account.getBalance() : 0.0;
    }

    @Override
    public void logTransaction(String username, double amount, String type, String description) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTION_FILE_PATH, true))) {
            String transactionRecord = String.format("%s,%.2f,%s,%s,%s\n", 
                username, amount, type, description,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.write(transactionRecord);
        } catch (IOException e) {
            System.out.println("Error logging transaction: " + e.getMessage());
        }
    }

    @Override
    public double getTotalIncoming(Account account) {
        double total = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5 && data[0].equals(account.getUsername())) {
                    double amount = Double.parseDouble(data[1]);
                    String type = data[2];
                    if (type.equals("Deposit")) {
                        total += amount;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error calculating total incoming: " + e.getMessage());
        }
        return total;
    }

    @Override
    public double getTotalOutgoing(Account account) {
        double total = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5 && data[0].equals(account.getUsername())) {
                    double amount = Double.parseDouble(data[1]);
                    String type = data[2];
                    if (type.equals("Withdraw")) {
                        total += Math.abs(amount);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error calculating total outgoing: " + e.getMessage());
        }
        return total;
    }

    @Override
    public List<Transaction> getRecentTransactions(int accountId, int limit) {
        List<Transaction> recentTransactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_FILE_PATH))) {
            String line;
            List<String> lines = new ArrayList<>();
            
            // First, read all lines into memory
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            
            // Process lines in reverse order to get most recent transactions
            for (int i = lines.size() - 1; i >= 0 && recentTransactions.size() < limit; i--) {
                String[] data = lines.get(i).split(",");
                if (data.length >= 5) {
                    String username = data[0];
                    double amount = Double.parseDouble(data[1]);
                    String type = data[2];
                    String description = data[3];
                    LocalDateTime timestamp = LocalDateTime.parse(data[4], 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    
                    // Only add transactions for the current account
                    if (username.equals(getAccountById(accountId).getUsername())) {
                        // Create a detailed description with amount, time, and description
                        String detailedDescription = String.format("%s: $%.2f | %s | %s", 
                            type, 
                            Math.abs(amount),
                            timestamp.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
                            description);
                        recentTransactions.add(new Transaction(idCounter.getAndIncrement(), accountId, accountId, amount, detailedDescription));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }
        return recentTransactions;
    }
}
