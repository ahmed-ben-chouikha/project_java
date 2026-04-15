package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.Review;
import edu.connexion3a36.services.ReviewService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminReviewModerationController implements Initializable {

    @FXML private TableView<Review> reviewsTableView;
    @FXML private Label pendingCountLabel;
    @FXML private Label approvedCountLabel;
    @FXML private Label rejectedCountLabel;
    @FXML private Button refreshButton;
    @FXML private VBox messageContainer;
    @FXML private VBox emptyStateContainer;

    private ReviewService reviewService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        setupEventHandlers();
        loadPendingReviews();
    }

    private void initializeServices() {
        reviewService = new ReviewService();
    }

    private void setupEventHandlers() {
        refreshButton.setOnAction(e -> loadPendingReviews());
    }

    private void loadPendingReviews() {
        try {
            List<Review> pendingReviews = reviewService.getPendingReviews();

            if (pendingReviews.isEmpty()) {
                emptyStateContainer.setVisible(true);
                reviewsTableView.setVisible(false);
            } else {
                emptyStateContainer.setVisible(false);
                reviewsTableView.setVisible(true);
                populateReviewsTable(pendingReviews);
            }

            updateStats();
        } catch (SQLException e) {
            showError("Error loading pending reviews: " + e.getMessage());
        }
    }

    private void populateReviewsTable(List<Review> reviews) {
        reviewsTableView.getItems().setAll(reviews);

        // Custom cell factory for rating column to display stars
        TableColumn<Review, Integer> ratingColumn =
                (TableColumn<Review, Integer>) reviewsTableView.getColumns().get(2);
        ratingColumn.setCellValueFactory(cellData -> {
            javafx.beans.value.ObservableValue<Integer> obs =
                    new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getRating());
            return (javafx.beans.value.ObservableValue) obs;
        });
        ratingColumn.setCellFactory(column -> new TableCell<Review, Integer>() {
            @Override
            protected void updateItem(Integer rating, boolean empty) {
                super.updateItem(rating, empty);
                if (empty || rating == null) {
                    setText(null);
                } else {
                    setText("★".repeat(rating));
                    setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14px;");
                }
            }
        });

        // Custom cell factory for status column to show colored badges
        TableColumn<Review, String> statusColumn =
                (TableColumn<Review, String>) reviewsTableView.getColumns().get(5);
        statusColumn.setCellValueFactory(cellData -> {
            javafx.beans.value.ObservableValue<String> obs =
                    new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStatus());
            return (javafx.beans.value.ObservableValue) obs;
        });
        statusColumn.setCellFactory(column -> new TableCell<Review, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toUpperCase());
                    if ("pending".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
                    } else if ("approved".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #34A853; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Custom cell factory for actions column
        TableColumn<Review, Void> actionsColumn =
                (TableColumn<Review, Void>) reviewsTableView.getColumns().get(6);
        actionsColumn.setCellFactory(column -> new TableCell<Review, Void>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");

            {
                approveButton.setStyle("-fx-background-color: #34A853; -fx-text-fill: white; " +
                        "-fx-padding: 6 12 6 12; -fx-background-radius: 8; -fx-font-size: 12px; -fx-cursor: hand;");
                rejectButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; " +
                        "-fx-padding: 6 12 6 12; -fx-background-radius: 8; -fx-font-size: 12px; -fx-cursor: hand;");

                approveButton.setOnAction(e -> handleApproveReview(getTableView().getItems().get(getIndex())));
                rejectButton.setOnAction(e -> handleRejectReview(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Review review = getTableView().getItems().get(getIndex());
                    if ("pending".equalsIgnoreCase(review.getStatus())) {
                        HBox actionsBox = new HBox(5, approveButton, rejectButton);
                        actionsBox.setAlignment(Pos.CENTER);
                        setGraphic(actionsBox);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void handleApproveReview(Review review) {
        try {
            reviewService.approveReview(review.getId());
            showSuccess("Review approved successfully!");
            loadPendingReviews();
        } catch (SQLException e) {
            showError("Error approving review: " + e.getMessage());
        }
    }

    private void handleRejectReview(Review review) {
        // Show rejection reason dialog
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Reject Review");
        dialog.setHeaderText("Enter rejection reason");
        dialog.setContentText("Reason:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            try {
                reviewService.rejectReview(review.getId(), result.get().trim());
                showSuccess("Review rejected successfully!");
                loadPendingReviews();
            } catch (SQLException e) {
                showError("Error rejecting review: " + e.getMessage());
            }
        }
    }

    private void updateStats() {
        try {
            List<Review> pending = reviewService.getReviewsByStatus("pending");
            List<Review> approved = reviewService.getReviewsByStatus("approved");
            List<Review> rejected = reviewService.getReviewsByStatus("rejected");

            pendingCountLabel.setText(String.valueOf(pending.size()));
            approvedCountLabel.setText(String.valueOf(approved.size()));
            rejectedCountLabel.setText(String.valueOf(rejected.size()));
        } catch (SQLException e) {
            showError("Error updating stats: " + e.getMessage());
        }
    }

    private void showSuccess(String message) {
        displayMessage(message, "success");
    }

    private void showError(String message) {
        displayMessage(message, "error");
    }

    private void displayMessage(String message, String type) {
        messageContainer.getChildren().clear();

        HBox messageBox = new HBox();
        messageBox.setPadding(new javafx.geometry.Insets(12));
        messageBox.setStyle("-fx-border-radius: 10;");

        if ("success".equals(type)) {
            messageBox.setStyle(messageBox.getStyle() + " -fx-background-color: rgba(52, 168, 83, 0.2); " +
                    "-fx-border-color: rgba(52, 168, 83, 0.5); -fx-border-width: 1;");
        } else {
            messageBox.setStyle(messageBox.getStyle() + " -fx-background-color: rgba(255, 107, 107, 0.2); " +
                    "-fx-border-color: rgba(255, 107, 107, 0.5); -fx-border-width: 1;");
        }

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 13px; " +
                ("success".equals(type) ? "-fx-text-fill: #34A853;" : "-fx-text-fill: #ff6b6b;"));
        messageLabel.setWrapText(true);

        messageBox.getChildren().add(messageLabel);
        messageContainer.getChildren().add(messageBox);
        messageContainer.setVisible(true);
    }
}
