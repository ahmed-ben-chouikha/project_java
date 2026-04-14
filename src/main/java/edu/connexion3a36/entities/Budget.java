package edu.connexion3a36.entities;

import java.time.LocalDateTime;
import java.util.Date;

public class Budget {
    private int id;
    private float montantAlloue;
    private float montantUtilise;
    private LocalDateTime dateAllocation;
    private LocalDateTime dateModification;
    private int teamId;
    private String teamName;
    private String notes;
    private String justificatif;

    // Constructors
    public Budget() {}

    public Budget(float montantAlloue, LocalDateTime dateAllocation, int teamId) {
        this.montantAlloue = montantAlloue;
        this.montantUtilise = 0;
        this.dateAllocation = dateAllocation;
        this.teamId = teamId;
    }

    public Budget(int id, float montantAlloue, float montantUtilise, LocalDateTime dateAllocation,
                  LocalDateTime dateModification, int teamId, String teamName, String notes,
                  String statut, String justificatif) {
        this.id = id;
        this.montantAlloue = montantAlloue;
        this.montantUtilise = montantUtilise;
        this.dateAllocation = dateAllocation;
        this.dateModification = dateModification;
        this.teamId = teamId;
        this.teamName = teamName;
        this.notes = notes;
        this.justificatif = justificatif;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getMontantAlloue() { return montantAlloue; }
    public void setMontantAlloue(float montantAlloue) { this.montantAlloue = montantAlloue; }

    public float getMontantUtilise() { return montantUtilise; }
    public void setMontantUtilise(float montantUtilise) { this.montantUtilise = montantUtilise; }

    public float getRestant() { return montantAlloue - montantUtilise; }

    public LocalDateTime getDateAllocation() { return dateAllocation; }
    public void setDateAllocation(LocalDateTime dateAllocation) { this.dateAllocation = dateAllocation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getJustificatif() { return justificatif; }
    public void setJustificatif(String justificatif) { this.justificatif = justificatif; }

    // Deprecated methods for backward compatibility
    public String getStatut() { return null; }
    public void setStatut(String statut) { /* statut removed */ }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", montantAlloue=" + montantAlloue +
                ", montantUtilise=" + montantUtilise +
                ", restant=" + getRestant() +
                ", teamName='" + teamName + '\'' +
                '}';
    }
}

