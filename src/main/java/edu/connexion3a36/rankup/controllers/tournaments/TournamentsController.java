package edu.connexion3a36.rankup.controllers.tournaments;

import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TournamentService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class TournamentsController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Tournament> tournamentsTable;
    @FXML private TableColumn<Tournament, String> nameCol;
    @FXML private TableColumn<Tournament, String> gameTypeCol;
    @FXML private TableColumn<Tournament, String> startDateCol;
    @FXML private TableColumn<Tournament, String> endDateCol;
    @FXML private TableColumn<Tournament, Integer> maxTeamsCol;
    @FXML private TableColumn<Tournament, String> statusCol;
    @FXML private Pagination pagination;

    private final TournamentService tournamentService = new TournamentService();
    private FilteredList<Tournament> filtered;

    @FXML
    void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        gameTypeCol.setCellValueFactory(new PropertyValueFactory<>("gameType"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        maxTeamsCol.setCellValueFactory(new PropertyValueFactory<>("maxTeams"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

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
                        "\nGame: " + safe(selected.getGameType()) +
                        "\nStart: " + selected.getStartDate() +
                        "\nEnd: " + selected.getEndDate() +
                        "\nMax Teams: " + selected.getMaxTeams() +
                        "\nStatus: " + selected.getStatus() +
                        "\nLocation: " + safe(selected.getLocation()) +
                        "\nPrize Pool: " + selected.getPrizePool());
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
                    || safe(row.getGameType()).toLowerCase().contains(q);
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

