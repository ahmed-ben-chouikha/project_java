package edu.connexion3a36.rankup.controllers.teams;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TeamsController {

    @FXML private TextField searchField;
    @FXML private TableView<TeamRow> teamsTable;
    @FXML private TableColumn<TeamRow, String> nameCol;
    @FXML private TableColumn<TeamRow, String> countryCol;
    @FXML private TableColumn<TeamRow, Integer> membersCol;
    @FXML private TableColumn<TeamRow, String> budgetCol;
    @FXML private Pagination pagination;

    private FilteredList<TeamRow> filtered;

    @FXML
    void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        membersCol.setCellValueFactory(new PropertyValueFactory<>("members"));
        budgetCol.setCellValueFactory(new PropertyValueFactory<>("budget"));

        filtered = new FilteredList<>(FXCollections.observableArrayList(
                new TeamRow("Falcons", "France", 6, "$12,000"),
                new TeamRow("Nova", "Germany", 5, "$9,500"),
                new TeamRow("Apex", "Tunisia", 7, "$14,200"),
                new TeamRow("Vortex", "Italy", 5, "$8,400")
        ));

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        teamsTable.setItems(filtered);
        pagination.setPageCount(1);
    }

    @FXML
    void onCreateTeam(ActionEvent event) { RankUpApp.loadInBase("/views/teams/team-form.fxml"); }

    @FXML
    void onViewTeam(ActionEvent event) { RankUpApp.loadInBase("/views/teams/team-details.fxml"); }

    @FXML
    void onEditTeam(ActionEvent event) { RankUpApp.loadInBase("/views/teams/team-form.fxml"); }

    @FXML
    void onDeleteTeam(ActionEvent event) { showInfo("Placeholder", "Delete handler placeholder."); }

    private void applyFilter() {
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        filtered.setPredicate(row -> row.getName().toLowerCase().contains(q) || row.getCountry().toLowerCase().contains(q));
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class TeamRow {
        private final String name;
        private final String country;
        private final Integer members;
        private final String budget;

        public TeamRow(String name, String country, Integer members, String budget) {
            this.name = name;
            this.country = country;
            this.members = members;
            this.budget = budget;
        }

        public String getName() { return name; }
        public String getCountry() { return country; }
        public Integer getMembers() { return members; }
        public String getBudget() { return budget; }
    }
}

