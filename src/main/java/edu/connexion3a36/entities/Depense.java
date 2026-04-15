package edu.connexion3a36.entities;

import java.time.LocalDateTime;

public class Depense {
    private int id;
    private String titre;
    private float montant;
    private String description;
    private LocalDateTime dateCreation;
    private String statut;
    private String categorie;
    private Integer teamId;
    private String teamName;
    private String facture;

    // Constructors
    public Depense() {}

    public Depense(String titre, float montant, LocalDateTime dateCreation, String statut) {
        this.titre = titre;
        this.montant = montant;
        this.dateCreation = dateCreation;
        this.statut = statut;
    }

    public Depense(int id, String titre, float montant, String description, LocalDateTime dateCreation,
                   String statut, String categorie, Integer teamId, String teamName, String facture) {
        this.id = id;
        this.titre = titre;
        this.montant = montant;
        this.description = description;
        this.dateCreation = dateCreation;
        this.statut = statut;
        this.categorie = categorie;
        this.teamId = teamId;
        this.teamName = teamName;
        this.facture = facture;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public float getMontant() { return montant; }
    public void setMontant(float montant) { this.montant = montant; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getFacture() { return facture; }
    public void setFacture(String facture) { this.facture = facture; }

    @Override
    public String toString() {
        return "Depense{" + "id=" + id + ", titre='" + titre + '\'' + ", montant=" + montant +
                ", statut='" + statut + '\'' + ", teamName='" + teamName + '\'' + '}';
    }
}

