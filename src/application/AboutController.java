package application;


// Import necessary JavaFX classes
// Used to link FXML elements with the controller
import javafx.fxml.FXML;
// Class for button controls
import javafx.scene.control.Button;
// Method to handle navigation
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;


// Controller class for handling events from the about.fxml file
public class AboutController {

    // Annotating backButton to link with the Button element in the FXML file
    @FXML
    private Button backButton;
    
    // Text area for the description of the app in the about page
    @FXML
    private TextArea aboutText;

    @FXML
    private void initialize() {
        aboutText.setText("""
            
        		Welcome to MyWallet!

        		Features:

        			- Real-time balance updates

        			- Track income and expenses visually

        			- Manage recent transactions
   

        			- Secure, personalized login

        			- Send and receive money easily
        			
        		欢迎使用 MyWallet！

        		功能特色：

        			- 实时余额更新

        			- 可视化追踪收入与支出

        			- 管理近期交易记录

        			- 安全个性化登录

        			- 便捷的收付款功能
            
            """);
    }
    
    // Method to handle the action event for the backButton
    @FXML
    private void handleBackButtonAction() {
        try {
            // Load the FXML file for the start screen
            Parent root = FXMLLoader.load(getClass().getResource("/application/start.fxml"));
            // Create a new scene with the loaded FXML content
            Scene scene = new Scene(root, 800, 600);
            // Link the CSS file to the scene
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            // Get the current stage (window)
            Stage primaryStage = (Stage) backButton.getScene().getWindow();
            // Set the scene to the primary stage
            primaryStage.setScene(scene);
        } catch (Exception e) {
            // Print the stack trace if there is an exception
            e.printStackTrace();
        }
    }
}
