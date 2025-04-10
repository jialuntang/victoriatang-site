package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import application.Account;
import application.Database;

public class CreateAccountController {

    @FXML private Button backButton;
    @FXML private Button createButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

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

    @FXML
    private void handleCreateButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.ERROR, "Registration Failed", "All fields must be filled out.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Registration Failed", "Passwords do not match.");
            return;
        }

        try {
            // Check if the username already exists
            if (Database.userExists(username)) {
                showAlert(AlertType.ERROR, "Registration Failed", "Username already exists.");
                return;
            }

            // Add new user to the database
            Database.addUser(username, password);

            // Create account with initial balance and hourly wage
            double initialBalance = 0.0;
            double hourlyWage = 0.0;
            Database.addAccount(username, initialBalance, hourlyWage);

            // Create Account object and set it in CurrentSession
            Account newAccount = new Account(username, 0, initialBalance, hourlyWage);
            CurrentSession.getInstance().setCurrentAccount(newAccount);

            showAlert(AlertType.INFORMATION, "Registration Successful", "Account created successfully!");
            showHomeScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegisterButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            if (Database.getAccountByUsername(username) != null) {
                showAlert(AlertType.ERROR, "Registration Failed", "Username already exists.");
                return;
            }

            Database.addUser(username, password);
            showAlert(AlertType.INFORMATION, "Registration Successful", "User registered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Registration Failed", "An unexpected error occurred. Please try again.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/home.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the user
            HomeController controller = loader.getController();
            Account currentAccount = CurrentSession.getInstance().getCurrentAccount();
            if (currentAccount != null) {
                controller.setAccount(currentAccount);
            }
            
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            
            Stage primaryStage = (Stage) createButton.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to navigate to home screen: " + e.getMessage());
        }
    }
}

