package application.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Account;
import application.model.Transaction;
import application.model.TransactionStatus;

public class JsonDataManager implements DataManager {
    private int nextId = 1;
    private Map<Integer, Account> accounts = new HashMap<>();
    private Map<Integer, List<Transaction>> transactions = new HashMap<>();
    private static final String USER_FILE_PATH = System.getProperty("user.dir") + "/user_data.txt";
    private static final String ACCOUNT_FILE_PATH = System.getProperty("user.dir") + "/account_data.txt";
    private static final String TRANSACTION_FILE_PATH = System.getProperty("user.dir") + "/transaction_data.txt";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public JsonDataManager() {
        createDataFiles();
        loadAccounts();
    }

    private void createDataFiles() {
        try {
            new File(USER_FILE_PATH).createNewFile();
            new File(ACCOUNT_FILE_PATH).createNewFile();
            new File(TRANSACTION_FILE_PATH).createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating files: " + e.getMessage());
        }
    }

    private void loadAccounts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    int id = Integer.parseInt(data[0]);
                    String username = data[1];
                    String passwordHash = data[2];
                    double balance = Double.parseDouble(data[3]);
                    
                    Account account = new Account(id, username, passwordHash);
                    account.setDataManager(this);
                    account.setBalance(balance);
                    accounts.put(id, account);
                    
                    if (id >= nextId) {
                        nextId = id + 1;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        for (Account account : accounts.values()) {
            if (account.getUsername().equals(username) && 
                account.getPasswordHash().equals(hashPassword(password))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Account createAccount(String username, String password) {
        int id = nextId++;
        Account account = new Account(id, username, hashPassword(password));
        account.setDataManager(this);
        accounts.put(id, account);
        saveAccounts();
        return account;
    }

    @Override
    public Account getAccountByUsername(String username) {
        for (Account account : accounts.values()) {
            if (account.getUsername().equals(username)) {
                double balance = calculateBalance(account);
                account.setBalance(balance);
                return account;
            }
        }
        return null;
    }

    public Account getAccountById(int accountId) {
        return accounts.get(accountId);
    }

    public void updateAccountBalance(Account account, double newBalance) {
        account.setBalance(newBalance);
        saveAccounts();
    }

    private void saveAccounts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE_PATH))) {
            for (Account account : accounts.values()) {
                writer.write(String.format("%d,%s,%s,%.2f%n",
                    account.getId(),
                    account.getUsername(),
                    account.getPasswordHash(),
                    account.getBalance()));
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    @Override
    public Transaction createTransaction(Account sender, Account recipient, double amount, String description) {
        Transaction transaction = new Transaction(nextId++, sender.getId(), recipient.getId(), amount, description);
        logTransaction(sender.getUsername(), amount, "Transfer", description);
        return transaction;
    }

    @Override
    public List<Transaction> getPendingTransactions(Account account) {
        List<Transaction> pending = new ArrayList<>();
        for (Transaction transaction : transactions.getOrDefault(account.getId(), new ArrayList<>())) {
            if (transaction.getStatus() == TransactionStatus.PENDING) {
                pending.add(transaction);
            }
        }
        return pending;
    }

    @Override
    public List<Transaction> getRecentTransactions(Account account, int limit) {
        return getRecentTransactions(account.getId(), limit);
    }

    @Override
    public void updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
    }

    @Override
    public boolean accountExists(String username) {
        for (Account account : accounts.values()) {
            if (account.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private String hashPassword(String password) {
        return String.valueOf(password.hashCode());
    }

    @Override
    public void addAccount(String username, String password) {
        createAccount(username, password);
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
            writer.write(String.format("%s,%.2f,%s,%s,%s%n",
                username,
                amount,
                type,
                description,
                LocalDateTime.now().format(ISO_FORMATTER)));
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
                    if (amount > 0) {
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
                    if (amount < 0) {
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
        List<Transaction> recent = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    Account account = getAccountById(accountId);
                    if (account != null && data[0].equals(account.getUsername())) {
                        double amount = Double.parseDouble(data[1]);
                        String type = data[2];
                        String description = data[3];
                        LocalDateTime timestamp;
                        try {
                            // Try parsing with ISO format first
                            timestamp = LocalDateTime.parse(data[4], ISO_FORMATTER);
                        } catch (DateTimeParseException e) {
                            try {
                                // If ISO format fails, try the custom format
                                timestamp = LocalDateTime.parse(data[4], DATE_TIME_FORMATTER);
                            } catch (DateTimeParseException e2) {
                                System.out.println("Error parsing date: " + data[4] + ". Using current time.");
                                timestamp = LocalDateTime.now();
                            }
                        }
                        
                        Transaction transaction = new Transaction(
                            nextId++,
                            accountId,
                            0,
                            amount,
                            description
                        );
                        transaction.setTimestamp(timestamp);
                        recent.add(0, transaction); // Add to beginning to maintain reverse chronological order
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading recent transactions: " + e.getMessage());
        }
        
        // Return only the most recent transactions up to the limit
        if (recent.size() > limit) {
            return recent.subList(0, limit);
        }
        return recent;
    }

    private double calculateBalance(Account account) {
        double balance = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5 && data[0].equals(account.getUsername())) {
                    balance += Double.parseDouble(data[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error calculating balance: " + e.getMessage());
        }
        return balance;
    }
}
