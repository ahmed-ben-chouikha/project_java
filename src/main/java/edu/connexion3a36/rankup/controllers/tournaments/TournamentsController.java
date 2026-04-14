package edu.connexion3a36.rankup.controllers.tournaments;

import edu.connexion3a36.entities.Review;
import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.ReviewService;
import edu.connexion3a36.services.TournamentService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TournamentsController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<TournamentRow> tournamentsTable;
    @FXML private TableColumn<TournamentRow, String> nameCol;
    @FXML private TableColumn<TournamentRow, String> descriptionCol;
    @FXML private TableColumn<TournamentRow, String> startDateCol;
    @FXML private TableColumn<TournamentRow, String> endDateCol;
    @FXML private TableColumn<TournamentRow, String> locationCol;
    @FXML private TableColumn<TournamentRow, String> statusCol;
    @FXML private TableColumn<TournamentRow, String> ratingsCol;
    @FXML private TableColumn<TournamentRow, Void> reviewCol;
    @FXML private Pagination pagination;

    private FilteredList<TournamentRow> filtered;
    private TournamentService tournamentService;
    private ReviewService reviewService;
    private Tournament selectedTournament;

    @FXML
    void initialize() {
        tournamentService = new TournamentService();
        reviewService = new ReviewService();

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Setup ratings column
        setupRatingsColumn();
        
        // Setup review column with buttons
        setupReviewColumn();

        statusFilter.setItems(FXCollections.observableArrayList("All", "pending", "ongoing", "finished"));
        statusFilter.setValue("All");

        loadTournaments();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        tournamentsTable.setItems(filtered);
        tournamentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTournament = new Tournament();
                selectedTournament.setId(newVal.getId());
                selectedTournament.setName(newVal.getName());
            }
        });

        pagination.setPageCount(1);
    }

    private void loadTournaments() {
        try {
            List<Tournament> tournaments = tournamentService.getData();
            filtered = new FilteredList<>(FXCollections.observableArrayList(
                tournaments.stream().map(TournamentRow::fromTournament).toArray(TournamentRow[]::new)
            ));
        } catch (SQLException e) {
            showError("Failed to load tournaments: " + e.getMessage());
            filtered = new FilteredList<>(FXCollections.observableArrayList());
        }
    }

    @FXML
    void onCreateTournament(ActionEvent event) {
        RankUpApp.loadInBase("/views/tournaments/tournament-form.fxml");
    }

    @FXML
    void onViewTournament(ActionEvent event) {
        if (selectedTournament != null) {
            try {
                Tournament t = tournamentService.getTournamentById(selectedTournament.getId());
                if (t != null) {
                    showInfo("Tournament Details", String.format(
                        "Name: %s\nLocation: %s\nStart: %s\nEnd: %s\nStatus: %s\nPrize Pool: %.0f\n\nDescription:\n%s",
                        t.getName(),
                        t.getLocation(),
                        t.getStartDate(),
                        t.getEndDate(),
                        t.getStatus(),
                        t.getPrizePool(),
                        t.getDescription()
                    ));
                }
            } catch (SQLException e) {
                showError("Failed to load tournament: " + e.getMessage());
            }
        } else {
            showError("Please select a tournament");
        }
    }

    @FXML
    void onEditTournament(ActionEvent event) {
        if (selectedTournament == null) {
            showError("Please select a tournament to edit");
            return;
        }
        RankUpApp.loadInBase("/views/tournaments/tournament-form.fxml");
    }

    @FXML
    void onDeleteTournament(ActionEvent event) {
        if (selectedTournament == null) {
            showError("Please select a tournament to delete");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Tournament");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("Delete tournament: " + selectedTournament.getName() + "?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                tournamentService.deleteEntity(selectedTournament);
                showSuccess("Tournament deleted successfully");
                loadTournaments();
                tournamentsTable.setItems(filtered);
            } catch (SQLException e) {
                showError("Failed to delete tournament: " + e.getMessage());
            }
        }
    }

    private void applyFilter() {
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        filtered.setPredicate(row -> {
            boolean statusOk = "All".equals(status) || row.getStatus().equalsIgnoreCase(status);
            boolean searchOk = row.getName().toLowerCase().contains(q);
            return statusOk && searchOk;
        });
    }

    private void setupRatingsColumn() {
        ratingsCol.setCellFactory(col -> new TableCell<TournamentRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0) {
                    setText(null);
                } else {
                    TournamentRow row = getTableView().getItems().get(getIndex());
                    try {
                        List<Review> reviews = reviewService.getReviewsByTournament(row.getId());
                        if (reviews != null && !reviews.isEmpty()) {
                            Review latestReview = reviews.get(0);
                            setText("★ " + latestReview.getRating() + "/5");
                            setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
                        } else {
                            setText("-");
                            setStyle("-fx-text-fill: #999999;");
                        }
                    } catch (Exception e) {
                        setText("-");
                    }
                }
            }
        });
    }

    private void setupReviewColumn() {
        reviewCol.setCellFactory(col -> new TableCell<TournamentRow, Void>() {
            private final Button reviewButton = new Button("Review");

            {
                reviewButton.setStyle("-fx-padding: 5 15 5 15; -fx-font-size: 12px; " +
                        "-fx-background-color: linear-gradient(to right, #38bdf8, #8b5cf6); " +
                        "-fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
                reviewButton.setOnAction(e -> {
                    TournamentRow row = getTableView().getItems().get(getIndex());
                    onReviewTournament(row);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0) {
                    setGraphic(null);
                } else {
                    TournamentRow row = getTableView().getItems().get(getIndex());
                    String status = row.getStatus();
                    if ("ongoing".equalsIgnoreCase(status) || "finished".equalsIgnoreCase(status)) {
                        setGraphic(reviewButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void onReviewTournament(TournamentRow tournamentRow) {
        // Open review dialog for this tournament
        openReviewDialog(tournamentRow.getId(), tournamentRow.getName());
    }

    private void openReviewDialog(int tournamentId, String tournamentName) {
        Dialog<Boolean> reviewDialog = new Dialog<>();
        reviewDialog.setTitle("Submit Review");
        reviewDialog.setHeaderText("Review: " + tournamentName);

        // Create review form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(10));

        // Player name
        TextField playerNameField = new TextField();
        playerNameField.setPromptText("Enter your player name");

        // Rating (stars)
        int[] selectedRating = {0};
        HBox starRatingBox = new HBox(5);
        starRatingBox.setAlignment(Pos.CENTER_LEFT);

        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            Button starBtn = new Button("★");
            starBtn.setStyle("-fx-font-size: 20px; -fx-padding: 0; -fx-background-color: transparent; " +
                    "-fx-text-fill: #666666; -fx-cursor: hand;");

            int finalI = i;
            starBtn.setOnMouseEntered(e -> {
                for (int j = 0; j < finalI && j < starRatingBox.getChildren().size(); j++) {
                    ((Button) starRatingBox.getChildren().get(j)).setStyle(
                            "-fx-font-size: 20px; -fx-padding: 0; -fx-background-color: transparent; " +
                                    "-fx-text-fill: #FFD700; -fx-cursor: hand;");
                }
            });

            starBtn.setOnMouseExited(e -> updateStarDisplayInDialog(starRatingBox, selectedRating[0]));
            starBtn.setOnAction(e -> {
                selectedRating[0] = rating;
                updateStarDisplayInDialog(starRatingBox, selectedRating[0]);
            });

            starRatingBox.getChildren().add(starBtn);
        }

        // Comment
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Share your feedback (10-300 characters)...");
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(4);
        Label charCount = new Label("0/300");

        commentArea.textProperty().addListener((obs, oldVal, newVal) -> {
            int length = newVal.length();
            charCount.setText(length + "/300");
            if (length > 300) {
                commentArea.deleteText(300, length);
            }
        });

        grid.add(new Label("Player Name:"), 0, 0);
        grid.add(playerNameField, 1, 0);
        grid.add(new Label("Rating:"), 0, 1);
        grid.add(starRatingBox, 1, 1);
        grid.add(new Label("Comment:"), 0, 2);
        grid.add(commentArea, 1, 2);
        grid.add(charCount, 1, 3);

        reviewDialog.getDialogPane().setContent(grid);
        reviewDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) reviewDialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText("Submit Review");

        okButton.setOnAction(e -> {
            String playerName = playerNameField.getText().trim();
            String comment = commentArea.getText().trim();

            if (playerName.isEmpty()) {
                showError("Please enter your player name");
                e.consume();
                return;
            }

            if (selectedRating[0] == 0) {
                showError("Please select a rating");
                e.consume();
                return;
            }

            if (comment.length() < 10 || comment.length() > 300) {
                showError("Comment must be between 10 and 300 characters");
                e.consume();
                return;
            }

            reviewDialog.close();
            submitReview(playerName, tournamentId, tournamentName, selectedRating[0], comment);
        });

        reviewDialog.showAndWait();
    }

    private void updateStarDisplayInDialog(HBox starBox, int rating) {
        for (int i = 0; i < starBox.getChildren().size(); i++) {
            Button btn = (Button) starBox.getChildren().get(i);
            if (i < rating) {
                btn.setStyle("-fx-font-size: 20px; -fx-padding: 0; -fx-background-color: transparent; " +
                        "-fx-text-fill: #00BCD4; -fx-cursor: hand;");
            } else {
                btn.setStyle("-fx-font-size: 20px; -fx-padding: 0; -fx-background-color: transparent; " +
                        "-fx-text-fill: #666666; -fx-cursor: hand;");
            }
        }
    }

    private void submitReview(String playerName, int tournamentId, String tournamentName, int rating, String comment) {
        try {
            Review review = new Review(playerName, tournamentId, tournamentName, rating, comment, LocalDate.now());
            review.setStatus("approved");
            reviewService.addEntity(review);
            showSuccess("Review submitted successfully!");
            loadTournaments();
            tournamentsTable.refresh();
        } catch (Exception e) {
            showError("Failed to submit review: " + e.getMessage());
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class TournamentRow {
        private final int id;
        private final String name;
        private final String description;
        private final String startDate;
        private final String endDate;
        private final String location;
        private final String status;

        public TournamentRow(int id, String name, String description, String startDate, String endDate,
                            String location, String status) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
            this.location = location;
            this.status = status;
        }

        public static TournamentRow fromTournament(Tournament t) {
            return new TournamentRow(t.getId(), t.getName(), t.getDescription(),
                t.getStartDate().toString(), t.getEndDate().toString(), t.getLocation(), t.getStatus());
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getLocation() { return location; }
        public String getStatus() { return status; }
    }
}




