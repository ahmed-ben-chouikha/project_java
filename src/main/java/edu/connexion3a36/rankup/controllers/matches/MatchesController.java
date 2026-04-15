package edu.connexion3a36.rankup.controllers.matches;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.entities.Match;
import edu.connexion3a36.services.MatchService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

import java.sql.SQLException;
import java.util.List;

public class MatchesController {

    private static final int PAGE_SIZE = 8;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Match> matchesTable;
    @FXML private TableColumn<Match, String> team1Col;
    @FXML private TableColumn<Match, String> scoreCol;
    @FXML private TableColumn<Match, String> team2Col;
    @FXML private TableColumn<Match, String> dateCol;
    @FXML private TableColumn<Match, String> statusCol;
    @FXML private Pagination pagination;

    private final MatchService matchService = new MatchService();
    private final ObservableList<Match> pageItems = FXCollections.observableArrayList();
    private FilteredList<Match> filtered;

    @FXML
    void initialize() {
        team1Col.setCellValueFactory(new PropertyValueFactory<>("team1"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        team2Col.setCellValueFactory(new PropertyValueFactory<>("team2"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        filtered = new FilteredList<>(FXCollections.observableArrayList());
        matchesTable.setItems(pageItems);
        pagination.setMaxPageIndicatorCount(5);
        pagination.setPageFactory(pageIndex -> {
            updateVisibleRows(pageIndex);
            return new Region();
        });

        statusFilter.setItems(FXCollections.observableArrayList("All", "Pending", "Ongoing", "Finished"));
        statusFilter.setValue("All");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        loadMatches();
    }

    @FXML
    void onCreateMatch(ActionEvent event) {
        MatchFormState.clear();
        RankUpApp.loadInBase("/views/matches/match-form.fxml");
    }

    @FXML
    void onViewMatch(ActionEvent event) {
        Match selected = matchesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a match first.");
            return;
        }
        showInfo("Match details", selected.getTeam1() + " vs " + selected.getTeam2() + "\nScore: " + selected.getScore() + "\nDate: " + selected.getDate() + "\nStatus: " + selected.getStatus());
    }

    @FXML
    void onEditMatch(ActionEvent event) {
        Match selected = matchesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a match first.");
            return;
        }
        MatchFormState.setEditingMatch(selected);
        RankUpApp.loadInBase("/views/matches/match-form.fxml");
    }

    @FXML
    void onDeleteMatch(ActionEvent event) {
        Match selected = matchesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a match first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Match");
        confirm.setHeaderText("Delete selected match?");
        confirm.setContentText(selected.getTeam1() + " vs " + selected.getTeam2());

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    matchService.deleteMatchById(selected.getId());
                    loadMatches();
                } catch (SQLException e) {
                    showError("Database Error", "Could not delete match.\n" + e.getMessage());
                }
            }
        });
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

        refreshPagination(true);
    }

    private void loadMatches() {
        try {
            List<Match> rows = matchService.getAllMatches();
            filtered = new FilteredList<>(FXCollections.observableArrayList(rows));
            applyFilter();
        } catch (SQLException e) {
            filtered = new FilteredList<>(FXCollections.observableArrayList());
            refreshPagination(true);
            showError("Database Error", "Could not load matches from the database.\n" + e.getMessage());
        }
    }

    private void refreshPagination(boolean resetToFirstPage) {
        int totalItems = filtered == null ? 0 : filtered.size();
        int pageCount = Math.max(1, (int) Math.ceil(totalItems / (double) PAGE_SIZE));
        pagination.setPageCount(pageCount);

        int targetPage = resetToFirstPage ? 0 : Math.min(pagination.getCurrentPageIndex(), pageCount - 1);
        if (pagination.getCurrentPageIndex() != targetPage) {
            pagination.setCurrentPageIndex(targetPage);
        }
        updateVisibleRows(targetPage);
    }

    private void updateVisibleRows(int pageIndex) {
        if (filtered == null) {
            pageItems.clear();
            return;
        }

        int fromIndex = Math.max(0, pageIndex) * PAGE_SIZE;
        if (fromIndex >= filtered.size()) {
            pageItems.clear();
            return;
        }

        int toIndex = Math.min(fromIndex + PAGE_SIZE, filtered.size());
        pageItems.setAll(filtered.subList(fromIndex, toIndex));
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
}

