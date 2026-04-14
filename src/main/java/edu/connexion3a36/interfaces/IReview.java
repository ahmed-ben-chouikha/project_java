package edu.connexion3a36.interfaces;

import edu.connexion3a36.entities.Review;
import java.sql.SQLException;
import java.util.List;

public interface IReview extends IService<Review> {

    /**
     * Get all reviews for a specific player
     * @param playerName the player's name
     * @return list of reviews
     */
    List<Review> getReviewsByPlayer(String playerName) throws SQLException;

    /**
     * Get all reviews for a specific tournament
     * @param tournamentId the tournament id
     * @return list of reviews
     */
    List<Review> getReviewsByTournament(int tournamentId) throws SQLException;

    /**
     * Get all pending reviews (for admin approval)
     * @return list of pending reviews
     */
    List<Review> getPendingReviews() throws SQLException;

    /**
     * Check if a player has already reviewed a tournament
     * @param playerName the player's name
     * @param tournamentId the tournament id
     * @return true if review exists, false otherwise
     */
    boolean hasReviewedTournament(String playerName, int tournamentId) throws SQLException;

    /**
     * Approve a review
     * @param reviewId the review id
     */
    void approveReview(int reviewId) throws SQLException;

    /**
     * Reject a review with a reason
     * @param reviewId the review id
     * @param rejectionReason the reason for rejection
     */
    void rejectReview(int reviewId, String rejectionReason) throws SQLException;

    /**
     * Get reviews by status
     * @param status the status (pending, approved, rejected)
     * @return list of reviews
     */
    List<Review> getReviewsByStatus(String status) throws SQLException;
}
