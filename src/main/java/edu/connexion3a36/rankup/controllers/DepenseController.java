package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.Depense;
import edu.connexion3a36.entities.Team;
import edu.connexion3a36.services.DepenseService;
import edu.connexion3a36.services.TeamService;
import edu.connexion3a36.tools.ValidationUtil;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DepenseController {

    @FXML
    private TableView<Depense> depenseTable;
    @FXML
    private TableColumn<Depense, Integer> idColumn;
    @FXML
    private TableColumn<Depense, String> titreColumn;
    @FXML
    private TableColumn<Depense, Float> montantColumn;
    @FXML
    private TableColumn<Depense, String> categorieColumn;
    @FXML
    private TableColumn<Depense, String> statutColumn;
    @FXML
    private TableColumn<Depense, String> teamNameColumn;
    @FXML
    private TableColumn<Depense, Void> actionsColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterCombo;

    private DepenseService depenseService;
    private TeamService teamService;
    private ObservableList<Depense> depenseList;

    @FXML
    public void initialize() {
        depenseService = new DepenseService();
        teamService = new TeamService();
        setupTableColumns();
        setupFilters();
        loadDepenses();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));

        actionsColumn.setCellFactory(param -> new TableCell<Depense, Void>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");
            private final Button viewBtn = new Button("👁️ View");

            {
                editBtn.setStyle("-fx-padding: 5px 10px; -fx-cursor: hand; -fx-font-size: 10px;");
                deleteBtn.setStyle("-fx-padding: 5px 10px; -fx-cursor: hand; -fx-font-size: 10px; -fx-text-fill: red;");
                viewBtn.setStyle("-fx-padding: 5px 10px; -fx-cursor: hand; -fx-font-size: 10px; -fx-text-fill: blue;");

                editBtn.setOnAction(event -> editDepense(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> deleteDepense(getTableView().getItems().get(getIndex())));
                viewBtn.setOnAction(event -> viewDepenseDetails(getTableView().getItems().get(getIndex())));
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
                "Tous", "en attente", "approuvé", "refusé", "payée"
            );
            filterCombo.setItems(filterOptions);
            filterCombo.setValue("Tous");
        }
    }

    private void loadDepenses() {
        List<Depense> depenses = depenseService.getAllDepenses();
        depenseList = FXCollections.observableArrayList(depenses);
        depenseTable.setItems(depenseList);
    }

    @FXML
    private void onAddDepense(ActionEvent event) {
        showDepenseDialog(null);
    }

    private void editDepense(Depense depense) {
        showDepenseDialog(depense);
    }

    private void viewDepenseDetails(Depense depense) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la Dépense - " + depense.getTitre());
        alert.setHeaderText(null);
        
        String details = String.format(
            "Titre: %s\nMontant: %.2f€\nCatégorie: %s\nStatut: %s\nÉquipe: %s\n\nDescription:\n%s",
            depense.getTitre(),
            depense.getMontant(),
            depense.getCategorie(),
            depense.getStatut(),
            depense.getTeamName() != null ? depense.getTeamName() : "Non assignée",
            depense.getDescription() != null ? depense.getDescription() : "N/A"
        );
        alert.setContentText(details);
        alert.showAndWait();
    }

    private void deleteDepense(Depense depense) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer la dépense");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la dépense: " + depense.getTitre() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (depenseService.deleteDepense(depense.getId())) {
                depenseList.remove(depense);
                showInfo("Succès", "Dépense supprimée avec succès!");
            } else {
                showError("Erreur", "Échec de la suppression de la dépense!");
            }
        }
    }

    private void showDepenseDialog(Depense depense) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(depense == null ? "Créer une nouvelle dépense" : "Modifier la dépense");
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20px; -fx-spacing: 10px;");

        Label titleLabel = new Label(depense == null ? "Créer une nouvelle dépense" : "Modifier la dépense");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField titreField = new TextField();
        titreField.setPromptText("Titre de la dépense");

        Spinner<Double> montantSpinner = new Spinner<>(0.01, 999999.99, 100.0, 10.0);
        montantSpinner.setEditable(true);

        ComboBox<String> categorieCombo = new ComboBox<>();
        categorieCombo.setItems(FXCollections.observableArrayList("salaire", "équipement", "voyage", "autre"));
        categorieCombo.setPromptText("Sélectionner la catégorie");

        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.setItems(FXCollections.observableArrayList("en attente", "approuvé", "refusé", "payée"));
        statutCombo.setPromptText("Sélectionner le statut");

        ComboBox<Team> teamCombo = new ComboBox<>();
        List<Team> teams = teamService.getAllTeams();
        teamCombo.setItems(FXCollections.observableArrayList(teams));
        teamCombo.setPromptText("Sélectionner l'équipe (optionnel)");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description (optionnel)");
        descriptionArea.setPrefHeight(80);
        descriptionArea.setWrapText(true);

        TextField factureField = new TextField();
        factureField.setPromptText("Référence facture (optionnel)");

        if (depense != null) {
            titreField.setText(depense.getTitre());
            montantSpinner.getValueFactory().setValue((double) depense.getMontant());
            if (depense.getCategorie() != null) {
                categorieCombo.setValue(depense.getCategorie());
            }
            statutCombo.setValue(depense.getStatut());
            if (depense.getTeamId() != null) {
                Team selectedTeam = teamService.getTeamById(depense.getTeamId());
                teamCombo.setValue(selectedTeam);
            }
            descriptionArea.setText(depense.getDescription() != null ? depense.getDescription() : "");
            factureField.setText(depense.getFacture() != null ? depense.getFacture() : "");
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button saveBtn = new Button("💾 Enregistrer");
        Button cancelBtn = new Button("❌ Annuler");

        saveBtn.setStyle("-fx-padding: 8px 20px; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-color: #00d4ff;");
        cancelBtn.setStyle("-fx-padding: 8px 20px; -fx-font-size: 12px; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            String titre = titreField.getText().trim();
            float montant = montantSpinner.getValue().floatValue();
            String categorie = categorieCombo.getValue();
            String statut = statutCombo.getValue();
            String description = descriptionArea.getText().trim();

            if (!ValidationUtil.validateDepense(titre, montant, statut, categorie)) {
                return;
            }

            if (!ValidationUtil.validateDescription(description)) {
                return;
            }

            if (depense == null) {
                Depense newDepense = new Depense(titre, montant, LocalDateTime.now(), statut);
                newDepense.setCategorie(categorie);
                newDepense.setDescription(description);
                newDepense.setFacture(factureField.getText().trim());
                if (teamCombo.getValue() != null) {
                    newDepense.setTeamId(teamCombo.getValue().getId());
                }

                if (depenseService.addDepense(newDepense)) {
                    loadDepenses();
                    showInfo("Succès", "Dépense ajoutée avec succès!");
                } else {
                    showError("Erreur", "Échec de l'ajout de la dépense!");
                }
            } else {
                depense.setTitre(titre);
                depense.setMontant(montant);
                depense.setCategorie(categorie);
                depense.setStatut(statut);
                depense.setDescription(description);
                depense.setFacture(factureField.getText().trim());
                if (teamCombo.getValue() != null) {
                    depense.setTeamId(teamCombo.getValue().getId());
                }

                if (depenseService.updateDepense(depense)) {
                    depenseTable.refresh();
                    showInfo("Succès", "Dépense mise à jour avec succès!");
                } else {
                    showError("Erreur", "Échec de la mise à jour de la dépense!");
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
            new Label("Titre:"), titreField,
            new Label("Montant (€):"), montantSpinner,
            new Label("Catégorie:"), categorieCombo,
            new Label("Statut:"), statutCombo,
            new Label("Équipe (optionnel):"), teamCombo,
            new Label("Description:"), descriptionArea,
            new Label("Facture:"), factureField,
            buttonBox
        );
        scrollPane.setContent(formBox);

        Scene dialogScene = new Scene(scrollPane, 500, 800);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    @FXML
    private void onFilterByStatus(ActionEvent event) {
        String filter = filterCombo.getValue();
        if (filter == null || filter.equals("Tous")) {
            loadDepenses();
        } else {
            List<Depense> filtered = depenseService.getDepensesByStatus(filter);
            depenseList = FXCollections.observableArrayList(filtered);
            depenseTable.setItems(depenseList);
        }
    }

    @FXML
    private void onRefresh(ActionEvent event) {
        if (filterCombo != null) {
            filterCombo.setValue("Tous");
        }
        loadDepenses();
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

