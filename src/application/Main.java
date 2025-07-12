package application;

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
    private static JsonDataManager dataManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        showStartScreen();
    }

 public static void showStartScreen() {
    try {
        Parent root = FXMLLoader.load(Main.class.getResource("/application/start.fxml"));
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Main.class.getResource("/application/styles.css").toExternalForm());
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
            Parent root = FXMLLoader.load(Main.class.getResource("/application/home.fxml"));
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(Main.class.getResource("/application/styles.css").toExternalForm());
            primaryStage.setTitle("MyWallet - Home");
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // macOS 调优（非必要，但建议）
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("prism.order", "sw");
            System.setProperty("javafx.animation.fullspeed", "true");
            System.setProperty("javafx.animation.framerate", "60");
        }

        launch(args);
    }

    public static FinanceService getFinanceService() {
        return financeService;
    }

    public static JsonDataManager getDataManager() {
        return dataManager;
    }
}
