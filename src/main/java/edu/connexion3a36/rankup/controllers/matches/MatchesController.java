package edu.connexion3a36.rankup.controllers.matches;

import edu.connexion3a36.entities.Match;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.MatchService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MatchesController {

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
    private FilteredList<Match> filtered;

    @FXML
    void initialize() {
        team1Col.setCellValueFactory(new PropertyValueFactory<>("team1"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        team2Col.setCellValueFactory(new PropertyValueFactory<>("team2"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        filtered = new FilteredList<>(FXCollections.observableArrayList());

        statusFilter.setItems(FXCollections.observableArrayList("All", "Pending", "Ongoing", "Finished"));
        statusFilter.setValue("All");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        loadMatches();
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
        Match selected = matchesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a match first.");
            return;
        }

        Dialog<Match> dialog = buildEditDialog(selected);
        dialog.showAndWait().ifPresent(updated -> {
            try {
                matchService.updateMatch(updated);
                loadMatches();
                showInfo("Updated", "Match updated successfully.");
            } catch (SQLException e) {
                showError("Database Error", "Could not update the match.\n" + e.getMessage());
            }
        });
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
                    showError("Database Error", "Could not delete the match.\n" + e.getMessage());
                }
            }
        });
    }

    private void applyFilter() {
        if (filtered == null) {
            return;
        }

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

    private void loadMatches() {
        try {
            List<Match> rows = matchService.getAllMatches();
            filtered = new FilteredList<>(FXCollections.observableArrayList(rows));
            matchesTable.setItems(filtered);
            applyFilter();
        } catch (SQLException e) {
            filtered = new FilteredList<>(FXCollections.observableArrayList());
            matchesTable.setItems(filtered);
            showError("Database Error", "Could not load matches from the database.\n" + e.getMessage());
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

    private Dialog<Match> buildEditDialog(Match selected) {
        Dialog<Match> dialog = new Dialog<>();
        dialog.setTitle("Edit Match");
        dialog.setHeaderText(selected.getTeam1() + " vs " + selected.getTeam2());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        TextField score1Field = new TextField();
        TextField score2Field = new TextField();
        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("pending", "ongoing", "finished"));

        LocalDateTime currentDate = parseDateTime(selected.getMatchDate());
        datePicker.setValue(currentDate.toLocalDate());
        timeField.setText(currentDate.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        score1Field.setText(String.valueOf(selected.getScore1()));
        score2Field.setText(String.valueOf(selected.getScore2()));
        statusBox.setValue(selected.getStatus().toLowerCase());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Date"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Time"), 0, 1);
        grid.add(timeField, 1, 1);
        grid.add(new Label("Score 1"), 0, 2);
        grid.add(score1Field, 1, 2);
        grid.add(new Label("Score 2"), 0, 3);
        grid.add(score2Field, 1, 3);
        grid.add(new Label("Status"), 0, 4);
        grid.add(statusBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button != saveButtonType) {
                return null;
            }

            int score1 = parseIntSafe(score1Field.getText());
            int score2 = parseIntSafe(score2Field.getText());
            String status = statusBox.getValue() == null ? selected.getStatus() : statusBox.getValue();

            LocalDate date = datePicker.getValue() == null ? currentDate.toLocalDate() : datePicker.getValue();
            LocalTime time = parseTime(timeField.getText());
            String dateTime = LocalDateTime.of(date, time).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            return new Match(
                    selected.getId(),
                    selected.getTeam1Id(),
                    selected.getTeam2Id(),
                    selected.getTournamentId(),
                    score1,
                    score2,
                    dateTime,
                    selected.getTeam1(),
                    selected.getTeam2(),
                    status
            );
        });

        return dialog;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return LocalDateTime.now();
        }

        DateTimeFormatter[] formats = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        };

        for (DateTimeFormatter formatter : formats) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }

        return LocalDateTime.now();
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return LocalTime.of(0, 0, 0);
        }

        DateTimeFormatter[] formats = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("HH:mm:ss"),
                DateTimeFormatter.ofPattern("HH:mm")
        };

        for (DateTimeFormatter formatter : formats) {
            try {
                return LocalTime.parse(value.trim(), formatter);
            } catch (DateTimeParseException ignored) {
                // try next format
            }
        }

        return LocalTime.of(0, 0, 0);
    }

    private int parseIntSafe(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

