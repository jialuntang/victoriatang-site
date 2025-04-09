package application;

import javafx.scene.control.Label;
import application.Transaction;
import application.Account;


import javafx.scene.control.TextField;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import application.Database;

/**
 * Controller for the Send/Receive feature, allowing users to send and accept transactions.
 */
public class SendReceiveController {

    // UI Elements
    @FXML private Button logOutButton;
    @FXML private Button dashboardButton;
    @FXML private Button DWbutton;
    @FXML private Button transactionsButton;
    @FXML private TextField recipientField; // Username input
    @FXML private TextField amountField; // Amount input
    @FXML private Button sendButton;
    @FXML private VBox incomingTransactionsBox; // Container for displaying incoming transactions

    /**
     * Initializes the controller and loads incoming transactions.
     */
    @FXML
    public void initialize() {
        loadIncomingTransactions();
    }

    /**
     * Handles sending money to another user.
     */
    @FXML
    private void handleSendMoneyAction() {
        Account senderAccount = CurrentSession.getInstance().getCurrentAccount();

        if (senderAccount == null) {
            showAlert(Alert.AlertType.ERROR, "Access Denied", "You must be logged in to send money.");
            return;
        }

        String recipientUsername = recipientField.getText();
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Enter a valid number for the amount.");
            return;
        }

        // Check if the recipient exists in the database
        Account recipientAccount = Database.getAccountByUsername(recipientUsername);
        if (recipientAccount != null && !recipientUsername.equals(senderAccount.getUsername())) {
            // Deduct the balance from sender
            double newSenderBalance = senderAccount.getBalance() - amount;
            if (newSenderBalance < 0) {
                showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "You do not have enough balance.");
                return;
            }
            senderAccount.setBalance(newSenderBalance);
            Database.updateAccountBalance(senderAccount.getIDnum(), newSenderBalance);

            // **Only log the pending transaction instead of logging two transactions**
            Database.logPendingTransaction(
                senderAccount.getIDnum(),
                recipientAccount.getIDnum(),
                -amount,
                "Money Sent to " + recipientUsername
            );

            showAlert(Alert.AlertType.INFORMATION, "Transaction Sent", "Money sent successfully to " + recipientUsername);
            updateHomePieChart();
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid User", "Recipient does not exist or is invalid.");
        }
    }


    /**
     * Loads incoming pending transactions for the current user.
     */
    @FXML
    private void loadIncomingTransactions() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();

        incomingTransactionsBox.getChildren().clear();

        if (currentAccount == null) {
            Label guestLabel = new Label("No account logged in. \nLog in to view incoming money.");
            guestLabel.getStyleClass().add("transaction-label");
            incomingTransactionsBox.getChildren().add(guestLabel);
            return;
        }

        // Fetch pending transactions from the database
        List<Transaction> incomingTransactions = Database.getPendingTransactions(currentAccount.getIDnum());

        for (Transaction transaction : incomingTransactions) {
            VBox transactionBox = new VBox(5);
            transactionBox.getStyleClass().add("transaction-item");

            Label fromLabel = new Label("From: " + transaction.getSender());
            fromLabel.getStyleClass().add("from-label");

            Label amountLabel = new Label("Amount: $" + String.format("%.2f", transaction.getAmount()));
            amountLabel.getStyleClass().add("amount-label");

            Label descriptionLabel = new Label("Description: " + transaction.getDescription());
            descriptionLabel.getStyleClass().add("description-label");

            Button acceptButton = new Button("Accept");
            acceptButton.getStyleClass().add("accept-button");

            acceptButton.setOnAction(e -> acceptTransaction(transaction, currentAccount));

            transactionBox.getChildren().addAll(fromLabel, amountLabel, descriptionLabel, acceptButton);
            incomingTransactionsBox.getChildren().add(transactionBox);
        }
    }

    /**
     * Accepts a pending transaction and updates the balance.
     */
    private void acceptTransaction(Transaction transaction, Account currentAccount) {
        try {
            double newBalance = currentAccount.getBalance() + transaction.getAmount();
            currentAccount.setBalance(newBalance);

            Database.updateAccountBalance(currentAccount.getIDnum(), newBalance);
            Database.logTransaction(currentAccount.getIDnum(), transaction.getAmount(), "Incoming",
                    "Accepted money from " + transaction.getSender());

            Database.markTransactionAsAccepted(transaction.getTransactionId());

            updateHomeRecentTransactions();
            showAlert(Alert.AlertType.INFORMATION, "Transaction Accepted",
                    "You received $" + transaction.getAmount());
            loadIncomingTransactions();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to accept the transaction.");
        }
    }

    /**
     * Handles navigation to the Dashboard screen.
     */
    @FXML
    private void handleDashboardButtonAction() {
        navigateTo("/application/home.fxml", dashboardButton);
    }

    /**
     * Handles navigation to the Deposit/Withdraw screen.
     */
    @FXML
    private void handleDWButtonAction() {
        navigateTo("/application/depwidth.fxml", DWbutton);
    }

    /**
     * Handles navigation to the Transactions screen.
     */
    @FXML
    private void handleTransactionsButtonAction() {
        navigateTo("/application/transactions.fxml", transactionsButton);
    }

    /**
     * Handles the logout action and navigates to the start screen.
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
     * Updates the home recent transactions list.
     */
    private void updateHomeRecentTransactions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/home.fxml"));
            loader.load();
            HomeController homeController = loader.getController();
            homeController.loadRecentTransactions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to a specified FXML file.
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
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
