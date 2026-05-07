package edu.connexion3a36.rankup.controllers.leaderboard;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.services.TeamService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Leaderboard — ranks all teams by score descending.
 * Top-3 are highlighted in a podium section.
 * Filterable by game type and level; searchable by team name.
 */
public class LeaderboardController implements Initializable {

    @FXML private Label lastUpdatedLabel;
    @FXML private Label rank1Name;
    @FXML private Label rank1Game;
    @FXML private Label rank1Score;
    @FXML private Label rank2Name;
    @FXML private Label rank2Game;
    @FXML private Label rank2Score;
    @FXML private Label rank3Name;
    @FXML private Label rank3Game;
    @FXML private Label rank3Score;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> gameFilter;
    @FXML private ComboBox<String> levelFilter;
    @FXML private Label totalLabel;
    @FXML private TableView<RankedTeam> rankTable;
    @FXML private TableColumn<RankedTeam, Integer>  rankCol;
    @FXML private TableColumn<RankedTeam, String>   medalCol;
    @FXML private TableColumn<RankedTeam, String>   nameCol;
    @FXML private TableColumn<RankedTeam, String>   gameCol;
    @FXML private TableColumn<RankedTeam, String>   levelCol;
    @FXML private TableColumn<RankedTeam, String>   countryCol;
    @FXML private TableColumn<RankedTeam, Integer>  scoreCol;
    @FXML private TableColumn<RankedTeam, String>   trendCol;

    private final TeamService teamService = new TeamService();
    private final ObservableList<RankedTeam> allRanked = FXCollections.observableArrayList();
    private FilteredList<RankedTeam> filtered;

    // -------------------------------------------------------------------------
    // Wrapper that adds rank + medal to a Team
    // -------------------------------------------------------------------------
    public static class RankedTeam {
        private final int rank;
        private final Team team;

        public RankedTeam(int rank, Team team) {
            this.rank  = rank;
            this.team  = team;
        }

        public int    getRank()    { return rank; }
        public String getMedal()   {
            return switch (rank) { case 1 -> "🥇"; case 2 -> "🥈"; case 3 -> "🥉"; default -> ""; };
        }
        public String getName()    { return team.getName(); }
        public String getGame()    { return team.getJeu()    != null ? team.getJeu()    : ""; }
        public String getLevel()   { return team.getNiveau() != null ? team.getNiveau() : ""; }
        public String getCountry() { return team.getCountry()!= null ? team.getCountry(): ""; }
        public int    getScore()   { return team.getScore(); }
        public String getTrend()   {
            // Simple visual indicator based on score tier
            if (team.getScore() >= 100) return "▲▲";
            if (team.getScore() >= 50)  return "▲";
            if (team.getScore() > 0)    return "—";
            return "▼";
        }
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumns();
        setupFilters();
        loadData();
    }

    private void setupColumns() {
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        medalCol.setCellValueFactory(new PropertyValueFactory<>("medal"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        gameCol.setCellValueFactory(new PropertyValueFactory<>("game"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        trendCol.setCellValueFactory(new PropertyValueFactory<>("trend"));

        // Highlight top-3 rows
        rankTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(RankedTeam item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("leaderboard-gold", "leaderboard-silver", "leaderboard-bronze");
                if (!empty && item != null) {
                    switch (item.getRank()) {
                        case 1 -> getStyleClass().add("leaderboard-gold");
                        case 2 -> getStyleClass().add("leaderboard-silver");
                        case 3 -> getStyleClass().add("leaderboard-bronze");
                    }
                }
            }
        });

        // Center-align rank and medal columns
        rankCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.valueOf(v));
                setAlignment(Pos.CENTER);
            }
        });
        medalCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty ? null : v);
                setAlignment(Pos.CENTER);
                setStyle("-fx-font-size: 16;");
            }
        });
        trendCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty ? null : v);
                setAlignment(Pos.CENTER);
                if (!empty && v != null) {
                    setStyle(v.startsWith("▲") ? "-fx-text-fill: #22c55e;" : "-fx-text-fill: #ef4444;");
                }
            }
        });
    }

    private void setupFilters() {
        filtered = new FilteredList<>(allRanked, t -> true);
        SortedList<RankedTeam> sorted = new SortedList<>(filtered);
        // Keep the score-based order from the DB; don't let the table re-sort
        sorted.setComparator(java.util.Comparator.comparingInt(RankedTeam::getRank));
        rankTable.setItems(sorted);

        searchField.textProperty().addListener((obs, o, n) -> applyFilter());
        gameFilter.valueProperty().addListener((obs, o, n) -> applyFilter());
        levelFilter.valueProperty().addListener((obs, o, n) -> applyFilter());
    }

    // -------------------------------------------------------------------------
    // Data loading
    // -------------------------------------------------------------------------

    private void loadData() {
        List<Team> teams = teamService.getAllTeams(); // already sorted by score DESC

        allRanked.clear();
        for (int i = 0; i < teams.size(); i++) {
            allRanked.add(new RankedTeam(i + 1, teams.get(i)));
        }

        updatePodium(teams);
        populateFilterOptions(teams);
        applyFilter();

        lastUpdatedLabel.setText("Updated at " +
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private void updatePodium(List<Team> teams) {
        setRankLabels(rank1Name, rank1Game, rank1Score, teams, 0);
        setRankLabels(rank2Name, rank2Game, rank2Score, teams, 1);
        setRankLabels(rank3Name, rank3Game, rank3Score, teams, 2);
    }

    private void setRankLabels(Label name, Label game, Label score, List<Team> teams, int idx) {
        if (idx < teams.size()) {
            Team t = teams.get(idx);
            name.setText(t.getName());
            game.setText(t.getJeu() != null ? t.getJeu() : "");
            score.setText(t.getScore() + " pts");
        } else {
            name.setText("—");
            game.setText("");
            score.setText("0 pts");
        }
    }

    private void populateFilterOptions(List<Team> teams) {
        List<String> games = teams.stream()
                .map(Team::getJeu).filter(v -> v != null && !v.isBlank())
                .distinct().sorted().toList();
        var gameItems = FXCollections.observableArrayList("All Games");
        gameItems.addAll(games);
        gameFilter.setItems(gameItems);
        gameFilter.setValue("All Games");

        List<String> levels = teams.stream()
                .map(Team::getNiveau).filter(v -> v != null && !v.isBlank())
                .distinct().sorted().toList();
        var levelItems = FXCollections.observableArrayList("All Levels");
        levelItems.addAll(levels);
        levelFilter.setItems(levelItems);
        levelFilter.setValue("All Levels");
    }

    private void applyFilter() {
        String q       = searchField.getText() == null ? "" : searchField.getText().toLowerCase(Locale.ROOT);
        String game    = gameFilter.getValue();
        String level   = levelFilter.getValue();

        filtered.setPredicate(rt -> {
            boolean matchSearch = q.isBlank() || rt.getName().toLowerCase(Locale.ROOT).contains(q)
                    || rt.getCountry().toLowerCase(Locale.ROOT).contains(q);
            boolean matchGame  = game  == null || "All Games".equals(game)  || game.equalsIgnoreCase(rt.getGame());
            boolean matchLevel = level == null || "All Levels".equals(level) || level.equalsIgnoreCase(rt.getLevel());
            return matchSearch && matchGame && matchLevel;
        });

        totalLabel.setText(filtered.size() + " team" + (filtered.size() == 1 ? "" : "s"));
    }

    @FXML
    void onRefresh(ActionEvent event) {
        loadData();
    }
}
