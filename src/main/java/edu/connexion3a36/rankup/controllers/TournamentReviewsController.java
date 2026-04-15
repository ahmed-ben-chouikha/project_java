package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.Review;
import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.entities.TournamentRegistration;
import edu.connexion3a36.services.ReviewService;
import edu.connexion3a36.services.TournamentRegistrationService;
import edu.connexion3a36.services.TournamentService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class TournamentReviewsController implements Initializable {

    @FXML private TextField playerNameField;
    @FXML private ComboBox<Tournament> tournamentComboBox;
    @FXML private HBox starRatingContainer;
    @FXML private TextArea commentArea;
    @FXML private DatePicker reviewDatePicker;
    @FXML private Button submitButton;
    @FXML private Button clearButton;
    @FXML private Label charCounter;
    @FXML private Label playerNameError;
    @FXML private Label tournamentError;
    @FXML private Label ratingError;
    @FXML private Label commentError;
    @FXML private TableView<Review> reviewsTableView;
    @FXML private VBox messageContainer;
    @FXML private VBox emptyStateContainer;

    private ReviewService reviewService;
    private TournamentService tournamentService;
    private TournamentRegistrationService registrationService;
    private int selectedRating = 0;
    private List<Button> starButtons = new ArrayList<>();
    private static final String CURRENT_PLAYER = "DefaultPlayer"; // Replace with actual session player

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        setupUI();
        setupEventHandlers();
        loadConfirmedTournaments();
        loadUserReviews();
    }

    private void initializeServices() {
        reviewService = new ReviewService();
        tournamentService = new TournamentService();
        registrationService = new TournamentRegistrationService();
    }

    private void setupUI() {
        // Set review date to today
        reviewDatePicker.setValue(LocalDate.now());
        reviewDatePicker.setDisable(true);

        // Set up tournament ComboBox to display tournament names
        tournamentComboBox.setCellFactory(lv -> new ListCell<Tournament>() {
            @Override
            protected void updateItem(Tournament item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        tournamentComboBox.setButtonCell(new ListCell<Tournament>() {
            @Override
            protected void updateItem(Tournament item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        // Create star rating buttons
        createStarRatingSelector();

        // Set button styles
        submitButton.setStyle("-fx-background-color: linear-gradient(to right, #38bdf8, #8b5cf6); " +
                "-fx-text-fill: white; -fx-font-weight: 700; -fx-background-radius: 14; " +
                "-fx-padding: 12 18 12 18; -fx-font-size: 14px; -fx-cursor: hand;");

        clearButton.setOnAction(e -> clearForm());
        submitButton.setOnAction(e -> handleSubmitReview());
    }

    private void createStarRatingSelector() {
        starRatingContainer.getChildren().clear();
        starButtons.clear();

        for (int i = 1; i <= 5; i++) {
            Button starButton = createStarButton(i);
            starButtons.add(starButton);
            starRatingContainer.getChildren().add(starButton);
        }
    }

    private Button createStarButton(int rating) {
        Button button = new Button("★");
        button.setStyle("-fx-font-size: 32px; -fx-padding: 5; -fx-background-color: transparent; " +
                "-fx-text-fill: #666666; -fx-cursor: hand;");

        button.setOnMouseEntered(e -> highlightStars(rating));
        button.setOnMouseExited(e -> updateStarDisplay());

        button.setOnAction(e -> {
            selectedRating = rating;
            updateStarDisplay();
        });

        return button;
    }

    private void highlightStars(int upToRating) {
        for (int i = 0; i < starButtons.size(); i++) {
            if (i < upToRating) {
                starButtons.get(i).setStyle("-fx-font-size: 32px; -fx-padding: 5; " +
                        "-fx-background-color: transparent; -fx-text-fill: #FFD700; -fx-cursor: hand;");
            } else {
                starButtons.get(i).setStyle("-fx-font-size: 32px; -fx-padding: 5; " +
                        "-fx-background-color: transparent; -fx-text-fill: #666666; -fx-cursor: hand;");
            }
        }
    }

    private void updateStarDisplay() {
        for (int i = 0; i < starButtons.size(); i++) {
            if (i < selectedRating) {
                starButtons.get(i).setStyle("-fx-font-size: 32px; -fx-padding: 5; " +
                        "-fx-background-color: transparent; -fx-text-fill: #00BCD4; -fx-cursor: hand;");
            } else {
                starButtons.get(i).setStyle("-fx-font-size: 32px; -fx-padding: 5; " +
                        "-fx-background-color: transparent; -fx-text-fill: #666666; -fx-cursor: hand;");
            }
        }
    }

    private void setupEventHandlers() {
        playerNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            playerNameError.setText("");
        });

        tournamentComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            tournamentError.setText("");
        });

        commentArea.textProperty().addListener((obs, oldVal, newVal) -> {
            String text = newVal;
            int length = text.length();
            charCounter.setText(length + "/300");

            if (length < 10) {
                charCounter.setStyle("-fx-text-fill: #ff6b6b;");
            } else if (length <= 300) {
                charCounter.setStyle("-fx-text-fill: #8bd8ff;");
            } else {
                charCounter.setStyle("-fx-text-fill: #ff6b6b;");
                commentArea.deleteText(300, length);
            }
            commentError.setText("");
        });
    }

    private void loadConfirmedTournaments() {
        try {
            List<TournamentRegistration> registrations = registrationService.getPlayerRegistrations(CURRENT_PLAYER);
            List<Tournament> confirmedTournaments = new ArrayList<>();

            for (TournamentRegistration reg : registrations) {
                if ("confirmed".equalsIgnoreCase(reg.getStatus())) {
                    // Get tournament details by ID
                    Tournament tournament = tournamentService.getTournamentById(reg.getTournamentId());
                    if (tournament != null) {
                        confirmedTournaments.add(tournament);
                    }
                }
            }

            tournamentComboBox.getItems().setAll(confirmedTournaments);
        } catch (SQLException e) {
            showError("Error loading tournaments: " + e.getMessage());
        }
    }

    private void loadUserReviews() {
        try {
            List<Review> userReviews = reviewService.getReviewsByPlayer(CURRENT_PLAYER);

            if (userReviews.isEmpty()) {
                emptyStateContainer.setVisible(true);
                reviewsTableView.setVisible(false);
            } else {
                emptyStateContainer.setVisible(false);
                reviewsTableView.setVisible(true);

                // Populate table columns dynamically with star display
                populateReviewsTable(userReviews);
            }
        } catch (SQLException e) {
            showError("Error loading reviews: " + e.getMessage());
        }
    }

    private void populateReviewsTable(List<Review> reviews) {
        reviewsTableView.getItems().setAll(reviews);

        // Custom cell factory for rating column to display stars
        TableColumn<Review, Integer> ratingColumn =
                (TableColumn<Review, Integer>) reviewsTableView.getColumns().get(1);
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

        // Custom cell factory for actions column
        TableColumn<Review, Void> actionsColumn =
                (TableColumn<Review, Void>) reviewsTableView.getColumns().get(6);
        actionsColumn.setCellFactory(column -> new TableCell<Review, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setStyle("-fx-background-color: #38bdf8; -fx-text-fill: white; " +
                        "-fx-padding: 6 12 6 12; -fx-background-radius: 8; -fx-font-size: 12px;");
                deleteButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; " +
                        "-fx-padding: 6 12 6 12; -fx-background-radius: 8; -fx-font-size: 12px;");

                editButton.setOnAction(e -> handleEditReview(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> handleDeleteReview(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Review review = getTableView().getItems().get(getIndex());
                    if ("pending".equalsIgnoreCase(review.getStatus())) {
                        HBox actionsBox = new HBox(5, editButton, deleteButton);
                        actionsBox.setAlignment(Pos.CENTER);
                        setGraphic(actionsBox);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void handleSubmitReview() {
        // Clear error messages
        clearErrorMessages();

        // Validate inputs
        String playerName = playerNameField.getText().trim();
        Tournament selectedTournament = tournamentComboBox.getValue();
        String comment = commentArea.getText().trim();

        if (playerName.isEmpty()) {
            playerNameError.setText("Player name is required");
            return;
        }

        if (selectedTournament == null) {
            tournamentError.setText("Please select a tournament");
            return;
        }

        if (selectedRating == 0) {
            ratingError.setText("Please select a rating (1-5 stars)");
            return;
        }

        if (comment.length() < 10 || comment.length() > 300) {
            commentError.setText("Comment must be between 10 and 300 characters");
            return;
        }

        // Create review object
        Review review = new Review(playerName, selectedTournament.getId(),
                selectedTournament.getName(), selectedRating, comment, LocalDate.now());

        try {
            reviewService.addEntity(review);
            showSuccess("Review submitted successfully! Waiting for admin approval.");
            clearForm();
            loadUserReviews();
        } catch (SQLException e) {
            showError("Error submitting review: " + e.getMessage());
        }
    }

    private void handleEditReview(Review review) {
        // Show edit dialog or navigate to edit screen
        TextInputDialog commentDialog = new TextInputDialog(review.getComment());
        commentDialog.setTitle("Edit Review");
        commentDialog.setHeaderText("Edit your review comment");
        commentDialog.setContentText("Comment:");

        Optional<String> result = commentDialog.showAndWait();
        if (result.isPresent()) {
            String newComment = result.get().trim();
            if (newComment.length() < 10 || newComment.length() > 300) {
                showError("Comment must be between 10 and 300 characters");
                return;
            }

            review.setComment(newComment);
            try {
                reviewService.updateEntity(review.getId(), review);
                showSuccess("Review updated successfully!");
                loadUserReviews();
            } catch (SQLException e) {
                showError("Error updating review: " + e.getMessage());
            }
        }
    }

    private void handleDeleteReview(Review review) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Review");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reviewService.deleteEntity(review);
                showSuccess("Review deleted successfully!");
                loadUserReviews();
            } catch (SQLException e) {
                showError("Error deleting review: " + e.getMessage());
            }
        }
    }

    private void clearForm() {
        playerNameField.clear();
        tournamentComboBox.setValue(null);
        selectedRating = 0;
        updateStarDisplay();
        commentArea.clear();
        reviewDatePicker.setValue(LocalDate.now());
        clearErrorMessages();
        messageContainer.getChildren().clear();
    }

    private void clearErrorMessages() {
        playerNameError.setText("");
        tournamentError.setText("");
        ratingError.setText("");
        commentError.setText("");
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
                ("-fx-text-fill: #34A853;".equals("success") ? "-fx-text-fill: #34A853;" : "-fx-text-fill: #ff6b6b;"));
        messageLabel.setWrapText(true);

        messageBox.getChildren().add(messageLabel);
        messageContainer.getChildren().add(messageBox);
        messageContainer.setVisible(true);
    }
}
