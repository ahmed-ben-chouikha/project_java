package edu.connexion3a36.rankup.controllers.tournaments;

import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.entities.TournamentRegistration;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TournamentRegistrationService;
import edu.connexion3a36.services.TournamentService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class RegistrationFormController {

    @FXML private TextField playerNameField;
    @FXML private TextField teamNameField;
    @FXML private ComboBox<TournamentComboItem> tournamentComboBox;
    @FXML private Label errorLabel;

    private TournamentService tournamentService;
    private TournamentRegistrationService registrationService;

    @FXML
    void initialize() {
        tournamentService = new TournamentService();
        registrationService = new TournamentRegistrationService();
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        loadOpenTournaments();
    }

    private void loadOpenTournaments() {
        try {
            List<Tournament> openTournaments = tournamentService.getOpenTournaments();
            tournamentComboBox.setItems(FXCollections.observableArrayList(
                openTournaments.stream()
                    .map(t -> new TournamentComboItem(t.getId(), t.getName()))
                    .toArray(TournamentComboItem[]::new)
            ));
        } catch (SQLException e) {
            showError("Failed to load tournaments: " + e.getMessage());
        }
    }

    @FXML
    void onRegister(ActionEvent event) {
        if (validateForm()) {
            try {
                TournamentRegistration registration = new TournamentRegistration();
                registration.setPlayerName(playerNameField.getText().trim());
                registration.setTeamName(teamNameField.getText().trim());
                registration.setTournamentId(tournamentComboBox.getValue().getId());
                registration.setStatus("pending");

                registrationService.addEntity(registration);
                showSuccess("Registration successful!");
                RankUpApp.loadInBase("/views/tournaments/tournament-registrations.fxml");
            } catch (SQLException e) {
                showError(e.getMessage());
            }
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        RankUpApp.loadInBase("/views/tournaments/tournament-registrations.fxml");
    }

    private boolean validateForm() {
        errorLabel.setText("");

        if (playerNameField.getText().trim().isEmpty()) {
            errorLabel.setText("Player name cannot be empty");
            return false;
        }

        if (teamNameField.getText().trim().isEmpty()) {
            errorLabel.setText("Team name cannot be empty");
            return false;
        }

        if (tournamentComboBox.getValue() == null) {
            errorLabel.setText("Please select a tournament");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class TournamentComboItem {
        private final int id;
        private final String name;

        public TournamentComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }

        @Override
        public String toString() { return name; }
    }
}
