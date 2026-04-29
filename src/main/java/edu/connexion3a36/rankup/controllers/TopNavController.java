package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class TopNavController implements Initializable {

    @FXML
    private Label userStatusLabel;

    @FXML
    private MenuButton accountMenuButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateNavigation();
    }

    private void updateNavigation() {
        String username = RankUpApp.getCurrentPlayerName();
        String role = RankUpApp.getCurrentRole();

        // Update account button text to show username and role
        accountMenuButton.setText(username + " (" + role + ")");

        // Add admin-specific menu item if user is admin
        if (RankUpApp.isAdmin()) {
            // Check if Admin Panel item already exists
            boolean hasAdminPanel = accountMenuButton.getItems().stream()
                    .anyMatch(item -> item.getText() != null && item.getText().equals("Admin Panel"));

            if (!hasAdminPanel) {
                MenuItem adminPanelItem = new MenuItem("Admin Panel");
                adminPanelItem.setOnAction(this::onAdminPanel);
                accountMenuButton.getItems().add(0, adminPanelItem);

                MenuItem separatorItem = new MenuItem("-");
                separatorItem.setDisable(true);
                accountMenuButton.getItems().add(1, separatorItem);
            }
        }
    }

    @FXML
    void onMyProfile(ActionEvent event) {
        RankUpApp.loadInBase("/views/players/player-profile.fxml");
    }

    @FXML
    void onSettings(ActionEvent event) {
        showInfo("Settings", "Settings screen placeholder.");
    }

    @FXML
    void onAdminPanel(ActionEvent event) {
        if (RankUpApp.isAdmin()) {
            RankUpApp.loadInBase("/views/admin/admin-dashboard.fxml");
        } else {
            showError("Access Denied", "Only administrators can access the admin panel.");
        }
    }

    @FXML
    void onLogout(ActionEvent event) {
        RankUpApp.logout();
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
}

