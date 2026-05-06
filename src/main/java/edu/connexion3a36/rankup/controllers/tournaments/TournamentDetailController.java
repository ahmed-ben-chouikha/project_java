package edu.connexion3a36.rankup.controllers.tournaments;

import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TournamentService;
import edu.connexion3a36.tools.ValidationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class TournamentDetailController {

    @FXML private Label titleLabel;
    @FXML private TextField tournamentNameField;
    @FXML private TextField gameTypeField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField maxTeamsField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionField;
    @FXML private TextField locationField;
    @FXML private TextField prizePoolField;
    @FXML private TextArea rulesField;
    @FXML private Label errorLabel;

    private TournamentService tournamentService;
    private Tournament currentTournament;
    private boolean isEditMode = false;

    @FXML
    void initialize() {
        tournamentService = new TournamentService();
        statusComboBox.getItems().addAll("open", "closed", "finished");
        errorLabel.setStyle("-fx-text-fill: #ef4444;");

        Tournament stateTournament = TournamentFormState.getEditingTournament();
        if (stateTournament != null) {
            setTournament(stateTournament);
        } else {
            isEditMode = false;
            titleLabel.setText("Create Tournament");
            clearForm();
        }
    }

    public void setTournament(Tournament tournament) {
        this.currentTournament = tournament;
        if (tournament != null) {
            isEditMode = true;
            titleLabel.setText("Edit Tournament");
            tournamentNameField.setText(tournament.getName());
            gameTypeField.setText(tournament.getGameType());
            startDatePicker.setValue(tournament.getStartDate());
            endDatePicker.setValue(tournament.getEndDate());
            maxTeamsField.setText(String.valueOf(tournament.getMaxTeams() <= 0 ? 8 : tournament.getMaxTeams()));
            statusComboBox.setValue(tournament.getStatus() == null || tournament.getStatus().isBlank() ? "open" : tournament.getStatus());
            descriptionField.setText(tournament.getDescription() == null ? "" : tournament.getDescription());
            locationField.setText(tournament.getLocation());
            prizePoolField.setText(String.valueOf(tournament.getPrizePool()));
            rulesField.setText(tournament.getRules());
        } else {
            isEditMode = false;
            clearForm();
        }
    }

    @FXML
    void onSave(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        try {
            Tournament tournament = currentTournament != null ? currentTournament : new Tournament();
            String rules = normalizeRules(rulesField.getText());
            tournament.setName(safeText(tournamentNameField.getText()));
            tournament.setGameType(safeText(gameTypeField.getText()));
            tournament.setStartDate(startDatePicker.getValue());
            tournament.setEndDate(endDatePicker.getValue());
            tournament.setMaxTeams(parseIntSafe(maxTeamsField.getText()));
            tournament.setStatus(statusComboBox.getValue());
            tournament.setDescription(safeText(descriptionField.getText()));
            tournament.setLocation(safeText(locationField.getText()));
            tournament.setPrizePool(parseDoubleSafe(prizePoolField.getText()));
            tournament.setRules(rules);

            if (isEditMode && currentTournament != null) {
                tournament.setId(currentTournament.getId());
                tournamentService.updateEntity(tournament.getId(), tournament);
                showSuccess("Tournament updated successfully");
            } else {
                tournamentService.addEntity(tournament);
                showSuccess("Tournament created successfully");
            }

            TournamentFormState.clear();
            RankUpApp.loadInBase("/views/tournaments/tournaments.fxml");
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Numeric fields must contain valid numbers");
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        RankUpApp.loadInBase("/views/tournaments/tournaments.fxml");
    }

    private boolean validateForm() {
        errorLabel.setText("");
        try {
            double prizePool = parseDoubleSafe(prizePoolField.getText());
            String rules = normalizeRules(rulesField.getText());
            String validation = ValidationUtil.validateTournament(
                    safeText(tournamentNameField.getText()),
                    safeText(gameTypeField.getText()),
                    startDatePicker.getValue(),
                    endDatePicker.getValue(),
                    statusComboBox.getValue(),
                    parseIntSafe(maxTeamsField.getText()),
                    safeText(descriptionField.getText()),
                    safeText(locationField.getText()),
                    rules,
                    prizePool
            );
            if (!validation.isEmpty()) {
                errorLabel.setText(validation);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            errorLabel.setText("Max teams and prize pool must be numeric.");
            return false;
        }
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
        gameTypeField.clear();
        descriptionField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        maxTeamsField.setText("8");
        statusComboBox.setValue("open");
        locationField.clear();
        prizePoolField.clear();
        rulesField.clear();
        errorLabel.setText("");
    }

    private int parseIntSafe(String value) {
        return Integer.parseInt(value == null || value.trim().isEmpty() ? "0" : value.trim());
    }

    private double parseDoubleSafe(String value) {
        return Double.parseDouble(value == null || value.trim().isEmpty() ? "0" : value.trim());
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeRules(String value) {
        String normalized = safeText(value);
        return normalized.isEmpty() ? "standard" : normalized;
    }
}
