package edu.connexion3a36.rankup.controllers.admin;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class AdminDashboardController {

    @FXML
    void openUserManagement(ActionEvent event) {
        RankUpApp.loadInBase("/views/admin/user-management.fxml");
    }

    @FXML
    void placeholder(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Placeholder");
        alert.setHeaderText(null);
        alert.setContentText("This admin section is a UI placeholder for future logic.");
        alert.showAndWait();
    }
}

