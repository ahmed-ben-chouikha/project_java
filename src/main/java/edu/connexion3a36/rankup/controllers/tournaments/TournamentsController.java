package edu.connexion3a36.rankup.controllers.tournaments;

import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.entities.Review;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.ReviewService;
import edu.connexion3a36.services.TournamentService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TournamentsController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Tournament> tournamentsTable;
    @FXML private TableColumn<Tournament, String> nameCol;
    @FXML private TableColumn<Tournament, String> descriptionCol;
    @FXML private TableColumn<Tournament, String> startDateCol;
    @FXML private TableColumn<Tournament, String> endDateCol;
    @FXML private TableColumn<Tournament, String> locationCol;
    @FXML private TableColumn<Tournament, String> statusCol;
    @FXML private TableColumn<Tournament, String> ratingsCol;
    @FXML private TableColumn<Tournament, Void> reviewCol;
    @FXML private Pagination pagination;

    private final TournamentService tournamentService = new TournamentService();
    private final ReviewService reviewService = new ReviewService();
    private FilteredList<Tournament> filtered;

    @FXML
    void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        setupRatingsColumn();
        setupReviewColumn();

        filtered = new FilteredList<>(FXCollections.observableArrayList());

        statusFilter.setItems(FXCollections.observableArrayList("All", "open", "closed", "finished"));
        statusFilter.setValue("All");

        loadTournaments();
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        tournamentsTable.setItems(filtered);
        pagination.setPageCount(1);
    }

    @FXML
    void onCreateTournament(ActionEvent event) {
        TournamentFormState.clear();
        RankUpApp.loadInBase("/views/tournaments/tournament-form.fxml");
    }

    @FXML
    void onViewTournament(ActionEvent event) {
        Tournament selected = tournamentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a tournament first.");
            return;
        }
        showInfo("Tournament details",
                "Name: " + selected.getName() +
                        "\nDescription: " + safe(selected.getDescription()) +
                        "\nStart: " + selected.getStartDate() +
                        "\nEnd: " + selected.getEndDate() +
                        "\nStatus: " + selected.getStatus() +
                        "\nLocation: " + safe(selected.getLocation()) +
                        "\nPrize Pool: " + selected.getPrizePool());
    }

    @FXML
    void onTournamentReviews(ActionEvent event) {
        Tournament selected = tournamentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a tournament first.");
            return;
        }
        openReviewFlow(selected);
    }

    @FXML
    void onEditTournament(ActionEvent event) {
        Tournament selected = tournamentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a tournament first.");
            return;
        }
        TournamentFormState.setEditingTournament(selected);
        RankUpApp.loadInBase("/views/tournaments/tournament-form.fxml");
    }

    @FXML
    void onDeleteTournament(ActionEvent event) {
        Tournament selected = tournamentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a tournament first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Tournament");
        confirm.setHeaderText("Delete selected tournament?");
        confirm.setContentText(selected.getName());

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    tournamentService.deleteEntity(selected);
                    loadTournaments();
                } catch (SQLException e) {
                    showError("Database Error", e.getMessage());
                }
            }
        });
    }

    private void applyFilter() {
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        filtered.setPredicate(row -> {
            boolean statusOk = "All".equals(status) || normalize(row.getStatus()).equalsIgnoreCase(status);
            boolean searchOk = row.getName().toLowerCase().contains(q)
                    || safe(row.getDescription()).toLowerCase().contains(q)
                    || safe(row.getLocation()).toLowerCase().contains(q);
            return statusOk && searchOk;
        });
    }

    private void loadTournaments() {
        try {
            List<Tournament> rows = tournamentService.getData();
            filtered = new FilteredList<>(FXCollections.observableArrayList(rows));
            tournamentsTable.setItems(filtered);
            applyFilter();
        } catch (SQLException e) {
            filtered = new FilteredList<>(FXCollections.observableArrayList());
            tournamentsTable.setItems(filtered);
            showError("Database Error", "Could not load tournaments.\n" + e.getMessage());
        }
    }

    private void setupRatingsColumn() {
        ratingsCol.setCellValueFactory(cellData -> new SimpleStringProperty(buildRatingStars(cellData.getValue())));
        ratingsCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    setOnMouseClicked(null);
                    return;
                }
                setText(item);
                setStyle("-fx-text-fill: #ffd166; -fx-font-weight: 700; -fx-cursor: hand;");
                setOnMouseClicked(e -> {
                    Tournament tournament = getTableView().getItems().get(getIndex());
                    openReviewFlow(tournament);
                });
            }
        });
    }

    private void setupReviewColumn() {
        reviewCol.setCellFactory(column -> new TableCell<>() {
            private final Button reviewButton = new Button("Review");

            {
                reviewButton.getStyleClass().add("btn-secondary");
                reviewButton.setOnAction(e -> {
                    Tournament tournament = getTableView().getItems().get(getIndex());
                    openReviewFlow(tournament);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(reviewButton);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    private String buildRatingStars(Tournament tournament) {
        try {
            List<Review> reviews = reviewService.getReviewsByTournament(tournament.getId());
            if (reviews.isEmpty()) {
                return "☆☆☆☆☆";
            }

            double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            int rounded = (int) Math.round(average);
            StringBuilder stars = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                stars.append(i < rounded ? '★' : '☆');
            }
            return stars + String.format(" %.1f", average);
        } catch (SQLException e) {
            return "—";
        }
    }

    private void openReviewFlow(Tournament selected) {
        TournamentReviewState.setSelectedTournament(selected);
        RankUpApp.loadInBase("/views/tournaments/tournament-reviews.fxml");
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String normalize(String status) {
        if (status == null) {
            return "";
        }
        return status.trim().toLowerCase();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}

