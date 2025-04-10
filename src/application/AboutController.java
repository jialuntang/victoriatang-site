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

        		Your all-in-one personal finance companion.

	        			Features:
	
						    - Real-time balance updates to stay on top of your finances
						    - Visual charts to help track income and expenses clearly
						    - Manage and review your recent transactions at a glance
						    - Personalized and secure login for your privacy
						    - User-friendly interface with a clean and responsive design
						    - Lightweight and fast – optimized for smooth performance
	
						Coming soon:
						    - Budget planner
						    - Expense category analysis
						    - Cloud backup & sync across devices

				欢迎使用 MyWallet！
					
				您的一站式个人财务助手。
					
						功能亮点：
						
						    - 实时更新账户余额，轻松掌握财务状况
						    - 图表展示收支明细，一目了然
						    - 快速查看与管理交易记录
						    - 个性化安全登录，保障隐私
						    - 简洁直观的用户界面，操作流畅
						
						即将推出：
						    - 预算规划工具
						    - 收支分类分析
						    - 云端备份与多设备同步
           
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
