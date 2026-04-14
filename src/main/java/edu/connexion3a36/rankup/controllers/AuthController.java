package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.entities.UserAccount;
import edu.connexion3a36.rankup.services.AuthService;
import edu.connexion3a36.rankup.session.SessionContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {

    private final AuthService authService = new AuthService();

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
        try {
            UserAccount user = authService.login(emailField.getText(), passwordField.getText());
            if (user == null) {
                showInfo("Login failed", "Invalid email or password.");
                return;
            }
            SessionContext.setCurrentUser(user);
            RankUpApp.showBase();
        } catch (Exception e) {
            showInfo("Login error", e.getMessage());
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
}

