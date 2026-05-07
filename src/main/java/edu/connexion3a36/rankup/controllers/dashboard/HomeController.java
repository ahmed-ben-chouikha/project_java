package edu.connexion3a36.rankup.controllers.dashboard;

import edu.connexion3a36.rankup.models.TournamentTier;
import edu.connexion3a36.services.TournamentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    @FXML
    private ListView<String> recentMatchesList;

    @FXML
    private ListView<String> upcomingMatchesList;

    @FXML
    private ListView<String> announcementsList;


    @FXML
    private ListView<String> tournamentTierList;

    private TournamentService tournamentService;

    @FXML
    void initialize() {
        tournamentService = new TournamentService();
        loadTournamentTierLeaderboard();

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

    private void loadTournamentTierLeaderboard() {
        // Sample data for demonstration
        List<TournamentTier> tiers = createSampleTiers();
        tiers.sort((a, b) -> Integer.compare(b.getTierScore(), a.getTierScore())); // Sort by score descending
        List<String> tierStrings = new ArrayList<>();
        for (TournamentTier tier : tiers) {
            tierStrings.add(tier.getCalculatedTier() + " - " + tier.getTournamentName() + " (" + tier.getGame() + ") - Score: " + tier.getTierScore());
        }
        tournamentTierList.setItems(FXCollections.observableArrayList(tierStrings));
    }

    private List<TournamentTier> createSampleTiers() {
        List<TournamentTier> tiers = new ArrayList<>();
        // Sample S-Tier
        TournamentTier sTier = new TournamentTier("The International", "Dota 2", 1500000, 40, 10, 600000, null, 0, LocalDate.now().minusDays(30));
        calculateTier(sTier);
        tiers.add(sTier);
        // Sample A-Tier
        TournamentTier aTier = new TournamentTier("Regional Cup", "CS:GO", 50000, 20, 5, 200000, null, 0, LocalDate.now().minusDays(10));
        calculateTier(aTier);
        tiers.add(aTier);
        // Sample B-Tier
        TournamentTier bTier = new TournamentTier("Community Event", "League of Legends", 10000, 12, 2, 50000, null, 0, LocalDate.now().minusDays(5));
        calculateTier(bTier);
        tiers.add(bTier);
        // Sample C-Tier
        TournamentTier cTier = new TournamentTier("Weekly Cup", "Overwatch", 1000, 8, 0, 10000, null, 0, LocalDate.now().minusDays(1));
        calculateTier(cTier);
        tiers.add(cTier);
        return tiers;
    }

    private void calculateTier(TournamentTier tier) {
        if (tier.getTeamCount() < 4 || tier.getPrizePool() <= 0) {
            tier.setCalculatedTier("C");
            tier.setTierScore(0);
            return;
        }

        int score = 0;

        // Prize pool
        if (tier.getPrizePool() > 100000) score += 40;
        else if (tier.getPrizePool() >= 20000) score += 32; // approx 80%

        // Team count
        if (tier.getTeamCount() > 32) score += 20;
        else if (tier.getTeamCount() >= 16) score += 16;

        // Pro teams
        if (tier.getProTeamsCount() > 8) score += 25;
        else if (tier.getProTeamsCount() >= 3) score += 20;

        // Viewers
        if (tier.getPeakViewers() > 500000) score += 15;
        else if (tier.getPeakViewers() >= 100000) score += 12;

        tier.setTierScore(score);

        if (score >= 85) tier.setCalculatedTier("S");
        else if (score >= 70) tier.setCalculatedTier("A");
        else if (score >= 50) tier.setCalculatedTier("B");
        else tier.setCalculatedTier("C");
    }
}

