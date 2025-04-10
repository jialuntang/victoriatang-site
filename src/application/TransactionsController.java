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

import application.Account;
import application.CurrentSession;
import application.model.Transaction;

/**
 * Controller for the Transactions screen, displaying a user's transaction history.
 */
public class TransactionsController {


    // UI Elements
    @FXML private Button backButton;
    @FXML private Button dashboardButton;
    @FXML private Button DWbutton;
    @FXML private VBox transactionsBox; // Container for displaying transactions
    @FXML private ScrollPane transactionScrollPane; // Scroll pane for transactions
    @FXML private Label transactionsLabel;

    /**
     * Initializes the Transactions screen by loading transaction history.
     */
    @FXML
    public void initialize() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null) {
            loadTransactions();
        }
    }

    /**
     * Handles navigation to the Dashboard screen.
     */
    @FXML
    private void handleDashboardButtonAction() {
        navigateTo("/application/home.fxml");
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
        CurrentSession.getInstance().logout();
        navigateTo("/application/start.fxml");
    }

    /**
     * Loads the transaction history for the currently logged-in user.
     */
    private void loadTransactions() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null) {
            List<Transaction> transactions = currentAccount.getTransactionHistory();
            transactionsBox.getChildren().clear();
            
            if (transactions.isEmpty()) {
                Label noTransactionsLabel = new Label("No transactions found");
                noTransactionsLabel.setStyle("-fx-text-fill: #666666;");
                transactionsBox.getChildren().add(noTransactionsLabel);
            } else {
                for (Transaction transaction : transactions) {
                    Label transactionLabel = new Label(String.format("%s: $%.2f - %s", 
                        transaction.getDescription(), 
                        transaction.getAmount(),
                        transaction.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))));
                    transactionLabel.setStyle("-fx-text-fill: #333333;");
                    transactionsBox.getChildren().add(transactionLabel);
                }
            }
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
            Stage primaryStage = (Stage) transactionsBox.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Failed to navigate to the requested screen.");
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

    @FXML
    private void handleBackButtonAction() {
        navigateTo("/application/home.fxml");
    }
    
}

