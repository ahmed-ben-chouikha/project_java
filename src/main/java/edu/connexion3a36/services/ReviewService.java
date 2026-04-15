package edu.connexion3a36.services;

import edu.connexion3a36.entities.Review;
import edu.connexion3a36.interfaces.IReview;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReviewService implements IReview {

    @Override
    public void addEntity(Review review) throws SQLException {
        // Input validation
        if (review == null || review.getPlayerName() == null ||
            review.getPlayerName().trim().isEmpty()) {
            throw new SQLException("Player name cannot be empty");
        }

        if (review.getTournamentId() <= 0) {
            throw new SQLException("Invalid tournament ID");
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new SQLException("Rating must be between 1 and 5");
        }

        if (review.getComment() == null || review.getComment().trim().length() < 10 ||
            review.getComment().length() > 300) {
            throw new SQLException("Comment must be between 10 and 300 characters");
        }

        if (review.getTournamentName() == null || review.getTournamentName().trim().isEmpty()) {
            throw new SQLException("Tournament name cannot be empty");
        }

        // Check if player has already reviewed this tournament
        if (hasReviewedTournament(review.getPlayerName(), review.getTournamentId())) {
            throw new SQLException("You have already reviewed this tournament");
        }

        String query = "INSERT INTO reviews (player_name, tournament_id, tournament_name, rating, comment, review_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, review.getPlayerName().trim());
            pst.setInt(2, review.getTournamentId());
            pst.setString(3, review.getTournamentName().trim());
            pst.setInt(4, review.getRating());
            pst.setString(5, review.getComment().trim());
            pst.setDate(6, java.sql.Date.valueOf(review.getReviewDate() != null ? review.getReviewDate() : LocalDate.now()));
            pst.setString(7, "pending");

            pst.executeUpdate();
            System.out.println("Review added successfully");
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException("You have already reviewed this tournament");
            }
            throw e;
        }
    }

    @Override
    public void deleteEntity(Review review) throws SQLException {
        if (review == null || review.getId() <= 0) {
            throw new SQLException("Invalid review for deletion");
        }

        // Check if review is pending or rejected - only these can be deleted
        Review existingReview = getReviewById(review.getId());
        if (existingReview != null && !existingReview.getStatus().equals("pending") &&
            !existingReview.getStatus().equals("rejected")) {
            throw new SQLException("Only pending or rejected reviews can be deleted");
        }

        String query = "DELETE FROM reviews WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, review.getId());

            int result = pst.executeUpdate();
            if (result > 0) {
                System.out.println("Review deleted successfully");
            } else {
                throw new SQLException("Review not found");
            }
        } catch (SQLException e) {
            throw new SQLException("Error deleting review: " + e.getMessage());
        }
    }

    @Override
    public void updateEntity(int id, Review review) throws SQLException {
        if (review == null || review.getId() <= 0) {
            throw new SQLException("Invalid review for update");
        }

        // Check if review is pending - only pending reviews can be updated
        Review existingReview = getReviewById(id);
        if (existingReview != null && !existingReview.getStatus().equals("pending")) {
            throw new SQLException("Only pending reviews can be edited");
        }

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new SQLException("Rating must be between 1 and 5");
        }

        if (review.getComment() == null || review.getComment().trim().length() < 10 ||
            review.getComment().length() > 300) {
            throw new SQLException("Comment must be between 10 and 300 characters");
        }

        String query = "UPDATE reviews SET rating = ?, comment = ?, review_date = ? WHERE id = ?";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, review.getRating());
            pst.setString(2, review.getComment().trim());
            pst.setDate(3, java.sql.Date.valueOf(review.getReviewDate() != null ? review.getReviewDate() : LocalDate.now()));
            pst.setInt(4, id);

            int result = pst.executeUpdate();
            if (result > 0) {
                System.out.println("Review updated successfully");
            } else {
                throw new SQLException("Review not found");
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public List<Review> getData() throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM reviews ORDER BY created_at DESC";

        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                reviews.add(buildReviewFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving reviews: " + e.getMessage());
        }

        return reviews;
    }

    @Override
    public List<Review> getReviewsByPlayer(String playerName) throws SQLException {
        List<Review> reviews = new ArrayList<>();

        if (playerName == null || playerName.trim().isEmpty()) {
            throw new SQLException("Player name cannot be empty");
        }

        String query = "SELECT * FROM reviews WHERE player_name = ? ORDER BY review_date DESC";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, playerName.trim());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                reviews.add(buildReviewFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving player reviews: " + e.getMessage());
        }

        return reviews;
    }

    @Override
    public List<Review> getReviewsByTournament(int tournamentId) throws SQLException {
        List<Review> reviews = new ArrayList<>();

        if (tournamentId <= 0) {
            throw new SQLException("Invalid tournament ID");
        }

        String query = "SELECT * FROM reviews WHERE tournament_id = ? ORDER BY review_date DESC";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, tournamentId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                reviews.add(buildReviewFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving tournament reviews: " + e.getMessage());
        }

        return reviews;
    }

    @Override
    public List<Review> getPendingReviews() throws SQLException {
        return getReviewsByStatus("pending");
    }

    @Override
    public List<Review> getReviewsByStatus(String status) throws SQLException {
        List<Review> reviews = new ArrayList<>();

        if (status == null || status.trim().isEmpty()) {
            throw new SQLException("Status cannot be empty");
        }

        String query = "SELECT * FROM reviews WHERE status = ? ORDER BY created_at DESC";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, status.trim());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                reviews.add(buildReviewFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving reviews by status: " + e.getMessage());
        }

        return reviews;
    }

    @Override
    public boolean hasReviewedTournament(String playerName, int tournamentId) throws SQLException {
        String query = "SELECT COUNT(*) FROM reviews WHERE player_name = ? AND tournament_id = ?";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, playerName);
            pst.setInt(2, tournamentId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new SQLException("Error checking review existence: " + e.getMessage());
        }

        return false;
    }

    @Override
    public void approveReview(int reviewId) throws SQLException {
        if (reviewId <= 0) {
            throw new SQLException("Invalid review ID");
        }

        String query = "UPDATE reviews SET status = ?, rejection_reason = NULL WHERE id = ?";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, "approved");
            pst.setInt(2, reviewId);

            int result = pst.executeUpdate();
            if (result > 0) {
                System.out.println("Review approved successfully");
            } else {
                throw new SQLException("Review not found");
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void rejectReview(int reviewId, String rejectionReason) throws SQLException {
        if (reviewId <= 0) {
            throw new SQLException("Invalid review ID");
        }

        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            throw new SQLException("Rejection reason cannot be empty");
        }

        String query = "UPDATE reviews SET status = ?, rejection_reason = ? WHERE id = ?";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, "rejected");
            pst.setString(2, rejectionReason.trim());
            pst.setInt(3, reviewId);

            int result = pst.executeUpdate();
            if (result > 0) {
                System.out.println("Review rejected successfully");
            } else {
                throw new SQLException("Review not found");
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Helper method to retrieve a review by ID
     */
    private Review getReviewById(int reviewId) throws SQLException {
        String query = "SELECT * FROM reviews WHERE id = ?";

        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, reviewId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return buildReviewFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving review: " + e.getMessage());
        }

        return null;
    }

    /**
     * Helper method to build Review object from ResultSet
     */
    private Review buildReviewFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String playerName = rs.getString("player_name");
        int tournamentId = rs.getInt("tournament_id");
        String tournamentName = rs.getString("tournament_name");
        int rating = rs.getInt("rating");
        String comment = rs.getString("comment");
        LocalDate reviewDate = rs.getDate("review_date").toLocalDate();
        String status = rs.getString("status");
        String rejectionReason = rs.getString("rejection_reason");
        LocalDateTime createdAt = rs.getTimestamp("created_at") != null ?
                rs.getTimestamp("created_at").toLocalDateTime() : null;
        LocalDateTime updatedAt = rs.getTimestamp("updated_at") != null ?
                rs.getTimestamp("updated_at").toLocalDateTime() : null;

        return new Review(id, playerName, tournamentId, tournamentName, rating, comment,
                reviewDate, status, rejectionReason, createdAt, updatedAt);
    }
}
