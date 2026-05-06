package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.User;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.UserService;
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

    private final UserService userService = new UserService();

    @FXML
    void onSignIn(ActionEvent event) {
        if (emailField.getText().isBlank() || passwordField.getText().isBlank()) {
            showError("Validation", "Email and password are required.");
            return;
        }

        try {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            // Authenticate against database
            User user = userService.authenticate(email, password);

            if (user != null) {
                // Store user info in session
                RankUpApp.setCurrentPlayerName(user.getUsername());
                RankUpApp.setCurrentRole(user.getRole());
                RankUpApp.setCurrentUserId(user.getId());
                RankUpApp.setCurrentEmail(email);

                showSuccess("Success", "Welcome " + user.getUsername() + "!");
                RankUpApp.showBase();
            } else {
                showError("Authentication Failed", "Invalid email or password.");
            }
        } catch (Exception e) {
            showError("Database Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onForgotPassword(ActionEvent event) {
        showInfo("Placeholder", "Forgot password flow will be connected later.");
    }

    @FXML
    void onSignUp(ActionEvent event) {
        RankUpApp.showRegister();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

