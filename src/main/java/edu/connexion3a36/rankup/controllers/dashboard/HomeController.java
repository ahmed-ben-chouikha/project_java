package edu.connexion3a36.rankup.controllers.dashboard;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class HomeController {

    @FXML
    private ListView<String> recentMatchesList;

    @FXML
    private ListView<String> upcomingMatchesList;

    @FXML
    private ListView<String> announcementsList;

    @FXML
    void initialize() {
        recentMatchesList.setItems(FXCollections.observableArrayList(
                "Falcons 2 - 1 Nova (Finished)",
                "Apex 0 - 2 Vortex (Finished)",
                "Titan 1 - 1 Eclipse (Ongoing)"
        ));

        upcomingMatchesList.setItems(FXCollections.observableArrayList(
                "Vortex vs Sigma - Tomorrow 18:00",
                "Nova vs Eclipse - Tue 20:00",
                "Falcons vs Titan - Wed 19:30"
        ));

        announcementsList.setItems(FXCollections.observableArrayList(
                "Season Finals registration closes Friday.",
                "Patch 14.2 ruleset now active.",
                "Admin panel maintenance on Sunday."
        ));
    }
}

