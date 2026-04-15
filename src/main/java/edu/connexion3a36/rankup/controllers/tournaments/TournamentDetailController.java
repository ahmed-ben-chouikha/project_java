package edu.connexion3a36.rankup.controllers.tournaments;

import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TournamentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;

public class TournamentDetailController {

    @FXML private TextField tournamentNameField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField locationField;
    @FXML private TextField prizePoolField;
    @FXML private TextArea rulesField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label errorLabel;

    private TournamentService tournamentService;
    private Tournament currentTournament;
    private boolean isEditMode = false;

    @FXML
    void initialize() {
        tournamentService = new TournamentService();
        statusComboBox.getItems().addAll("pending", "ongoing", "finished");
        errorLabel.setStyle("-fx-text-fill: #ef4444;");
    }

    public void setTournament(Tournament tournament) {
        this.currentTournament = tournament;
        if (tournament != null) {
            isEditMode = true;
            tournamentNameField.setText(tournament.getName());
            descriptionField.setText(tournament.getDescription());
            startDatePicker.setValue(tournament.getStartDate());
            endDatePicker.setValue(tournament.getEndDate());
            locationField.setText(tournament.getLocation());
            prizePoolField.setText(String.valueOf(tournament.getPrizePool()));
            rulesField.setText(tournament.getRules());
            statusComboBox.setValue(tournament.getStatus());
        } else {
            isEditMode = false;
            clearForm();
        }
    }

    @FXML
    void onSave(ActionEvent event) {
        if (validateForm()) {
            try {
                Tournament tournament = new Tournament();
                tournament.setName(tournamentNameField.getText().trim());
                tournament.setDescription(descriptionField.getText().trim());
                tournament.setStartDate(startDatePicker.getValue());
                tournament.setEndDate(endDatePicker.getValue());
                tournament.setLocation(locationField.getText().trim());
                tournament.setPrizePool(Double.parseDouble(prizePoolField.getText().trim()));
                tournament.setRules(rulesField.getText().trim());
                tournament.setStatus(statusComboBox.getValue());

                if (isEditMode) {
                    tournament.setId(currentTournament.getId());
                    tournamentService.updateEntity(tournament.getId(), tournament);
                    showSuccess("Tournament updated successfully");
                } else {
                    tournamentService.addEntity(tournament);
                    showSuccess("Tournament created successfully");
                }

                RankUpApp.loadInBase("/views/tournaments/tournaments.fxml");
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            } catch (NumberFormatException e) {
                showError("Prize pool must be a valid number");
            }
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        RankUpApp.loadInBase("/views/tournaments/tournaments.fxml");
    }

    private boolean validateForm() {
        errorLabel.setText("");

        if (tournamentNameField.getText().trim().isEmpty()) {
            errorLabel.setText("Tournament name cannot be empty");
            return false;
        }

        if (startDatePicker.getValue() == null) {
            errorLabel.setText("Start date cannot be empty");
            return false;
        }

        if (endDatePicker.getValue() == null) {
            errorLabel.setText("End date cannot be empty");
            return false;
        }

        LocalDate today = LocalDate.now();
        if (startDatePicker.getValue().isBefore(today)) {
            errorLabel.setText("Start date cannot be in the past");
            return false;
        }

        if (!endDatePicker.getValue().isAfter(startDatePicker.getValue())) {
            errorLabel.setText("End date must be after start date");
            return false;
        }

        if (locationField.getText().trim().isEmpty()) {
            errorLabel.setText("Location cannot be empty");
            return false;
        }

        if (prizePoolField.getText().trim().isEmpty()) {
            errorLabel.setText("Prize pool cannot be empty");
            return false;
        }

        try {
            double prizePool = Double.parseDouble(prizePoolField.getText().trim());
            if (prizePool < 0) {
                errorLabel.setText("Prize pool must be a non-negative number");
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Prize pool must be a valid number");
            return false;
        }

        if (statusComboBox.getValue() == null || statusComboBox.getValue().isEmpty()) {
            errorLabel.setText("Status must be selected");
            return false;
        }

        return true;
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        tournamentNameField.clear();
        descriptionField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        locationField.clear();
        prizePoolField.clear();
        rulesField.clear();
        statusComboBox.setValue(null);
        errorLabel.setText("");
    }
}
