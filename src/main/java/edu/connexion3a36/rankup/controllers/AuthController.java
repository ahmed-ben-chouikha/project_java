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
        String email = emailField.getText();
        String password = passwordField.getText();
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showInfo("Validation", "Email and password are required.");
            return;
        }
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
}

