package application;

import application.data.JsonDataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
                navigateTo("/application/home.fxml", loginButton);
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
        navigateTo("/application/start.fxml", backButton);
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

    private void navigateTo(String fxmlFile, Button button) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            Stage stage = (Stage) button.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Failed to navigate to the requested screen.");
        }
    }
}
