package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.User;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private final UserService userService = new UserService();

    @FXML
    void onRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            showError("Validation Error", "All fields are required.");
            return;
        }

        if (username.length() < 3) {
            showError("Validation Error", "Username must be at least 3 characters long.");
            return;
        }

        if (email.length() < 5 || !email.contains("@")) {
            showError("Validation Error", "Please enter a valid email address.");
            return;
        }

        if (password.length() < 6) {
            showError("Validation Error", "Password must be at least 6 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Validation Error", "Passwords do not match.");
            return;
        }

        try {
            // Check if email already exists
            if (userService.emailExists(email)) {
                showError("Registration Error", "An account with this email already exists.");
                return;
            }

            // Create new user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole("player"); // Default role for new users
            newUser.setStatus("pending"); // Pending approval by default
            newUser.setCreatedAt(java.time.LocalDateTime.now().toString());

            boolean success = userService.createUser(newUser);

            if (success) {
                showSuccess("Registration Successful", 
                    "Account created! Your account is pending approval.\n" +
                    "An administrator will review your account shortly.");
                
                // Clear fields
                usernameField.clear();
                emailField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
                
                // Navigate back to login
                RankUpApp.showLogin();
            } else {
                showError("Registration Failed", "Could not create account. Please try again.");
            }

        } catch (Exception e) {
            showError("Database Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onBackToLogin(ActionEvent event) {
        RankUpApp.showLogin();
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
