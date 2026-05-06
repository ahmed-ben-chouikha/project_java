package edu.connexion3a36.rankup.models;

import java.time.LocalDate;

public class TournamentTier {
    private String tournamentName;
    private String game;
    private double prizePool;
    private int teamCount;
    private int proTeamsCount;
    private int peakViewers;
    private String calculatedTier; // S, A, B, C
    private int tierScore;
    private LocalDate endDate;

    public TournamentTier() {}

    public TournamentTier(String tournamentName, String game, double prizePool, int teamCount, int proTeamsCount, int peakViewers, String calculatedTier, int tierScore, LocalDate endDate) {
        this.tournamentName = tournamentName;
        this.game = game;
        this.prizePool = prizePool;
        this.teamCount = teamCount;
        this.proTeamsCount = proTeamsCount;
        this.peakViewers = peakViewers;
        this.calculatedTier = calculatedTier;
        this.tierScore = tierScore;
        this.endDate = endDate;
    }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }

    public double getPrizePool() { return prizePool; }
    public void setPrizePool(double prizePool) { this.prizePool = prizePool; }

    public int getTeamCount() { return teamCount; }
    public void setTeamCount(int teamCount) { this.teamCount = teamCount; }

    public int getProTeamsCount() { return proTeamsCount; }
    public void setProTeamsCount(int proTeamsCount) { this.proTeamsCount = proTeamsCount; }

    public int getPeakViewers() { return peakViewers; }
    public void setPeakViewers(int peakViewers) { this.peakViewers = peakViewers; }

    public String getCalculatedTier() { return calculatedTier; }
    public void setCalculatedTier(String calculatedTier) { this.calculatedTier = calculatedTier; }

    public int getTierScore() { return tierScore; }
    public void setTierScore(int tierScore) { this.tierScore = tierScore; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
