package application;

// Singleton class, manages the current user session
// Ensures only one active session at a time
import application.data.JsonDataManager;
public class CurrentSession {

    // Static instance of the CurrentSession class, ensuring a single instance
    private static CurrentSession instance;

    // Holds data for the currently logged in account
    private Account currentAccount;
    private JsonDataManager dataManager = Main.getDataManager();

    // Private constructor to prevent instantiation from outside of the class
    private CurrentSession() {}

    // Getter for the current instance of the CurrentSession class
    public static CurrentSession getInstance() {
        if (instance == null) {
            instance = new CurrentSession();
        }
        return instance;
    }

    // Getter method for accessing the currently logged-in account
    public Account getCurrentAccount() {
        return currentAccount;
    }

    // Setter method for current account
    public void setCurrentAccount(Account account) {
        this.currentAccount = account;
    }

    // Method for logging in a user
    public void login(String username) {
        currentAccount = dataManager.getAccountByUsername(username);
        if (currentAccount != null) {
            System.out.println("User logged in: " + currentAccount.getUsername());
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    // Method for logging out a user
    public void logout() {
        if (currentAccount != null) {
            System.out.println("Logging out user: " + currentAccount.getUsername());
        }
        currentAccount = null;
    }
}
