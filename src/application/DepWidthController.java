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
    @FXML private Button DWButton;
    
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
            currentAccount.setBalance(balance);
            balanceLabel.setText(String.format("Balance: $%.2f", balance));
        } else {
            balanceLabel.setText("Balance: - - -");
        }
        if (DWButton != null) {
            DWButton.getStyleClass().add("active-button");
        }
    }

    /**
     * Handles the deposit action.
     * Validates input and updates the account balance in the database.
     */
    @FXML
    private void handleDepositButtonAction() {
        try {
            double amount = Double.parseDouble(wageField.getText());
            String description = descriptionField.getText();
            
            if (amount <= 0) {
                showAlert(AlertType.ERROR, "Deposit Denied", "Enter a value greater than 0.");
                return;
            }
            
            if (description.isEmpty()) {
                showAlert(AlertType.ERROR, "Description Required", "Please enter a description.");
                return;
            }

            Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
            if (currentAccount != null) {
                double newBalance = currentAccount.getBalance() + amount;
                currentAccount.setBalance(newBalance);
                dataManager.updateAccountBalance(currentAccount, newBalance);
                dataManager.logTransaction(currentAccount.getUsername(), amount, "Deposit", description);
                
                // Clear fields
                wageField.clear();
                descriptionField.clear();
                
                // Show success message
                showAlert(AlertType.INFORMATION, "Success", "Deposit successful!");
                
                // Navigate to home screen with fresh data
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/home.fxml"));
                Parent root = loader.load();
                HomeController homeController = loader.getController();
                homeController.initialize(); // Force initialization
                
                Scene scene = new Scene(root, 800, 600);
                scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
                Stage stage = (Stage) depositButton.getScene().getWindow();
                stage.setScene(scene);
            } else {
                showAlert(AlertType.ERROR, "Access Denied", "You must be logged in to deposit money.");
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Input", "Enter a valid number for the amount.");
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Navigation Error", "Could not navigate to home screen.");
        }
    }

    /**
     * Handles the withdraw action.
     * Validates input and updates the account balance in the database.
     */
    @FXML
    private void handleWithdrawButtonAction() {
        try {
            double amount = Double.parseDouble(wageField.getText());
            String description = descriptionField.getText();
            
            if (amount <= 0) {
                showAlert(AlertType.ERROR, "Withdrawal Denied", "Enter a value greater than 0.");
                return;
            }
            
            if (description.isEmpty()) {
                showAlert(AlertType.ERROR, "Description Required", "Please enter a description.");
                return;
            }

            Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
            if (currentAccount != null) {
                if (amount > currentAccount.getBalance()) {
                    showAlert(AlertType.ERROR, "Insufficient Funds", "You don't have enough money to withdraw.");
                    return;
                }

                double newBalance = currentAccount.getBalance() - amount;
                currentAccount.setBalance(newBalance);
                dataManager.updateAccountBalance(currentAccount, newBalance);
                dataManager.logTransaction(currentAccount.getUsername(), -amount, "Withdrawal", description);
                
                // Clear fields
                wageField.clear();
                descriptionField.clear();
                
                // Show success message
                showAlert(AlertType.INFORMATION, "Success", "Withdrawal successful!");
                
                // Navigate to home screen with fresh data
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/home.fxml"));
                Parent root = loader.load();
                HomeController homeController = loader.getController();
                homeController.initialize(); // Force initialization
                
                Scene scene = new Scene(root, 800, 600);
                scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
                Stage stage = (Stage) withdrawButton.getScene().getWindow();
                stage.setScene(scene);
            } else {
                showAlert(AlertType.ERROR, "Access Denied", "You must be logged in to withdraw money.");
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Input", "Enter a valid number for the amount.");
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Navigation Error", "Could not navigate to home screen.");
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
     * Handles the logout action.
     * Clears the current session and navigates to the start screen.
     */
    @FXML
    private void handleLogOutButtonAction() {
        CurrentSession.getInstance().logout();
        navigateTo("/application/start.fxml", logOutButton);
    }

    /**
     * Updates the home pie chart to reflect the latest transactions.
     */
    private void updatePieChart() {
        try {
            Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
            if (currentAccount != null) {
                double incoming = dataManager.getTotalIncoming(currentAccount);
                double outgoing = dataManager.getTotalOutgoing(currentAccount);
                
                pieChart.getData().clear();
                pieChart.getData().add(new PieChart.Data("Incoming", incoming));
                pieChart.getData().add(new PieChart.Data("Outgoing", outgoing));
                
                pieChart.setTitle("Transaction Overview");
                pieChart.setStyle("-fx-pie-label-fill: #666666;");
                
                for (PieChart.Data data : pieChart.getData()) {
                    if (data.getName().equals("Incoming")) {
                        data.getNode().setStyle("-fx-pie-color: #82B1FF;");
                    } else {
                        data.getNode().setStyle("-fx-pie-color: #FF9E80;");
                    }
                }
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Failed to update pie chart.");
        }
    }

    private void resetButtonStyles() {
        Button[] buttons = {dashboardButton, DWButton, transactionsButton, logOutButton};
        for (Button btn : buttons) {
            if (btn != null) {
                btn.getStyleClass().remove("active-button");
            }
        }
    }


    private void navigateTo(String fxmlFile, Button button) {
        try {
            resetButtonStyles();
            button.getStyleClass().add("active-button");

            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage stage = (Stage) button.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
        	
            showAlert(AlertType.ERROR, "Error", "Failed to navigate to the requested screen.");
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
