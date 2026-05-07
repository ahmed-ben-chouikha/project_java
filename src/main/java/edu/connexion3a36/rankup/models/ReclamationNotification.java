package edu.connexion3a36.rankup.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ReclamationNotification {
    private int id;
    private int adminResponseId;
    private String adminMessage;
    private int reclamationId;
    private String reclamationTitre;
    private LocalDateTime timestamp;
    private String type; // "RESPONSE" or "PUNITION"
    private boolean read;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReclamationNotification(int id, int adminResponseId, String adminMessage, int reclamationId, 
                                   String reclamationTitre, LocalDateTime timestamp, String type) {
        this.id = id;
        this.adminResponseId = adminResponseId;
        this.adminMessage = adminMessage;
        this.reclamationId = reclamationId;
        this.reclamationTitre = reclamationTitre;
        this.timestamp = timestamp;
        this.type = type;
        this.read = false;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdminResponseId() {
        return adminResponseId;
    }

    public void setAdminResponseId(int adminResponseId) {
        this.adminResponseId = adminResponseId;
    }

    public String getAdminMessage() {
        return adminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        this.adminMessage = adminMessage;
    }

    public int getReclamationId() {
        return reclamationId;
    }

    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
    }

    public String getReclamationTitre() {
        return reclamationTitre;
    }

    public void setReclamationTitre(String reclamationTitre) {
        this.reclamationTitre = reclamationTitre;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp != null ? timestamp.format(FORMATTER) : "N/A";
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReclamationNotification that = (ReclamationNotification) o;
        return id == that.id && adminResponseId == that.adminResponseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, adminResponseId);
    }

    @Override
    public String toString() {
        return "ReclamationNotification{" +
                "id=" + id +
                ", adminResponseId=" + adminResponseId +
                ", reclamationTitre='" + reclamationTitre + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

