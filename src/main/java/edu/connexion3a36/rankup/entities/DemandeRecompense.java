package edu.connexion3a36.rankup.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class DemandeRecompense {
    private int id;
    private Integer userId;
    private String nomDemandeur;
    private String email;
    private String motif;
    private LocalDateTime dateDemande;
    private String statut;
    private Integer recompenseId;
    private Integer scoreApprobation;
    private String priorite;
    private String raisonRejet;
    private LocalDateTime dateApprobation;
    private LocalDateTime dateRejet;

    public DemandeRecompense() {
    }

    public DemandeRecompense(String nomDemandeur, String email, String motif, LocalDateTime dateDemande, String statut) {
        this.nomDemandeur = nomDemandeur;
        this.email = email;
        this.motif = motif;
        this.dateDemande = dateDemande;
        this.statut = statut;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomDemandeur() {
        return nomDemandeur;
    }

    public void setNomDemandeur(String nomDemandeur) {
        this.nomDemandeur = nomDemandeur;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Integer getRecompenseId() {
        return recompenseId;
    }

    public void setRecompenseId(Integer recompenseId) {
        this.recompenseId = recompenseId;
    }

    public Integer getScoreApprobation() {
        return scoreApprobation;
    }

    public void setScoreApprobation(Integer scoreApprobation) {
        this.scoreApprobation = scoreApprobation;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public String getRaisonRejet() {
        return raisonRejet;
    }

    public void setRaisonRejet(String raisonRejet) {
        this.raisonRejet = raisonRejet;
    }

    public LocalDateTime getDateApprobation() {
        return dateApprobation;
    }

    public void setDateApprobation(LocalDateTime dateApprobation) {
        this.dateApprobation = dateApprobation;
    }

    public LocalDateTime getDateRejet() {
        return dateRejet;
    }

    public void setDateRejet(LocalDateTime dateRejet) {
        this.dateRejet = dateRejet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DemandeRecompense that = (DemandeRecompense) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DemandeRecompense{" +
                "id=" + id +
                ", nomDemandeur='" + nomDemandeur + '\'' +
                ", email='" + email + '\'' +
                ", motif='" + motif + '\'' +
                ", dateDemande=" + dateDemande +
                ", statut='" + statut + '\'' +
                ", recompenseId=" + recompenseId +
                ", scoreApprobation=" + scoreApprobation +
                ", priorite='" + priorite + '\'' +
                '}';
    }
}

