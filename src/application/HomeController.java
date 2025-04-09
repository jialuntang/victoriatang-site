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

/**
 * Controller class for the Home screen in the JavaFX application.
 * Handles UI interactions, displays account details, and updates transaction data.
 */
public class HomeController {

    // UI Elements
    @FXML private Button backButton, DWbutton, transactionsButton, sendreceiveButton;
    @FXML private Label balanceLabel, dashboardLabel, welcomeLabel, dateTimeLabel, recentTransactionsLabel;
    @FXML private Circle placeholderCircle;
    @FXML private PieChart pieChart;
    @FXML private VBox recentTransactionsBox;

    /**
     * Initializes the Home screen.
     * Binds the account balance, updates UI elements, and starts the date-time updater.
     */
    public void initialize() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();

        if (currentAccount != null) {
            balanceLabel.textProperty().bind(currentAccount.balanceProperty().asString("Balance: $%.2f"));
            updatePieChart();
            loadRecentTransactions();
        } else {
            balanceLabel.setText("Balance: - - -");
            pieChart.setTitle("No Account Logged In");
            placeholderCircle.setVisible(true);
        }

        handleWelcomeLabel();
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
            try {
                double totalIncoming = Database.getTotalIncoming(currentAccount.getIDnum());
                double totalOutgoing = Database.getTotalOutgoing(currentAccount.getIDnum());

                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Incoming", totalIncoming),
                    new PieChart.Data("Outgoing", totalOutgoing)
                );
                pieChart.setData(pieChartData);
                pieChart.setTitle("Money Flow");
                placeholderCircle.setVisible(false);

                Platform.runLater(() -> {
                    for (PieChart.Data data : pieChartData) {
                        Node slice = data.getNode();
                        if (slice != null) {
                            slice.setStyle("-fx-pie-color: " + (data.getName().equals("Incoming") ? "green;" : "red;"));
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            pieChart.setData(FXCollections.observableArrayList());
            pieChart.setTitle("No Account Logged In");
            placeholderCircle.setVisible(true);
        }
    }

    /**
     * Starts a timeline to update the date-time label every second.
     */
    private void startDateTimeUpdater() {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event ->
            dateTimeLabel.setText(LocalDateTime.now().format(dateTimeFormat))
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Loads and displays the most recent transactions.
     */
    public void loadRecentTransactions() {
        Account currentAccount = CurrentSession.getInstance().getCurrentAccount();

        if (currentAccount != null) {
            try {
                // Fetch recent transactions from the database
                List<Transaction> transactions = Database.getRecentTransactions(currentAccount.getIDnum(), 3);

                // Clear previous entries
                recentTransactionsBox.getChildren().clear();

                // Display the most recent transactions
                for (Transaction transaction : transactions) {
                    // Create a styled VBox for each transaction
                    VBox transactionBox = new VBox();
                    transactionBox.setSpacing(1);
                    transactionBox.setStyle("-fx-background-color: #3E3E3E; -fx-padding: 1; -fx-border-color: #666666; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
                    transactionBox.setPrefWidth(280);
                    transactionBox.setMaxHeight(80);

                    // Add details to the transaction box
                    Label senderLabel = new Label("Sender: " + transaction.getSender());
                    Label amountLabel = new Label("Amount: $" + String.format("%.2f", transaction.getAmount()));
                    Label descriptionLabel = new Label("Description: " + transaction.getDescription());

                    // Style each label
                    String labelStyle = "-fx-text-fill: #FFFFFF; -fx-font-size: 12px;";
                    senderLabel.setStyle(labelStyle);
                    amountLabel.setStyle(labelStyle);
                    descriptionLabel.setStyle(labelStyle);
                    descriptionLabel.setWrapText(true);

                    // Add labels to the transaction box
                    transactionBox.getChildren().addAll(senderLabel, amountLabel, descriptionLabel);

                    // Add the transaction box to the recentTransactionsBox
                    recentTransactionsBox.getChildren().add(transactionBox);
                }
            } catch (Exception e) {
                e.printStackTrace();
                recentTransactionsBox.getChildren().clear();
                Label errorLabel = new Label("Error loading transactions.");
                errorLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14px;");
                recentTransactionsBox.getChildren().add(errorLabel);
            }
        } else {
            // If no account is logged in, display a message
            recentTransactionsBox.getChildren().clear();
            Label noTransactionsLabel = new Label("No recent transactions available.");
            noTransactionsLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14px;");
            recentTransactionsBox.getChildren().add(noTransactionsLabel);
        }
    }

    /**
     * Handles navigation between different application views.
     */
    @FXML private void handleDWButtonAction() { switchScene("depwidth.fxml", DWbutton); }
    @FXML private void handleSendReceiveButtonAction() { switchScene("sendreceive.fxml", sendreceiveButton); }
    @FXML private void handleTransactionButtonAction() { switchScene("transactions.fxml", transactionsButton); }

    /**
     * Logs out the current user and returns to the start screen.
     */
    @FXML
    private void handleLogOutButtonAction() {
        CurrentSession.getInstance().setCurrentAccount(null);
        switchScene("start.fxml", backButton);
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
}
