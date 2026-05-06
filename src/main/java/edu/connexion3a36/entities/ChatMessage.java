package edu.connexion3a36.entities;

import java.util.Objects;

public class ChatMessage {

    private int id;
    private int teamId;
    private int userId;
    private String message;
    private String createdAt;
    private String username; // For display purposes, not stored in DB

    public ChatMessage() {
    }

    public ChatMessage(int teamId, int userId, String message) {
        this.teamId = teamId;
        this.userId = userId;
        this.message = message;
    }

    public ChatMessage(int id, int teamId, int userId, String message, String createdAt) {
        this.id = id;
        this.teamId = teamId;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", teamId=" + teamId +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}