package edu.connexion3a36.rankup.controllers.matches;

import edu.connexion3a36.entities.Match;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.MatchService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MatchFormController {

    @FXML private Label titleLabel;
    @FXML private ComboBox<MatchService.TeamOption> team1Combo;
    @FXML private ComboBox<MatchService.TeamOption> team2Combo;
    @FXML private DatePicker matchDatePicker;
    @FXML private TextField matchTimeField;
    @FXML private TextField score1Field;
    @FXML private TextField score2Field;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField tournamentIdField;
    @FXML private Label feedbackLabel;

    private final MatchService matchService = new MatchService();
    private Match editingMatch;

    @FXML
    void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList("pending", "ongoing", "finished"));
        statusCombo.setValue("pending");

        editingMatch = MatchFormState.getEditingMatch();
        loadTeams();

        if (editingMatch != null) {
            titleLabel.setText("Edit Match");
            bindMatchToForm(editingMatch);
        } else {
            titleLabel.setText("Create Match");
            matchDatePicker.setValue(LocalDate.now());
            matchTimeField.setText("18:00:00");
            score1Field.setText("0");
            score2Field.setText("0");
        }
    }

    @FXML
    void onSave(ActionEvent event) {
        MatchService.TeamOption team1 = team1Combo.getValue();
        MatchService.TeamOption team2 = team2Combo.getValue();

        if (team1 == null || team2 == null) {
            feedbackLabel.setText("Please select both teams.");
            return;
        }

        if (team1.getId() == team2.getId()) {
            feedbackLabel.setText("Team 1 and Team 2 must be different.");
            return;
        }

        int score1 = parseIntSafe(score1Field.getText());
        int score2 = parseIntSafe(score2Field.getText());
        int tournamentId = parseIntSafe(tournamentIdField.getText());

        LocalDate date = matchDatePicker.getValue() == null ? LocalDate.now() : matchDatePicker.getValue();
        LocalTime time = parseTime(matchTimeField.getText());
        String matchDateTime = LocalDateTime.of(date, time).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String status = statusCombo.getValue() == null ? "pending" : statusCombo.getValue();

        Match payload = new Match(
                editingMatch == null ? -1 : editingMatch.getId(),
                team1.getId(),
                team2.getId(),
                tournamentId,
                score1,
                score2,
                matchDateTime,
                team1.getLabel(),
                team2.getLabel(),
                status
        );

        try {
            if (editingMatch == null) {
                matchService.createMatch(payload);
            } else {
                matchService.updateMatch(payload);
            }
            MatchFormState.clear();
            RankUpApp.loadInBase("/views/matches/matches.fxml");
        } catch (SQLException e) {
            feedbackLabel.setText("Could not save match: " + e.getMessage());
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        MatchFormState.clear();
        RankUpApp.loadInBase("/views/matches/matches.fxml");
    }

    private void loadTeams() {
        try {
            List<MatchService.TeamOption> teams = matchService.getTeamOptions();
            team1Combo.setItems(FXCollections.observableArrayList(teams));
            team2Combo.setItems(FXCollections.observableArrayList(teams));
        } catch (SQLException e) {
            feedbackLabel.setText("Could not load teams: " + e.getMessage());
        }
    }

    private void bindMatchToForm(Match match) {
        LocalDateTime parsed = parseDateTime(match.getMatchDate());
        matchDatePicker.setValue(parsed.toLocalDate());
        matchTimeField.setText(parsed.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        score1Field.setText(String.valueOf(match.getScore1()));
        score2Field.setText(String.valueOf(match.getScore2()));
        statusCombo.setValue(match.getStatus() == null ? "pending" : match.getStatus().toLowerCase());
        if (match.getTournamentId() > 0) {
            tournamentIdField.setText(String.valueOf(match.getTournamentId()));
        }

        selectTeamById(team1Combo, match.getTeam1Id());
        selectTeamById(team2Combo, match.getTeam2Id());
    }

    private void selectTeamById(ComboBox<MatchService.TeamOption> combo, int teamId) {
        for (MatchService.TeamOption option : combo.getItems()) {
            if (option.getId() == teamId) {
                combo.setValue(option);
                return;
            }
        }
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return LocalDateTime.now();
        }

        DateTimeFormatter[] formats = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        };

        for (DateTimeFormatter formatter : formats) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }

        return LocalDateTime.now();
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return LocalTime.of(0, 0, 0);
        }

        DateTimeFormatter[] formats = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("HH:mm:ss"),
                DateTimeFormatter.ofPattern("HH:mm")
        };

        for (DateTimeFormatter formatter : formats) {
            try {
                return LocalTime.parse(value.trim(), formatter);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }

        return LocalTime.of(0, 0, 0);
    }

    private int parseIntSafe(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

