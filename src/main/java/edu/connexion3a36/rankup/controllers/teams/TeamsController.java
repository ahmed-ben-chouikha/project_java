package edu.connexion3a36.rankup.controllers.teams;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TeamService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TeamsController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> countryFilterCombo;
    @FXML private ComboBox<String> gameFilterCombo;
    @FXML private ComboBox<String> sortCombo;
    @FXML private TableView<Team> teamsTable;
    @FXML private TableColumn<Team, String> nameCol;
    @FXML private TableColumn<Team, String> countryCol;
    @FXML private TableColumn<Team, String> gameCol;
    @FXML private TableColumn<Team, Integer> scoreCol;
    @FXML private TableColumn<Team, Void> actionsCol;
    @FXML private Pagination pagination;

    private final TeamService teamService = new TeamService();
    private final ObservableList<Team> teams = FXCollections.observableArrayList();
    private FilteredList<Team> filtered;
    private SortedList<Team> sorted;

    @FXML
    void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        gameCol.setCellValueFactory(new PropertyValueFactory<>("jeu"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        setupActionsColumn();

        filtered = new FilteredList<>(teams, t -> true);
        sorted = new SortedList<>(filtered);
        teamsTable.setItems(sorted);

        setupControls();
        loadTeams();
        pagination.setPageCount(1);
    }

    private void setupControls() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        if (countryFilterCombo != null) {
            countryFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
            countryFilterCombo.setItems(FXCollections.observableArrayList("All Countries"));
            countryFilterCombo.setValue("All Countries");
        }
        if (gameFilterCombo != null) {
            gameFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
            gameFilterCombo.setItems(FXCollections.observableArrayList("All Games"));
            gameFilterCombo.setValue("All Games");
        }
        if (sortCombo != null) {
            sortCombo.valueProperty().addListener((obs, oldVal, newVal) -> applySorting());
            sortCombo.setItems(FXCollections.observableArrayList(
                "Name (A-Z)",
                "Name (Z-A)",
                "Score (Low-High)",
                "Score (High-Low)",
                "Country (A-Z)",
                "Game (A-Z)"
            ));
            sortCombo.setValue("Name (A-Z)");
        }
    }

    private void setupActionsColumn() {
        if (actionsCol == null) {
            return;
        }

        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁️ View");
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");

            {
                viewBtn.getStyleClass().addAll("action-btn", "action-btn-view");
                editBtn.getStyleClass().addAll("action-btn", "action-btn-edit");
                deleteBtn.getStyleClass().addAll("action-btn", "action-btn-delete");

                viewBtn.setOnAction(event -> {
                    Team team = getTableView().getItems().get(getIndex());
                    viewTeamDetails(team);
                });
                editBtn.setOnAction(event -> {
                    Team team = getTableView().getItems().get(getIndex());
                    editTeam(team);
                });
                deleteBtn.setOnAction(event -> {
                    Team team = getTableView().getItems().get(getIndex());
                    deleteTeam(team);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                HBox box = new HBox(3);
                box.setAlignment(Pos.CENTER);
                box.getChildren().addAll(viewBtn, editBtn, deleteBtn);
                setGraphic(box);
            }
        });
    }

    @FXML
    void onCreateTeam(ActionEvent event) {
        TeamFormState.clear();
        RankUpApp.loadInBase("/views/teams/team-form.fxml");
    }

    @FXML
    void onViewTeam(ActionEvent event) {
        Team selected = teamsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a team first.");
            return;
        }
        viewTeamDetails(selected);
    }

    private void viewTeamDetails(Team selected) {
        String message = "Team: " + selected.getName()
                + "\nCountry: " + selected.getCountry()
                + "\nGame: " + selected.getJeu()
                + "\nLevel: " + selected.getNiveau()
            + "\nScore: " + selected.getScore();
        showInfo("Team details", message);
    }

    @FXML
    void onEditTeam(ActionEvent event) {
        Team selected = teamsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a team first.");
            return;
        }
        editTeam(selected);
    }

    private void editTeam(Team selected) {
        TeamFormState.setEditingTeam(selected);
        RankUpApp.loadInBase("/views/teams/team-form.fxml");
    }

    @FXML
    void onDeleteTeam(ActionEvent event) {
        Team selected = teamsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a team first.");
            return;
        }
        deleteTeam(selected);
    }

    private void deleteTeam(Team selected) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Team");
        confirm.setHeaderText("Delete selected team?");
        confirm.setContentText(selected.getName());

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (teamService.deleteTeam(selected.getId())) {
                    loadTeams();
                } else {
                    showError("Database Error", "Could not delete team.");
                }
            }
        });
    }

    private void applyFilter() {
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase(Locale.ROOT);
        String country = countryFilterCombo != null ? countryFilterCombo.getValue() : "All Countries";
        String game = gameFilterCombo != null ? gameFilterCombo.getValue() : "All Games";

        filtered.setPredicate(row -> {
            boolean matchesSearch = row.getName().toLowerCase(Locale.ROOT).contains(q)
                    || row.getCountry().toLowerCase(Locale.ROOT).contains(q)
                    || (row.getJeu() != null && row.getJeu().toLowerCase(Locale.ROOT).contains(q));

            boolean matchesCountry = country == null
                    || "All Countries".equals(country)
                    || (row.getCountry() != null && country.equalsIgnoreCase(row.getCountry()));

            boolean matchesGame = game == null
                    || "All Games".equals(game)
                    || (row.getJeu() != null && game.equalsIgnoreCase(row.getJeu()));

            return matchesSearch && matchesCountry && matchesGame;
        });
    }

    private void loadTeams() {
        List<Team> rows = teamService.getAllTeams();
        teams.setAll(rows);

        if (countryFilterCombo != null) {
            String current = countryFilterCombo.getValue();
            List<String> countries = rows.stream()
                    .map(Team::getCountry)
                    .filter(v -> v != null && !v.isBlank())
                    .distinct()
                    .sorted(String::compareToIgnoreCase)
                    .toList();
            var values = FXCollections.observableArrayList("All Countries");
            values.addAll(countries);
            countryFilterCombo.setItems(values);
            countryFilterCombo.setValue(values.contains(current) ? current : "All Countries");
        }

        if (gameFilterCombo != null) {
            String current = gameFilterCombo.getValue();
            List<String> games = rows.stream()
                    .map(Team::getJeu)
                    .filter(v -> v != null && !v.isBlank())
                    .distinct()
                    .sorted(String::compareToIgnoreCase)
                    .toList();
            var values = FXCollections.observableArrayList("All Games");
            values.addAll(games);
            gameFilterCombo.setItems(values);
            gameFilterCombo.setValue(values.contains(current) ? current : "All Games");
        }

        applyFilter();
        applySorting();
    }

    private void applySorting() {
        if (sorted == null || sortCombo == null || sortCombo.getValue() == null) {
            return;
        }

        Comparator<Team> comparator = switch (sortCombo.getValue()) {
            case "Name (Z-A)" -> Comparator.comparing((Team t) -> safeLower(t.getName())).reversed();
            case "Score (Low-High)" -> Comparator.comparing(Team::getScore, Comparator.nullsLast(Integer::compareTo));
            case "Score (High-Low)" -> Comparator.comparing(Team::getScore, Comparator.nullsLast(Integer::compareTo)).reversed();
            case "Country (A-Z)" -> Comparator.comparing(t -> safeLower(t.getCountry()));
            case "Game (A-Z)" -> Comparator.comparing(t -> safeLower(t.getJeu()));
            default -> Comparator.comparing(t -> safeLower(t.getName()));
        };

        sorted.setComparator(comparator);
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
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

