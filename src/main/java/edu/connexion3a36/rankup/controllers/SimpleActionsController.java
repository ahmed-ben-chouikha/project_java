package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class SimpleActionsController {

    @FXML
    void goBackMatches(ActionEvent event) {
        RankUpApp.loadInBase("/views/matches/matches.fxml");
    }

    @FXML
    void goBackTeams(ActionEvent event) {
        RankUpApp.loadInBase("/views/teams/teams.fxml");
    }

    @FXML
    void goBackTournaments(ActionEvent event) {
        RankUpApp.loadInBase("/views/tournaments/tournaments.fxml");
    }

    @FXML
    void onSave(ActionEvent event) {
        show("Placeholder", "Save handler placeholder.");
    }

    @FXML
    void onCancel(ActionEvent event) {
        show("Placeholder", "Cancel handler placeholder.");
    }

    @FXML
    void onEdit(ActionEvent event) {
        show("Placeholder", "Edit handler placeholder.");
    }

    @FXML
    void onInvite(ActionEvent event) {
        show("Placeholder", "Invite player handler placeholder.");
    }

    @FXML
    void onCreate(ActionEvent event) {
        show("Placeholder", "Create handler placeholder.");
    }

    private void show(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

