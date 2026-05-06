package edu.connexion3a36.rankup.controllers.dashboard;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.ManagerRequestService;
import edu.connexion3a36.services.PlayerApplicationService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;

import java.sql.SQLException;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ListView<String> matchesList;
    @FXML private ListView<String> tournamentsList;
    @FXML private Button createTeamButton;
    @FXML private Button applyManagerButton;
    @FXML private Button applyPlayerButton;
    @FXML private Button myTeamsButton;

    private final PlayerApplicationService playerApplicationService = new PlayerApplicationService();
    private final ManagerRequestService managerRequestService = new ManagerRequestService();

    @FXML
    void initialize() {
        welcomeLabel.setText("Welcome, " + RankUpApp.getCurrentPlayerName());

        updateManagerButtons();
        hideApplyPlayerButtonIfAlreadyPlayer();

        matchesList.setItems(FXCollections.observableArrayList(
                "Falcons 2 - 1 Nova | Finished",
                "Titan 1 - 1 Eclipse | Ongoing",
                "Vortex 0 - 0 Sigma | Tonight 20:00"
        ));

        tournamentsList.setItems(FXCollections.observableArrayList(
                "Season Finals - Registration open",
                "Championship Cup - Starts next week",
                "Weekend Clash - Live brackets"
        ));
    }

    @FXML
    void onOpenMatches(ActionEvent event) {
        RankUpApp.loadInBase("/views/matches/matches.fxml");
    }

    @FXML
    void onOpenTournaments(ActionEvent event) {
        RankUpApp.loadInBase("/views/tournaments/tournaments.fxml");
    }

    @FXML
    void onBuyTickets(ActionEvent event) {
        RankUpApp.loadInBase("/views/tickets/buy-tickets.fxml");
    }

    @FXML
    void onApplyManager(ActionEvent event) {
        RankUpApp.loadInBase("/views/manager/manager-application-form.fxml");
    }

    @FXML
    void onCreateTeam(ActionEvent event) {
        RankUpApp.loadInBase("/views/teams/team-form.fxml");
    }

    @FXML
    void onMyTeams(ActionEvent event) {
        RankUpApp.loadInBase("/views/manager/my-teams.fxml");
    }

    @FXML
    void onApplyPlayer(ActionEvent event) {
        RankUpApp.loadInBase("/views/players/player-application-form.fxml");
    }

    private void updateManagerButtons() {
        try {
            boolean approvedManager = RankUpApp.getCurrentUserId() > 0
                    && managerRequestService.hasApprovedManagerRequest(RankUpApp.getCurrentUserId());
            if (approvedManager) {
                applyManagerButton.setVisible(false);
                applyManagerButton.setManaged(false);
                createTeamButton.setVisible(true);
                createTeamButton.setManaged(true);
                if (myTeamsButton != null) {
                    myTeamsButton.setVisible(true);
                    myTeamsButton.setManaged(true);
                }
            } else {
                applyManagerButton.setVisible(true);
                applyManagerButton.setManaged(true);
                createTeamButton.setVisible(false);
                createTeamButton.setManaged(false);
                if (myTeamsButton != null) {
                    myTeamsButton.setVisible(false);
                    myTeamsButton.setManaged(false);
                }
            }
        } catch (SQLException e) {
            applyManagerButton.setDisable(true);
            createTeamButton.setDisable(true);
            if (myTeamsButton != null) {
                myTeamsButton.setDisable(true);
            }
        }
    }

    private void hideApplyPlayerButtonIfAlreadyPlayer() {
        try {
            if (RankUpApp.getCurrentUserId() > 0 && playerApplicationService.playerExists(RankUpApp.getCurrentUserId())) {
                applyPlayerButton.setVisible(false);
                applyPlayerButton.setManaged(false);
            }
        } catch (SQLException e) {
            applyPlayerButton.setDisable(true);
            applyPlayerButton.setText("Apply as Player");
        }
    }
}
