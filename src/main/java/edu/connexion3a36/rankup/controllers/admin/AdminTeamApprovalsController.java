package edu.connexion3a36.rankup.controllers.admin;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TeamService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.List;

public class AdminTeamApprovalsController {

    @FXML private Label pendingCountLabel;
    @FXML private Label approvedCountLabel;
    @FXML private Label rejectedCountLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<Team> teamsTable;
    @FXML private TableColumn<Team, Integer> idCol;
    @FXML private TableColumn<Team, String> nameCol;
    @FXML private TableColumn<Team, String> creatorCol;
    @FXML private TableColumn<Team, String> countryCol;
    @FXML private TableColumn<Team, String> gameCol;
    @FXML private TableColumn<Team, String> levelCol;
    @FXML private TableColumn<Team, String> statusCol;
    @FXML private TableColumn<Team, Integer> scoreCol;
    @FXML private TableColumn<Team, String> createdAtCol;
    @FXML private TableColumn<Team, Void> actionsCol;

    private final TeamService teamService = new TeamService();

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        creatorCol.setCellValueFactory(cell -> {
            Team team = cell.getValue();
            Integer creatorId = team.getCreatorId();
            return new SimpleStringProperty(creatorId == null ? "-" : String.valueOf(creatorId));
        });
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        gameCol.setCellValueFactory(new PropertyValueFactory<>("jeu"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        createdAtCol.setCellValueFactory(cell -> {
            Object value = cell.getValue().getCreatedAt();
            return new SimpleStringProperty(value == null ? "" : value.toString());
        });
        configureActionsColumn();
        loadTeams();
    }

    @FXML
    void onRefresh() {
        loadTeams();
    }

    @FXML
    void onBack() {
        RankUpApp.loadInBase("/views/admin/admin-dashboard.fxml");
    }

    private void configureActionsColumn() {
        actionsCol.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");
            private final HBox box = new HBox(8, approveButton, rejectButton);

            {
                approveButton.setStyle("-fx-background-color: #34A853; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
                rejectButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
                approveButton.setOnAction(e -> handleApprove(getTableView().getItems().get(getIndex())));
                rejectButton.setOnAction(e -> handleReject(getTableView().getItems().get(getIndex())));
                box.setAlignment(javafx.geometry.Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                Team team = getTableView().getItems().get(getIndex());
                setGraphic("en attente".equalsIgnoreCase(team.getStatut()) ? box : null);
            }
        });
    }

    private void loadTeams() {
        List<Team> pendingTeams = teamService.searchTeamsByStatus("en attente");
        teamsTable.setItems(FXCollections.observableArrayList(pendingTeams));
        pendingCountLabel.setText(String.valueOf(teamService.countTeamsByStatus("en attente")));
        approvedCountLabel.setText(String.valueOf(teamService.countTeamsByStatus("approuvé")));
        rejectedCountLabel.setText(String.valueOf(teamService.countTeamsByStatus("refusé")));
        statusLabel.setText(pendingTeams.isEmpty()
                ? "No teams are waiting for approval."
                : "Loaded " + pendingTeams.size() + " team(s) awaiting review.");
    }

    private void handleApprove(Team team) {
        if (teamService.updateTeamStatus(team.getId(), "approuvé")) {
            showInfo("Team approved", team.getName() + " is now approved.");
            loadTeams();
        } else {
            showInfo("Approval failed", "Could not approve the selected team.");
        }
    }

    private void handleReject(Team team) {
        if (teamService.updateTeamStatus(team.getId(), "refusé")) {
            showInfo("Team rejected", team.getName() + " has been rejected.");
            loadTeams();
        } else {
            showInfo("Rejection failed", "Could not reject the selected team.");
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
