package edu.connexion3a36.rankup.controllers.players;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.PlayerApplicationService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class PlayerApplicationController {

    @FXML private TextField nicknameField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField roleField;
    @FXML private Label statusLabel;

    private final PlayerApplicationService service = new PlayerApplicationService();

    @FXML
    void initialize() {
        nicknameField.setText(defaultIfBlank(RankUpApp.getCurrentPlayerName(), ""));
        roleField.setText("player");
    }

    @FXML
    void onSubmit() {
        String nickname = nicknameField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String role = roleField.getText();

        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            statusLabel.setText("First name and last name are required.");
            return;
        }

        try {
            service.createApplication(nickname, firstName, lastName, birthDatePicker.getValue(), role, RankUpApp.getCurrentUserId());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Application Sent");
            alert.setHeaderText(null);
            alert.setContentText("Your player application has been saved and is now pending review.");
            alert.showAndWait();

            RankUpApp.loadInBase("/views/dashboard/user-dashboard.fxml");
        } catch (SQLException e) {
            statusLabel.setText("Could not submit application: " + e.getMessage());
        }
    }

    @FXML
    void onCancel() {
        RankUpApp.loadInBase("/views/dashboard/user-dashboard.fxml");
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
