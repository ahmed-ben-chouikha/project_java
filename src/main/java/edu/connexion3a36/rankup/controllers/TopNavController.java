package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class TopNavController {

    @FXML
    void onMyProfile(ActionEvent event) {
        RankUpApp.loadInBase("/views/players/player-profile.fxml");
    }

    @FXML
    void onSettings(ActionEvent event) {
        showInfo("Settings", "Settings screen placeholder.");
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
}

