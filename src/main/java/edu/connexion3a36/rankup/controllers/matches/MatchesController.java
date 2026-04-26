package edu.connexion3a36.rankup.controllers.matches;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MatchesController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<MatchRow> matchesTable;
    @FXML private TableColumn<MatchRow, String> team1Col;
    @FXML private TableColumn<MatchRow, String> scoreCol;
    @FXML private TableColumn<MatchRow, String> team2Col;
    @FXML private TableColumn<MatchRow, String> dateCol;
    @FXML private TableColumn<MatchRow, String> statusCol;
    @FXML private Pagination pagination;

    private FilteredList<MatchRow> filtered;

    @FXML
    void initialize() {
        team1Col.setCellValueFactory(new PropertyValueFactory<>("team1"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        team2Col.setCellValueFactory(new PropertyValueFactory<>("team2"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        filtered = new FilteredList<>(FXCollections.observableArrayList(
                new MatchRow("Falcons", "2 - 1", "Nova", "2026-04-12 18:30", "Finished"),
                new MatchRow("Apex", "0 - 0", "Vortex", "2026-04-13 20:00", "Pending"),
                new MatchRow("Titan", "1 - 1", "Eclipse", "2026-04-11 19:00", "Ongoing"),
                new MatchRow("Sigma", "-", "Blaze", "2026-04-14 17:00", "Pending")
        ));

        statusFilter.setItems(FXCollections.observableArrayList("All", "Pending", "Ongoing", "Finished"));
        statusFilter.setValue("All");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        matchesTable.setItems(filtered);
        pagination.setPageCount(1);
    }

    @FXML
    void onCreateMatch(ActionEvent event) {
        RankUpApp.loadInBase("/views/matches/match-form.fxml");
    }

    @FXML
    void onViewMatch(ActionEvent event) {
        RankUpApp.loadInBase("/views/matches/match-details.fxml");
    }

    @FXML
    void onEditMatch(ActionEvent event) {
        RankUpApp.loadInBase("/views/matches/match-form.fxml");
    }

    @FXML
    void onDeleteMatch(ActionEvent event) {
        showInfo("Placeholder", "Delete handler placeholder.");
    }

    private void applyFilter() {
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String status = statusFilter.getValue();

        filtered.setPredicate(row -> {
            boolean statusOk = "All".equals(status) || row.getStatus().equalsIgnoreCase(status);
            boolean searchOk = row.getTeam1().toLowerCase().contains(q)
                    || row.getTeam2().toLowerCase().contains(q)
                    || row.getDate().toLowerCase().contains(q);
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

    public static class MatchRow {
        private final String team1;
        private final String score;
        private final String team2;
        private final String date;
        private final String status;

        public MatchRow(String team1, String score, String team2, String date, String status) {
            this.team1 = team1;
            this.score = score;
            this.team2 = team2;
            this.date = date;
            this.status = status;
        }

        public String getTeam1() { return team1; }
        public String getScore() { return score; }
        public String getTeam2() { return team2; }
        public String getDate() { return date; }
        public String getStatus() { return status; }
    }
}

