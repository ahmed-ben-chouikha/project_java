package edu.connexion3a36.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reclamation {

    protected int id;
    protected String titre;
    protected String description;
    protected String type;
    protected String etat;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected String attachmentFilename;
    protected Integer playerId;
    protected AdminResponse adminResponse;
    protected Punition punition;

    public Reclamation() {
    }

    public Reclamation(String titre, String description, String type, String etat, Integer playerId) {
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.etat = etat;
        this.playerId = playerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAttachmentFilename() {
        return attachmentFilename;
    }

    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public AdminResponse getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(AdminResponse adminResponse) {
        this.adminResponse = adminResponse;
    }

    public Punition getPunition() {
        return punition;
    }

    public void setPunition(Punition punition) {
        this.punition = punition;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", etat='" + etat + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", attachmentFilename='" + attachmentFilename + '\'' +
                ", playerId=" + playerId +
                ", adminResponseId=" + (adminResponse != null ? adminResponse.getId() : null) +
                ", punitionId=" + (punition != null ? punition.getId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reclamation that = (Reclamation) o;
        return id == that.id && Objects.equals(titre, that.titre) && Objects.equals(description, that.description) && Objects.equals(type, that.type) && Objects.equals(etat, that.etat) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(attachmentFilename, that.attachmentFilename) && Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titre, description, type, etat, createdAt, updatedAt, attachmentFilename, playerId);
    }
}


