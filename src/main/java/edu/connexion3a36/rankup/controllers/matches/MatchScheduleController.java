package edu.connexion3a36.rankup.controllers.matches;

import edu.connexion3a36.entities.Match;
import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.services.MatchService;
import edu.connexion3a36.services.TournamentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Match Schedule — lets users plan upcoming matches by picking teams,
 * date, time and an optional tournament.
 *
 * Builds on the existing MatchService / Match entity without any schema changes.
 * The "scheduled" status is used to distinguish planned matches from live/finished ones.
 */
public class MatchScheduleController implements Initializable {

    // ── Stats labels ──────────────────────────────────────────────────────────
    @FXML private Label scheduledCount;
    @FXML private Label todayCount;
    @FXML private Label liveCount;
    @FXML private Label finishedCount;

    // ── Schedule form ─────────────────────────────────────────────────────────
    @FXML private ComboBox<MatchService.TeamOption> team1Combo;
    @FXML private ComboBox<MatchService.TeamOption> team2Combo;
    @FXML private DatePicker matchDatePicker;
    @FXML private TextField matchTimeField;
    @FXML private ComboBox<TournamentOption> tournamentCombo;
    @FXML private ComboBox<String> statusCombo;

    // ── Filter bar ────────────────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker dateFilter;

    // ── Table ─────────────────────────────────────────────────────────────────
    @FXML private TableView<Match> scheduleTable;
    @FXML private TableColumn<Match, String> dateTimeCol;
    @FXML private TableColumn<Match, String> team1Col;
    @FXML private TableColumn<Match, String> vsCol;
    @FXML private TableColumn<Match, String> team2Col;
    @FXML private TableColumn<Match, String> tournamentCol;
    @FXML private TableColumn<Match, String> statusCol;
    @FXML private TableColumn<Match, Void>   actionsCol;

    private final MatchService      matchService      = new MatchService();
    private final TournamentService tournamentService = new TournamentService();

    private final ObservableList<Match> allMatches = FXCollections.observableArrayList();
    private FilteredList<Match> filtered;

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Simple wrapper for tournament combo ───────────────────────────────────
    public static class TournamentOption {
        private final int    id;
        private final String name;
        TournamentOption(int id, String name) { this.id = id; this.name = name; }
        public int    getId()   { return id; }
        public String getName() { return name; }
        @Override public String toString() { return name; }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupForm();
        setupFilters();
        loadData();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Setup helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void setupTable() {
        dateTimeCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        team1Col.setCellValueFactory(new PropertyValueFactory<>("team1"));
        team2Col.setCellValueFactory(new PropertyValueFactory<>("team2"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // "VS" column — always shows "VS"
        vsCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty ? null : "VS");
                setAlignment(Pos.CENTER);
                setStyle("-fx-font-weight: bold; -fx-text-fill: #94a3b8;");
            }
        });

