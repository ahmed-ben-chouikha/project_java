package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SideNavController {

    @FXML
    void goHome(ActionEvent event) { RankUpApp.loadInBase("/views/dashboard/home.fxml"); }

    @FXML
    void goMatches(ActionEvent event) { RankUpApp.loadInBase("/views/matches/matches.fxml"); }

    @FXML
    void goTeams(ActionEvent event) { RankUpApp.loadInBase("/views/teams/teams.fxml"); }

    @FXML
    void goPlayers(ActionEvent event) { RankUpApp.loadInBase("/views/players/player-profile.fxml"); }

    @FXML
    void goTournaments(ActionEvent event) { RankUpApp.loadInBase("/views/tournaments/tournaments.fxml"); }

    @FXML
    void goBudget(ActionEvent event) { RankUpApp.loadInBase("/views/budget/budget-dashboard.fxml"); }

    @FXML
    void goNotifications(ActionEvent event) { RankUpApp.loadInBase("/views/notifications/notifications.fxml"); }

    @FXML
    void goTickets(ActionEvent event) { RankUpApp.loadInBase("/views/tickets/tickets.fxml"); }

    @FXML
    void goReclamations(ActionEvent event) { RankUpApp.loadInBase("/views/reclamations/reclamations.fxml"); }

    @FXML
    void goAdminResponses(ActionEvent event) { RankUpApp.loadInBase("/views/adminresponses/admin-responses.fxml"); }

    @FXML
    void goPunitions(ActionEvent event) { RankUpApp.loadInBase("/views/punitions/punitions.fxml"); }

    @FXML
    void goAdmin(ActionEvent event) { RankUpApp.loadInBase("/views/admin/admin-dashboard.fxml"); }
}

