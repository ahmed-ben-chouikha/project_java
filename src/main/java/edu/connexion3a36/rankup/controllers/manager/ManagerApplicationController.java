package edu.connexion3a36.rankup.controllers.manager;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.ManagerRequestService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import java.sql.SQLException;

public class ManagerApplicationController {

    @FXML private TextField teamNameField;
    @FXML private TextArea motivationArea;
    @FXML private Label statusLabel;

    private final ManagerRequestService service = new ManagerRequestService();

    @FXML
    void onSubmit() {
        String teamName = teamNameField.getText();
        String motivation = motivationArea.getText();
        if (teamName == null || teamName.isBlank()) {
            statusLabel.setText("Please enter a team name.");
            return;
        }
        try {
            service.createRequest(RankUpApp.getCurrentUserId(), teamName, motivation);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Application Sent");
            a.setHeaderText(null);
            a.setContentText("Your manager application was submitted and will be reviewed by admins.");
            a.showAndWait();
            RankUpApp.loadInBase("/views/dashboard/user-dashboard.fxml");
        } catch (SQLException e) {
            statusLabel.setText("Could not submit application: " + e.getMessage());
        }
    }

    @FXML
    void onCancel() {
        RankUpApp.loadInBase("/views/dashboard/user-dashboard.fxml");
    }
}
