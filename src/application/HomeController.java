package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.scene.chart.PieChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.io.IOException;

import application.Account;
import application.CurrentSession;
import application.Database;
import application.model.Transaction;
import application.model.User;

/**
 * Controller class for the Home screen in the JavaFX application.
 * Handles UI interactions, displays account details, and updates transaction data.
 */
public class HomeController {

    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    // UI Elements
    @FXML private Button backButton, DWbutton, transactionsButton;
    @FXML private Label balanceLabel, dashboardLabel, welcomeLabel, dateTimeLabel, recentTransactionsLabel;
    @FXML private Circle placeholderCircle;
    @FXML private PieChart pieChart;
    @FXML private VBox recentTransactionsBox;

    /**
     * Initializes the Home screen.
     * Binds the account balance, updates UI elements, and starts the date-time updater.
     */
    @FXML
    public void initialize() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();

        if (currentAccount != null) {
            // Update balance from database
            double currentBalance = Database.getAccountBalance(currentAccount.getUsername());
            currentAccount.setBalance(currentBalance);
            
            // Bind balance label to account balance
            balanceLabel.textProperty().bind(currentAccount.balanceProperty().asString("Balance: $%.2f"));
            
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

        startDateTimeUpdater();
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
            double incoming = Database.getTotalIncoming(currentAccount.getIDnum());
            double outgoing = Database.getTotalOutgoing(currentAccount.getIDnum());
            
            pieChart.getData().clear();
            pieChart.getData().add(new PieChart.Data("Incoming", incoming));
            pieChart.getData().add(new PieChart.Data("Outgoing", outgoing));
            pieChart.setTitle("Transaction Overview");
            placeholderCircle.setVisible(false);
        }
    }

    /**
     * Loads recent transactions into the transactions box.
     */
    private void loadRecentTransactions() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
        if (currentAccount != null) {
            List<Transaction> transactions = Database.getRecentTransactions(currentAccount.getIDnum(), 5);
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
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage primaryStage = (Stage) DWbutton.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to navigate to deposit/withdraw screen.");
        }
    }
    @FXML private void handleTransactionButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/transactions.fxml"));
            Parent root = loader.load();
            TransactionsController controller = loader.getController();
            controller.setUser(user);
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
