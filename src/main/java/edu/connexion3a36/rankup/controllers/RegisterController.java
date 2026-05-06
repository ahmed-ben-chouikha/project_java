package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.User;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.UserService;
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
    public void onRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas", Alert.AlertType.ERROR);
            return;
        }

        if (password.length() < 6) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 6 caractères", Alert.AlertType.ERROR);
            return;
        }

        if (!email.contains("@")) {
            showAlert("Erreur", "Veuillez entrer une adresse email valide", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Vérifier si l'email existe déjà
            if (userService.emailExists(email)) {
                showAlert("Erreur", "Cet email est déjà utilisé", Alert.AlertType.ERROR);
                return;
            }

            // Créer le nouvel utilisateur
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole("player"); // Rôle par défaut
            user.setStatus("pending"); // Statut d'approbation par défaut

            if (userService.createUser(user)) {
                showAlert("Succès", "Compte créé avec succès! Veuillez vous connecter.", Alert.AlertType.INFORMATION);
                // Rediriger vers le login
                RankUpApp.showLogin();
            } else {
                showAlert("Erreur", "Erreur lors de la création du compte. Veuillez réessayer.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'enregistrement: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    public void onBackToLogin() {
        RankUpApp.showLogin();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
