package edu.connexion3a36.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Punition {

    protected int id;
    protected LocalDateTime startAt;
    protected LocalDateTime endAt;
    protected String playerStatus;
    protected int reclamationId;
    protected Reclamation reclamation;

    public Punition() {
    }

    public Punition(LocalDateTime startAt, LocalDateTime endAt, String playerStatus, int reclamationId) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.playerStatus = playerStatus;
        this.reclamationId = reclamationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public String getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(String playerStatus) {
        this.playerStatus = playerStatus;
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
        return "Punition{" +
                "id=" + id +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                ", playerStatus='" + playerStatus + '\'' +
                ", reclamationId=" + reclamationId +
                ", reclamationRefId=" + (reclamation != null ? reclamation.getId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Punition punition = (Punition) o;
        return id == punition.id && reclamationId == punition.reclamationId && Objects.equals(startAt, punition.startAt) && Objects.equals(endAt, punition.endAt) && Objects.equals(playerStatus, punition.playerStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startAt, endAt, playerStatus, reclamationId);
    }
}


