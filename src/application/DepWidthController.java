package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller for handling deposit and withdrawal operations in the application.
 */
public class DepWidthController {

    // UI Elements
    @FXML private Button logOutButton;
    @FXML private Button dashboardButton;
    @FXML private Button sendreceiveButton;
    @FXML private Button transactionsButton;
    
    @FXML private TextField wageField;
    @FXML private Button depositButton;
    @FXML private Button withdrawButton;
    
    @FXML private Label depWitLabel,balanceLabel,welcomeLabel;

    
    /**
     * Initializes the controller with the current account.
     * Binds the balance label to reflect account balance updates.
     */
    public void initialize() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null) {
            double balance = Database.getAccountBalance(currentAccount.getIDnum());
            currentAccount.setBalance(balance); // Sync balance with CurrentSession
            balanceLabel.textProperty().bind(currentAccount.balanceProperty().asString("Balance: $%.2f"));
        } else {
            balanceLabel.setText("Balance:   -   -   -");
        }
    }

    /**
     * Handles the deposit action.
     * Validates input and updates the account balance in the database.
     */
    @FXML
    private void handleDepositButtonAction() {
        try {
            double depAmount = Double.parseDouble(wageField.getText());
            if (depAmount <= 0) {
                showAlert(AlertType.ERROR, "Deposit Denied", "Enter a value greater than 0.");
                return;
            }

            Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
            if (currentAccount != null) {
                double newBalance = currentAccount.getBalance() + depAmount;
                currentAccount.setBalance(newBalance);
                Database.updateAccountBalance(currentAccount.getIDnum(), newBalance);
                Database.logTransaction(currentAccount.getIDnum(), depAmount, "Deposit", "Deposited money");
                updateHomePieChart();
            } else {
                showAlert(AlertType.ERROR, "Access Denied", "You must be logged in to deposit money.");
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Input", "Enter a valid number for the amount.");
        }
    }

    /**
     * Handles the withdraw action.
     * Validates input and updates the account balance in the database.
     */
    @FXML
    private void handleWithdrawButtonAction() {
        try {
            double witAmount = Double.parseDouble(wageField.getText());
            if (witAmount <= 0) {
                showAlert(AlertType.ERROR, "Withdraw Denied", "Enter a value greater than 0.");
                return;
            }

            Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
            if (currentAccount != null) {
                if (witAmount > currentAccount.getBalance()) {
                    showAlert(AlertType.ERROR, "Withdraw Denied", "Insufficient balance.");
                    return;
                }

                double newBalance = currentAccount.getBalance() - witAmount;
                currentAccount.setBalance(newBalance);
                Database.updateAccountBalance(currentAccount.getIDnum(), newBalance);
                Database.logTransaction(currentAccount.getIDnum(), -witAmount, "Withdraw", "Withdrew money");
                updateHomePieChart();
            } else {
                showAlert(AlertType.ERROR, "Access Denied", "You must be logged in to withdraw money.");
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Input", "Enter a valid number for the amount.");
        }
    }

    /**
     * Updates the balance label to reflect the current account balance.
     */
    @FXML
    private void handleBalanceLabel() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null) {
            balanceLabel.setText(String.format("Balance: $%.2f", currentAccount.getBalance()));
        } else {
            balanceLabel.setText("No Account Logged In");
        }
    }

    /**
     * Handles navigation to the transactions screen.
     */
    @FXML
    private void handleTransactionsButtonAction() {
        navigateTo("/application/transactions.fxml", transactionsButton);
    }

    /**
     * Handles navigation to the dashboard screen.
     */
    @FXML
    private void handleDashboardButtonAction() {
        navigateTo("/application/home.fxml", dashboardButton);
    }

    /**
     * Handles navigation to the send/receive screen.
     */
    @FXML
    private void handleSendReceiveButtonAction() {
        navigateTo("/application/sendreceive.fxml", sendreceiveButton);
    }

    /**
     * Handles the logout action.
     * Clears the current session and navigates to the start screen.
     */
    @FXML
    private void handleLogOutButtonAction() {
        CurrentSession.getInstance().setCurrentAccount(null);
        navigateTo("/application/start.fxml", logOutButton);
    }

    /**
     * Updates the home pie chart to reflect the latest transactions.
     */
    private void updateHomePieChart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/home.fxml"));
            loader.load();
            HomeController homeController = loader.getController();
            homeController.updatePieChart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the specified FXML file.
     */
    private void navigateTo(String fxmlFile, Button button) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage primaryStage = (Stage) button.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays an alert dialog.
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
