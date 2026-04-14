package edu.connexion3a36.entities;

import java.util.Date;

public class Team {
    private int id;
    private String name;
    private String country;
    private String description;
    private String detailedDescription;
    private String logo;
    private String jeu;  // LoL, CS:GO, etc.
    private String niveau;  // Débutant, Intermédiaire, Pro
    private String statut;  // en attente, approuvé, refusé
    private Date dateValidation;
    private int score;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Team() {}

    public Team(String name, String country, String description, String jeu, String niveau) {
        this.name = name;
        this.country = country;
        this.description = description;
        this.jeu = jeu;
        this.niveau = niveau;
        this.statut = "en attente";
        this.score = 0;
    }

    public Team(int id, String name, String country, String description, String detailedDescription,
                String logo, String jeu, String niveau, String statut,
                Date dateValidation, int score, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.description = description;
        this.detailedDescription = detailedDescription;
        this.logo = logo;
        this.jeu = jeu;
        this.niveau = niveau;
        this.statut = statut;
        this.dateValidation = dateValidation;
        this.score = score;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDetailedDescription() { return detailedDescription; }
    public void setDetailedDescription(String detailedDescription) { this.detailedDescription = detailedDescription; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public String getJeu() { return jeu; }
    public void setJeu(String jeu) { this.jeu = jeu; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }


    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Date getDateValidation() { return dateValidation; }
    public void setDateValidation(Date dateValidation) { this.dateValidation = dateValidation; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", jeu='" + jeu + '\'' +
                ", niveau='" + niveau + '\'' +
                ", statut='" + statut + '\'' +
                ", score=" + score +
                '}';
    }
}

