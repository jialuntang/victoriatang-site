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
        System.out.println("Starting application");
        System.out.println(Main.class.getResource("start.fxml"));

        try {   
            Main.primaryStage = primaryStage;
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
            primaryStage.setTitle("Finance App");
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
            primaryStage.setTitle("Finance App - Home");
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Set a global uncaught exception handler
//        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
//            System.err.println("Uncaught exception in thread " + thread.getName());
//            throwable.printStackTrace();
//        });
        System.out.println("Starting application");
        System.out.println(Main.class.getResource("start.fxml"));


        // Initialize the data manager and finance service
        DataManager dataManager = new JsonDataManager();
        financeService = new FinanceService(dataManager);

        // Add some test users and accounts for demonstration
        financeService.createUser("john_doe", "password123");
        financeService.createUser("jane_smith", "mypassword");

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
            System.err.println("Failed to launch JavaFX application");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static FinanceService getFinanceService() {
        return financeService;
    }
}

