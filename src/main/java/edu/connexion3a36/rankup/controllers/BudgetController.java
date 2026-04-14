package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.Budget;
import edu.connexion3a36.entities.Team;
import edu.connexion3a36.services.BudgetService;
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
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BudgetController {

    @FXML
    private TableView<Budget> budgetTable;
    @FXML
    private TableColumn<Budget, Integer> idColumn;
    @FXML
    private TableColumn<Budget, String> teamNameColumn;
    @FXML
    private TableColumn<Budget, Float> montantAlloueColumn;
    @FXML
    private TableColumn<Budget, Float> montantUtiliseColumn;
    @FXML
    private TableColumn<Budget, Float> restantColumn;
    @FXML
    private TableColumn<Budget, String> statutColumn;
    @FXML
    private TableColumn<Budget, Void> actionsColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterCombo;

    private BudgetService budgetService;
    private TeamService teamService;
    private ObservableList<Budget> budgetList;

    @FXML
    public void initialize() {
        budgetService = new BudgetService();
        teamService = new TeamService();
        setupTableColumns();
        setupFilters();
        loadBudgets();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        montantAlloueColumn.setCellValueFactory(new PropertyValueFactory<>("montantAlloue"));
        montantUtiliseColumn.setCellValueFactory(new PropertyValueFactory<>("montantUtilise"));
        restantColumn.setCellValueFactory(new PropertyValueFactory<>("restant"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        actionsColumn.setCellFactory(param -> new TableCell<Budget, Void>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");
            private final Button viewBtn = new Button("👁️ View");

            {
                editBtn.setStyle("-fx-padding: 5px 10px; -fx-cursor: hand; -fx-font-size: 10px;");
                deleteBtn.setStyle("-fx-padding: 5px 10px; -fx-cursor: hand; -fx-font-size: 10px; -fx-text-fill: red;");
                viewBtn.setStyle("-fx-padding: 5px 10px; -fx-cursor: hand; -fx-font-size: 10px; -fx-text-fill: blue;");

                editBtn.setOnAction(event -> editBudget(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> deleteBudget(getTableView().getItems().get(getIndex())));
                viewBtn.setOnAction(event -> viewBudgetDetails(getTableView().getItems().get(getIndex())));
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
                "Tous", "en attente", "approuvé", "refusé", "épuisé"
            );
            filterCombo.setItems(filterOptions);
            filterCombo.setValue("Tous");
        }
    }

    private void loadBudgets() {
        List<Budget> budgets = budgetService.getAllBudgets();
        budgetList = FXCollections.observableArrayList(budgets);
        budgetTable.setItems(budgetList);
    }

    @FXML
    private void onAddBudget(ActionEvent event) {
        showBudgetDialog(null);
    }

    private void editBudget(Budget budget) {
        showBudgetDialog(budget);
    }

    private void viewBudgetDetails(Budget budget) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du Budget - " + budget.getTeamName());
        alert.setHeaderText(null);
        
        String details = String.format(
            "Équipe: %s\nMontant alloué: %.2f€\nMontant utilisé: %.2f€\nRestant: %.2f€\nStatut: %s\n\nNotes:\n%s",
            budget.getTeamName(),
            budget.getMontantAlloue(),
            budget.getMontantUtilise(),
            budget.getRestant(),
            budget.getStatut(),
            budget.getNotes() != null ? budget.getNotes() : "N/A"
        );
        alert.setContentText(details);
        alert.showAndWait();
    }

    private void deleteBudget(Budget budget) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer le budget");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le budget de " + budget.getTeamName() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (budgetService.deleteBudget(budget.getId())) {
                budgetList.remove(budget);
                showInfo("Succès", "Budget supprimé avec succès!");
            } else {
                showError("Erreur", "Échec de la suppression du budget!");
            }
        }
    }

    private void showBudgetDialog(Budget budget) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(budget == null ? "Créer un nouveau budget" : "Modifier le budget");
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20px; -fx-spacing: 10px;");

        Label titleLabel = new Label(budget == null ? "Créer un nouveau budget" : "Modifier le budget");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ComboBox<Team> teamCombo = new ComboBox<>();
        List<Team> teams = teamService.getAllTeams();
        teamCombo.setItems(FXCollections.observableArrayList(teams));
        teamCombo.setPromptText("Sélectionner l'équipe");
        teamCombo.setConverter(new StringConverter<Team>() {
            @Override
            public String toString(Team team) {
                return team == null ? "" : team.getName();
            }

            @Override
            public Team fromString(String string) {
                return teamCombo.getItems().stream()
                        .filter(team -> team.getName().equals(string))
                        .findFirst().orElse(null);
            }
        });

        Spinner<Double> montantAlloueSpinner = new Spinner<>(0.0, 999999.99, 1000.0, 100.0);
        montantAlloueSpinner.setEditable(true);

        Spinner<Double> montantUtiliseSpinner = new Spinner<>(0.0, 999999.99, 0.0, 100.0);
        montantUtiliseSpinner.setEditable(true);

        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.setItems(FXCollections.observableArrayList("en attente", "approuvé", "refusé", "épuisé"));
        statutCombo.setPromptText("Sélectionner le statut");

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Notes (optionnel)");
        notesArea.setPrefHeight(100);
        notesArea.setWrapText(true);

        TextField justificatifField = new TextField();
        justificatifField.setPromptText("Justificatif (optionnel)");

        if (budget != null) {
            Team selectedTeam = teamService.getTeamById(budget.getTeamId());
            teamCombo.setValue(selectedTeam);
            montantAlloueSpinner.getValueFactory().setValue((double) budget.getMontantAlloue());
            montantUtiliseSpinner.getValueFactory().setValue((double) budget.getMontantUtilise());
            statutCombo.setValue(budget.getStatut());
            notesArea.setText(budget.getNotes() != null ? budget.getNotes() : "");
            justificatifField.setText(budget.getJustificatif() != null ? budget.getJustificatif() : "");
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button saveBtn = new Button("💾 Enregistrer");
        Button cancelBtn = new Button("❌ Annuler");

        saveBtn.setStyle("-fx-padding: 8px 20px; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-color: #00d4ff;");
        cancelBtn.setStyle("-fx-padding: 8px 20px; -fx-font-size: 12px; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            Team selectedTeam = teamCombo.getValue();
            float montantAlloue = montantAlloueSpinner.getValue().floatValue();
            float montantUtilise = montantUtiliseSpinner.getValue().floatValue();
            String statut = statutCombo.getValue();

            if (selectedTeam == null) {
                showError("Erreur de validation", "Veuillez sélectionner une équipe");
                return;
            }

            if (!ValidationUtil.validateBudget(montantAlloue, selectedTeam.getId(), statut)) {
                return;
            }

            if (!ValidationUtil.validateMontantUtilise(montantUtilise, montantAlloue)) {
                return;
            }

            if (budget == null) {
                Budget newBudget = new Budget(montantAlloue, LocalDateTime.now(), selectedTeam.getId());
                newBudget.setMontantUtilise(montantUtilise);
                newBudget.setNotes(notesArea.getText());
                newBudget.setJustificatif(justificatifField.getText());

                if (budgetService.addBudget(newBudget)) {
                    loadBudgets();
                    showInfo("Succès", "Budget ajouté avec succès!");
                } else {
                    showError("Erreur", "Échec de l'ajout du budget!");
                }
            } else {
                budget.setMontantAlloue(montantAlloue);
                budget.setMontantUtilise(montantUtilise);
                budget.setNotes(notesArea.getText());
                budget.setJustificatif(justificatifField.getText());

                if (budgetService.updateBudget(budget)) {
                    budgetTable.refresh();
                    showInfo("Succès", "Budget mise à jour avec succès!");
                } else {
                    showError("Erreur", "Échec de la mise à jour du budget!");
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
            new Label("Équipe:"), teamCombo,
            new Label("Montant alloué (€):"), montantAlloueSpinner,
            new Label("Montant utilisé (€):"), montantUtiliseSpinner,
            new Label("Statut:"), statutCombo,
            new Label("Notes:"), notesArea,
            new Label("Justificatif:"), justificatifField,
            buttonBox
        );
        scrollPane.setContent(formBox);

        Scene dialogScene = new Scene(scrollPane, 500, 700);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    @FXML
    private void onFilterByStatus(ActionEvent event) {
        // Filtering by status disabled - statut column removed from table
        loadBudgets();
    }

    @FXML
    private void onRefresh(ActionEvent event) {
        if (filterCombo != null) {
            filterCombo.setValue("Tous");
        }
        loadBudgets();
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
