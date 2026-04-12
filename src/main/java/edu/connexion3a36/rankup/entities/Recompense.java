package edu.connexion3a36.rankup.entities;

import java.util.Objects;

public class Recompense {
    private int id;
    private String recompense;
    private String type;
    private int classement;
    private String description;
    private int tournamentId;

    public Recompense() {
    }

    public Recompense(String recompense, String type, int classement, String description, int tournamentId) {
        this.recompense = recompense;
        this.type = type;
        this.classement = classement;
        this.description = description;
        this.tournamentId = tournamentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecompense() {
        return recompense;
    }

    public void setRecompense(String recompense) {
        this.recompense = recompense;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getClassement() {
        return classement;
    }

    public void setClassement(int classement) {
        this.classement = classement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recompense that = (Recompense) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Recompense{" +
                "id=" + id +
                ", recompense='" + recompense + '\'' +
                ", type='" + type + '\'' +
                ", classement=" + classement +
                ", description='" + description + '\'' +
                ", tournamentId=" + tournamentId +
                '}';
    }
}

