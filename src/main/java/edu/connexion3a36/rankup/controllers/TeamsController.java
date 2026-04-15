package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.services.TeamService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class TeamsController {

    @FXML
    private TableView<Team> teamTable;
    @FXML
    private TableColumn<Team, Integer> idColumn;
    @FXML
    private TableColumn<Team, String> nameColumn;
    @FXML
    private TableColumn<Team, String> countryColumn;
    @FXML
    private TableColumn<Team, String> jeuColumn;
    @FXML
    private TableColumn<Team, String> niveauColumn;
    @FXML
    private TableColumn<Team, String> statutColumn;
    @FXML
    private TableColumn<Team, Integer> scoreColumn;
    @FXML
    private TableColumn<Team, Void> actionsColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterCombo;

    private TeamService teamService;
    private ObservableList<Team> teamList;

    @FXML
    public void initialize() {
        teamService = new TeamService();
        setupTableColumns();
        setupFilters();
        loadTeams();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        jeuColumn.setCellValueFactory(new PropertyValueFactory<>("jeu"));
        niveauColumn.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        // Action column with Edit and Delete buttons
        actionsColumn.setCellFactory(param -> new TableCell<Team, Void>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");
            private final Button viewBtn = new Button("👁️ View");

            {
                editBtn.setStyle("-fx-padding: 5px 10px; -fx-cursor: hand; -fx-font-size: 10px;");
                deleteBtn.setStyle("-fx-padding: 5px 10px; -fx-cursor: hand; -fx-font-size: 10px; -fx-text-fill: red;");
                viewBtn.setStyle("-fx-padding: 5px 10px; -fx-cursor: hand; -fx-font-size: 10px; -fx-text-fill: blue;");

                editBtn.setOnAction(event -> {
                    Team team = getTableView().getItems().get(getIndex());
                    editTeam(team);
                });

                deleteBtn.setOnAction(event -> {
                    Team team = getTableView().getItems().get(getIndex());
                    deleteTeam(team);
                });

                viewBtn.setOnAction(event -> {
                    Team team = getTableView().getItems().get(getIndex());
                    viewTeamDetails(team);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(3);
                    hbox.setAlignment(Pos.CENTER);
                    hbox.getChildren().addAll(viewBtn, editBtn, deleteBtn);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void setupFilters() {
        if (filterCombo != null) {
            ObservableList<String> filterOptions = FXCollections.observableArrayList(
                "Tous", "en attente", "approuvé", "refusé"
            );
            filterCombo.setItems(filterOptions);
            filterCombo.setValue("Tous");
        }
    }

    private void loadTeams() {
        List<Team> teams = teamService.getAllTeams();
        teamList = FXCollections.observableArrayList(teams);
        teamTable.setItems(teamList);
    }

    @FXML
    private void onAddTeam(ActionEvent event) {
        showTeamDialog(null);
    }

    private void editTeam(Team team) {
        showTeamDialog(team);
    }

    private void viewTeamDetails(Team team) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'équipe - " + team.getName());
        alert.setHeaderText(null);
        
        String details = String.format(
            "Nom: %s\nPays: %s\nJeu: %s\nNiveau: %s\nScore: %d\nStatut: %s\n\nDescription:\n%s\n\nDescription détaillée:\n%s",
            team.getName(),
            team.getCountry(),
            team.getJeu(),
            team.getNiveau(),
            team.getScore(),
            team.getStatut(),
            team.getDescription() != null ? team.getDescription() : "N/A",
            team.getDetailedDescription() != null ? team.getDetailedDescription() : "N/A"
        );
        alert.setContentText(details);
        alert.showAndWait();
    }

    private void deleteTeam(Team team) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer l'équipe");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer l'équipe: " + team.getName() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (teamService.deleteTeam(team.getId())) {
                teamList.remove(team);
                showInfo("Succès", "Équipe supprimée avec succès!");
            } else {
                showError("Erreur", "Échec de la suppression de l'équipe!");
            }
        }
    }

    private void showTeamDialog(Team team) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(team == null ? "Créer une nouvelle équipe" : "Modifier l'équipe");
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20px; -fx-spacing: 10px;");

        Label titleLabel = new Label(team == null ? "Créer une nouvelle équipe" : "Modifier l'équipe");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Nom de l'équipe");

        TextField countryField = new TextField();
        countryField.setPromptText("Pays");

        ComboBox<String> jeuCombo = new ComboBox<>();
        jeuCombo.setItems(FXCollections.observableArrayList("League of Legends", "Valorant", "CS:GO", "Dota 2", "Autre"));
        jeuCombo.setPromptText("Sélectionner le jeu");

        ComboBox<String> niveauCombo = new ComboBox<>();
        niveauCombo.setItems(FXCollections.observableArrayList("Débutant", "Intermédiaire", "Pro"));
        niveauCombo.setPromptText("Sélectionner le niveau");

        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.setItems(FXCollections.observableArrayList("en attente", "approuvé", "refusé"));
        statutCombo.setPromptText("Sélectionner le statut");


        Spinner<Integer> scoreSpinner = new Spinner<>(0, 10000, 0, 100);

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description courte");
        descriptionArea.setPrefHeight(80);
        descriptionArea.setWrapText(true);

        TextArea detailedDescriptionArea = new TextArea();
        detailedDescriptionArea.setPromptText("Description détaillée");
        detailedDescriptionArea.setPrefHeight(100);
        detailedDescriptionArea.setWrapText(true);

        if (team != null) {
            nameField.setText(team.getName());
            countryField.setText(team.getCountry());
            jeuCombo.setValue(team.getJeu());
            niveauCombo.setValue(team.getNiveau());
            statutCombo.setValue(team.getStatut());
            scoreSpinner.getValueFactory().setValue(team.getScore());
            descriptionArea.setText(team.getDescription() != null ? team.getDescription() : "");
            detailedDescriptionArea.setText(team.getDetailedDescription() != null ? team.getDetailedDescription() : "");
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button saveBtn = new Button("💾 Enregistrer");
        Button cancelBtn = new Button("❌ Annuler");

        saveBtn.setStyle("-fx-padding: 8px 20px; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-color: #00d4ff;");
        cancelBtn.setStyle("-fx-padding: 8px 20px; -fx-font-size: 12px; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String country = countryField.getText().trim();
            String jeu = jeuCombo.getValue();
            String niveau = niveauCombo.getValue();
            String statut = statutCombo.getValue();

            if (name.isEmpty() || country.isEmpty() || jeu == null || niveau == null) {
                showError("Erreur de validation", "Les champs obligatoires sont: Nom, Pays, Jeu, Niveau");
                return;
            }

            Team newTeam;
            if (team == null) {
                newTeam = new Team(name, country, descriptionArea.getText(), jeu, niveau);
                newTeam.setDetailedDescription(detailedDescriptionArea.getText());
                newTeam.setStatut(statut != null ? statut : "en attente");
                newTeam.setScore(scoreSpinner.getValue());

                if (teamService.addTeam(newTeam)) {
                    teamList.add(newTeam);
                    showInfo("Succès", "Équipe ajoutée avec succès!");
                } else {
                    showError("Erreur", "Échec de l'ajout de l'équipe!");
                }
            } else {
                team.setName(name);
                team.setCountry(country);
                team.setDescription(descriptionArea.getText());
                team.setDetailedDescription(detailedDescriptionArea.getText());
                team.setJeu(jeu);
                team.setNiveau(niveau);
                team.setStatut(statut != null ? statut : "en attente");
                team.setScore(scoreSpinner.getValue());

                if (teamService.updateTeam(team)) {
                    teamTable.refresh();
                    showInfo("Succès", "Équipe mise à jour avec succès!");
                } else {
                    showError("Erreur", "Échec de la mise à jour de l'équipe!");
                }
            }
            dialogStage.close();
        });

        cancelBtn.setOnAction(e -> dialogStage.close());

        buttonBox.getChildren().addAll(saveBtn, cancelBtn);
        
        ScrollPane scrollPane = new ScrollPane();
        VBox formBox = new VBox(8);
        formBox.setStyle("-fx-padding: 10px;");
        formBox.getChildren().addAll(
            titleLabel,
            new Label("Nom de l'équipe:"), nameField,
            new Label("Pays:"), countryField,
            new Label("Jeu:"), jeuCombo,
            new Label("Niveau:"), niveauCombo,
            new Label("Statut:"), statutCombo,
            new Label("Score:"), scoreSpinner,
            new Label("Description:"), descriptionArea,
            new Label("Description détaillée:"), detailedDescriptionArea,
            buttonBox
        );
        scrollPane.setContent(formBox);

        Scene dialogScene = new Scene(scrollPane, 500, 800);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    @FXML
    private void onSearchTeams(ActionEvent event) {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadTeams();
        } else {
            List<Team> searchResults = teamService.searchTeamsByName(searchText);
            teamList = FXCollections.observableArrayList(searchResults);
            teamTable.setItems(teamList);
        }
    }

    @FXML
    private void onFilterByStatus(ActionEvent event) {
        String filter = filterCombo.getValue();
        if (filter == null || filter.equals("Tous")) {
            loadTeams();
        } else {
            List<Team> filtered = teamService.searchTeamsByStatus(filter);
            teamList = FXCollections.observableArrayList(filtered);
            teamTable.setItems(teamList);
        }
    }

    @FXML
    private void onRefresh(ActionEvent event) {
        searchField.clear();
        if (filterCombo != null) {
            filterCombo.setValue("Tous");
        }
        loadTeams();
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

