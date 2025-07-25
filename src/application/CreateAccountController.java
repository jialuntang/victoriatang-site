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

public class CreateAccountController {

    @FXML private Button backButton;
    @FXML private Button createButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private JsonDataManager dataManager = Main.getDataManager();

    @FXML
    private void handleBackButtonAction() {
        navigateTo("/application/start.fxml", backButton);
    }

    @FXML
    private void handleCreateButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "All fields must be filled out");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Error", "Passwords do not match");
            return;
        }

        try {
            if (dataManager.accountExists(username)) {
                showAlert(AlertType.ERROR, "Error", "Username already exists");
                return;
            }

            dataManager.addAccount(username, password);
            Account newAccount = dataManager.getAccountByUsername(username);
            if (newAccount != null) {
                CurrentSession.getInstance().setCurrentAccount(newAccount);
                showAlert(AlertType.INFORMATION, "Success", "Account created successfully");
                navigateTo("/application/home.fxml", createButton);
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to create account");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Failed to create account: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegisterButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            if (dataManager.accountExists(username)) {
                showAlert(AlertType.ERROR, "Registration Failed", "Username already exists.");
                return;
            }

            dataManager.addAccount(username, password);
            showAlert(AlertType.INFORMATION, "Registration Successful", "User registered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Registration Failed", "An unexpected error occurred. Please try again.");
        }
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

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

