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
       @FXML private Button depenseBtn;
    @FXML private Button myBudgetBtn;
    @FXML private Button myExpensesBtn;
    @FXML private Button chatbotBtn;
    @FXML private Button teamChatBtn;
    @FXML private Button statBtn;

    private final Map<Button, String> buttonLabels = new LinkedHashMap<>();
    private final List<Label> sectionLabels = new ArrayList<>();
    private boolean collapsed;

    @FXML
    private void initialize() {
        sideNavRoot.setFillWidth(true);
        collectNodes(sideNavRoot);
        // Show/hide manager-specific nav items
        boolean isManager = SessionManager.isManager();
        if (depenseBtn != null) {
            depenseBtn.setVisible(isManager);
            depenseBtn.setManaged(isManager);
        }
        if (myBudgetBtn != null) {
            myBudgetBtn.setVisible(isManager);
            myBudgetBtn.setManaged(isManager);
        }
        if (myExpensesBtn != null) {
            myExpensesBtn.setVisible(isManager);
            myExpensesBtn.setManaged(isManager);
        }
        if (chatbotBtn != null) {
            // Make chatbot available to all users
            chatbotBtn.setVisible(true);
            chatbotBtn.setManaged(true);
        }
        if (teamChatBtn != null) {
            // Make team chat available to all users
            teamChatBtn.setVisible(true);
            teamChatBtn.setManaged(true);
        }
        if (statBtn != null) {
            statBtn.setVisible(true);
            statBtn.setManaged(true);
        }

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
    void goBuyTickets(ActionEvent event) { RankUpApp.loadInBase("/views/tickets/buy-tickets.fxml"); }

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
    void goMyBudget(ActionEvent event) {
        if (!SessionManager.isManager()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/manager/manager-budget-depense.fxml");
    }

    @FXML
    void goMyExpenses(ActionEvent event) {
        if (!SessionManager.isManager()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/manager/manager-budget-depense.fxml");
    }

    @FXML
    void goChatbot(ActionEvent event) {
        RankUpApp.loadInBase("/views/chatbot/chatbot.fxml");
    }

    @FXML
    void goTeamChat(ActionEvent event) {
        // allow any logged-in user to open team chat; chat controller will pick the team
        if (SessionManager.getCurrentUserId() <= 0) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/chat/chat-room.fxml");
    }

    @FXML
    void goStatistique(ActionEvent event) {
        RankUpApp.loadInBase("/views/stats/budget-stats.fxml");
    }

    @FXML
    void goDepenses(ActionEvent event) {
        if (SessionManager.isAdmin()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Access Denied");
            alert.setHeaderText(null);
            alert.setContentText("Expense management is only available for managers. Admins manage budgets instead.");
            alert.showAndWait();
            return;
        }
        RankUpApp.loadInBase("/views/depense/depense-list.fxml");
    }

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

    @FXML
    void goPlayerRequests(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/admin/admin-player-requests.fxml");
    }

    @FXML
    void goManagerRequests(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/admin/admin-manager-requests.fxml");
    }

    @FXML
    void goTeamApprovals(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/admin/admin-team-approvals.fxml");
    }

    @FXML
    void goPaymentsDashboard(ActionEvent event) {
        if (!SessionManager.isAdmin()) {
            showAccessDenied();
            return;
        }
        RankUpApp.loadInBase("/views/admin/admin-payments.fxml");
    }

    private void showAccessDenied() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Access denied");
        alert.setHeaderText(null);
        alert.setContentText("Admin access is required for this section.");
        alert.showAndWait();
    }

}

