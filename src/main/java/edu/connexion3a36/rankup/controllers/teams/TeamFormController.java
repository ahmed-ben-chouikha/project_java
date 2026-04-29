package edu.connexion3a36.rankup.controllers.teams;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TeamService;
import edu.connexion3a36.tools.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class TeamFormController {

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField countryField;
    @FXML private TextArea descriptionArea;
    @FXML private TextArea detailedDescriptionArea;
    @FXML private TextField logoField;
    @FXML private ComboBox<String> gameCombo;
    @FXML private ComboBox<String> levelCombo;
    @FXML private TextField scoreField;
    @FXML private Label feedbackLabel;

    private final TeamService teamService = new TeamService();
    private Team editingTeam;

    @FXML
    void initialize() {
        gameCombo.setItems(FXCollections.observableArrayList("League of Legends", "Valorant", "CS2", "Dota 2", "Rocket League", "Other"));
        levelCombo.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Pro"));

        editingTeam = TeamFormState.getEditingTeam();
        if (editingTeam != null) {
            titleLabel.setText("Edit Team");
            bind(editingTeam);
        } else {
            titleLabel.setText("Create Team");
            scoreField.setText("0");
        }
    }

    @FXML
    void onSave(ActionEvent event) {
        feedbackLabel.setText("");

        String name = safe(nameField.getText());
        String country = safe(countryField.getText());
        String game = gameCombo.getValue();
        String level = levelCombo.getValue();
        String description = safe(descriptionArea.getText());
        String detailedDescription = safe(detailedDescriptionArea.getText());
        String logo = safe(logoField.getText());

        Integer score = parseScore(scoreField.getText());
        if (score == null) {
            return;
        }

        String validation = ValidationUtil.validateTeam(name, country, game, level, description, detailedDescription, score);
        if (!validation.isEmpty()) {
            feedbackLabel.setText(validation);
            return;
        }

        if (editingTeam == null && teamService.teamNameExists(name)) {
            feedbackLabel.setText("Ce nom d'équipe existe déjà. Veuillez choisir un autre nom.");
            return;
        }

        if (editingTeam != null && teamService.teamNameExistsForAnotherId(name, editingTeam.getId())) {
            feedbackLabel.setText("Ce nom d'équipe existe déjà. Veuillez choisir un autre nom.");
            return;
        }

        Team payload = editingTeam == null ? new Team() : editingTeam;
        payload.setName(name);
        payload.setCountry(country);
        payload.setDescription(description);
        payload.setDetailedDescription(detailedDescription);
        payload.setLogo(logo);
        payload.setJeu(game);
        payload.setNiveau(level);
        payload.setStatut("en attente");
        payload.setScore(score);

        boolean ok = editingTeam == null ? teamService.addTeam(payload) : teamService.updateTeam(payload);
        if (!ok) {
            feedbackLabel.setText("Impossible d'enregistrer l'équipe. Vérifiez les données ou la base.");
            return;
        }

        TeamFormState.clear();
        RankUpApp.loadInBase("/views/teams/teams.fxml");
    }

    @FXML
    void onCancel(ActionEvent event) {
        TeamFormState.clear();
        RankUpApp.loadInBase("/views/teams/teams.fxml");
    }

    private void bind(Team team) {
        nameField.setText(team.getName());
        countryField.setText(team.getCountry());
        descriptionArea.setText(safe(team.getDescription()));
        detailedDescriptionArea.setText(safe(team.getDetailedDescription()));
        logoField.setText(safe(team.getLogo()));
        gameCombo.setValue(team.getJeu());
        levelCombo.setValue(team.getNiveau());
        scoreField.setText(String.valueOf(team.getScore()));
    }

    private Integer parseScore(String value) {
        String normalized = safe(value);
        if (normalized.isEmpty()) {
            return 0;
        }
        try {
            int score = Integer.parseInt(normalized);
            if (score < 0) {
                feedbackLabel.setText("Score must be 0 or greater.");
                return null;
            }
            return score;
        } catch (NumberFormatException e) {
            feedbackLabel.setText("Score must be a valid integer.");
            return null;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}

