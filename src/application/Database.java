package application;

import java.io.*;
import java.util.*;

import application.model.Transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Database {
    private static final String USER_FILE_PATH = System.getProperty("user.dir") + "/user_data.txt";
    private static final String ACCOUNT_FILE_PATH = System.getProperty("user.dir") + "/account_data.txt";
    private static final String TRANSACTION_FILE_PATH = System.getProperty("user.dir") + "/transaction_data.txt";

    // Initialize or create the database files if they don't exist
    public static void initializeDatabase() {
        File userFile = new File(USER_FILE_PATH);
        File accountFile = new File(ACCOUNT_FILE_PATH);
        File transactionFile = new File(TRANSACTION_FILE_PATH);
        
        System.out.println("Database files location:");
        System.out.println("User file: " + USER_FILE_PATH);
        System.out.println("Account file: " + ACCOUNT_FILE_PATH);
        System.out.println("Transaction file: " + TRANSACTION_FILE_PATH);
        
        try {
            if (!userFile.exists()) {
                userFile.createNewFile();
                System.out.println("User database file has been created.");
            }
            if (!accountFile.exists()) {
                accountFile.createNewFile();
                System.out.println("Account database file has been created.");
            }
            if (!transactionFile.exists()) {
                transactionFile.createNewFile();
                System.out.println("Transaction database file has been created.");
            }
        } catch (IOException e) {
            System.out.println("Error creating database files: " + e.getMessage());
        }
    }

    @Override
    public static void addUser(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH, true))) {
            // Format: username,password
            writer.write(username + "," + password + "\n");
            System.out.println("User added: " + username);
        } catch (IOException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    @Override
    public static boolean userExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0 && data[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }

    @Override
    public static boolean validateUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].equals(username) && data[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error validating user: " + e.getMessage());
        }
        return false;
    }

    @Override
    public static void addAccount(String username, double initialBalance, double initialHourlyWage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE_PATH, true))) {
            // Format: username,balance,hourlyWage
            writer.write(username + "," + initialBalance + "," + initialHourlyWage + "\n");
            System.out.println("Account added for user: " + username);
        } catch (IOException e) {
            System.out.println("Error adding account: " + e.getMessage());
        }
    }

    @Override
    public static Account getAccountByUsername(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            int lineNumber = 1; // Use line number as a simple ID
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3 && data[0].equals(username)) {
                    return new Account(
                        username,
                        lineNumber, // Use line number as ID
                        Double.parseDouble(data[1]),
                        Double.parseDouble(data[2])
                    );
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.out.println("Error getting account: " + e.getMessage());
        }
        return null;
    }

    @Override
    public static double getAccountBalance(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].equals(username)) {
                    return Double.parseDouble(data[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching account balance: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public static void updateAccountBalance(String username, double newBalance) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3 && data[0].equals(username)) {
                    line = String.format("%s,%.2f,%s", username, newBalance, data[2]);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading account data: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE_PATH))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error updating account balance: " + e.getMessage());
        }
    }

    @Override
    public static void logTransaction(String username, double amount, String description) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTION_FILE_PATH, true))) {
            // Format: username,amount,description,timestamp
            String transactionRecord = String.format("%s,%.2f,%s,%s\n", 
                username, amount, description, 
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.write(transactionRecord);
            System.out.println("Transaction logged for user: " + username);
        } catch (IOException e) {
            System.out.println("Error logging transaction: " + e.getMessage());
        }
    }

    @Override
    public static double getHourlyWage(String userId) {
        double hourlyWage = 0.0;
        File file = new File("user_data.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String storedUserId = parts[0].split(": ")[1];
                String wageStr = parts[1].split(": ")[1];
                
                if (storedUserId.equals(userId)) {
                    hourlyWage = Double.parseDouble(wageStr);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hourlyWage;
    }


    @Override
    public static void showUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error showing users: " + e.getMessage());
        }
    }

    public static List<Transaction> getRecentTransactions(int accountId, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null && transactions.size() < limit) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    String username = data[0];
                    double amount = Double.parseDouble(data[1]);
                    String description = data[2];
                    LocalDateTime timestamp = LocalDateTime.parse(data[3], 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    transactions.add(new Transaction(username, amount, description, timestamp));
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }
        return transactions;
    }

    @Override
    public static double getTotalIncoming(int accountId) {
        double total = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
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
    public static double getTotalOutgoing(int accountId) {
        double total = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
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
}


