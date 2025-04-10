package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import application.data.JsonDataManager;

/**
 * Controller class for handling user login in the JavaFX application.
 */
public class LoginController {

    // UI Elements
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button backButton;

    private JsonDataManager dataManager = Main.getDataManager();

    /**
     * Handles the login button action.
     * Retrieves the entered username and password, validates the credentials,
     * and navigates to the home screen if successful.
     */
    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Login Failed", "Please enter both username and password.");
            return;
        }

        if (!dataManager.accountExists(username)) {
            showAlert(AlertType.ERROR, "Login Failed", "Username does not exist.");
            return;
        }

        if (dataManager.validateAccount(username, password)) {
            Account account = dataManager.getAccountByUsername(username);
            if (account != null) {
                CurrentSession.getInstance().setCurrentAccount(account);
                showHomeScreen();
            } else {
                showAlert(AlertType.ERROR, "Login Failed", "Could not retrieve account information.");
            }
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Invalid password.");
        }
    }

    /**
     * Handles the back button action.
     * Navigates back to the start screen.
     */
    @FXML
    private void handleBackButtonAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/start.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage primaryStage = (Stage) backButton.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Loads and displays the home screen after successful login.
     */
    private void showHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/home.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage primaryStage = (Stage) loginButton.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
