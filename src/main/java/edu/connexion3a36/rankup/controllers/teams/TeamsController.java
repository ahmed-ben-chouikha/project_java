package edu.connexion3a36.rankup.controllers.teams;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TeamService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TeamsController {

    @FXML private TextField searchField;
    @FXML private TableView<Team> teamsTable;
    @FXML private TableColumn<Team, String> nameCol;
    @FXML private TableColumn<Team, String> countryCol;
    @FXML private TableColumn<Team, String> gameCol;
    @FXML private TableColumn<Team, Integer> scoreCol;
    @FXML private Pagination pagination;

    private final TeamService teamService = new TeamService();
    private FilteredList<Team> filtered;

    @FXML
    void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        gameCol.setCellValueFactory(new PropertyValueFactory<>("jeu"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        filtered = new FilteredList<>(FXCollections.observableArrayList());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        loadTeams();
        teamsTable.setItems(filtered);
        pagination.setPageCount(1);
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
        String message = "Team: " + selected.getName()
                + "\nCountry: " + selected.getCountry()
                + "\nGame: " + selected.getJeu()
                + "\nLevel: " + selected.getNiveau()
                + "\nScore: " + selected.getScore()
                + "\nStatus: " + selected.getStatut();
        showInfo("Team details", message);
    }

    @FXML
    void onEditTeam(ActionEvent event) {
        Team selected = teamsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a team first.");
            return;
        }
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
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        filtered.setPredicate(row -> row.getName().toLowerCase().contains(q)
                || row.getCountry().toLowerCase().contains(q)
                || (row.getJeu() != null && row.getJeu().toLowerCase().contains(q)));
    }

    private void loadTeams() {
        List<Team> rows = teamService.getAllTeams();
        filtered = new FilteredList<>(FXCollections.observableArrayList(rows));
        teamsTable.setItems(filtered);
        applyFilter();
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

