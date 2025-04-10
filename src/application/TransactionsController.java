package application;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import application.model.Transaction;
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
    @FXML private Button sortAllButton;
    @FXML private Button sortDepositButton;
    @FXML private Button sortWithdrawButton;

    private List<Transaction> allTransactions;
    private String currentSort = "all";

    /**
     * Initializes the Transactions screen by loading transaction history.
     */
    @FXML
    public void initialize() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null) {
            // Force reload of transactions
            currentAccount.loadTransactionHistory();
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
            allTransactions = currentAccount.getTransactionHistory();
            displayTransactions();
        }
    }

    /**
     * Displays transactions based on current sort filter
     */
    private void displayTransactions() {
        transactionsBox.getChildren().clear();
        
        if (allTransactions.isEmpty()) {
            Label noTransactionsLabel = new Label("No transactions found");
            noTransactionsLabel.setStyle("-fx-text-fill: #666666;");
            transactionsBox.getChildren().add(noTransactionsLabel);
        } else {
            List<Transaction> filteredTransactions = allTransactions.stream()
                .filter(transaction -> {
                    if (currentSort.equals("all")) return true;
                    if (currentSort.equals("deposit")) return transaction.getAmount() > 0;
                    if (currentSort.equals("withdraw")) return transaction.getAmount() < 0;
                    return true;
                })
                .collect(Collectors.toList());

            for (Transaction transaction : filteredTransactions) {
                Label transactionLabel = new Label(transaction.getDescription());
                transactionLabel.setStyle("-fx-text-fill: #333333;");
                transactionsBox.getChildren().add(transactionLabel);
            }
        }
    }

    @FXML
    private void handleSortAll() {
        currentSort = "all";
        displayTransactions();
    }

    @FXML
    private void handleSortDeposit() {
        currentSort = "deposit";
        displayTransactions();
    }

    @FXML
    private void handleSortWithdraw() {
        currentSort = "withdraw";
        displayTransactions();
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

