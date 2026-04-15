package edu.connexion3a36.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Review {
    private int id;
    private String playerName;
    private int tournamentId;
    private String tournamentName;
    private int rating;
    private String comment;
    private LocalDate reviewDate;
    private String status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Empty constructor
    public Review() {
    }

    // Constructor without id (for creation)
    public Review(String playerName, int tournamentId, String tournamentName, int rating,
                  String comment, LocalDate reviewDate) {
        this.playerName = playerName;
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.status = "pending";
    }

    // Full constructor with id and timestamps
    public Review(int id, String playerName, int tournamentId, String tournamentName, int rating,
                  String comment, LocalDate reviewDate, String status, String rejectionReason,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.playerName = playerName;
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (comment == null || comment.trim().length() < 10 || comment.length() > 300) {
            throw new IllegalArgumentException("Comment must be between 10 and 300 characters");
        }
        this.comment = comment;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return id == review.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", tournamentName='" + tournamentName + '\'' +
                ", rating=" + rating +
                ", status='" + status + '\'' +
                '}';
    }
}
