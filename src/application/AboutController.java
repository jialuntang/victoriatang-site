package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller class for handling the About screen of the application.
 */
public class AboutController {

    // Back button linked from the FXML
    @FXML
    private Button backButton;

    // Text area displaying the application's description
    @FXML
    private TextArea aboutText;

    /**
     * Initializes the content of the About screen.
     */
    @FXML
    private void initialize() {
        aboutText.setText("""
            Welcome to MyWallet!

            Your all-in-one personal finance companion.

            Features:
            - Real-time balance updates
            - Visual charts for income and expenses
            - Transaction history
            - Secure login
            - User-friendly interface
            - Fast performance

            Coming soon:
            - Budget planner
            - Expense analysis
            - Cloud backup
            """);
    }

    /**
     * Handles the action triggered by the Back button.
     * Navigates the user back to the start screen.
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
}

