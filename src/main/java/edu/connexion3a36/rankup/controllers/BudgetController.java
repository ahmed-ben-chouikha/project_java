package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.Budget;
import edu.connexion3a36.entities.Team;
import edu.connexion3a36.services.BudgetService;
import edu.connexion3a36.services.TeamService;
import edu.connexion3a36.tools.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
    private TableColumn<Budget, Void> actionsColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> teamFilterCombo;
    @FXML
    private ComboBox<String> sortCombo;

    private BudgetService budgetService;
    private TeamService teamService;
    private ObservableList<Budget> budgetList;
    private FilteredList<Budget> filteredBudgets;
    private SortedList<Budget> sortedBudgets;

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

        actionsColumn.setCellFactory(param -> new TableCell<Budget, Void>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");
            private final Button viewBtn = new Button("👁️ View");

            {
                editBtn.getStyleClass().addAll("action-btn", "action-btn-edit");
                deleteBtn.getStyleClass().addAll("action-btn", "action-btn-delete");
                viewBtn.getStyleClass().addAll("action-btn", "action-btn-view");

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
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        }
        if (teamFilterCombo != null) {
            teamFilterCombo.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        }
        if (sortCombo != null) {
            sortCombo.valueProperty().addListener((obs, oldValue, newValue) -> applySorting());
        }

        setupFilterOptions();
    }

    private void setupFilterOptions() {
        if (teamFilterCombo != null) {
            teamFilterCombo.setItems(FXCollections.observableArrayList("Toutes les équipes"));
            teamFilterCombo.setValue("Toutes les équipes");
        }

        if (sortCombo != null) {
            sortCombo.setItems(FXCollections.observableArrayList(
                "ID (croissant)",
                "ID (décroissant)",
                "Montant alloué (croissant)",
                "Montant alloué (décroissant)",
                "Montant restant (croissant)",
                "Montant restant (décroissant)",
                "Équipe (A-Z)",
                "Équipe (Z-A)"
            ));
            sortCombo.setValue("ID (croissant)");
        }
    }

    private void loadBudgets() {
        List<Budget> budgets = budgetService.getAllBudgets();
        if (budgetList == null) {
            budgetList = FXCollections.observableArrayList();
            filteredBudgets = new FilteredList<>(budgetList, b -> true);
            sortedBudgets = new SortedList<>(filteredBudgets);
            budgetTable.setItems(sortedBudgets);
            sortedBudgets.comparatorProperty().bind(budgetTable.comparatorProperty());
        }
        budgetList.setAll(budgets);

        if (teamFilterCombo != null) {
            String current = teamFilterCombo.getValue();
            List<String> teamNames = budgetList.stream()
                .map(Budget::getTeamName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .toList();
            ObservableList<String> values = FXCollections.observableArrayList("Toutes les équipes");
            values.addAll(teamNames);
            teamFilterCombo.setItems(values);
            teamFilterCombo.setValue(values.contains(current) ? current : "Toutes les équipes");
        }

        applyFilters();
        applySorting();
    }

    private void applyFilters() {
        if (filteredBudgets == null) {
            return;
        }

        String query = searchField != null && searchField.getText() != null
            ? searchField.getText().trim().toLowerCase(Locale.ROOT)
            : "";
        String selectedTeam = teamFilterCombo != null ? teamFilterCombo.getValue() : "Toutes les équipes";
        filteredBudgets.setPredicate(budget -> {
            if (query.isEmpty()) {
                if (selectedTeam == null || "Toutes les équipes".equals(selectedTeam)) {
                    return true;
                }
                return selectedTeam.equalsIgnoreCase(budget.getTeamName());
            }

            String teamName = budget.getTeamName() != null ? budget.getTeamName().toLowerCase(Locale.ROOT) : "";
            String notes = budget.getNotes() != null ? budget.getNotes().toLowerCase(Locale.ROOT) : "";
            String justificatif = budget.getJustificatif() != null ? budget.getJustificatif().toLowerCase(Locale.ROOT) : "";

            boolean matchesQuery = teamName.contains(query)
                || notes.contains(query)
                || justificatif.contains(query)
                || String.valueOf(budget.getMontantAlloue()).contains(query)
                || String.valueOf(budget.getMontantUtilise()).contains(query);

            boolean matchesTeam = selectedTeam == null
                || "Toutes les équipes".equals(selectedTeam)
                || (budget.getTeamName() != null && selectedTeam.equalsIgnoreCase(budget.getTeamName()));

            return matchesQuery && matchesTeam;
        });
    }

    private void applySorting() {
        if (sortedBudgets == null || sortCombo == null || sortCombo.getValue() == null) {
            return;
        }

        Comparator<Budget> comparator = switch (sortCombo.getValue()) {
            case "ID (décroissant)" -> Comparator.comparing(Budget::getId, Comparator.nullsLast(Integer::compareTo)).reversed();
            case "Montant alloué (croissant)" -> Comparator.comparing(Budget::getMontantAlloue, Comparator.nullsLast(Float::compareTo));
            case "Montant alloué (décroissant)" -> Comparator.comparing(Budget::getMontantAlloue, Comparator.nullsLast(Float::compareTo)).reversed();
            case "Montant restant (croissant)" -> Comparator.comparing(Budget::getRestant, Comparator.nullsLast(Float::compareTo));
            case "Montant restant (décroissant)" -> Comparator.comparing(Budget::getRestant, Comparator.nullsLast(Float::compareTo)).reversed();
            case "Équipe (A-Z)" -> Comparator.comparing(b -> safeLower(b.getTeamName()));
            case "Équipe (Z-A)" -> Comparator.comparing((Budget b) -> safeLower(b.getTeamName())).reversed();
            default -> Comparator.comparing(Budget::getId, Comparator.nullsLast(Integer::compareTo));
        };

        sortedBudgets.comparatorProperty().unbind();
        sortedBudgets.setComparator(comparator);
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
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

        float usageRatio = budget.getMontantAlloue() > 0 ? budget.getMontantUtilise() / budget.getMontantAlloue() : 0;
        String warningText = usageRatio >= 1
            ? "\n\nAlerte: Dépassement ou épuisement du budget."
            : usageRatio >= 0.8f
            ? "\n\nAlerte: Plus de 80% du budget est déjà utilisé."
            : "";
        
        String details = String.format(
            "Équipe: %s\nMontant alloué: %.2f€\nMontant utilisé: %.2f€\nRestant: %.2f€\n\nNotes:\n%s%s",
            budget.getTeamName(),
            budget.getMontantAlloue(),
            budget.getMontantUtilise(),
            budget.getRestant(),
            budget.getNotes() != null ? budget.getNotes() : "N/A",
            warningText
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

        Spinner<Double> montantUtiliseSpinner = null;
        if (budget != null) {
            montantUtiliseSpinner = new Spinner<>(0.0, 999999.99, 0.0, 100.0);
            montantUtiliseSpinner.setEditable(true);
        }

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
            notesArea.setText(budget.getNotes() != null ? budget.getNotes() : "");
            justificatifField.setText(budget.getJustificatif() != null ? budget.getJustificatif() : "");
        }

        final Spinner<Double> montantUtiliseSpinnerRef = montantUtiliseSpinner;

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button saveBtn = new Button("💾 Enregistrer");
        Button cancelBtn = new Button("❌ Annuler");

        saveBtn.setStyle("-fx-padding: 8px 20px; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-color: #00d4ff;");
        cancelBtn.setStyle("-fx-padding: 8px 20px; -fx-font-size: 12px; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            Team selectedTeam = teamCombo.getValue();
            Float montantAlloue = parseNumericInput(montantAlloueSpinner.getEditor().getText(), "Montant alloué", false);
            if (montantAlloue == null) {
                return;
            }

            float montantUtilise = 0;
            if (budget != null) {
                Float parsedMontantUtilise = parseNumericInput(montantUtiliseSpinnerRef.getEditor().getText(), "Montant utilisé", true);
                if (parsedMontantUtilise == null) {
                    return;
                }
                montantUtilise = parsedMontantUtilise;
            }

            if (!validateLettersOnlyOptional(notesArea.getText(), "Notes")) {
                return;
            }

            if (!validateLettersOnlyOptional(justificatifField.getText(), "Justificatif")) {
                return;
            }

            if (selectedTeam == null) {
                showError("Erreur de validation", "Veuillez sélectionner une équipe");
                return;
            }

            if (budget == null && budgetService.hasBudgetForTeam(selectedTeam.getId())) {
                showError("Erreur de validation", "Cette équipe possède déjà un budget. Modifiez-le au lieu d'en créer un nouveau.");
                return;
            }

            if (budget != null && selectedTeam.getId() != budget.getTeamId() && budgetService.hasBudgetForTeam(selectedTeam.getId())) {
                showError("Erreur de validation", "Impossible de transférer ce budget: l'équipe sélectionnée possède déjà un budget.");
                return;
            }

            if (!ValidationUtil.validateBudget(montantAlloue, selectedTeam.getId(), "approuvé")) {
                showError("Erreur de validation", "Le montant alloué doit être positif et l'équipe doit être valide.");
                return;
            }

            if (!ValidationUtil.validateMontantUtilise(montantUtilise, montantAlloue)) {
                showError("Erreur de validation", "Le montant utilisé doit être positif et ne pas dépasser le montant alloué.");
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
                budget.setTeamId(selectedTeam.getId());
                budget.setTeamName(selectedTeam.getName());
                budget.setMontantAlloue(montantAlloue);
                budget.setMontantUtilise(montantUtilise);
                budget.setNotes(notesArea.getText());
                budget.setJustificatif(justificatifField.getText());

                if (budgetService.updateBudget(budget)) {
                    loadBudgets();
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
            new Label("Montant alloué (€):"), montantAlloueSpinner
        );

        if (budget != null) {
            formBox.getChildren().addAll(new Label("Montant utilisé (€):"), montantUtiliseSpinner);
        }

        formBox.getChildren().addAll(
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
        applyFilters();
    }

    @FXML
    private void onRefresh(ActionEvent event) {
        loadBudgets();
    }

    @FXML
    private void onExportBudgets(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les budgets");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        fileChooser.setInitialFileName("budgets_export.csv");

        File file = fileChooser.showSaveDialog(budgetTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write("id,team,montant_alloue,montant_utilise,montant_restant,notes,justificatif");
            writer.newLine();

            for (Budget budget : filteredBudgets) {
                writer.write(String.format(
                    Locale.ROOT,
                    "%d,%s,%.2f,%.2f,%.2f,%s,%s",
                    budget.getId(),
                    csvEscape(budget.getTeamName()),
                    budget.getMontantAlloue(),
                    budget.getMontantUtilise(),
                    budget.getRestant(),
                    csvEscape(budget.getNotes()),
                    csvEscape(budget.getJustificatif())
                ));
                writer.newLine();
            }

            showInfo("Export réussi", "Fichier exporté: " + file.getAbsolutePath());
        } catch (IOException ex) {
            showError("Erreur d'export", "Impossible d'exporter le fichier CSV.");
        }
    }

    private String csvEscape(String value) {
        if (value == null) {
            return "\"\"";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
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

    private Float parseNumericInput(String rawInput, String fieldLabel, boolean allowZero) {
        if (rawInput == null || rawInput.trim().isEmpty()) {
            showError("Erreur de validation", "Le champ '" + fieldLabel + "' est obligatoire.");
            return null;
        }

        String normalized = rawInput.trim().replace(',', '.');
        float parsed;
        try {
            parsed = Float.parseFloat(normalized);
        } catch (NumberFormatException ex) {
            showError("Erreur de validation", "Le champ '" + fieldLabel + "' doit contenir uniquement des chiffres.");
            return null;
        }

        if ((!allowZero && parsed <= 0) || (allowZero && parsed < 0)) {
            showError("Erreur de validation", "Le champ '" + fieldLabel + "' contient une valeur invalide.");
            return null;
        }

        return parsed;
    }

    private boolean validateLettersOnlyOptional(String value, String fieldLabel) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        if (!ValidationUtil.isLettersOnly(value)) {
            showError("Erreur de validation", "Le champ '" + fieldLabel + "' doit contenir des lettres, pas des chiffres.");
            return false;
        }

        return true;
    }
}
