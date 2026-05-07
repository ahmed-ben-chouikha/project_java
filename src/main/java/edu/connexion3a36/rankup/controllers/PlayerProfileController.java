package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.User;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.app.SessionManager;
import edu.connexion3a36.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Player Profile Display
 * Shows the currently logged-in user's profile information.
 * Always fetches fresh data from the database using the session user ID
 * so the displayed name/email/role is always accurate.
 */
public class PlayerProfileController implements Initializable {

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userDetailsLabel;

    @FXML
    private Label matchesLabel;

    @FXML
    private Label winRateLabel;

    @FXML
    private Label kdaLabel;

    @FXML
    private Label mvpLabel;

    @FXML
    private TableView<Object> recentMatchesTable;

    @FXML
    private ListView<String> teamsListView;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserProfile();
    }

    /**
     * Load user profile data.
     * Fetches the current user from the database by session user ID so the
     * profile always reflects the actual logged-in account, not stale session
     * data or any hardcoded value.
     */
    private void loadUserProfile() {
        try {
            int userId = SessionManager.getCurrentUserId();

            if (userId <= 0) {
                showError("Profile Error", "No user is currently logged in.");
                return;
            }

            // Always read fresh data from the database
            User user = userService.getUserById(userId);

            if (user == null) {
                showError("Profile Error", "Could not load user data. Please log in again.");
                return;
            }

            // Sync session with the latest DB values (keeps TopNav consistent too)
            RankUpApp.setCurrentPlayerName(user.getUsername());
            RankUpApp.setCurrentRole(user.getRole());
            RankUpApp.setCurrentEmail(user.getEmail());

            // Update UI
            userNameLabel.setText(user.getUsername());
            String joinedDate = (user.getCreatedAt() != null && !user.getCreatedAt().isBlank())
                    ? user.getCreatedAt().substring(0, 10)
                    : "N/A";
            userDetailsLabel.setText(
                    "Email: " + user.getEmail()
                    + " | Role: " + user.getRole()
                    + " | Joined: " + joinedDate
            );

            loadProfileStats(userId);

        } catch (Exception e) {
            showError("Error Loading Profile", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load profile statistics
     */
    private void loadProfileStats(Integer userId) {
        try {
            // TODO: Query database for actual user statistics
            // For now, showing placeholder data
            matchesLabel.setText("0");
            winRateLabel.setText("0%");
            kdaLabel.setText("0.0");
            mvpLabel.setText("0");

            // Load recent matches (placeholder)
            loadRecentMatches();

            // Load teams (placeholder)
            loadTeams();

        } catch (Exception e) {
            showError("Error Loading Statistics", e.getMessage());
        }
    }

    /**
     * Load recent matches from database
     */
    private void loadRecentMatches() {
        try {
            // TODO: Query database for recent matches
            // recentMatchesTable.setItems(...);
        } catch (Exception e) {
            System.err.println("Error loading recent matches: " + e.getMessage());
        }
    }

    /**
     * Load teams the user has joined
     */
    private void loadTeams() {
        try {
            // TODO: Query database for user's teams
            teamsListView.getItems().add("Team placeholder - " + RankUpApp.getCurrentPlayerName());
        } catch (Exception e) {
            System.err.println("Error loading teams: " + e.getMessage());
        }
    }

    /**
     * Handle edit profile button click
     */
    @FXML
    void onEdit() {
        showInfo("Edit Profile", "Edit profile feature coming soon!");
    }

    /**
     * Show information dialog
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show error dialog
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

