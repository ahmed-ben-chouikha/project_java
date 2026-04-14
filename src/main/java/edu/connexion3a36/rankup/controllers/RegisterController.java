package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.services.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private final AuthService authService = new AuthService();

    @FXML
    void onRegister() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            showInfo("Validation", "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showInfo("Validation", "Passwords do not match.");
            return;
        }

        try {
            if (authService.registerPlayer(fullName, email, password)) {
                showInfo("Success", "Account created. You can now sign in.");
                RankUpApp.showLogin();
            } else {
                showInfo("Duplicate", "An account with this email already exists.");
            }
        } catch (Exception e) {
            showInfo("Error", "Registration failed: " + e.getMessage());
        }
    }

    @FXML
    void onBackToLogin() {
        RankUpApp.showLogin();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


