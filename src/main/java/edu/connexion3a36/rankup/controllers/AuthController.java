package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.User;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.ui.components.RecaptchaCheckBox;
import edu.connexion3a36.rankup.utils.CredentialManager;
import edu.connexion3a36.rankup.utils.RecaptchaUtil;
import edu.connexion3a36.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AuthController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheckBox;

    @FXML
    private VBox recaptchaContainer;

    private RecaptchaCheckBox recaptchaCheckBox;
    private final UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeRecaptcha();
        loadRememberedCredentials();
    }

    private void initializeRecaptcha() {
        recaptchaCheckBox = new RecaptchaCheckBox();
        recaptchaContainer.getChildren().add(recaptchaCheckBox);
    }

    private void loadRememberedCredentials() {
        String[] credentials = CredentialManager.getCredentials();
        if (credentials != null && credentials.length == 2) {
            emailField.setText(credentials[0]);
            passwordField.setText(credentials[1]);
            rememberMeCheckBox.setSelected(true);
        }
    }

    @FXML
    void onSignIn(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            showError("Validation Error", "Email and password are required.");
            return;
        }

        if (!recaptchaCheckBox.isVerified()) {
            showError("Verification Failed", "Please verify that you are not a robot.");
            return;
        }

        try {
            User user = userService.authenticate(email, password);

            if (user != null) {
                if ("BANNED".equals(user.getStatus())) {
                    showError("Access Denied", "Your account has been banned.");
                    return;
                }

                CredentialManager.saveCredentials(email, password, rememberMeCheckBox.isSelected());

                RankUpApp.setCurrentPlayerName(user.getUsername());
                RankUpApp.setCurrentRole(user.getRole());
                RankUpApp.setCurrentUserId(user.getId());
                RankUpApp.setCurrentEmail(email);

                showSuccess("Sign In Successful", "Welcome, " + user.getUsername() + "!");
                RankUpApp.showBase();
                return;
            }

            showError("Sign In Failed", "Invalid email or password.");

        } catch (SQLException e) {
            showError("Database Error", "Error accessing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onForgotPassword(ActionEvent event) {
        // Step 1 - ask for email
        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Password Recovery");
        emailDialog.setHeaderText("Enter your email address");
        emailDialog.setContentText("Email:");

        Optional<String> emailResult = emailDialog.showAndWait();
        if (emailResult.isEmpty() || emailResult.get().isBlank()) return;

        String email = emailResult.get().trim();

        // Step 2 - check user exists (forgotPassword/OTP not yet implemented on this branch)
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                showError("Not Found", "No account is registered with that email address.");
                return;
            }
        } catch (SQLException e) {
            showError("Error", "Failed to find account: " + e.getMessage());
            return;
        }

        // TODO: wire up OTP email sending via BrevoMailService
        showInfo("Coming Soon", "Password reset via email is not yet available on this branch.");
    }

    @FXML
    void onSignUp(ActionEvent event) {
        try {
            RankUpApp.showRegister();
        } catch (Exception e) {
            showError("Navigation Error", "Could not open registration page.");
        }
    }

    public static void logout() {
        CredentialManager.clearCredentials();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}