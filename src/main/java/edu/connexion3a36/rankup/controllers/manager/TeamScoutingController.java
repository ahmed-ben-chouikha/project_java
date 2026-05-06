package edu.connexion3a36.rankup.controllers.manager;

import edu.connexion3a36.entities.PlayerCandidate;
import edu.connexion3a36.entities.Team;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.PlayerScoutingService;
import edu.connexion3a36.services.TeamInvitationService;
import edu.connexion3a36.services.TeamService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeamScoutingController {

    @FXML private Label teamNameLabel;
    @FXML private Label teamInfoLabel;
    @FXML private Label aiStatusLabel;
    @FXML private Label formationLabel;
    @FXML private Label inviteStatusLabel;
    @FXML private Button recommendButton;
    @FXML private Button inviteAllButton;
    @FXML private Button backButton;
    @FXML private TableView<Team> teamTable;
    @FXML private TableColumn<Team, Integer> teamIdCol;
    @FXML private TableColumn<Team, String> teamNameCol;
    @FXML private TableColumn<Team, String> teamGameCol;
    @FXML private TableColumn<Team, String> teamLevelCol;
    @FXML private TableColumn<Team, String> teamStatusCol;
    @FXML private TableColumn<Team, Integer> teamScoreCol;
    @FXML private TableView<PlayerCandidate> recommendedTable;
    @FXML private TableColumn<PlayerCandidate, Integer> playerIdCol;
    @FXML private TableColumn<PlayerCandidate, String> playerNameCol;
    @FXML private TableColumn<PlayerCandidate, String> playerRoleCol;
    @FXML private TableColumn<PlayerCandidate, String> playerStatusCol;
    @FXML private TableColumn<PlayerCandidate, String> playerTeamCol;
    @FXML private TableColumn<PlayerCandidate, String> playerBirthCol;
    @FXML private TableColumn<PlayerCandidate, Void> playerActionsCol;

    @FXML private TextField playerSearchField;
    @FXML private TableView<PlayerCandidate> searchResultsTable;
    @FXML private TableColumn<PlayerCandidate, Integer> searchPlayerIdCol;
    @FXML private TableColumn<PlayerCandidate, String> searchPlayerNameCol;
    @FXML private TableColumn<PlayerCandidate, String> searchPlayerRoleCol;
    @FXML private TableColumn<PlayerCandidate, String> searchPlayerStatusCol;
    @FXML private TableColumn<PlayerCandidate, String> searchPlayerTeamCol;
    @FXML private TableColumn<PlayerCandidate, Void> searchPlayerActionsCol;
    @FXML private Label searchStatusLabel;

    private final TeamService teamService = new TeamService();
    private final PlayerScoutingService scoutingService = new PlayerScoutingService();
    private final TeamInvitationService invitationService = new TeamInvitationService();

    private Team selectedTeam;
    private List<PlayerCandidate> recommendedPlayers = new ArrayList<>();

    @FXML
    void initialize() {
        teamIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        teamGameCol.setCellValueFactory(new PropertyValueFactory<>("jeu"));
        teamLevelCol.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        teamStatusCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        teamScoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        playerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        playerNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDisplayName()));
        playerRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        playerStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        playerTeamCol.setCellValueFactory(cell -> {
            Integer teamId = cell.getValue().getTeamId();
            return new SimpleStringProperty(teamId == null ? "Free agent" : "Team #" + teamId);
        });
        playerBirthCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        configurePlayerActions();

        teamTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        recommendedTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Configure search results table
        searchPlayerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        searchPlayerNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDisplayName()));
        searchPlayerRoleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        searchPlayerStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        searchPlayerTeamCol.setCellValueFactory(cell -> {
            Integer teamId = cell.getValue().getTeamId();
            return new SimpleStringProperty(teamId == null ? "Free agent" : "Team #" + teamId);
        });
        configureSearchPlayerActions();
        
        loadTeams();
        Team team = TeamScoutingState.getSelectedTeam();
        if (team != null) {
            selectTeam(team);
        }
    }

    @FXML
    void onRecommendPlayers() {
        Team team = teamTable.getSelectionModel().getSelectedItem();
        if (team == null) {
            showAlert("Select a team first.");
            return;
        }
        selectedTeam = team;
        teamNameLabel.setText(team.getName());
        teamInfoLabel.setText(team.getJeu() + " • " + team.getNiveau() + " • " + team.getStatut());
        formationLabel.setText("Generating a 5-player formation for " + team.getName() + "...");

        try {
            recommendedPlayers = scoutingService.recommendFormation(team, 5);
            recommendedTable.setItems(FXCollections.observableArrayList(recommendedPlayers));
            aiStatusLabel.setText(recommendedPlayers.isEmpty()
                    ? "No players were recommended."
                    : "AI recommended " + recommendedPlayers.size() + " player(s). Use Invite All to send the whole formation.");
            formationLabel.setText(buildFormationText(recommendedPlayers));
            inviteAllButton.setDisable(recommendedPlayers.isEmpty());
        } catch (Exception e) {
            aiStatusLabel.setText("Could not generate AI recommendation: " + e.getMessage());
            recommendedTable.setItems(FXCollections.observableArrayList());
            formationLabel.setText("Formation unavailable.");
            inviteAllButton.setDisable(true);
        }
    }

    @FXML
    void onInviteAll() {
        Team team = selectedTeam != null ? selectedTeam : teamTable.getSelectionModel().getSelectedItem();
        if (team == null) {
            showAlert("Select a team first.");
            return;
        }
        List<PlayerCandidate> selection = recommendedPlayers.isEmpty()
                ? recommendedTable.getItems().stream().limit(5).collect(Collectors.toList())
                : recommendedPlayers;
        if (selection.isEmpty()) {
            showAlert("Generate recommendations first.");
            return;
        }

        try {
            List<Integer> playerIds = selection.stream().map(PlayerCandidate::getId).collect(Collectors.toList());
            int created = invitationService.invitePlayers(team.getId(), playerIds, RankUpApp.getCurrentUserId(),
                    "Invitation to join " + team.getName() + " as part of the recommended 5-player formation.");
            inviteStatusLabel.setText("Created " + created + " invitation(s) for " + team.getName() + ".");
            showInfo("Invitations sent", "Sent " + created + " invitation(s) for " + team.getName() + ".");
        } catch (Exception e) {
            inviteStatusLabel.setText("Could not send invitations: " + e.getMessage());
            showAlert("Could not send invitations: " + e.getMessage());
        }
    }

    @FXML
    void onBack() {
        TeamScoutingState.clear();
        RankUpApp.loadInBase("/views/manager/my-teams.fxml");
    }

    private void loadTeams() {
        teamTable.setItems(FXCollections.observableArrayList(teamService.getTeamsByCreatorId(RankUpApp.getCurrentUserId())));
        if (!teamTable.getItems().isEmpty() && teamTable.getSelectionModel().getSelectedItem() == null) {
            teamTable.getSelectionModel().selectFirst();
            selectedTeam = teamTable.getSelectionModel().getSelectedItem();
            teamNameLabel.setText(selectedTeam.getName());
            teamInfoLabel.setText(selectedTeam.getJeu() + " • " + selectedTeam.getNiveau() + " • " + selectedTeam.getStatut());
            aiStatusLabel.setText("Select a team and generate a 5-player formation.");
            formationLabel.setText("No formation generated yet.");
        } else {
            teamNameLabel.setText("No teams found");
            teamInfoLabel.setText("Create a team first.");
            aiStatusLabel.setText("You do not have any teams yet.");
            formationLabel.setText("No formation available.");
        }
        inviteAllButton.setDisable(true);
    }

    private void selectTeam(Team team) {
        for (Team row : teamTable.getItems()) {
            if (row.getId() == team.getId()) {
                teamTable.getSelectionModel().select(row);
                selectedTeam = row;
                teamNameLabel.setText(row.getName());
                teamInfoLabel.setText(row.getJeu() + " • " + row.getNiveau() + " • " + row.getStatut());
                aiStatusLabel.setText("Selected team loaded. Generate a 5-player formation.");
                formationLabel.setText("No formation generated yet.");
                return;
            }
        }
    }

    private void configurePlayerActions() {
        playerActionsCol.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            private final Button inviteButton = new Button("Invite");

            {
                inviteButton.setOnAction(e -> {
                    PlayerCandidate candidate = getTableView().getItems().get(getIndex());
                    inviteSingle(candidate);
                });
                inviteButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : inviteButton);
            }
        });
    }

    @FXML
    void onSearchKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onSearchPlayer();
        }
    }

    @FXML
    void onSearchPlayer() {
        Team team = selectedTeam != null ? selectedTeam : teamTable.getSelectionModel().getSelectedItem();
        if (team == null) {
            searchStatusLabel.setText("Select a team first.");
            return;
        }

        String searchQuery = playerSearchField.getText().trim();
        if (searchQuery.isEmpty()) {
            searchStatusLabel.setText("Enter a player name to search.");
            searchResultsTable.setItems(FXCollections.observableArrayList());
            return;
        }

        try {
            List<PlayerCandidate> results = scoutingService.searchPlayersByName(searchQuery);
            if (results.isEmpty()) {
                searchStatusLabel.setText("No players found matching \"" + searchQuery + "\".");
                searchResultsTable.setItems(FXCollections.observableArrayList());
            } else {
                searchStatusLabel.setText("Found " + results.size() + " player(s) matching \"" + searchQuery + "\".");
                searchResultsTable.setItems(FXCollections.observableArrayList(results));
            }
        } catch (Exception e) {
            searchStatusLabel.setText("Error searching players: " + e.getMessage());
            searchResultsTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void configureSearchPlayerActions() {
        searchPlayerActionsCol.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            private final Button inviteButton = new Button("Invite");

            {
                inviteButton.setOnAction(e -> {
                    PlayerCandidate candidate = getTableView().getItems().get(getIndex());
                    inviteSearchResult(candidate);
                });
                inviteButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : inviteButton);
            }
        });
    }

    private void inviteSearchResult(PlayerCandidate player) {
        Team team = selectedTeam != null ? selectedTeam : teamTable.getSelectionModel().getSelectedItem();
        if (team == null) {
            showAlert("Select a team first.");
            return;
        }

        try {
            int created = invitationService.invitePlayers(team.getId(), List.of(player.getId()), RankUpApp.getCurrentUserId(),
                    "Invitation to join " + team.getName() + ".");
            if (created > 0) {
                searchStatusLabel.setText("Invited " + player.getDisplayName() + " to " + team.getName() + ".");
                showInfo("Invitation sent", "Invitation sent to " + player.getDisplayName() + ".");
                // Clear search after successful invitation
                playerSearchField.clear();
                searchResultsTable.setItems(FXCollections.observableArrayList());
            } else {
                searchStatusLabel.setText("This player already has an invitation for this team.");
                showInfo("Player already invited", player.getDisplayName() + " already has a pending invitation for this team.");
            }
        } catch (Exception e) {
            showAlert("Could not invite player: " + e.getMessage());
        }
    }

    private void inviteSingle(PlayerCandidate player) {
        Team team = selectedTeam != null ? selectedTeam : teamTable.getSelectionModel().getSelectedItem();
        if (team == null) {
            showAlert("Select a team first.");
            return;
        }

        try {
            int created = invitationService.invitePlayers(team.getId(), List.of(player.getId()), RankUpApp.getCurrentUserId(),
                    "Invitation to join " + team.getName() + ".");
            if (created > 0) {
                inviteStatusLabel.setText("Invited " + player.getDisplayName() + ".");
                showInfo("Invitation sent", "Invitation sent to " + player.getDisplayName() + ".");
            } else {
                inviteStatusLabel.setText("This player already has an invitation for this team.");
                showInfo("Player already invited", player.getDisplayName() + " already has a pending invitation for this team.");
            }
        } catch (Exception e) {
            showAlert("Could not invite player: " + e.getMessage());
        }
    }

    private String buildFormationText(List<PlayerCandidate> players) {
        if (players == null || players.isEmpty()) {
            return "No formation generated yet.";
        }
        StringBuilder builder = new StringBuilder("Recommended 5-player formation:\n");
        String[] slots = {"Leader", "Entry", "Support", "Flex", "Anchor"};
        for (int i = 0; i < players.size(); i++) {
            PlayerCandidate player = players.get(i);
            String slot = i < slots.length ? slots[i] : "Player " + (i + 1);
            builder.append(slot).append(": ").append(player.getDisplayName()).append(" - ")
                    .append(player.getRole() == null ? "" : player.getRole())
                    .append("\n");
        }
        return builder.toString().trim();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Team scouting");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
