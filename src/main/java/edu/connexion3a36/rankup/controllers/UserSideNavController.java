package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.ManagerRequestService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.sql.SQLException;

public class UserSideNavController {

    @FXML private Button createTeamButton;
    @FXML private Button myTeamsButton;
    @FXML private Button myBudgetButton;
    @FXML private Button chatbotButton;
    @FXML private Button teamChatButton;
    @FXML private Button statButton;
    @FXML private Button applyManagerButton;

    private final ManagerRequestService managerRequestService = new ManagerRequestService();

    @FXML
    void initialize() {
        updateManagerButtons();
    }

    @FXML
    void goDashboard(ActionEvent event) {
        RankUpApp.loadInBase("/views/dashboard/user-dashboard.fxml");
    }

    @FXML
    void goMatches(ActionEvent event) {
        RankUpApp.loadInBase("/views/matches/matches.fxml");
    }

    @FXML
    void goTournaments(ActionEvent event) {
        RankUpApp.loadInBase("/views/tournaments/tournaments.fxml");
    }

    @FXML
    void goBuyTickets(ActionEvent event) {
        RankUpApp.loadInBase("/views/tickets/buy-tickets.fxml");
    }

    @FXML
    void goCreateTeam(ActionEvent event) {
        RankUpApp.loadInBase("/views/teams/team-form.fxml");
    }

    @FXML
    void goMyTeams(ActionEvent event) {
        RankUpApp.loadInBase("/views/manager/my-teams.fxml");
    }

    @FXML
    void goManagerApplication(ActionEvent event) {
        RankUpApp.loadInBase("/views/manager/manager-application-form.fxml");
    }

    @FXML
    void goProfile(ActionEvent event) {
        RankUpApp.loadInBase("/views/players/player-profile.fxml");
    }

    @FXML
    void goMyInvitations(ActionEvent event) {
        RankUpApp.loadInBase("/views/player/my-invitations.fxml");
    }

    private void updateManagerButtons() {
        try {
            boolean approvedManager = RankUpApp.getCurrentUserId() > 0
                    && managerRequestService.hasApprovedManagerRequest(RankUpApp.getCurrentUserId());

            if (createTeamButton != null) {
                createTeamButton.setVisible(approvedManager);
                createTeamButton.setManaged(approvedManager);
            }
            if (myTeamsButton != null) {
                myTeamsButton.setVisible(approvedManager);
                myTeamsButton.setManaged(approvedManager);
            }
            if (myBudgetButton != null) {
                myBudgetButton.setVisible(approvedManager);
                myBudgetButton.setManaged(approvedManager);
            }
            if (chatbotButton != null) {
                chatbotButton.setVisible(approvedManager);
                chatbotButton.setManaged(approvedManager);
            }
            if (statButton != null) {
                statButton.setVisible(approvedManager);
                statButton.setManaged(approvedManager);
            }
            if (applyManagerButton != null) {
                applyManagerButton.setVisible(!approvedManager);
                applyManagerButton.setManaged(!approvedManager);
            }
        } catch (SQLException e) {
            if (createTeamButton != null) {
                createTeamButton.setDisable(true);
            }
            if (myTeamsButton != null) {
                myTeamsButton.setDisable(true);
            }
            if (myBudgetButton != null) {
                myBudgetButton.setDisable(true);
            }
            if (chatbotButton != null) {
                chatbotButton.setDisable(true);
            }
            if (applyManagerButton != null) {
                applyManagerButton.setDisable(true);
            }
        }
    }

    @FXML
    void goMyBudget(ActionEvent event) {
        RankUpApp.loadInBase("/views/manager/manager-budget-depense.fxml");
    }

    @FXML
    void goChatbot(ActionEvent event) {
        RankUpApp.loadInBase("/views/chatbot/chatbot.fxml");
    }

    @FXML
    void goTeamChat(ActionEvent event) {
        RankUpApp.loadInBase("/views/chat/chat-room.fxml");
    }

    @FXML
    void goStatistique(ActionEvent event) {
        RankUpApp.loadInBase("/views/stats/budget-stats.fxml");
    }
}
