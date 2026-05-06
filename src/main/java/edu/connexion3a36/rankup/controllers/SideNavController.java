package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.ReclamationService;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Controller for sidebar navigation with real-time reclamation badge
 */
public class SideNavController {

    @FXML private VBox sideNavRoot;
    @FXML private ToggleButton collapseToggle;
    @FXML private Label reclamationsBadge;
    @FXML private StackPane reclamationsBtnStack;

    private ScheduledExecutorService executorService;
    private LocalDateTime lastCheckedTime;
    private int unreadReclamationCount = 0;
    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    void initialize() {
        // Initialize last checked time
        lastCheckedTime = LocalDateTime.now();
        
        // Start the polling service
        startReclamationPolling();
    }

    /**
     * Start polling for new reclamations every 10 seconds
     */
    private void startReclamationPolling() {
        executorService = Executors.newScheduledThreadPool(1, runnable -> {
            Thread thread = new Thread(runnable, "ReclamationPoller");
            thread.setDaemon(true);
            return thread;
        });

        executorService.scheduleAtFixedRate(() -> {
            try {
                checkForNewReclamations();
            } catch (Exception e) {
                System.err.println("Error checking for new reclamations: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Check for new reclamations since last check
     */
    private void checkForNewReclamations() {
        try {
            // Count total reclamations with pending status
            var allReclamations = reclamationService.getData();
            int pendingCount = (int) allReclamations.stream()
                    .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().isAfter(lastCheckedTime))
                    .count();
            
            if (pendingCount > 0) {
                unreadReclamationCount = pendingCount;
                
                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    updateBadge(unreadReclamationCount);
                });
            }
            
            // Update last checked time
            lastCheckedTime = LocalDateTime.now();
            
        } catch (Exception e) {
            System.err.println("Error in reclamation polling: " + e.getMessage());
        }
    }

    /**
     * Update badge display
     */
    private void updateBadge(int count) {
        if (count > 0) {
            reclamationsBadge.setText(String.valueOf(count));
            if (!reclamationsBadge.isVisible()) {
                reclamationsBadge.setVisible(true);
                reclamationsBadge.setManaged(true);
                animateBadge();
            }
        } else {
            reclamationsBadge.setVisible(false);
            reclamationsBadge.setManaged(false);
        }
    }

    /**
     * Animate badge with pulse effect
     */
    private void animateBadge() {
        FadeTransition fade = new FadeTransition(Duration.millis(600), reclamationsBadge);
        fade.setFromValue(0.3);
        fade.setToValue(1.0);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();
    }

    @FXML
    void toggleSidebar(ActionEvent event) {
        // vide pour l'instant
    }

    @FXML
    void goHome(ActionEvent event) { 
        RankUpApp.loadInBase("/views/dashboard/home.fxml"); 
    }

    @FXML
    void goMatches(ActionEvent event) { 
        RankUpApp.loadInBase("/views/matches/matches.fxml"); 
    }

    @FXML
    void goTeams(ActionEvent event) { 
        RankUpApp.loadInBase("/views/teams/teams.fxml"); 
    }

    @FXML
    void goPlayers(ActionEvent event) { 
        RankUpApp.loadInBase("/views/players/player-profile.fxml"); 
    }

    @FXML
    void goTournaments(ActionEvent event) { 
        RankUpApp.loadInBase("/views/tournaments/tournaments.fxml"); 
    }

    @FXML
    void goTournamentReviews(ActionEvent event) { 
        RankUpApp.loadInBase("/views/tournaments/tournament-reviews.fxml"); 
    }

    @FXML
    void goBudget(ActionEvent event) { 
        RankUpApp.loadInBase("/views/budget/budget-dashboard.fxml"); 
    }

    @FXML
    void goDepenses(ActionEvent event) { 
        RankUpApp.loadInBase("/views/depense/depenses.fxml"); 
    }

    @FXML
    void goNotifications(ActionEvent event) { 
        RankUpApp.loadInBase("/views/notifications/notifications.fxml"); 
    }

    @FXML
    void goTickets(ActionEvent event) { 
        RankUpApp.loadInBase("/views/tickets/tickets.fxml"); 
    }

    @FXML
    void goReclamations(ActionEvent event) { 
        // Reset badge when user visits reclamations
        unreadReclamationCount = 0;
        updateBadge(0);
        lastCheckedTime = LocalDateTime.now();
        
        RankUpApp.loadInBase("/views/reclamations/reclamations.fxml"); 
    }

    @FXML
    void goAdminResponses(ActionEvent event) { 
        RankUpApp.loadInBase("/views/adminresponses/admin-responses.fxml"); 
    }

    @FXML
    void goPunitions(ActionEvent event) { 
        RankUpApp.loadInBase("/views/punitions/punitions.fxml"); 
    }

    @FXML
    void goReviewModeration(ActionEvent event) { 
        RankUpApp.loadInBase("/views/admin/review-moderation.fxml"); 
    }

    @FXML
    void goAdmin(ActionEvent event) { 
        RankUpApp.loadInBase("/views/admin/admin-dashboard.fxml"); 
    }

    /**
     * Stop polling when view is closed
     */
    public void stopPolling() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}