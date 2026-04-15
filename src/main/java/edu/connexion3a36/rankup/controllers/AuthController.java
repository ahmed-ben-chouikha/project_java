package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    void onSignIn(ActionEvent event) {
        if (emailField.getText().isBlank() || passwordField.getText().isBlank()) {
            showInfo("Validation", "Email and password are required.");
            return;
        }
        String email = emailField.getText().trim();
        RankUpApp.setCurrentPlayerName(extractPlayerName(email));
        RankUpApp.setCurrentRole(inferRole(email));
        RankUpApp.showBase();
    }

    @FXML
    void onForgotPassword(ActionEvent event) {
        showInfo("Placeholder", "Forgot password flow will be connected later.");
    }

    @FXML
    void onSignUp(ActionEvent event) {
        showInfo("Placeholder", "Sign up flow will be connected later.");
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String extractPlayerName(String email) {
        if (email == null || email.isBlank()) {
            return "DefaultPlayer";
        }
        String normalized = email.trim();
        int at = normalized.indexOf('@');
        return at > 0 ? normalized.substring(0, at) : normalized;
    }

    private String inferRole(String email) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        return normalized.startsWith("admin") || normalized.contains("admin") ? "admin" : "player";
    }
}

