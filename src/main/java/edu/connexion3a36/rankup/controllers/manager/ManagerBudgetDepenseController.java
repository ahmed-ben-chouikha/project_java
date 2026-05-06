package edu.connexion3a36.rankup.controllers.manager;

import edu.connexion3a36.entities.Budget;
import edu.connexion3a36.entities.Depense;
import edu.connexion3a36.entities.Team;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.BudgetService;
import edu.connexion3a36.services.DepenseService;
import edu.connexion3a36.services.TeamService;
import edu.connexion3a36.services.GroqChatbotService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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

public class ManagerBudgetDepenseController {

    // Budget Labels
    @FXML private Label montantAlloueLabel;
    @FXML private Label montantUtiliseLabel;
    @FXML private Label montantRestantLabel;
    @FXML private Label budgetStatutLabel;
    @FXML private Label teamNameLabel;
    @FXML private Label dateAllocationLabel;
    @FXML private Label justificatifLabel;
    @FXML private Label notesLabel;

    // Expenses Table
    @FXML private TableView<Depense> depenseTable;
    @FXML private TableColumn<Depense, Integer> idColumn;
    @FXML private TableColumn<Depense, String> titreColumn;
    @FXML private TableColumn<Depense, Float> montantColumn;
    @FXML private TableColumn<Depense, String> categorieColumn;
    @FXML private TableColumn<Depense, String> dateCreationColumn;
    @FXML private TableColumn<Depense, String> statutColumn;
    @FXML private TableColumn<Depense, Void> actionsColumn;

    // Filter Controls
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;

    // Stats Labels
    @FXML private Label totalDepensesLabel;
    @FXML private Label depensesApproveesLabel;
    @FXML private Label depensesAttenteLabel;
    @FXML private Label nombreDepensesLabel;

    // Chatbot Controls
    @FXML private TextArea chatDisplayArea;
    @FXML private TextField chatInputField;
    @FXML private Button chatSendBtn;

    private BudgetService budgetService;
    private DepenseService depenseService;
    private TeamService teamService;
    private ObservableList<Depense> depenseList;
    private FilteredList<Depense> filteredDepenses;
    private SortedList<Depense> sortedDepenses;

    private Budget currentBudget;
    private Team currentTeam;

    @FXML
    public void initialize() {
        budgetService = new BudgetService();
        depenseService = new DepenseService();
        teamService = new TeamService();

        setupTableColumns();
        setupFilters();
        loadManagerData();
        startAutoRefreshStats();
    }
    
