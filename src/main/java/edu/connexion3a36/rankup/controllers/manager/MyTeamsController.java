package edu.connexion3a36.rankup.controllers.manager;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.controllers.teams.TeamFormState;
import edu.connexion3a36.services.TeamService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class MyTeamsController {

    @FXML private Label teamsCountLabel;
    @FXML private Label pendingCountLabel;
    @FXML private Label approvedCountLabel;
    @FXML private Label rejectedCountLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<Team> teamsTable;
    @FXML private TableColumn<Team, Integer> idCol;
    @FXML private TableColumn<Team, String> nameCol;
    @FXML private TableColumn<Team, String> countryCol;
    @FXML private TableColumn<Team, String> gameCol;
    @FXML private TableColumn<Team, String> levelCol;
    @FXML private TableColumn<Team, String> statusCol;
    @FXML private TableColumn<Team, Integer> scoreCol;
    @FXML private TableColumn<Team, String> createdAtCol;
    @FXML private FlowPane membersFlowPane;

    private final TeamService teamService = new TeamService();

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        gameCol.setCellValueFactory(new PropertyValueFactory<>("jeu"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        createdAtCol.setCellValueFactory(cell -> {
            Object value = cell.getValue().getCreatedAt();
            return new SimpleStringProperty(value == null ? "" : value.toString());
        });
        
        // Listen for team selection changes
        teamsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayTeamMembers(newVal);
            } else {
                membersFlowPane.getChildren().clear();
            }
        });
        
        loadTeams();
    }

    private void displayTeamMembers(Team team) {
        membersFlowPane.getChildren().clear();
        try {
            List<TeamService.TeamMember> members = teamService.getTeamMembers(team.getId());
            if (members.isEmpty()) {
                VBox emptyState = new VBox(8);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setPadding(new Insets(40, 20, 40, 20));

                Label emptyIcon = new Label("👥");
                emptyIcon.setStyle("-fx-font-size: 48;");

                Label emptyTitle = new Label("No Team Members");
                emptyTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #e7edf8;");

                Label emptyDesc = new Label("This team doesn't have any members yet.\nUse the scouting feature to invite players!");
                emptyDesc.setStyle("-fx-font-size: 12; -fx-text-fill: #cbd5e1; -fx-text-alignment: center;");
                emptyDesc.setWrapText(true);

                emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyDesc);
                membersFlowPane.getChildren().add(emptyState);
                return;
            }

            for (TeamService.TeamMember member : members) {
                VBox memberCard = createMemberCard(member);
                membersFlowPane.getChildren().add(memberCard);
            }
        } catch (Exception e) {
            System.err.println("Error loading team members: " + e.getMessage());
            VBox errorState = new VBox(8);
            errorState.setAlignment(Pos.CENTER);
            errorState.setPadding(new Insets(40, 20, 40, 20));

            Label errorIcon = new Label("⚠️");
            errorIcon.setStyle("-fx-font-size: 48;");

            Label errorTitle = new Label("Error Loading Members");
            errorTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #dc2626;");

            errorState.getChildren().addAll(errorIcon, errorTitle);
            membersFlowPane.getChildren().add(errorState);
        }
    }

    private VBox createMemberCard(TeamService.TeamMember member) {
        VBox card = new VBox(8);
        card.setStyle("-fx-border-color: rgba(148, 163, 184, 0.2); -fx-border-radius: 12; -fx-background-color: rgba(15, 23, 42, 0.95); -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 4, 0, 0, 2);");
        card.setPadding(new Insets(16));
        card.setPrefWidth(180);
        card.setAlignment(Pos.TOP_LEFT);

        // Header with avatar-like circle and name
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // Avatar circle with initial
        Circle avatar = new Circle(20);
        avatar.setFill(Color.web("#3b82f6"));
        avatar.setStroke(Color.web("#dbeafe"));
        avatar.setStrokeWidth(2);

        Label avatarInitial = new Label(member.getNickname().substring(0, 1).toUpperCase());
        avatarInitial.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12;");
        StackPane avatarPane = new StackPane(avatar, avatarInitial);

        // Name section
        VBox nameBox = new VBox(2);
        Label nameLabel = new Label(member.getDisplayName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #e7edf8;");
        nameLabel.setWrapText(true);

        Label idLabel = new Label("Player #" + member.getId());
        idLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #94a3b8;");

        nameBox.getChildren().addAll(nameLabel, idLabel);
        headerBox.getChildren().addAll(avatarPane, nameBox);

        // Role badge or status
        HBox contentBox = new HBox();
        contentBox.setAlignment(Pos.CENTER_LEFT);

        if (member.getRole() != null && !member.getRole().isEmpty()) {
            Label roleBadge = new Label("🎯 " + member.getRole());
            roleBadge.setStyle("-fx-background-color: rgba(59, 130, 246, 0.2); -fx-text-fill: #60a5fa; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11; -fx-font-weight: bold;");
            contentBox.getChildren().add(roleBadge);
        } else {
            Label noRoleLabel = new Label("⚪ No role assigned");
            noRoleLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #64748b; -fx-font-style: italic;");
            contentBox.getChildren().add(noRoleLabel);
        }

        card.getChildren().addAll(headerBox, contentBox);

        // Hover effect for better interactivity
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-border-color: rgba(59, 130, 246, 0.4); -fx-border-radius: 12; -fx-background-color: rgba(30, 41, 59, 0.95); -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(59,130,246,0.3), 6, 0, 0, 3);");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-border-color: rgba(148, 163, 184, 0.2); -fx-border-radius: 12; -fx-background-color: rgba(15, 23, 42, 0.95); -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 4, 0, 0, 2);");
        });

        return card;
    }

    private void openScoutingForTeam(Team team) {
        TeamScoutingState.setSelectedTeam(team);
        RankUpApp.loadInBase("/views/manager/team-scouting.fxml");
    }

    @FXML
    void onRefresh() {
        loadTeams();
    }

    @FXML
    void onCreateTeam() {
        TeamFormState.clear();
        RankUpApp.loadInBase("/views/teams/team-form.fxml");
    }

    @FXML
    void onBack() {
        RankUpApp.loadInBase("/views/dashboard/user-dashboard.fxml");
    }

    @FXML
    void onViewSelectedTeam() {
        Team selected = teamsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No selection");
            alert.setHeaderText(null);
            alert.setContentText("Select a team first.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Team details");
        alert.setHeaderText(selected.getName());
        alert.setContentText(
                "Country: " + selected.getCountry() + "\n" +
                "Game: " + selected.getJeu() + "\n" +
                "Level: " + selected.getNiveau() + "\n" +
                "Status: " + selected.getStatut() + "\n" +
                "Score: " + selected.getScore() + "\n" +
                "Description: " + (selected.getDescription() == null ? "" : selected.getDescription())
        );
        alert.showAndWait();
    }

    @FXML
    void onScoutTeam() {
        Team selected = teamsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No selection");
            alert.setHeaderText(null);
            alert.setContentText("Select a team first.");
            alert.showAndWait();
            return;
        }
        openScoutingForTeam(selected);
    }

    private void loadTeams() {
        int currentUserId = RankUpApp.getCurrentUserId();
        List<Team> rows = teamService.getTeamsByCreatorId(currentUserId);
        teamsTable.setItems(FXCollections.observableArrayList(rows));
        teamsCountLabel.setText(String.valueOf(rows.size()));
        pendingCountLabel.setText(String.valueOf(rows.stream().filter(team -> "en attente".equalsIgnoreCase(team.getStatut())).count()));
        approvedCountLabel.setText(String.valueOf(rows.stream().filter(team -> "approuvé".equalsIgnoreCase(team.getStatut())).count()));
        rejectedCountLabel.setText(String.valueOf(rows.stream().filter(team -> "refusé".equalsIgnoreCase(team.getStatut())).count()));
        statusLabel.setText(rows.isEmpty()
                ? "You have not created any teams yet."
                : "Loaded " + rows.size() + " team(s) created by you.");
        membersFlowPane.getChildren().clear();
    }

    @FXML
    void onBudgetExpenses() {
        RankUpApp.loadInBase("/views/manager/manager-budget-depense.fxml");
    }
}
