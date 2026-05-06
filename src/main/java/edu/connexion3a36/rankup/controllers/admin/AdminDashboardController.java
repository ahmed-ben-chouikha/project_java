package edu.connexion3a36.rankup.controllers.admin;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.app.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class AdminDashboardController {

    @FXML
    void openUserManagement(ActionEvent event) {
        RankUpApp.loadInBase("/views/admin/user-management.fxml");
    }

    @FXML
    void openReviewModeration(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/admin/admin-review-moderation.fxml");
    }

    @FXML
    void openPlayerRequests(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/admin/admin-player-requests.fxml");
    }

    @FXML
    void openManagerRequests(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/admin/admin-manager-requests.fxml");
    }

    @FXML
    void openTeamApprovals(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/admin/admin-team-approvals.fxml");
    }

    @FXML
    void openPaymentsDashboard(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/admin/admin-payments.fxml");
    }

    @FXML
    void openBudgetManagement(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/budget/budget-list.fxml");
    }

    @FXML
    void placeholder(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Placeholder");
        alert.setHeaderText(null);
        alert.setContentText("This admin section is a UI placeholder for future logic.");
        alert.showAndWait();
    }

    private void showAccessDenied() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Access denied");
        alert.setHeaderText(null);
        alert.setContentText("Admin access is required for this section.");
        alert.showAndWait();
    }
}

