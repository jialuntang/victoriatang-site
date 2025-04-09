package application;

// Import necessary JavaFX classes
// Used to link FXML elements with the controller
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
// Class for button controls
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Alert.AlertType;
// Method to handle navigation
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import application.services.FinanceService;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

// Controller class for handling events from the start.fxml file
public class StartController {

    // Annotating loginButton to link with the Button element in the FXML file
    @FXML
    private Button loginButton;
    
    // Annotating guestButton to link with the Button element in the FXML file
    @FXML
    private Button guestButton;

    // Annotating exitButton to link with the Button element in the FXML file
    @FXML
    private Button exitButton;

    // Annotating aboutButton to link with the Button element in the FXML file
    @FXML
    private Button aboutButton;

    // Annotating createAccountLink to link with the Hyperlink element in the FXML file
    @FXML
    private Hyperlink createAccountLink;
    
    @FXML
    private VBox  loginBox;
    @FXML
    private VBox  createAccountBox;
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

    // Method to handle the action event for the loginButton
    @FXML
    private void handleLoginButtonAction() {
    	 navigateTo("/application/login.fxml");
    }
    
    // Method to handle the create account button
    @FXML
    private void handleCreateAccountLinkAction() {
        navigateTo("/application/create_account.fxml");
    }
    
    // Method to handle guest button
    @FXML
    private void handleGuestButtonAction() {
    	navigateTo("/application/home.fxml");
    }
    

    // Method to handle about button
    @FXML
    private void handleAboutButtonAction() {
    	navigateTo("/application/about.fxml");
    }
    
    // Method to handle the action event for the exitButton
    @FXML
    private void handleExitButtonAction() {
        
    	System.exit(0);
    	
    }
    
    
    // Method to navigate to inputted FXML file scenes
    private void navigateTo(String fxmlFile) {
        try {
        	// Load the FXML file for the inputted scene
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            
            // Create a new scene for the loaded content
            Scene scene = new Scene(root, 800, 600);
            
            // Link the CSS file to the scene
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            
            // Get the current state (Window)
            Stage primaryStage = (Stage) exitButton.getScene().getWindow();
            
            //Set the scene to the primary stage
            primaryStage.setScene(scene);
            
        } catch (Exception e) {
        	// print the stack trace if there is an exception
            e.printStackTrace();
            
            // show an alert error
            showAlert(AlertType.ERROR, "Error", "Unable to load the requested page.");
        }
    }
    
    
    // Method to show Alerts
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogin() {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        if (financeService.login(username, password)) {
            // Navigate to home screen
            Main.showHomeScreen();
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

