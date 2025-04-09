package application;

import java.io.*;
import java.util.*;

public class Database {
    private static final String USER_FILE_PATH = System.getProperty("user.dir") + "/user_data.txt";
    private static final String ACCOUNT_FILE_PATH = System.getProperty("user.dir") + "/account_data.txt";

    // Initialize or create the database files if they don't exist
    public static void initializeDatabase() {
        File userFile = new File(USER_FILE_PATH);
        File accountFile = new File(ACCOUNT_FILE_PATH);
        
        System.out.println("Database files location:");
        System.out.println("User file: " + USER_FILE_PATH);
        System.out.println("Account file: " + ACCOUNT_FILE_PATH);
        
        try {
            if (!userFile.exists()) {
                userFile.createNewFile();
                System.out.println("User database file has been created.");
            }
            if (!accountFile.exists()) {
                accountFile.createNewFile();
                System.out.println("Account database file has been created.");
            }
        } catch (IOException e) {
            System.out.println("Error creating database files: " + e.getMessage());
        }
    }

    public static void addAccount(int IDnum, double initialBalance, double initialHourlyWage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE_PATH, true))) {
            writer.write(IDnum + "," + initialBalance + "," + initialHourlyWage + "\n");
            System.out.println("Account added.");
        } catch (IOException e) {
            System.out.println("Error adding account: " + e.getMessage());
        }
    }

    public static double getHourlyWage(String userId) {
        double hourlyWage = 0.0;
        File file = new File("user_data.txt"); // File that contains the user data

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                // Split the line into userId and hourlyWage
                String[] parts = line.split(", ");
                
                // Assuming the first part is userId and the second part is hourlyWage
                String storedUserId = parts[0].split(": ")[1];  // Extract userId
                String wageStr = parts[1].split(": ")[1];  // Extract hourly wage as a string
                
                if (storedUserId.equals(userId)) {
                    hourlyWage = Double.parseDouble(wageStr);  // Set the hourly wage if userId matches
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show alert or handle the exception
        }

        return hourlyWage; // Return the hourly wage or 0.0 if not found
    }
    
    // Method to log a pending transaction
    public static void logPendingTransaction(int senderAccountId, int recipientAccountId, double amount, String description) {
        // First update sender's balance
        double senderBalance = getAccountBalance(senderAccountId);
        if (senderBalance >= amount) {
            updateAccountBalance(senderAccountId, senderBalance - amount);
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE_PATH, true))) {
                String transactionRecord = senderAccountId + "," + recipientAccountId + "," + amount + "," + description + ",Pending\n";
                writer.write(transactionRecord);
                System.out.println("Pending transaction logged.");
            } catch (IOException e) {
                System.out.println("Error logging pending transaction: " + e.getMessage());
                // Rollback the balance change if transaction logging fails
                updateAccountBalance(senderAccountId, senderBalance);
            }
        }
    }

    public static List<Transaction> getPendingTransactions(String string) {
        List<Transaction> pendingTransactions = new ArrayList<>();
        
        // Open the database file
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            
            // Read each line in the file
            while ((line = reader.readLine()) != null) {
                // Split the line to extract transaction details (e.g., sender, recipient, amount, status)
                String[] data = line.split(",");
                
                int sender = Integer.parseInt(data[0]);        // Assuming sender is at index 0
                int recipient = Integer.parseInt(data[1]);     // Assuming recipient is at index 1
                double amount = Double.parseDouble(data[2]);   // Amount is at index 2
                String description = data[3];                  // Description is at index 3
                String status = data[4];                       // Status is at index 4
                
                // Check if the transaction status is "Pending" and if it involves the given account ID (either sender or recipient)
                if (status.equals("Pending") && (String.valueOf(sender).equals(string) || String.valueOf(recipient).equals(string))) {
                    // Create a new Transaction object
                    Transaction transaction = new Transaction(
                            String.valueOf(sender),        // Convert sender ID to String
                            String.valueOf(recipient),     // Convert recipient ID to String
                            amount, 
                            description
                    );

                    // Add the transaction to the list
                    pendingTransactions.add(transaction);
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching pending transactions: " + e.getMessage());
        }

        // Return the list of pending transactions
        return pendingTransactions;
    }

    // Method to fetch the hourly wage from the database using the account ID
    public static double getHourlyWage(int accountId) {
        double hourlyWage = 0.0;  // Default value if not found
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into components (accountId, username, hourlyWage)
                String[] data = line.split(",");
                
                // Ensure the line has enough data
                if (data.length < 3) {
                    continue;  // Skip malformed lines
                }
                
                // Parse accountId and hourlyWage, and check if it matches the accountId
                try {
                    int storedAccountId = Integer.parseInt(data[0].trim());  // Assuming account ID is the first field
                    double storedHourlyWage = Double.parseDouble(data[2].trim());  // Assuming hourly wage is the third field
                    
                    if (storedAccountId == accountId) {
                        hourlyWage = storedHourlyWage;  // Return the hourly wage if the account ID matches
                        break;  // No need to continue reading once we find a match
                    }
                } catch (NumberFormatException e) {
                    // Log the error and continue if there's a problem with the data format
                    System.out.println("Error parsing data for line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching hourly wage: " + e.getMessage());
        }
        
        return hourlyWage;  // Return the found hourly wage or the default 0.0 if not found
    }

    // Update the method to accept a transaction ID as an argument
    public static void markTransactionAsAccepted(int transactionId) {
        List<String> lines = new ArrayList<>();
        Transaction acceptedTransaction = null;
        
        // First read all lines and find the transaction
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Check if this is a transaction record (should have 5 fields)
                if (data.length == 5) {
                    try {
                        int senderId = Integer.parseInt(data[0]);
                        int recipientId = Integer.parseInt(data[1]);
                        double amount = Double.parseDouble(data[2]);
                        String description = data[3];
                        String status = data[4];
                        
                        if (status.trim().equals("Pending")) {
                            // Update the transaction status
                            line = String.format("%d,%d,%.2f,%s,Accepted", 
                                senderId, recipientId, amount, description);
                            
                            // Store transaction details for balance update
                            acceptedTransaction = new Transaction(
                                String.valueOf(senderId),
                                String.valueOf(recipientId),
                                amount,
                                description
                            );
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                        continue;
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error processing transaction: " + e.getMessage());
            return;
        }
        
        // If we found and processed the transaction, update the recipient's balance
        if (acceptedTransaction != null) {
            int recipientId = Integer.parseInt(acceptedTransaction.getRecipient());
            double amount = acceptedTransaction.getAmount();
            double recipientBalance = getAccountBalance(recipientId);
            
            // Update recipient's balance
            updateAccountBalance(recipientId, recipientBalance + amount);
            
            // Write back all lines with updated transaction status
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE_PATH))) {
                for (String line : lines) {
                    writer.write(line + "\n");
                }
            } catch (IOException e) {
                System.out.println("Error updating transaction status: " + e.getMessage());
            }
        }
    }

    public static List<Transaction> getPendingTransactions(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Check if this is a transaction line (should have at least 5 elements)
                if (data.length >= 5) {
                    try {
                        int senderId = Integer.parseInt(data[0]);
                        int recipientId = Integer.parseInt(data[1]);
                        double amount = Double.parseDouble(data[2]);
                        String description = data[3];
                        String status = data[4];

                        // Only add if it's a pending transaction and involves the current account
                        if ("Pending".equals(status.trim()) && (senderId == accountId || recipientId == accountId)) {
                            Transaction transaction = new Transaction(
                                String.valueOf(senderId),
                                String.valueOf(recipientId),
                                amount,
                                description
                            );
                            transaction.setStatus(status);
                            transactions.add(transaction);
                        }
                    } catch (NumberFormatException e) {
                        // Skip lines that don't contain valid transaction data
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching pending transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Update the status of a transaction by its ID
    public static void setStatus(int transactionId, String newStatus) {
        List<Transaction> transactions = getPendingTransactions(transactionId); // Retrieve the transactions

        for (Transaction t : transactions) {
            if (t.getTransactionId() == transactionId) {
                t.setStatus(newStatus);
                break;
            }
        }

        // Write the updated transactions back to the file (or another storage method)
        saveTransactionsToFile(transactions);
    }

    // Method to validate user credentials (username and password)
    public static boolean validateUser(String username, String password) {
        System.out.println("\n=== Validating User ===");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("User file path: " + USER_FILE_PATH);

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                System.out.println("Line " + lineNumber + ": " + line);
                
                if (line.isEmpty()) {
                    System.out.println("Skipping empty line");
                    continue;
                }
                
                String[] data = line.split(",");
                System.out.println("Data length: " + data.length);
                System.out.println("Data: " + Arrays.toString(data));
                
                if (data.length >= 3) {
                    System.out.println("Username match: " + data[0].equals(username));
                    System.out.println("Password match: " + data[2].equals(password));
                    
                    if (data[0].equals(username) && data[2].equals(password)) {
                        System.out.println("User validated successfully!");
                        return true;
                    }
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.out.println("Error validating user: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("User validation failed!");
        return false;
    }

    // Method to fetch the account balance from the database using the account ID
    public static double getAccountBalance(int accountId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                int storedAccountId = Integer.parseInt(data[0]);  // Assuming account ID is the first field
                double storedBalance = Double.parseDouble(data[1]);  // Assuming balance is the second field
                
                if (storedAccountId == accountId) {
                    return storedBalance;  // Return the balance if the account ID matches
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching account balance: " + e.getMessage());
        }
        return 0.0;  // Return 0.0 if the account is not found
    }

    // Add a user to the file
    public static void addUser(String username, String password) {
        System.out.println("\n=== Adding User ===");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        
        int userCount = 1;

        // Count existing users to generate an ID
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    userCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user count: " + e.getMessage());
        }

        // create a user
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH, true))) {
            String userData = username + "," + userCount + "," + password;
            writer.write(userData + "\n");
            System.out.println("User added successfully: " + userData);
            
            // Initialize account with balance and hourly wage
            addAccount(userCount, 0.0, 0.0);
        } catch (IOException e) {
            System.out.println("Error creating a new user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Check if a user exists by username
    public static boolean userExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username)) {
                    return true; // Return true if the user is found
                }
            }
        } catch (IOException e) {
            System.out.println("Error checking user: " + e.getMessage());
        }
        return false;
    }

    // Show all users
    public static void showUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print each line (user data)
            }
        } catch (IOException e) {
            System.out.println("Error reading users: " + e.getMessage());
        }
    }

    // Get account by username
    public static Account getAccountByUsername(String username) {
        System.out.println("\n=== Getting Account ===");
        System.out.println("Username: " + username);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                System.out.println("Line " + lineNumber + ": " + line);
                
                if (line.isEmpty()) {
                    System.out.println("Skipping empty line");
                    continue;
                }
                
                String[] data = line.split(",");
                System.out.println("Data length: " + data.length);
                System.out.println("Data: " + Arrays.toString(data));
                
                if (data.length >= 3 && data[0].equals(username)) {
                    int accountId = Integer.parseInt(data[1].trim());
                    System.out.println("Account found! ID: " + accountId);
                    return new Account(username, accountId, 0.0, 0.0);
                }
                lineNumber++;
            }
        } catch (IOException e) {
            System.out.println("Error finding user: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("No account found!");
        return null;
    }

    // Update account balance in file (update balance in file storage)
    public static void updateAccountBalance(int iDnum, double newBalance) {
        List<String> lines = new ArrayList<>();
        boolean accountFound = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Check if this is an account record (should have exactly 3 fields: id, balance, hourlyWage)
                if (data.length == 3) {
                    try {
                        int accountId = Integer.parseInt(data[0].trim());
                        if (accountId == iDnum) {
                            // Update the balance while preserving other account data
                            line = String.format("%d,%.2f,%s", iDnum, newBalance, data[2]);
                            accountFound = true;
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                        continue;
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return;
        }

        if (!accountFound) {
            System.out.println("Account not found: " + iDnum);
            return;
        }

        // Rewrite the file with updated data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE_PATH))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error updating account balance: " + e.getMessage());
        }
    }

    // Log a transaction (add to a separate file or append to the existing one)
    public static void logTransaction(int iDnum, double amount, String type, String description) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE_PATH, true))) {
            writer.write(iDnum + "," + amount + "," + type + "," + description + "\n");
            System.out.println("Transaction logged.");
        } catch (IOException e) {
            System.out.println("Error logging transaction: " + e.getMessage());
        }
    }

    // Get recent transactions (this method can be implemented by reading a transactions file and returning a list of recent transactions)
    public static List<Transaction> getRecentTransactions(int iDnum, int limit) {
        return new ArrayList<>();
    }

    // Get total incoming transactions for an account
    public static double getTotalIncoming(int iDnum) {
        double total = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (Integer.parseInt(data[0]) == iDnum && Double.parseDouble(data[0]) > 0) {  // Assuming data[0] is amount
                    total += Double.parseDouble(data[0]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching total incoming: " + e.getMessage());
        }
        return total;
    }

    public static void saveTransactionsToFile(List<Transaction> transactions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE_PATH))) {
            // Iterate over the list of transactions and write them to the file
            for (Transaction t : transactions) {
                writer.write(t.getSender() + "," + t.getRecipient() + "," + 
                             t.getAmount() + "," + t.getDescription() + "," + t.getStatus() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    // Get total outgoing transactions for an account
    public static double getTotalOutgoing(int iDnum) {
        double total = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (Integer.parseInt(data[0]) == iDnum && Double.parseDouble(data[0]) < 0) {  // Assuming data[0] is amount
                    total += Math.abs(Double.parseDouble(data[0]));  // Sum negative values (outgoing)
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching total outgoing: " + e.getMessage());
        }
        return total;
    }

    // Update hourly wage (similar logic to balance update)
    public static void updateHourlyWage(int iDnum, double newHourlyWage) {
        // Similar to updateAccountBalance, read the file and update the hourly wage field
    }
}


