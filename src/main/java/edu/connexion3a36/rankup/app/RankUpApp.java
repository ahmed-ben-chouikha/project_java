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
    private static Integer pendingReclamationFocusId;
    private static Integer pendingAdminResponseFocusId;
    private static Integer pendingPunitionFocusId;

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

    public static void setCurrentPlayerName(String playerName) {
        SessionManager.setCurrentPlayerName(playerName);
    }

    public static String getCurrentPlayerName() {
        return SessionManager.getCurrentPlayerName();
    }

    public static void setCurrentRole(String role) {
        SessionManager.setCurrentRole(role);
    }

    public static String getCurrentRole() {
        return SessionManager.getCurrentRole();
    }

    public static void setCurrentEmail(String email) {
        SessionManager.setCurrentEmail(email);
    }

    public static String getCurrentEmail() {
        return SessionManager.getCurrentEmail();
    }

    public static void setCurrentUserId(int userId) {
        SessionManager.setCurrentUserId(userId);
    }

    public static int getCurrentUserId() {
        return SessionManager.getCurrentUserId();
    }

    public static boolean isAdmin() {
        return SessionManager.isAdmin();
    }

    public static void showLogin() {
        setRoot("/views/auth/login.fxml", 1100, 760);
    }

    public static void showRegister() {
        setRoot("/views/auth/register.fxml", 1100, 760);
    }

    public static void showBase() {
        setRoot("/views/base.fxml", 1400, 900);
    }

    public static void loadInBase(String viewPath) {
        if (baseController != null) {
            baseController.loadCenter(viewPath);
        }
    }

    public static void setPendingReclamationFocusId(Integer reclamationId) {
        pendingReclamationFocusId = reclamationId;
    }

    public static Integer consumePendingReclamationFocusId() {
        Integer id = pendingReclamationFocusId;
        pendingReclamationFocusId = null;
        return id;
    }

    public static void setPendingAdminResponseFocusId(Integer responseId) {
        pendingAdminResponseFocusId = responseId;
    }

    public static Integer consumePendingAdminResponseFocusId() {
        Integer id = pendingAdminResponseFocusId;
        pendingAdminResponseFocusId = null;
        return id;
    }

    public static void setPendingPunitionFocusId(Integer punitionId) {
        pendingPunitionFocusId = punitionId;
    }

    public static Integer consumePendingPunitionFocusId() {
        Integer id = pendingPunitionFocusId;
        pendingPunitionFocusId = null;
        return id;
    }

    public static void logout() {
        baseController = null;
        SessionManager.clear();
        showLogin();
    }

    private static void setRoot(String fxml, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(RankUpApp.class.getResource(fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            addStylesheetIfPresent(scene, "/styles.css");
            addStylesheetIfPresent(scene, "/styles/esports.css");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxml, e);
        }
    }

    private static void addStylesheetIfPresent(Scene scene, String path) {
        java.net.URL css = RankUpApp.class.getResource(path);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }
}
