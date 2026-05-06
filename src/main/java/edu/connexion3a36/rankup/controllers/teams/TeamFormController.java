package edu.connexion3a36.rankup.controllers.teams;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.app.SessionManager;
import edu.connexion3a36.services.ManagerRequestService;
import edu.connexion3a36.services.TeamService;
import edu.connexion3a36.tools.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private final ManagerRequestService managerRequestService = new ManagerRequestService();
    private Team editingTeam;

    @FXML
    void initialize() {
        if (!canManageTeams()) {
            showAccessDenied();
            return;
        }

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
        if (!canManageTeams()) {
            showAccessDenied();
            return;
        }

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
        if (editingTeam == null) {
            payload.setCreatorId(RankUpApp.getCurrentUserId());
        }

        int teamId = -1;
        if (editingTeam == null) {
            teamId = teamService.addTeam(payload);
            if (teamId <= 0) {
                feedbackLabel.setText("Impossible d'enregistrer l'équipe. Vérifiez les données ou la base.");
                return;
            }
        } else {
            boolean ok = teamService.updateTeam(payload);
            if (!ok) {
                feedbackLabel.setText("Impossible d'enregistrer l'équipe. Vérifiez les données ou la base.");
                return;
            }
        }

        // If new team, add creator as member
        if (editingTeam == null && teamId > 0) {
            int creatorId = RankUpApp.getCurrentUserId();
            try {
                // Update player's team_id to make them a member
                String updatePlayerSql = "UPDATE player SET team_id = ? WHERE id = ?";
                java.sql.Connection cnx = edu.connexion3a36.tools.MyConnection.getInstance().getCnx();
                try (java.sql.PreparedStatement pst = cnx.prepareStatement(updatePlayerSql)) {
                    pst.setInt(1, teamId);
                    pst.setInt(2, creatorId);
                    pst.executeUpdate();
                }
            } catch (Exception e) {
                System.err.println("Could not add creator to team members: " + e.getMessage());
            }
        }

        TeamFormState.clear();
        if (editingTeam == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Team created");
            alert.setHeaderText(null);
            alert.setContentText("Tu as cree ton team en attend de l'approvation de l'admin.");
            alert.showAndWait();
        }
        RankUpApp.loadInBase("/views/manager/my-teams.fxml");
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

    private boolean canManageTeams() {
        if (SessionManager.isAdmin()) {
            return true;
        }
        int currentUserId = RankUpApp.getCurrentUserId();
        if (currentUserId <= 0) {
            return false;
        }
        try {
            return managerRequestService.hasApprovedManagerRequest(currentUserId);
        } catch (Exception e) {
            return false;
        }
    }

    private void showAccessDenied() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Access denied");
        alert.setHeaderText(null);
        alert.setContentText("Only approved managers can create teams.");
        alert.showAndWait();
        RankUpApp.loadInBase("/views/dashboard/user-dashboard.fxml");
    }
}