    private void startAutoRefreshStats() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(10), event -> loadManagerData())
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        dateCreationColumn.setCellValueFactory(cell -> {
            Object value = cell.getValue().getDateCreation();
            return new javafx.beans.property.SimpleStringProperty(value == null ? "" : value.toString());
        });
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        actionsColumn.setCellFactory(param -> new TableCell<Depense, Void>() {
            private final Button editBtn = new Button("✏️ Modifier");
            private final Button deleteBtn = new Button("🗑️ Supprimer");
            private final Button viewBtn = new Button("👁️ Voir");

            {
                editBtn.getStyleClass().addAll("action-btn", "action-btn-edit");
                deleteBtn.getStyleClass().addAll("action-btn", "action-btn-delete");
                viewBtn.getStyleClass().addAll("action-btn", "action-btn-view");

                editBtn.setOnAction(event -> editExpense(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> deleteExpense(getTableView().getItems().get(getIndex())));
                viewBtn.setOnAction(event -> viewExpenseDetails(getTableView().getItems().get(getIndex())));
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
        if (categoryFilterCombo != null) {
            categoryFilterCombo.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        }
        if (statusFilterCombo != null) {
            statusFilterCombo.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        }

        setupFilterOptions();
    }

    private void setupFilterOptions() {
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

        if (statusFilterCombo != null) {
            statusFilterCombo.setItems(FXCollections.observableArrayList(
                "Tous les statuts",
                "en attente",
                "approuvé",
                "refusé",
                "payée"
            ));
            statusFilterCombo.setValue("Tous les statuts");
        }
    }

    private void loadManagerData() {
        int currentUserId = RankUpApp.getCurrentUserId();
        List<Team> managerTeams = teamService.getTeamsByCreatorId(currentUserId);

        if (managerTeams.isEmpty()) {
            showInfo("Aucune équipe", "Vous n'avez créé aucune équipe. Créez une équipe d'abord.");
            return;
        }

        // Use first team (for now)
        currentTeam = managerTeams.get(0);
        teamNameLabel.setText(currentTeam.getName());

        // Load budget for this team
        currentBudget = budgetService.getBudgetByTeamId(currentTeam.getId());

        if (currentBudget == null) {
            // No budget allocated yet
            montantAlloueLabel.setText("Pas de budget");
            montantUtiliseLabel.setText("€ 0,00");
            montantRestantLabel.setText("€ 0,00");
            budgetStatutLabel.setText("Non alloué");
            dateAllocationLabel.setText("N/A");
            justificatifLabel.setText("N/A");
            notesLabel.setText("Aucun budget n'a été alloué à votre équipe par l'administrateur.");
        } else {
            montantAlloueLabel.setText(String.format("€ %.2f", currentBudget.getMontantAlloue()));
            montantUtiliseLabel.setText(String.format("€ %.2f", currentBudget.getMontantUtilise()));
            montantRestantLabel.setText(String.format("€ %.2f", currentBudget.getRestant()));
            budgetStatutLabel.setText(currentBudget.getStatut() != null ? currentBudget.getStatut() : "N/A");
            dateAllocationLabel.setText(currentBudget.getDateAllocation() != null ? currentBudget.getDateAllocation().toString() : "N/A");
            justificatifLabel.setText(currentBudget.getJustificatif() != null ? currentBudget.getJustificatif() : "N/A");
            notesLabel.setText(currentBudget.getNotes() != null && !currentBudget.getNotes().isEmpty() ? currentBudget.getNotes() : "Aucune note");
        }

        loadExpenses();
    }

    private void loadExpenses() {
        if (currentTeam == null) {
            return;
        }

        List<Depense> depenses = depenseService.getDepensesByTeam(currentTeam.getId());

        if (depenseList == null) {
            depenseList = FXCollections.observableArrayList();
            filteredDepenses = new FilteredList<>(depenseList, d -> true);
            sortedDepenses = new SortedList<>(filteredDepenses);
            depenseTable.setItems(sortedDepenses);
            sortedDepenses.comparatorProperty().bind(depenseTable.comparatorProperty());
        }
        depenseList.setAll(depenses);

        updateStatistics();
        applyFilters();
    }

    private void updateStatistics() {
        float totalDepenses = depenseList.stream().map(Depense::getMontant).reduce(0f, Float::sum);
        float depensesApprouvees = depenseList.stream()
            .filter(d -> "approuvé".equalsIgnoreCase(d.getStatut()) || "payée".equalsIgnoreCase(d.getStatut()))
            .map(Depense::getMontant)
            .reduce(0f, Float::sum);
        long depensesAttente = depenseList.stream().filter(d -> "en attente".equalsIgnoreCase(d.getStatut())).count();

        totalDepensesLabel.setText(String.format("€ %.2f", totalDepenses));
        depensesApproveesLabel.setText(String.format("€ %.2f", depensesApprouvees));
        depensesAttenteLabel.setText(String.valueOf(depensesAttente));
        nombreDepensesLabel.setText(String.valueOf(depenseList.size()));
    }

    private void applyFilters() {
        if (filteredDepenses == null) {
            return;
        }

        String query = searchField != null && searchField.getText() != null
            ? searchField.getText().trim().toLowerCase(Locale.ROOT)
            : "";
        String category = categoryFilterCombo != null ? categoryFilterCombo.getValue() : "Toutes les catégories";
        String status = statusFilterCombo != null ? statusFilterCombo.getValue() : "Tous les statuts";

        filteredDepenses.setPredicate(depense -> {
            // Category filter
            if (!category.equals("Toutes les catégories") && !category.equalsIgnoreCase(depense.getCategorie())) {
                return false;
            }

            // Status filter
            if (!status.equals("Tous les statuts") && !status.equalsIgnoreCase(depense.getStatut())) {
                return false;
            }

            // Search query filter
            if (query.isEmpty()) {
                return true;
            }

            String titre = depense.getTitre() != null ? depense.getTitre().toLowerCase(Locale.ROOT) : "";
            String description = depense.getDescription() != null ? depense.getDescription().toLowerCase(Locale.ROOT) : "";

            return titre.contains(query) || description.contains(query) || String.valueOf(depense.getMontant()).contains(query);
        });
    }

    @FXML
    void onAddExpense(ActionEvent event) {
        if (currentTeam == null) {
            showError("Erreur", "Aucune équipe sélectionnée.");
            return;
        }

        if (currentBudget == null || currentBudget.getRestant() <= 0) {
            showError("Budget insuffisant", "Vous n'avez pas de budget alloué ou il est épuisé.");
            return;
        }

        showExpenseDialog(null);
    }

    private void editExpense(Depense depense) {
        showExpenseDialog(depense);
    }

    private void deleteExpense(Depense depense) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText("Supprimer la dépense");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer \"" + depense.getTitre() + "\" ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (depenseService.deleteDepense(depense.getId())) {
                depenseList.remove(depense);
                updateStatistics();
                showInfo("Succès", "Dépense supprimée avec succès!");
            } else {
                showError("Erreur", "Échec de la suppression de la dépense!");
            }
        }
    }

    private void viewExpenseDetails(Depense depense) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la dépense");
        alert.setHeaderText(depense.getTitre());

        String details = String.format(
            "ID: %d\n" +
            "Titre: %s\n" +
            "Montant: €%.2f\n" +
            "Catégorie: %s\n" +
            "Date: %s\n" +
            "Statut: %s\n" +
            "Description: %s\n" +
            "Facture: %s",
            depense.getId(),
            depense.getTitre(),
            depense.getMontant(),
            depense.getCategorie(),
            depense.getDateCreation(),
            depense.getStatut(),
            depense.getDescription() != null ? depense.getDescription() : "N/A",
            depense.getFacture() != null ? depense.getFacture() : "N/A"
        );
        alert.setContentText(details);
        alert.showAndWait();
    }

    private void showExpenseDialog(Depense depense) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(depense == null ? "Créer une nouvelle dépense" : "Modifier la dépense");
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        VBox dialogVBox = new VBox(10);
        dialogVBox.setStyle("-fx-padding: 20px; -fx-spacing: 10px;");

        Label titleLabel = new Label(depense == null ? "Créer une nouvelle dépense" : "Modifier la dépense");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField titreField = new TextField();
        titreField.setPromptText("Titre de la dépense");

        Spinner<Double> montantSpinner = new Spinner<>(0.0, 999999.99, 0.0, 50.0);
        montantSpinner.setEditable(true);

        ComboBox<String> categorieCombo = new ComboBox<>();
        categorieCombo.setItems(FXCollections.observableArrayList(
            "salaire",
            "équipement",
            "voyage",
            "autre"
        ));
        categorieCombo.setPromptText("Sélectionner une catégorie");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description (optionnelle)");
        descriptionArea.setPrefHeight(100);
        descriptionArea.setWrapText(true);

        TextField factureField = new TextField();
        factureField.setPromptText("Référence facture (optionnelle)");

        if (depense != null) {
            titreField.setText(depense.getTitre());
            montantSpinner.getValueFactory().setValue((double) depense.getMontant());
            categorieCombo.setValue(depense.getCategorie());
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
            if (titre.isEmpty()) {
                showError("Validation", "Le titre est obligatoire.");
                return;
            }

            Float montant = parseNumericInput(montantSpinner.getEditor().getText(), "Montant", false);
            if (montant == null || montant <= 0) {
                showError("Validation", "Le montant doit être positif.");
                return;
            }

            String categorie = categorieCombo.getValue();
            if (categorie == null || categorie.isEmpty()) {
                showError("Validation", "Sélectionnez une catégorie.");
                return;
            }

            // Check if montant exceeds remaining budget
            if (depense == null && currentBudget != null && montant > currentBudget.getRestant()) {
                showError("Budget insuffisant", 
                    String.format("Le montant (€%.2f) dépasse le budget restant (€%.2f).",
                        montant, currentBudget.getRestant()));
                return;
            }

            if (depense == null) {
                // Create new expense
                Depense newDepense = new Depense();
                newDepense.setTitre(titre);
                newDepense.setMontant(montant);
                newDepense.setCategorie(categorie);
                newDepense.setDescription(descriptionArea.getText());
                newDepense.setFacture(factureField.getText());
                newDepense.setTeamId(currentTeam.getId());
                newDepense.setDateCreation(LocalDateTime.now());
                newDepense.setStatut("en attente");

                if (depenseService.addDepense(newDepense)) {
                    loadExpenses();
                    showInfo("Succès", "Dépense créée avec succès!");
                } else {
                    showError("Erreur", "Échec de la création de la dépense!");
                }
            } else {
                // Update existing expense
                depense.setTitre(titre);
                depense.setMontant(montant);
                depense.setCategorie(categorie);
                depense.setDescription(descriptionArea.getText());
                depense.setFacture(factureField.getText());

                if (depenseService.updateDepense(depense)) {
                    loadExpenses();
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
            new Label("Description:"), descriptionArea,
            new Label("Facture:"), factureField,
            new Label("Statut: En attente (sera approuvé par l'admin)"),
            buttonBox
        );
        scrollPane.setContent(formBox);

        Scene dialogScene = new Scene(scrollPane, 500, 700);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    @FXML
    void onRefreshBudget(ActionEvent event) {
        loadManagerData();
    }

    @FXML
    void onRefreshExpenses(ActionEvent event) {
        loadExpenses();
    }

    @FXML
    void onBack(ActionEvent event) {
        RankUpApp.loadInBase("/views/manager/my-teams.fxml");
    }

    private Float parseNumericInput(String text, String fieldName, boolean allowZero) {
        try {
            float value = Float.parseFloat(text);
            if (value < 0 || (value == 0 && !allowZero)) {
                showError("Validation", fieldName + " doit être positif.");
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            showError("Validation", fieldName + " doit être un nombre valide.");
            return null;
        }
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

    @FXML
    private void onChatSend(ActionEvent event) {
        String userMessage = chatInputField.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }

        // Display user message
        appendToChatDisplay("Vous: " + userMessage);
        chatInputField.clear();
        chatSendBtn.setDisable(true);

        // Get current team budget info
        String teamName = currentTeam != null ? currentTeam.getName() : "votre équipe";
        float allocatedBudget = currentBudget != null ? currentBudget.getMontantAlloue() : 0f;
        float usedBudget = currentBudget != null ? currentBudget.getMontantUtilise() : 0f;
        float remainingBudget = allocatedBudget - usedBudget;
        int expenseCount = depenseList != null ? depenseList.size() : 0;

        // Call Groq chatbot in background
        final String finalTeamName = teamName;
        final float finalAllocated = allocatedBudget;
        final float finalUsed = usedBudget;
        final float finalRemaining = remainingBudget;
        
        new Thread(() -> {
            try {
                String response = GroqChatbotService.chat(userMessage, finalTeamName, finalAllocated, finalUsed, finalRemaining, expenseCount);
                javafx.application.Platform.runLater(() -> {
                    appendToChatDisplay("Assistant IA: " + response);
                    chatSendBtn.setDisable(false);
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    appendToChatDisplay("Assistant IA: Désolé, une erreur est survenue. " + e.getMessage());
                    chatSendBtn.setDisable(false);
                });
            }
        }).start();
    }

    private void appendToChatDisplay(String message) {
        if (chatDisplayArea != null) {
            String current = chatDisplayArea.getText();
            String separator = current.isEmpty() ? "" : "\n\n";
            chatDisplayArea.appendText(separator + message);
            chatDisplayArea.setScrollTop(Double.MAX_VALUE); // Auto-scroll to bottom
        }
    }
}
