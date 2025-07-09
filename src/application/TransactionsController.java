package application;

import java.util.ArrayList;
import java.util.List;

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

public class TransactionsController {

    @FXML private Button backButton;
    @FXML private Button dashboardButton;
    @FXML private Button transactionsButton;
    @FXML private Button DWButton;
    @FXML private VBox transactionsBox;
    @FXML private ScrollPane transactionScrollPane;
    @FXML private Label transactionsLabel;
    @FXML private Button sortAllButton;
    @FXML private Button sortDepositButton;
    @FXML private Button sortWithdrawButton;
    @FXML private Button logOutButton;

    private List<Transaction> allTransactions;
    private String currentSort = "all";

    @FXML
    public void initialize() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null) {
            currentAccount.loadTransactionHistory();
            loadTransactions();
        }

        // Always highlight the Transactions button
        if (transactionsButton != null) {
            transactionsButton.getStyleClass().add("active-button");
        }

        // Highlight "All" filter button
        setActiveFilterButton(sortAllButton);
    }


    @FXML
    private void handleDashboardButtonAction() {
        navigateTo("/application/home.fxml", dashboardButton);
    }

    @FXML
    private void handleDepWidthButtonAction() {
        navigateTo("/application/depwidth.fxml", DWButton);
    }

    @FXML
    private void handleLogOutButtonAction() {
        CurrentSession.getInstance().logout();
        navigateTo("/application/start.fxml", logOutButton);
    }

    private void loadTransactions() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null) {
            currentAccount.loadTransactionHistory();
            allTransactions = currentAccount.getTransactionHistory();

            if (allTransactions == null || allTransactions.isEmpty()) {
                allTransactions = Main.getDataManager().getRecentTransactions(currentAccount.getId(), 100);
            }

            displayTransactions();
        }
    }

    private void displayTransactions() {
        transactionsBox.getChildren().clear();

        if (allTransactions == null || allTransactions.isEmpty()) {
            Label noTransactionsLabel = new Label("No transactions found");
            noTransactionsLabel.setStyle("-fx-text-fill: #666666;");
            transactionsBox.getChildren().add(noTransactionsLabel);
        } else {
            List<Transaction> filteredTransactions = new ArrayList<>();

            for (Transaction transaction : allTransactions) {
                if (currentSort.equals("all") ||
                    (currentSort.equals("deposit") && transaction.getAmount() > 0) ||
                    (currentSort.equals("withdraw") && transaction.getAmount() < 0)) {
                    filteredTransactions.add(transaction);
                }
            }

            for (Transaction transaction : filteredTransactions) {
                String amountText = String.format("$%.2f", Math.abs(transaction.getAmount()));
                String typeText = transaction.getAmount() > 0 ? "Deposit: " : "Withdrawal: ";
                String transactionText = String.format("%s%s - %s - %s",
                        typeText,
                        amountText,
                        transaction.getDescription(),
                        transaction.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")));

                Label transactionLabel = new Label(transactionText);
                transactionLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 5 10; -fx-background-color: #FFFFFF; -fx-background-radius: 5;");
                transactionLabel.setOnMouseEntered(e -> transactionLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 5 10; -fx-background-color: #FFE0B2; -fx-background-radius: 5;"));
                transactionLabel.setOnMouseExited(e -> transactionLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px; -fx-padding: 5 10; -fx-background-color: #FFFFFF; -fx-background-radius: 5;"));

                transactionsBox.getChildren().add(transactionLabel);
            }
        }

        transactionsBox.setStyle("-fx-padding: 10; -fx-spacing: 5;");
    }

    @FXML
    private void handleSortAll() {
        currentSort = "all";
        setActiveFilterButton(sortAllButton);
        displayTransactions();
    }

    @FXML
    private void handleSortDeposit() {
        currentSort = "deposit";
        setActiveFilterButton(sortDepositButton);
        displayTransactions();
    }

    @FXML
    private void handleSortWithdraw() {
        currentSort = "withdraw";
        setActiveFilterButton(sortWithdrawButton);
        displayTransactions();
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

    private void resetFilterButtonStyles() {
        sortAllButton.getStyleClass().remove("active-button");
        sortDepositButton.getStyleClass().remove("active-button");
        sortWithdrawButton.getStyleClass().remove("active-button");
    }

    private void setActiveFilterButton(Button activeButton) {
        resetFilterButtonStyles();
        if (!activeButton.getStyleClass().contains("active-button")) {
            activeButton.getStyleClass().add("active-button");
        }
    }


    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
