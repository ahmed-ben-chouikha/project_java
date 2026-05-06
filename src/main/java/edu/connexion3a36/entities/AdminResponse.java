package edu.connexion3a36.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class AdminResponse {

    protected int id;
    protected String message;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected int reclamationId;
    protected Reclamation reclamation;

    public AdminResponse() {
    }

    public AdminResponse(String message, int reclamationId) {
        this.message = message;
        this.reclamationId = reclamationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public int getReclamationId() {
        return reclamationId;
    }

    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
    }

    public Reclamation getReclamation() {
        return reclamation;
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
    }

    @Override
    public String toString() {
        return "AdminResponse{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", reclamationId=" + reclamationId +
                ", reclamationRefId=" + (reclamation != null ? reclamation.getId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminResponse that = (AdminResponse) o;
        return id == that.id && reclamationId == that.reclamationId && Objects.equals(message, that.message) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, createdAt, updatedAt, reclamationId);
    }
}


