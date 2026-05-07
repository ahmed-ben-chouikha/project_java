package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.Depense;
import edu.connexion3a36.entities.Team;
import edu.connexion3a36.services.DepenseService;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
    private TableColumn<Depense, Float> montantRestantColumn;
    @FXML
    private TableColumn<Depense, String> teamNameColumn;
    @FXML
    private TableColumn<Depense, Void> actionsColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> teamFilterCombo;
    @FXML
    private ComboBox<String> categoryFilterCombo;
    @FXML
    private ComboBox<String> sortCombo;

    private DepenseService depenseService;
    private TeamService teamService;
    private ObservableList<Depense> depenseList;
    private FilteredList<Depense> filteredDepenses;
    private SortedList<Depense> sortedDepenses;

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
        montantRestantColumn.setCellValueFactory(new PropertyValueFactory<>("montantRestant"));
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));

        actionsColumn.setCellFactory(param -> new TableCell<Depense, Void>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");
            private final Button viewBtn = new Button("👁️ View");

            {
                editBtn.getStyleClass().addAll("action-btn", "action-btn-edit");
                deleteBtn.getStyleClass().addAll("action-btn", "action-btn-delete");
                viewBtn.getStyleClass().addAll("action-btn", "action-btn-view");

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
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        }
        if (teamFilterCombo != null) {
            teamFilterCombo.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        }
        if (categoryFilterCombo != null) {
            categoryFilterCombo.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
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

        if (categoryFilterCombo != null) {
            categoryFilterCombo.setItems(FXCollections.observableArrayList(
                "Toutes les catégories",
                "salaire",
                "équipement",
                "voyage",
                "autre"
            ));
            categoryFilterCombo.setValue("Toutes les catégories");
        }

        if (sortCombo != null) {
            sortCombo.setItems(FXCollections.observableArrayList(
                "ID (croissant)",
                "ID (décroissant)",
                "Montant (croissant)",
                "Montant (décroissant)",
                "Montant restant (croissant)",
                "Montant restant (décroissant)",
                "Titre (A-Z)",
                "Titre (Z-A)"
            ));
            sortCombo.setValue("ID (croissant)");
        }
    }

    private void loadDepenses() {
        List<Depense> depenses = depenseService.getAllDepenses();
        if (depenseList == null) {
            depenseList = FXCollections.observableArrayList();
            filteredDepenses = new FilteredList<>(depenseList, d -> true);
            sortedDepenses = new SortedList<>(filteredDepenses);
            depenseTable.setItems(sortedDepenses);
            sortedDepenses.comparatorProperty().bind(depenseTable.comparatorProperty());
        }
        depenseList.setAll(depenses);

        if (teamFilterCombo != null) {
            String current = teamFilterCombo.getValue();
            List<String> teams = depenseList.stream()
                .map(Depense::getTeamName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .toList();
            ObservableList<String> values = FXCollections.observableArrayList("Toutes les équipes");
            values.addAll(teams);
            teamFilterCombo.setItems(values);
            teamFilterCombo.setValue(values.contains(current) ? current : "Toutes les équipes");
        }

        applyFilters();
        applySorting();
    }

    private void applyFilters() {
        if (filteredDepenses == null) {
            return;
        }

        String query = searchField != null && searchField.getText() != null
            ? searchField.getText().trim().toLowerCase(Locale.ROOT)
            : "";
        String team = teamFilterCombo != null ? teamFilterCombo.getValue() : "Toutes les équipes";
        String category = categoryFilterCombo != null ? categoryFilterCombo.getValue() : "Toutes les catégories";
        filteredDepenses.setPredicate(depense -> {
            if (query.isEmpty()) {
                return matchesTeam(depense, team) && matchesCategory(depense, category);
            }

            String titre = depense.getTitre() != null ? depense.getTitre().toLowerCase(Locale.ROOT) : "";
            String categorie = depense.getCategorie() != null ? depense.getCategorie().toLowerCase(Locale.ROOT) : "";
            String teamName = depense.getTeamName() != null ? depense.getTeamName().toLowerCase(Locale.ROOT) : "";
            String description = depense.getDescription() != null ? depense.getDescription().toLowerCase(Locale.ROOT) : "";

            boolean matchesQuery = titre.contains(query)
                || categorie.contains(query)
                || teamName.contains(query)
                || description.contains(query)
                || String.valueOf(depense.getMontant()).contains(query)
                || String.valueOf(depense.getMontantRestant()).contains(query);

            return matchesQuery && matchesTeam(depense, team) && matchesCategory(depense, category);
        });
    }

    private boolean matchesTeam(Depense depense, String team) {
        if (team == null || "Toutes les équipes".equals(team)) {
            return true;
        }
        return depense.getTeamName() != null && team.equalsIgnoreCase(depense.getTeamName());
    }

    private boolean matchesCategory(Depense depense, String category) {
        if (category == null || "Toutes les catégories".equals(category)) {
            return true;
        }
        return depense.getCategorie() != null && category.equalsIgnoreCase(depense.getCategorie());
    }

    private void applySorting() {
        if (sortedDepenses == null || sortCombo == null || sortCombo.getValue() == null) {
            return;
        }

        Comparator<Depense> comparator = switch (sortCombo.getValue()) {
            case "ID (décroissant)" -> Comparator.comparing(Depense::getId, Comparator.nullsLast(Integer::compareTo)).reversed();
            case "Montant (croissant)" -> Comparator.comparing(Depense::getMontant, Comparator.nullsLast(Float::compareTo));
            case "Montant (décroissant)" -> Comparator.comparing(Depense::getMontant, Comparator.nullsLast(Float::compareTo)).reversed();
            case "Montant restant (croissant)" -> Comparator.comparing(Depense::getMontantRestant, Comparator.nullsLast(Float::compareTo));
            case "Montant restant (décroissant)" -> Comparator.comparing(Depense::getMontantRestant, Comparator.nullsLast(Float::compareTo)).reversed();
            case "Titre (A-Z)" -> Comparator.comparing(d -> safeLower(d.getTitre()));
            case "Titre (Z-A)" -> Comparator.comparing((Depense d) -> safeLower(d.getTitre())).reversed();
            default -> Comparator.comparing(Depense::getId, Comparator.nullsLast(Integer::compareTo));
        };

        sortedDepenses.comparatorProperty().unbind();
        sortedDepenses.setComparator(comparator);
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
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
            "Titre: %s\nMontant: %.2f€\nMontant restant (équipe): %.2f€\nCatégorie: %s\nÉquipe: %s\n\nDescription:\n%s",
            depense.getTitre(),
            depense.getMontant(),
            depense.getMontantRestant(),
            depense.getCategorie(),
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

        ComboBox<Team> teamCombo = new ComboBox<>();
        List<Team> teams = teamService.getAllTeams();
        teamCombo.setItems(FXCollections.observableArrayList(teams));
        teamCombo.setPromptText("Sélectionner l'équipe");
        teamCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Team team) {
                return team == null ? "" : team.getName();
            }

            @Override
            public Team fromString(String string) {
                return teamCombo.getItems().stream()
                    .filter(team -> team.getName().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });

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
            Float montant = parseNumericInput(montantSpinner.getEditor().getText(), "Montant", false);
            if (montant == null) {
                return;
            }

            String categorie = categorieCombo.getValue();
            String description = descriptionArea.getText().trim();
            Team selectedTeam = teamCombo.getValue();
            String statut = depense == null ? "payée" : (depense.getStatut() == null ? "payée" : depense.getStatut());

            if (titre.isEmpty()) {
                showError("Erreur de validation", "Le champ 'Titre' est obligatoire.");
                return;
            }

            if (!ValidationUtil.isLettersOnly(titre)) {
                showError("Erreur de validation", "Le champ 'Titre' doit contenir des lettres, pas des chiffres.");
                return;
            }

            if (!validateLettersOnlyOptional(description, "Description")) {
                return;
            }

            if (!validateLettersOnlyOptional(factureField.getText(), "Facture")) {
                return;
            }

            if (selectedTeam == null) {
                showError("Erreur de validation", "Veuillez sélectionner une équipe. Une dépense doit appartenir à une équipe.");
                return;
            }

            if (!ValidationUtil.validateDepense(titre, montant, statut, categorie)) {
                showError("Erreur de validation", "Vérifiez les champs requis: titre, montant, statut et catégorie.");
                return;
            }

            if (!ValidationUtil.validateDescription(description)) {
                showError("Erreur de validation", "La description est invalide.");
                return;
            }

            if (depense == null) {
                Depense newDepense = new Depense(titre, montant, LocalDateTime.now(), statut);
                newDepense.setCategorie(categorie);
                newDepense.setDescription(description);
                newDepense.setFacture(factureField.getText().trim());
                newDepense.setTeamId(selectedTeam.getId());
                newDepense.setTeamName(selectedTeam.getName());

                if (depenseService.addDepense(newDepense)) {
                    loadDepenses();
                    showInfo("Succès", "Dépense ajoutée avec succès!");
                } else {
                    showError("Erreur", "Échec de l'ajout de la dépense. Vérifiez que l'équipe a un budget suffisant.");
                }
            } else {
                depense.setTitre(titre);
                depense.setMontant(montant);
                depense.setCategorie(categorie);
                depense.setStatut(statut);
                depense.setDescription(description);
                depense.setFacture(factureField.getText().trim());
                depense.setTeamId(selectedTeam.getId());
                depense.setTeamName(selectedTeam.getName());

                if (depenseService.updateDepense(depense)) {
                    loadDepenses();
                    showInfo("Succès", "Dépense mise à jour avec succès!");
                } else {
                    showError("Erreur", "Échec de la mise à jour de la dépense. Vérifiez que le budget de l'équipe est suffisant.");
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
            new Label("Équipe:"), teamCombo,
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
        applyFilters();
    }

    @FXML
    private void onRefresh(ActionEvent event) {
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

