package edu.connexion3a36.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class TournamentRegistration {
    private int id;
    private String playerName;
    private String teamName;
    private int tournamentId;
    private String tournamentName;
    private LocalDateTime registrationDate;
    private String status;
    private String rejectionReason;

    // Empty constructor
    public TournamentRegistration() {
    }

    // Constructor without id (for creation)
    public TournamentRegistration(String playerName, String teamName, int tournamentId, String status) {
        this.playerName = playerName;
        this.teamName = teamName;
        this.tournamentId = tournamentId;
        this.status = status;
    }

    // Full constructor with id and timestamps
    public TournamentRegistration(int id, String playerName, String teamName, int tournamentId,
                                 String tournamentName, LocalDateTime registrationDate, String status, String rejectionReason) {
        this.id = id;
        this.playerName = playerName;
        this.teamName = teamName;
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.registrationDate = registrationDate;
        this.status = status;
        this.rejectionReason = rejectionReason;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @Override
    public String toString() {
        return "TournamentRegistration{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", teamName='" + teamName + '\'' +
                ", tournamentId=" + tournamentId +
                ", tournamentName='" + tournamentName + '\'' +
                ", registrationDate=" + registrationDate +
                ", status='" + status + '\'' +
                ", rejectionReason='" + rejectionReason + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentRegistration that = (TournamentRegistration) o;
        return id == that.id && Objects.equals(playerName, that.playerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, playerName);
    }
}
