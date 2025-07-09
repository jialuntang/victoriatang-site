package application;

import java.time.format.DateTimeFormatter;
import java.util.List;

import application.data.JsonDataManager;
import application.model.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Controller class for the Home screen in the JavaFX application.
 * Handles UI interactions, displays account details, and updates transaction data.
 */
public class HomeController {

    private Account account;
    private JsonDataManager dataManager = Main.getDataManager();

    public void setAccount(Account account) {
        this.account = account;
    }

    // UI Elements
    @FXML private Button backButton, dashboardButton, DWButton, transactionsButton, logOutButton;
    @FXML private Label balanceLabel, dashboardLabel, welcomeLabel, dateTimeLabel, recentTransactionsLabel;
    @FXML private Circle placeholderCircle;
    @FXML private PieChart pieChart;
    @FXML private VBox recentTransactionsBox;

    /**
     * Initializes the Home screen.
     */
    @FXML
    public void initialize() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        // Set active style for Dashboard button
        if (dashboardButton != null) {
        	dashboardButton.getStyleClass().add("active-button");
        }
        
        if (currentAccount != null) {
            updateAccountInfo();
        }
        startDateTimeUpdater();

     
    }


    private void updateAccountInfo() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null) {
            // Force reload transaction history
            currentAccount.loadTransactionHistory();
            
            // Update balance
            double currentBalance = dataManager.getAccountBalance(currentAccount.getUsername());
            balanceLabel.setText(String.format("Balance: $%.2f", currentBalance));

            // Update welcome message
            welcomeLabel.setText("Welcome, " + currentAccount.getUsername() + "!");

            // Update transactions
            loadRecentTransactions();

            // Update pie chart
            updatePieChart();
        } else {
            balanceLabel.setText("Balance: - - -");
            welcomeLabel.setText("Welcome, Guest!");
        }
    }

    /**
     * Updates the welcome label with the current user's name.
     */
    @FXML
    private void updateWelcomeLabel() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        welcomeLabel.setText(currentAccount != null ? "Welcome, " + currentAccount.getUsername() + "!" : "Welcome, Guest!");
    }

    /**
     * Updates the PieChart with incoming and outgoing transaction data.
     */
    @FXML
    public void updatePieChart() {
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
            
            placeholderCircle.setVisible(false);
        } else {
            pieChart.setTitle("No Account Logged In");
            placeholderCircle.setVisible(true);
        }
    }

    /**
     * Loads recent transactions into the transactions box.
     */
    private void loadRecentTransactions() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null && recentTransactionsBox != null) {
            // Clear existing items
            recentTransactionsBox.getChildren().clear();
            
            // Get recent transactions
            List<Transaction> transactions = dataManager.getRecentTransactions(currentAccount.getId(), 10); // Show more transactions
            
            if (transactions.isEmpty()) {
                Label noTransactionsLabel = new Label("No recent transactions");
                noTransactionsLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px; -fx-padding: 5 10;");
                recentTransactionsBox.getChildren().add(noTransactionsLabel);
            } else {
                for (Transaction transaction : transactions) {
                    // Create a VBox for each transaction
                    VBox transactionBox = new VBox(5); // 5px spacing between elements
                    transactionBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-border-color: #FFE0B2; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;");
                    
                    // Amount and type
                    String amountText = String.format("$%.2f", Math.abs(transaction.getAmount()));
                    String typeText = transaction.getAmount() > 0 ? "Deposit: " : "Withdrawal: ";
                    Label amountLabel = new Label(typeText + amountText);
                    amountLabel.setStyle("-fx-text-fill: " + (transaction.getAmount() > 0 ? "#4CAF50" : "#F44336") + "; -fx-font-weight: bold; -fx-font-size: 14px;");
                    
                    // Description
                    Label descriptionLabel = new Label(transaction.getDescription());
                    descriptionLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
                    
                    // Date
                    Label dateLabel = new Label(transaction.getTimestamp().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")));
                    dateLabel.setStyle("-fx-text-fill: #999999; -fx-font-size: 11px;");
                    
                    // Add all elements to the transaction box
                    transactionBox.getChildren().addAll(amountLabel, descriptionLabel, dateLabel);
                    
                    // Add hover effect
                    transactionBox.setOnMouseEntered(e -> transactionBox.setStyle("-fx-background-color: rgba(255, 243, 224, 0.9); -fx-border-color: #FFB499; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;"));
                    transactionBox.setOnMouseExited(e -> transactionBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-border-color: #FFE0B2; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;"));
                    
                    recentTransactionsBox.getChildren().add(transactionBox);
                }
            }
        }
    }

    /**
     * Starts the date-time updater.
     */
    private void startDateTimeUpdater() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
                String currentTime = java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"));
                dateTimeLabel.setText(currentTime);
            })
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Handles navigation between different application views.
     */
    @FXML private void handleDWButtonAction() {
        navigateTo("/application/depwidth.fxml", DWButton);
    }

    @FXML private void handleTransactionButtonAction() {
        navigateTo("/application/transactions.fxml", transactionsButton);
    }

    /**
     * Logs out the current user and returns to the start screen.
     */
    @FXML
    private void handleLogOutButtonAction() {
        CurrentSession.getInstance().logout();
        navigateTo("/application/start.fxml", logOutButton);
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
        	 e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to navigate to the requested screen.");
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
