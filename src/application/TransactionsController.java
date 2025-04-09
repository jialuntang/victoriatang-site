package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the Transactions screen, displaying a user's transaction history.
 */
public class TransactionsController {

    // UI Elements
    @FXML private Button logOutButton;
    @FXML private Button dashboardButton;
    @FXML private Button DWbutton;
    @FXML private Button sendreceiveButton;
    @FXML private VBox transactionsBox; // Container for displaying transactions
    @FXML private ScrollPane transactionScrollPane; // Scroll pane for transactions

    /**
     * Initializes the Transactions screen by loading transaction history.
     */
    @FXML
    public void initialize() {
        loadTransactions();
    }

    /**
     * Handles navigation to the Dashboard screen.
     */
    @FXML
    private void handleDashboardButtonAction() {
        navigateTo("/application/home.fxml");
    }

    /**
     * Handles navigation to the Send/Receive screen.
     */
    @FXML
    private void handleSendReceiveButtonAction() {
        navigateTo("/application/sendreceive.fxml");
    }

    /**
     * Handles navigation to the Deposit/Withdraw screen.
     */
    @FXML
    private void handleDepWidthButtonAction() {
        navigateTo("/application/depwidth.fxml");
    }

    /**
     * Handles the logout action by clearing the session and navigating to the start screen.
     */
    @FXML
    private void handleLogOutButtonAction() {
        CurrentSession.getInstance().setCurrentAccount(null);
        navigateTo("/application/start.fxml");
    }

    /**
     * Loads the transaction history for the currently logged-in user.
     */
    private void loadTransactions() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        transactionsBox.getChildren().clear(); // Clear previous entries

        if (currentAccount == null) {
            Label noAccountLabel = new Label("No account logged in. Log in to view transactions.");
            noAccountLabel.getStyleClass().add("transaction-label");
            transactionsBox.getChildren().add(noAccountLabel);
            return;
        }

        // Fetch transactions from the database (up to 100 recent transactions)
        List<Transaction> transactions = Database.getRecentTransactions(currentAccount.getIDnum(), 100);

        if (transactions.isEmpty()) {
            Label noTransactionsLabel = new Label("No transactions found.");
            noTransactionsLabel.getStyleClass().add("transaction-label");
            transactionsBox.getChildren().add(noTransactionsLabel);
            return;
        }

        // Populate the transactions box with transaction details
        for (Transaction transaction : transactions) {
            VBox transactionBox = new VBox(5);
            transactionBox.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 10; "
                    + "-fx-border-color: #FFA41B; -fx-border-width: 1; "
                    + "-fx-border-radius: 5; -fx-background-radius: 5;");
            transactionBox.setPrefWidth(transactionScrollPane.getPrefWidth() - 20);

            Label senderLabel = new Label("Sender: " + transaction.getSender());
            Label recipientLabel = new Label("Recipient: " + transaction.getRecipient());
            Label amountLabel = new Label("Amount: $" + String.format("%.2f", transaction.getAmount()));
            Label descriptionLabel = new Label("Description: " + transaction.getDescription());

            // Apply consistent label styling
            String labelStyle = "-fx-text-fill: #333333; -fx-font-size: 12px;";
            senderLabel.setStyle(labelStyle);
            recipientLabel.setStyle(labelStyle);
            amountLabel.setStyle(labelStyle);
            descriptionLabel.setStyle(labelStyle);
            descriptionLabel.setWrapText(true); // Enable text wrapping for long descriptions

            // Add labels to the transaction container
            transactionBox.getChildren().addAll(senderLabel, recipientLabel, amountLabel, descriptionLabel);

            // Add each transaction to the transactionsBox
            transactionsBox.getChildren().add(transactionBox);
        }
    }

    /**
     * Navigates to a specified FXML file.
     *
     * @param fxmlFile The name of the FXML file to load.
     */
    private void navigateTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage primaryStage = (Stage) logOutButton.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Unable to load the requested page.");
        }
    }

    /**
     * Displays an alert dialog.
     *
     * @param alertType The type of alert to display.
     * @param title     The title of the alert.
     * @param message   The message to display in the alert.
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

