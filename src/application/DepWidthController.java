package application;

import java.io.IOException;

import application.data.JsonDataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    @FXML private TextField descriptionField;
    @FXML private Button depositButton;
    @FXML private Button withdrawButton;
    
    @FXML private Label depWitLabel,balanceLabel,welcomeLabel;
    @FXML private PieChart pieChart;

    private JsonDataManager dataManager = Main.getDataManager();
    
    /**
     * Initializes the controller with the current account.
     * Binds the balance label to reflect account balance updates.
     */
    public void initialize() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null) {
            double balance = dataManager.getAccountBalance(currentAccount.getUsername());
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
            String description = descriptionField.getText();
            
            if (depAmount <= 0) {
                showAlert(AlertType.ERROR, "Deposit Denied", "Enter a value greater than 0.");
                return;
            }
            
            if (description.isEmpty()) {
                showAlert(AlertType.ERROR, "Description Required", "Please enter a description for this deposit.");
                return;
            }

            Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
            if (currentAccount != null) {
                double newBalance = currentAccount.getBalance() + depAmount;
                currentAccount.setBalance(newBalance);
                dataManager.updateAccountBalance(currentAccount, newBalance);
                dataManager.logTransaction(currentAccount.getUsername(), depAmount, "Deposit", description);
                
                // Clear the input fields after successful deposit
                wageField.clear();
                descriptionField.clear();
                
                showAlert(AlertType.INFORMATION, "Success", "Deposit successful!");
                
                // Navigate back to home screen
                handleDashboardButtonAction();
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
            String description = descriptionField.getText();
            
            if (witAmount <= 0) {
                showAlert(AlertType.ERROR, "Withdraw Denied", "Enter a value greater than 0.");
                return;
            }
            
            if (description.isEmpty()) {
                showAlert(AlertType.ERROR, "Description Required", "Please enter a description for this withdrawal.");
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
                dataManager.updateAccountBalance(currentAccount, newBalance);
                dataManager.logTransaction(currentAccount.getUsername(), -witAmount, "Withdraw", description);
                updateHomePieChart();
                
                // Clear the input fields after successful withdrawal
                wageField.clear();
                descriptionField.clear();
                
                showAlert(AlertType.INFORMATION, "Success", "Withdrawal successful!");
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/transactions.fxml"));
            Parent root = loader.load();
            TransactionsController controller = loader.getController();
            
            // Force reload of transactions
            Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
            if (currentAccount != null) {
                currentAccount.loadTransactionHistory();
            }
            
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage primaryStage = (Stage) transactionsButton.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to navigate to transactions screen.");
        }
    }

    /**
     * Handles navigation to the dashboard screen.
     */
    @FXML
    private void handleDashboardButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/home.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage primaryStage = (Stage) dashboardButton.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to navigate to dashboard screen.");
        }
    }

    /**
     * Handles the logout action.
     * Clears the current session and navigates to the start screen.
     */
    @FXML
    private void handleLogOutButtonAction() {
        CurrentSession.getInstance().setCurrentAccount(null);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/start.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage primaryStage = (Stage) logOutButton.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to log out.");
        }
    }

    /**
     * Updates the home pie chart to reflect the latest transactions.
     */
    private void updateHomePieChart() {
        try {
            Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
            if (currentAccount != null) {
                double incoming = dataManager.getTotalIncoming(currentAccount);
                double outgoing = dataManager.getTotalOutgoing(currentAccount);
                
                // Update the pie chart data
                pieChart.getData().clear();
                PieChart.Data incomingData = new PieChart.Data("Incoming", incoming);
                PieChart.Data outgoingData = new PieChart.Data("Outgoing", outgoing);
                pieChart.getData().addAll(incomingData, outgoingData);
                
                // Style the pie chart
                pieChart.setTitle("Transaction Overview");
                pieChart.setStyle("-fx-pie-label-fill: #666666;");
                
                // Set colors for the pie slices
                for (PieChart.Data data : pieChart.getData()) {
                    if (data.getName().equals("Incoming")) {
                        data.getNode().setStyle("-fx-pie-color: #82B1FF;");
                    } else {
                        data.getNode().setStyle("-fx-pie-color: #FF9E80;");
                    }
                }
            }
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
