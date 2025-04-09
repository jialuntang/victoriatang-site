package application;

import java.io.*;
import java.util.*;

public class Database {
    private static final String FILE_PATH = "mydatabase.txt"; 

    // Initialize or create the database file if it doesn't exist
    public static void initializeDatabase() {
        File dbFile = new File(FILE_PATH);
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
                System.out.println("Database file has been created.");
            } catch (IOException e) {
                System.out.println("Error creating database file: " + e.getMessage());
            }
        }
    }
    public static void addAccount(int IDnum, double initialBalance, double initialHourlyWage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            // Write account data (IDnum, initial balance, and hourly wage) to the file
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String transactionRecord = senderAccountId + "," + recipientAccountId + "," + amount + "," + description + ",Pending\n";
            writer.write(transactionRecord);
            System.out.println("Pending transaction logged.");
        } catch (IOException e) {
            System.out.println("Error logging pending transaction: " + e.getMessage());
        }
    }

    
    public static List<Transaction> getPendingTransactions(String string) {
        List<Transaction> pendingTransactions = new ArrayList<>();
        
        // Open the database file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
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
        // Retrieve the list of pending transactions
        List<Transaction> transactions = getPendingTransactions(transactionId);

        // Iterate over the transactions to find the matching one
        for (Transaction t : transactions) {
            if (t.getTransactionId() == transactionId) {
                t.setStatus("Accepted"); // Update the status of the transaction
                break;  // Stop searching once the transaction is found
            }
        }

        // Save the updated transactions back to the database or file
        saveTransactionsToFile(transactions);
    }

    public static List<Transaction> getPendingTransactions(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Example of how to create a transaction from the line data
                int transactionId = Integer.parseInt(data[0]);
                String sender = data[1];
                String recipient = data[2];
                double amount = Double.parseDouble(data[3]);
                String description = data[4];
                String status = data[5]; // Assuming the status is the 6th element

                // If the status is "Pending", we add it to the list
                if ("Pending".equals(status)) {
                    Transaction transaction = new Transaction(sender, recipient, amount, description);
                    transaction.setTransactionId(transactionId);
                    transaction.setStatus(status);
                    transactions.add(transaction);
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
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username) && data[1].equals(password)) {
                    return true;  // User exists and password matches
                }
            }
        } catch (IOException e) {
            System.out.println("Error validating user: " + e.getMessage());
        }
        return false;  // Invalid username or password
    }

 // Method to fetch the account balance from the database using the account ID
    public static double getAccountBalance(int accountId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
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
    	int userCount = 1;

        // Count existing users to generate a  ID
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                userCount++;
            }
        } catch (IOException e) {
            System.out.println("Error creating a new user");
        }

        // create a user
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(username + "," + userCount + "," + password + "\n");
            System.out.println("User has been added successfully.");
        } catch (IOException e) {
            System.out.println("Error creating a new user");
        }
    }

    // Check if a user exists by username
    public static boolean userExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
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
    	try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3 && data[0].equals(username)) {				//length contains username, id, password. so length >=3
                    int accountId = Integer.parseInt(data[1].trim());
                    return new Account(username, accountId, 0.0, 0.0);
                }
            }
        } catch (IOException e) {
            System.out.println("Error finding this user");
        }
        return null;
    }

    
    
    
    // Update account balance in file (update balance in file storage)
    public static void updateAccountBalance(int iDnum, double newBalance) {
        // Read the file, update the balance and rewrite it
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // Update the specific line for the account
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] data = line.split(",");
            if (Integer.parseInt(data[1]) == iDnum) {  // Assuming IDnum is the second element
                data[2] = String.valueOf(newBalance);  // Update balance (assuming balance is the 3rd element)
                lines.set(i, String.join(",", data));
                break;
            }
        }

        // Rewrite the file with updated data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error updating account balance: " + e.getMessage());
        }
    }

    // Log a transaction (add to a separate file or append to the existing one)
    public static void logTransaction(int iDnum, double amount, String type, String description) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (Integer.parseInt(data[1]) == iDnum && Double.parseDouble(data[1]) > 0) {  // Assuming data[1] is amount
                    total += Double.parseDouble(data[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching total incoming: " + e.getMessage());
        }
        return total;
    }



    
    public static void saveTransactionsToFile(List<Transaction> transactions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (Integer.parseInt(data[1]) == iDnum && Double.parseDouble(data[1]) < 0) {  // Assuming data[1] is amount
                    total += Math.abs(Double.parseDouble(data[1]));  // Sum negative values (outgoing)
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


