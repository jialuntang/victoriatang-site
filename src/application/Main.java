package application;

import application.data.DataManager;
import application.data.JsonDataManager;
import application.services.FinanceService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;
    private static FinanceService financeService;

    @Override
    public void start(Stage primaryStage) {
        try {   
            Main.primaryStage = primaryStage;
            Database.initializeDatabase();
            showStartScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showStartScreen() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("start.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
            primaryStage.setTitle("MyWallet");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showHomeScreen() {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("home.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
            primaryStage.setTitle("MyWallet - Home");
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Initialize the data manager and finance service
        DataManager dataManager = new JsonDataManager();
        financeService = new FinanceService(dataManager);

        // Platform-specific initialization
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("prism.order", "sw");
            System.setProperty("javafx.animation.fullspeed", "true");
            System.setProperty("javafx.animation.framerate", "60");
        }

        // Launch the JavaFX application
        try {
            Application.launch(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static FinanceService getFinanceService() {
        return financeService;
    }
}

