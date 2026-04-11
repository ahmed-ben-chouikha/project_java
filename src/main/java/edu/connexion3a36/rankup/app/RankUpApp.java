package edu.connexion3a36.rankup.app;

import edu.connexion3a36.rankup.controllers.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class RankUpApp {

    private static Stage primaryStage;
    private static BaseController baseController;

    private RankUpApp() {
    }

    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("RankUp E-Sports Platform");
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(760);
    }

    public static void registerBaseController(BaseController controller) {
        baseController = controller;
    }

    public static void showLogin() {
        setRoot("/views/auth/login.fxml", 1100, 760);
    }

    public static void showBase() {
        setRoot("/views/base.fxml", 1400, 900);
    }

    public static void loadInBase(String viewPath) {
        if (baseController != null) {
            baseController.loadCenter(viewPath);
        }
    }

    public static void logout() {
        baseController = null;
        showLogin();
    }

    private static void setRoot(String fxml, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(RankUpApp.class.getResource(fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(RankUpApp.class.getResource("/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxml, e);
        }
    }
}

