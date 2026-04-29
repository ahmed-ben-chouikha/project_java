package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.app.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SideNavController {

    private static final double EXPANDED_WIDTH = 220;
    private static final double COLLAPSED_WIDTH = 76;

    @FXML private VBox sideNavRoot;
    @FXML private ToggleButton collapseToggle;
    @FXML private FontIcon collapseIcon;

    private final Map<Button, String> buttonLabels = new LinkedHashMap<>();
    private final List<Label> sectionLabels = new ArrayList<>();
    private boolean collapsed;

    @FXML
    private void initialize() {
        sideNavRoot.setFillWidth(true);
        collectNodes(sideNavRoot);

        buttonLabels.forEach((button, label) -> {
            button.setMaxWidth(Double.MAX_VALUE);
            button.setTooltip(new Tooltip(label));
        });

        applySidebarState(false);
    }

    private void collectNodes(Parent parent) {
        parent.getChildrenUnmodifiable().forEach(node -> {
            if (node instanceof Button button && button.getStyleClass().contains("nav-btn")) {
                buttonLabels.putIfAbsent(button, button.getText());
            } else if (node instanceof Label label && label.getStyleClass().contains("nav-section-label")) {
                sectionLabels.add(label);
            } else if (node instanceof Parent childParent) {
                collectNodes(childParent);
            }
        });
    }

    @FXML
    void toggleSidebar(ActionEvent event) {
        collapsed = collapseToggle.isSelected();
        applySidebarState(collapsed);
    }

    private void applySidebarState(boolean isCollapsed) {
        collapsed = isCollapsed;
        sideNavRoot.setPrefWidth(isCollapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH);
        sideNavRoot.setMinWidth(isCollapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH);
        sideNavRoot.setMaxWidth(isCollapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH);
        sideNavRoot.getStyleClass().remove("side-nav-collapsed");
        if (isCollapsed) {
            sideNavRoot.getStyleClass().add("side-nav-collapsed");
        }

        if (collapseToggle != null) {
            collapseToggle.setSelected(isCollapsed);
        }
        if (collapseIcon != null) {
            collapseIcon.setIconLiteral(isCollapsed ? "fas-angle-right" : "fas-angle-left");
        }

        sectionLabels.forEach(label -> {
            label.setVisible(!isCollapsed);
            label.setManaged(!isCollapsed);
        });

        buttonLabels.forEach((button, originalText) -> {
            if (isCollapsed) {
                button.setText("");
                button.setAlignment(Pos.CENTER);
                button.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
                button.setPrefWidth(COLLAPSED_WIDTH - 18);
                button.setMinWidth(COLLAPSED_WIDTH - 18);
                button.setTooltip(new Tooltip(originalText));
            } else {
                button.setText(originalText);
                button.setAlignment(Pos.CENTER_LEFT);
                button.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);
                button.setPrefWidth(EXPANDED_WIDTH - 24);
                button.setMinWidth(EXPANDED_WIDTH - 24);
                button.setTooltip(new Tooltip(originalText));
            }
        });
    }

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
    void goTournamentReviews(ActionEvent event) { RankUpApp.loadInBase("/views/tournaments/tournament-reviews.fxml"); }

    @FXML
    void goBudget(ActionEvent event) { RankUpApp.loadInBase("/views/budget/budget-list.fxml"); }

    @FXML
    void goDepenses(ActionEvent event) { RankUpApp.loadInBase("/views/depense/depense-list.fxml"); }

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
    void goAdmin(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/admin/admin-dashboard.fxml");
    }

    @FXML
    void goReviewModeration(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/admin/admin-review-moderation.fxml");
    }

    private void showAccessDenied() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Access denied");
        alert.setHeaderText(null);
        alert.setContentText("Admin access is required for this section.");
        alert.showAndWait();
    }

}