        // Tournament column — derived from tournamentId
        tournamentCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); return;
                }
                Match m = (Match) getTableRow().getItem();
                setText(m.getTournamentId() > 0 ? "Tournament #" + m.getTournamentId() : "—");
            }
        });

        // Status column with colour badge
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                setText(v);
                String color = switch (v.toLowerCase(Locale.ROOT)) {
                    case "scheduled" -> "#3b82f6";
                    case "ongoing"   -> "#22c55e";
                    case "finished"  -> "#6b7280";
                    case "cancelled" -> "#ef4444";
                    default          -> "#94a3b8";
                };
                setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
            }
        });

        // Actions column
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button cancelBtn = new Button("✕ Cancel");
            private final Button finishBtn = new Button("✔ Finish");
            {
                cancelBtn.getStyleClass().addAll("action-btn", "action-btn-delete");
                finishBtn.getStyleClass().addAll("action-btn", "action-btn-edit");
                cancelBtn.setOnAction(e -> updateStatus(getTableView().getItems().get(getIndex()), "Cancelled"));
                finishBtn.setOnAction(e -> updateStatus(getTableView().getItems().get(getIndex()), "Finished"));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                Match m = getTableView().getItems().get(getIndex());
                String s = m.getStatus() == null ? "" : m.getStatus().toLowerCase(Locale.ROOT);
                HBox box = new HBox(4);
                box.setAlignment(Pos.CENTER);
                if ("scheduled".equals(s) || "pending".equals(s)) {
                    box.getChildren().addAll(finishBtn, cancelBtn);
                }
                setGraphic(box);
            }
        });
    }

    private void setupForm() {
        // Status options for new match
        statusCombo.setItems(FXCollections.observableArrayList(
                "Scheduled", "Ongoing", "Finished", "Cancelled"));
        statusCombo.setValue("Scheduled");

        // Default date = today
        matchDatePicker.setValue(LocalDate.now());
        matchTimeField.setText("18:00");

        // Load teams
        try {
            List<MatchService.TeamOption> teams = matchService.getTeamOptions();
            team1Combo.setItems(FXCollections.observableArrayList(teams));
            team2Combo.setItems(FXCollections.observableArrayList(teams));
        } catch (SQLException e) {
            showError("Error", "Could not load teams: " + e.getMessage());
        }

        // Load tournaments
        try {
            List<Tournament> tournaments = tournamentService.getData();
            ObservableList<TournamentOption> opts = FXCollections.observableArrayList();
            opts.add(new TournamentOption(0, "— None —"));
            for (Tournament t : tournaments) {
                opts.add(new TournamentOption(t.getId(), t.getName()));
            }
            tournamentCombo.setItems(opts);
            tournamentCombo.setValue(opts.get(0));
        } catch (SQLException e) {
            tournamentCombo.setItems(FXCollections.observableArrayList(
                    new TournamentOption(0, "— None —")));
            tournamentCombo.setValue(tournamentCombo.getItems().get(0));
        }
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
                "All", "Scheduled", "Ongoing", "Finished", "Cancelled", "Pending"));
        statusFilter.setValue("All");

        filtered = new FilteredList<>(allMatches, m -> true);
        scheduleTable.setItems(filtered);

        searchField.textProperty().addListener((obs, o, n) -> applyFilter());
        statusFilter.valueProperty().addListener((obs, o, n) -> applyFilter());
        dateFilter.valueProperty().addListener((obs, o, n) -> applyFilter());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Data
    // ─────────────────────────────────────────────────────────────────────────

    private void loadData() {
        try {
            List<Match> matches = matchService.getAllMatches();
            allMatches.setAll(matches);
            updateStats(matches);
            applyFilter();
        } catch (SQLException e) {
            showError("Database Error", "Could not load matches: " + e.getMessage());
        }
    }

    private void updateStats(List<Match> matches) {
        String today = LocalDate.now().toString();
        long scheduled = matches.stream().filter(m -> "scheduled".equalsIgnoreCase(m.getStatus())).count();
        long todayN    = matches.stream().filter(m -> m.getDate() != null && m.getDate().startsWith(today)).count();
        long live      = matches.stream().filter(m -> "ongoing".equalsIgnoreCase(m.getStatus())).count();
        long finished  = matches.stream().filter(m -> "finished".equalsIgnoreCase(m.getStatus())).count();

        scheduledCount.setText(String.valueOf(scheduled));
        todayCount.setText(String.valueOf(todayN));
        liveCount.setText(String.valueOf(live));
        finishedCount.setText(String.valueOf(finished));
    }

    private void applyFilter() {
        String q      = searchField.getText() == null ? "" : searchField.getText().toLowerCase(Locale.ROOT);
        String status = statusFilter.getValue();
        LocalDate date = dateFilter.getValue();

        filtered.setPredicate(m -> {
            boolean matchSearch = q.isBlank()
                    || (m.getTeam1() != null && m.getTeam1().toLowerCase(Locale.ROOT).contains(q))
                    || (m.getTeam2() != null && m.getTeam2().toLowerCase(Locale.ROOT).contains(q));
            boolean matchStatus = "All".equals(status)
                    || (m.getStatus() != null && m.getStatus().equalsIgnoreCase(status));
            boolean matchDate = date == null
                    || (m.getDate() != null && m.getDate().startsWith(date.toString()));
            return matchSearch && matchStatus && matchDate;
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    void onScheduleMatch(ActionEvent event) {
        // Validate inputs
        if (team1Combo.getValue() == null || team2Combo.getValue() == null) {
            showError("Validation", "Please select both teams.");
            return;
        }
        if (team1Combo.getValue().getId() == team2Combo.getValue().getId()) {
            showError("Validation", "Team 1 and Team 2 must be different.");
            return;
        }
        if (matchDatePicker.getValue() == null) {
            showError("Validation", "Please select a date.");
            return;
        }

        String timeStr = matchTimeField.getText() == null ? "" : matchTimeField.getText().trim();
        if (!timeStr.matches("\\d{1,2}:\\d{2}")) {
            showError("Validation", "Time must be in HH:mm format (e.g. 18:30).");
            return;
        }

        // Build datetime string
        LocalDateTime dt;
        try {
            dt = LocalDateTime.of(matchDatePicker.getValue(),
                    java.time.LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:mm")));
        } catch (Exception e) {
            showError("Validation", "Invalid time value.");
            return;
        }

        int tournamentId = (tournamentCombo.getValue() != null)
                ? tournamentCombo.getValue().getId() : 0;

        Match newMatch = new Match(
                0,
                team1Combo.getValue().getId(),
                team2Combo.getValue().getId(),
                tournamentId,
                0, 0,
                dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                team1Combo.getValue().getLabel(),
                team2Combo.getValue().getLabel(),
                statusCombo.getValue()
        );

        try {
            matchService.createMatch(newMatch);
            showSuccess("Match Scheduled",
                    team1Combo.getValue().getLabel() + " vs " + team2Combo.getValue().getLabel()
                    + "\n📅 " + dt.format(DISPLAY_FMT));
            onClearForm(null);
            loadData();
        } catch (SQLException e) {
            showError("Database Error", "Could not schedule match: " + e.getMessage());
        }
    }

    @FXML
    void onClearForm(ActionEvent event) {
        team1Combo.setValue(null);
        team2Combo.setValue(null);
        matchDatePicker.setValue(LocalDate.now());
        matchTimeField.setText("18:00");
        statusCombo.setValue("Scheduled");
        if (!tournamentCombo.getItems().isEmpty()) {
            tournamentCombo.setValue(tournamentCombo.getItems().get(0));
        }
    }

    @FXML
    void onClearFilter(ActionEvent event) {
        searchField.clear();
        statusFilter.setValue("All");
        dateFilter.setValue(null);
    }

    @FXML
    void onRefresh(ActionEvent event) {
        loadData();
    }

    private void updateStatus(Match match, String newStatus) {
        Match updated = new Match(
                match.getId(),
                match.getTeam1Id(), match.getTeam2Id(),
                match.getTournamentId(),
                match.getScore1(), match.getScore2(),
                match.getMatchDate(),
                match.getTeam1(), match.getTeam2(),
                newStatus
        );
        try {
            matchService.updateMatch(updated);
            loadData();
        } catch (SQLException e) {
            showError("Error", "Could not update match status: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Dialogs
    // ─────────────────────────────────────────────────────────────────────────

    private void showSuccess(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
