package application;

import java.io.IOException;
import java.util.List;

import application.data.JsonDataManager;
import application.model.Transaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    @FXML private Button backButton, DWbutton, transactionsButton;
    @FXML private Label balanceLabel, dashboardLabel, welcomeLabel, dateTimeLabel, recentTransactionsLabel;
    @FXML private Circle placeholderCircle;
    @FXML private PieChart pieChart;
    @FXML private VBox recentTransactionsBox;
    @FXML private ListView<Transaction> recentTransactionsList;

    /**
     * Initializes the Home screen.
     * Binds the account balance, updates UI elements, and starts the date-time updater.
     */
    @FXML
    public void initialize() {
        updateAccountInfo();
        startDateTimeUpdater();
        
        // Force update of pie chart
        updatePieChart();
    }

    private void updateAccountInfo() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null) {
            // Update balance
            double currentBalance = dataManager.getAccountBalance(currentAccount.getUsername());
            balanceLabel.setText(String.format("Balance: $%.2f", currentBalance));

            // Update recent transactions
            List<Transaction> transactions = dataManager.getRecentTransactions(currentAccount.getId(), 5);
            recentTransactionsList.getItems().setAll(transactions);

            // Update UI elements
            updatePieChart();
            loadRecentTransactions();
            handleWelcomeLabel();
        } else {
            balanceLabel.setText("Balance: - - -");
            pieChart.setTitle("No Account Logged In");
            placeholderCircle.setVisible(true);
            welcomeLabel.setText("Hi, Guest");
        }
    }

    /**
     * Updates the welcome label with the current user's name.
     */
    @FXML
    private void handleWelcomeLabel() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        welcomeLabel.setText(currentAccount != null ? "Hi, " + currentAccount.getUsername() : "Hi, Guest");
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
            
            // Make the placeholder circle invisible
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
        if (currentAccount != null) {
            List<Transaction> transactions = dataManager.getRecentTransactions(currentAccount.getId(), 5);
            recentTransactionsBox.getChildren().clear();
            
            for (Transaction transaction : transactions) {
                Label transactionLabel = new Label(String.format("%s: $%.2f - %s", 
                    transaction.getDescription(), 
                    transaction.getAmount(),
                    transaction.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))));
                recentTransactionsBox.getChildren().add(transactionLabel);
            }
        }
    }

    /**
     * Starts the date-time updater.
     */
    private void startDateTimeUpdater() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
                dateTimeLabel.setText(java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
            })
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Handles navigation between different application views.
     */
    @FXML private void handleDWButtonAction() { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/depwidth.fxml"));
            Parent root = loader.load();
            DepWidthController controller = loader.getController();
            Stage stage = (Stage) DWbutton.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to navigate to deposit/withdraw screen.");
        }
    }
    @FXML private void handleTransactionButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/transactions.fxml"));
            Parent root = loader.load();
            TransactionsController controller = loader.getController();
            Stage stage = (Stage) transactionsButton.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to navigate to transactions screen.");
        }
    }

    /**
     * Logs out the current user and returns to the start screen.
     */
    @FXML
    private void handleLogOutButtonAction() {
        try {
            CurrentSession.getInstance().logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/start.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage primaryStage = (Stage) backButton.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to log out.");
        }
    }

    /**
     * Switches the scene to a new FXML file.
     */
    private void switchScene(String fxmlFile, Button button) {
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

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
