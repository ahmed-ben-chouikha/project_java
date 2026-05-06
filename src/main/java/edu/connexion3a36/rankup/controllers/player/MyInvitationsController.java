package edu.connexion3a36.rankup.controllers.player;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.entities.TeamInvitation;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TeamInvitationService;
import edu.connexion3a36.services.TeamService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class MyInvitationsController {

    @FXML private Label statusLabel;
    @FXML private TableView<InvitationRow> invitationsTable;
    @FXML private TableColumn<InvitationRow, Integer> teamIdCol;
    @FXML private TableColumn<InvitationRow, String> teamNameCol;
    @FXML private TableColumn<InvitationRow, String> teamGameCol;
    @FXML private TableColumn<InvitationRow, String> teamLevelCol;
    @FXML private TableColumn<InvitationRow, String> messageCol;
    @FXML private TableColumn<InvitationRow, String> createdAtCol;
    @FXML private TableColumn<InvitationRow, Void> actionsCol;

    private final TeamInvitationService invitationService = new TeamInvitationService();
    private final TeamService teamService = new TeamService();

    @FXML
    void initialize() {
        setupTableColumns();
        refreshInvitations();
    }

    private void setupTableColumns() {
        teamIdCol.setCellValueFactory(new PropertyValueFactory<>("teamId"));
        teamNameCol.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        teamGameCol.setCellValueFactory(new PropertyValueFactory<>("teamGame"));
        teamLevelCol.setCellValueFactory(new PropertyValueFactory<>("teamLevel"));
        messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        configureActionsColumn();
    }

    private void configureActionsColumn() {
        actionsCol.setCellFactory(column -> new TableCell<>() {
            private final Button acceptButton = new Button("Accept");
            private final Button rejectButton = new Button("Reject");
            private final HBox buttonsBox = new HBox(6);

            {
                acceptButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-padding: 6 12; -fx-border-radius: 4;");
                rejectButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 6 12; -fx-border-radius: 4;");

                acceptButton.setOnAction(e -> {
                    InvitationRow row = getTableView().getItems().get(getIndex());
                    acceptInvitation(row);
                });

                rejectButton.setOnAction(e -> {
                    InvitationRow row = getTableView().getItems().get(getIndex());
                    rejectInvitation(row);
                });

                buttonsBox.getChildren().addAll(acceptButton, rejectButton);
                buttonsBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsBox);
            }
        });
    }

    @FXML
    void refreshInvitations() {
        try {
            int playerId = RankUpApp.getCurrentUserId();
            List<TeamInvitation> invitations = invitationService.getPendingInvitationsForPlayer(playerId);

            if (invitations.isEmpty()) {
                statusLabel.setText("You have no pending team invitations.");
                invitationsTable.setItems(FXCollections.observableArrayList());
                return;
            }

            List<InvitationRow> rows = new ArrayList<>();
            for (TeamInvitation invitation : invitations) {
                Team team = teamService.getTeamById(invitation.getTeamId());
                if (team != null) {
                    rows.add(new InvitationRow(
                            invitation.getId(),
                            team.getId(),
                            team.getName(),
                            team.getJeu(),
                            team.getNiveau(),
                            invitation.getMessage(),
                            invitation.getCreatedAt()
                    ));
                }
            }

            invitationsTable.setItems(FXCollections.observableArrayList(rows));
            statusLabel.setText("You have " + rows.size() + " pending invitation(s).");
        } catch (Exception e) {
            statusLabel.setText("Error loading invitations: " + e.getMessage());
            showAlert("Error", "Could not load invitations: " + e.getMessage());
        }
    }

    private void acceptInvitation(InvitationRow row) {
        try {
            invitationService.acceptInvitation(row.invitationId, RankUpApp.getCurrentUserId(), row.teamId);
            showInfo("Invitation Accepted", "You have successfully joined " + row.teamName + "!");
            refreshInvitations();
        } catch (Exception e) {
            showAlert("Error", "Could not accept invitation: " + e.getMessage());
        }
    }

    private void rejectInvitation(InvitationRow row) {
        try {
            invitationService.rejectInvitation(row.invitationId);
            showInfo("Invitation Rejected", "You have rejected the invitation from " + row.teamName + ".");
            refreshInvitations();
        } catch (Exception e) {
            showAlert("Error", "Could not reject invitation: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
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

    public static class InvitationRow {
        private final int invitationId;
        private final int teamId;
        private final String teamName;
        private final String teamGame;
        private final String teamLevel;
        private final String message;
        private final String createdAt;

        public InvitationRow(int invitationId, int teamId, String teamName, String teamGame,
                            String teamLevel, String message, String createdAt) {
            this.invitationId = invitationId;
            this.teamId = teamId;
            this.teamName = teamName;
            this.teamGame = teamGame;
            this.teamLevel = teamLevel;
            this.message = message;
            this.createdAt = createdAt;
        }

        public int getInvitationId() { return invitationId; }
        public int getTeamId() { return teamId; }
        public String getTeamName() { return teamName; }
        public String getTeamGame() { return teamGame; }
        public String getTeamLevel() { return teamLevel; }
        public String getMessage() { return message; }
        public String getCreatedAt() { return createdAt; }
    }
}
