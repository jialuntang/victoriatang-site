package application;

import application.services.FinanceService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class StartController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button loginButton;
    @FXML
    private Button guestButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button aboutButton;
    @FXML
    private Hyperlink createAccountLink;
    @FXML
    private VBox loginBox;
    @FXML
    private VBox createAccountBox;
    @FXML
    private TextField loginUsername;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private TextField createUsername;
    @FXML
    private PasswordField createPassword;
    @FXML
    private Label errorLabel;

    private final FinanceService financeService = Main.getFinanceService();

 @Override
public void initialize(URL location, ResourceBundle resources) {
    Timeline timeline = new Timeline(
        new KeyFrame(Duration.seconds(0), e -> rootPane.setStyle("-fx-background-color: linear-gradient(#ff9a9e, #fad0c4);")),
        new KeyFrame(Duration.seconds(5), e -> rootPane.setStyle("-fx-background-color: linear-gradient(#a18cd1, #fbc2eb);")),
        new KeyFrame(Duration.seconds(10), e -> rootPane.setStyle("-fx-background-color: linear-gradient(#fad0c4, #ffd1ff);"))
    );
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
}

    @FXML
    private void handleLoginButtonAction() {
        navigateTo("/application/login.fxml");
    }

    @FXML
    private void handleCreateAccountLinkAction() {
        navigateTo("/application/create_account.fxml");
    }

    @FXML
    private void handleGuestButtonAction() {
        navigateTo("/application/home.fxml");
    }

    @FXML
    private void handleAboutButtonAction() {
        navigateTo("/application/about.fxml");
    }

    @FXML
    private void handleExitButtonAction() {
        System.exit(0);
    }

    private void navigateTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage primaryStage = (Stage) exitButton.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load the requested page.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        if (financeService.login(username, password)) {
            CurrentSession.getInstance().setCurrentAccount(financeService.getCurrentAccount());
            navigateTo("/application/home.fxml");
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    @FXML
    private void showLoginBox() {
        loginBox.setVisible(true);
        createAccountBox.setVisible(false);
        guestButton.setVisible(true);
        aboutButton.setVisible(true);
        exitButton.setVisible(true);
        errorLabel.setText("");
    }
}
