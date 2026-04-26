package edu.connexion3a36.rankup.controllers.tournaments;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TournamentsController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<TournamentRow> tournamentsTable;
    @FXML private TableColumn<TournamentRow, String> nameCol;
    @FXML private TableColumn<TournamentRow, String> startDateCol;
    @FXML private TableColumn<TournamentRow, String> endDateCol;
    @FXML private TableColumn<TournamentRow, String> statusCol;
    @FXML private TableColumn<TournamentRow, String> prizePoolCol;
    @FXML private Pagination pagination;

    private FilteredList<TournamentRow> filtered;

    @FXML
    void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        prizePoolCol.setCellValueFactory(new PropertyValueFactory<>("prizePool"));

        filtered = new FilteredList<>(FXCollections.observableArrayList(
                new TournamentRow("RankUp Spring Cup", "2026-05-01", "2026-05-10", "Upcoming", "$50,000"),
                new TournamentRow("Arena Masters", "2026-04-05", "2026-04-20", "Ongoing", "$20,000"),
                new TournamentRow("Winter Clash", "2026-01-10", "2026-01-25", "Finished", "$35,000")
        ));

        statusFilter.setItems(FXCollections.observableArrayList("All", "Upcoming", "Ongoing", "Finished"));
        statusFilter.setValue("All");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        tournamentsTable.setItems(filtered);
        pagination.setPageCount(1);
    }

    @FXML
    void onCreateTournament(ActionEvent event) { showInfo("Placeholder", "Create tournament form placeholder."); }

    @FXML
    void onViewTournament(ActionEvent event) { RankUpApp.loadInBase("/views/tournaments/tournament-details.fxml"); }

    @FXML
    void onEditTournament(ActionEvent event) { showInfo("Placeholder", "Edit tournament placeholder."); }

    @FXML
    void onDeleteTournament(ActionEvent event) { showInfo("Placeholder", "Delete tournament placeholder."); }

    private void applyFilter() {
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        filtered.setPredicate(row -> {
            boolean statusOk = "All".equals(status) || row.getStatus().equalsIgnoreCase(status);
            boolean searchOk = row.getName().toLowerCase().contains(q);
            return statusOk && searchOk;
        });
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class TournamentRow {
        private final String name;
        private final String startDate;
        private final String endDate;
        private final String status;
        private final String prizePool;

        public TournamentRow(String name, String startDate, String endDate, String status, String prizePool) {
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.prizePool = prizePool;
        }

        public String getName() { return name; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getStatus() { return status; }
        public String getPrizePool() { return prizePool; }
    }
}

