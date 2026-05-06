package edu.connexion3a36.rankup.controllers.reclamations;

import edu.connexion3a36.entities.AdminResponse;
import edu.connexion3a36.entities.Punition;
import edu.connexion3a36.entities.Reclamation;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.models.ReclamationNotification;
import edu.connexion3a36.services.AdminResponseService;
import edu.connexion3a36.services.PunitionService;
import edu.connexion3a36.services.ReclamationService;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.Scene;
import edu.connexion3a36.rankup.services.AIApiClient;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;
import javafx.geometry.Insets;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReclamationsController {

    private static final String TYPE_JOUEUR = "JOUEUR";
    private static final String TYPE_TECHNIQUE = "TECHNIQUE";
    private static final String ETAT_EN_COURS = "EN_COURS";
    private static final String ETAT_APPROUVE = "APPROUVE";
    private static final String ETAT_RESOLU = "RESOLU";
    private static final String ETAT_REJETE = "REJETE";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static class PlayerChoice {
        private final Integer id;
        private final String nickname;

        private PlayerChoice(Integer id, String nickname) {
            this.id = id;
            this.nickname = nickname;
        }

        Integer getId() {
            return id;
        }

        @Override
        public String toString() {
            if (nickname == null || nickname.isBlank()) {
                return String.valueOf(id);
            }
            return id + " - " + nickname;
        }
    }

    @FXML private TextField searchField;
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> typeCombo;
    @FXML private Label etatLabel;
    @FXML private VBox etatBox;
    @FXML private ComboBox<String> etatCombo;
    @FXML private ComboBox<PlayerChoice> playerIdCombo;
    @FXML private TextField attachmentField;
    @FXML private Label createdAtLabel;
    @FXML private TextField createdAtField;
    @FXML private Label updatedAtLabel;
    @FXML private TextField updatedAtField;
    @FXML private VBox reclamationCardsBox;
    @FXML private Button submitButton;
    @FXML private Button notificationButton;
    @FXML private Label notificationBadge;

    private final ReclamationService service = new ReclamationService();
    private final AdminResponseService adminResponseService = new AdminResponseService();
    private final ObservableList<Reclamation> rows = FXCollections.observableArrayList();
    private final FilteredList<Reclamation> filtered = new FilteredList<>(rows, item -> true);
    private final ObservableList<ReclamationNotification> notifications = FXCollections.observableArrayList();
    private final Set<Integer> seenAdminResponseIds = new HashSet<>();
    private final Set<Integer> seenPunitionIds = new HashSet<>();
    private final PunitionService punitionService = new PunitionService();
    
    private Integer editingReclamationId;
    private Integer focusedReclamationId;
    private Thread notificationPollingThread;

    @FXML
    void initialize() {
        if (typeCombo != null) {
            typeCombo.getItems().setAll(List.of(TYPE_JOUEUR, TYPE_TECHNIQUE));
            typeCombo.valueProperty().addListener((obs, oldValue, newValue) -> syncPlayerAvailabilityWithType());
        }
        if (etatCombo != null) {
            etatCombo.getItems().setAll(List.of(ETAT_EN_COURS, ETAT_RESOLU, ETAT_APPROUVE, ETAT_REJETE));
        }
        if (createdAtField != null) {
            createdAtField.setEditable(false);
        }
        if (updatedAtField != null) {
            updatedAtField.setEditable(false);
        }
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        }

        loadPlayerIds();
        loadData();
        
        if (typeCombo != null) {
            clearForm();
        }
        
        // Start notification polling in background thread
        startNotificationPolling();
    }

    @FXML
    void onOpenCreatePopup(ActionEvent event) {
        clearForm();
        openFormPopup("Create New Reclamation", null);
    }

    private void openFormPopup(String title, Reclamation reclamationToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/reclamations/reclamation-form.fxml"));
            loader.setController(this);
            VBox root = loader.load();
            
            // Re-bind elements from the loaded FXML
            ComboBox<String> popupEtatCombo = (ComboBox<String>) root.lookup("#etatCombo");
            if (popupEtatCombo != null) {
                this.etatCombo = popupEtatCombo;
                this.etatCombo.getItems().setAll(List.of(ETAT_EN_COURS, ETAT_RESOLU, ETAT_APPROUVE, ETAT_REJETE));
            }
            
            // Populate form if in edit mode
            if (reclamationToEdit != null) {
                fillForm(reclamationToEdit);
                if (etatBox != null) {
                    etatBox.setVisible(true);
                    etatBox.setManaged(true);
                }
            }
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle(title);
            
            Scene scene = new Scene(root);
            scene.setFill(null);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            
            // Re-bind buttons from the loaded FXML
            Button popupSubmit = (Button) root.lookup("#submitButton");
            Button popupCancel = (Button) root.lookup("#cancelButton");
            Label titleLabel = (Label) root.lookup("#formTitle");
            if (titleLabel != null) titleLabel.setText(title);

            popupCancel.setOnAction(e -> stage.close());
            popupSubmit.setOnAction(e -> {
                onCreateReclamation(null);
                if (editingReclamationId == null) { // Success clears editingId
                    stage.close();
                }
            });

            // Set state for update mode
            if (editingReclamationId != null) {
                VBox etatBox = (VBox) root.lookup("#etatBox");
                if (etatBox != null) {
                    etatBox.setVisible(true);
                    etatBox.setManaged(true);
                }
                popupSubmit.setText("UPDATE RECLAMATION");
            }

            stage.show();
        } catch (Exception e) {
            showError("Popup Error", "Unable to open form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onCreateReclamation(ActionEvent event) {
        try {
            Reclamation reclamation = buildFromForm();
            boolean updateMode = editingReclamationId != null;
            if (updateMode) {
                service.updateEntity(editingReclamationId, reclamation);
            } else {
                service.addEntity(reclamation);
            }
            loadData();
            clearForm();
            showInfo("Success", updateMode ? "Reclamation updated successfully." : "Reclamation created successfully.");
        } catch (Exception e) {
            showError("Save failed", e.getMessage());
        }
    }

    @FXML
    void onClearForm(ActionEvent event) {
        clearForm();
    }

    private Reclamation buildFromForm() {
        // Validate titre
        String titre = titreField.getText().trim();
        if (titre.isEmpty()) {
            throw new IllegalArgumentException("Titre is required.");
        }
        if (titre.length() < 5) {
            throw new IllegalArgumentException("Titre must be at least 5 characters long.");
        }
        if (titre.length() > 255) {
            throw new IllegalArgumentException("Titre must not exceed 255 characters.");
        }
        if (!containsAlphabet(titre)) {
            throw new IllegalArgumentException("Titre must contain at least one alphabetic character (not only numbers).");
        }

        // Validate description
        String description = descriptionArea.getText().trim();
        if (description.length() > 5000) {
            throw new IllegalArgumentException("Description must not exceed 5000 characters.");
        }

        // Validate type
        String selectedType = normalizeType(typeCombo.getValue());
        if (selectedType == null) {
            throw new IllegalArgumentException("Type is required.");
        }

        // Validate player selection based on type
        PlayerChoice selectedPlayer = playerIdCombo.getValue();
        if (TYPE_JOUEUR.equals(selectedType) && selectedPlayer == null) {
            throw new IllegalArgumentException("Player is required for type JOUEUR.");
        }

        // Validate attachment filename
        String attachment = attachmentField.getText().trim();
        if (attachment.length() > 500) {
            throw new IllegalArgumentException("Attachment filename must not exceed 500 characters.");
        }

        Reclamation reclamation = new Reclamation();
        reclamation.setTitre(titre);
        reclamation.setDescription(description.isEmpty() ? null : description);
        reclamation.setType(selectedType);
        reclamation.setEtat(editingReclamationId != null ? normalizeEtat(etatCombo.getValue()) : ETAT_EN_COURS);
        reclamation.setPlayerId(TYPE_TECHNIQUE.equals(selectedType) ? null : (selectedPlayer != null ? selectedPlayer.getId() : null));
        reclamation.setAttachmentFilename(attachment.isEmpty() ? null : attachment);
        reclamation.setCreatedAt(null);
        reclamation.setUpdatedAt(null);
        return reclamation;
    }

    private void fillForm(Reclamation reclamation) {
        if (reclamation == null) {
            return;
        }
        if (titreField != null) titreField.setText(reclamation.getTitre());
        if (descriptionArea != null) descriptionArea.setText(reclamation.getDescription());
        if (typeCombo != null) typeCombo.setValue(normalizeType(reclamation.getType()));
        if (etatCombo != null) etatCombo.setValue(normalizeEtat(reclamation.getEtat()));
        setPlayerSelection(reclamation.getPlayerId());
        if (attachmentField != null) attachmentField.setText(reclamation.getAttachmentFilename() == null ? "" : reclamation.getAttachmentFilename());
        if (createdAtField != null) createdAtField.setText(formatDateTime(reclamation.getCreatedAt()));
        if (updatedAtField != null) updatedAtField.setText(formatDateTime(reclamation.getUpdatedAt()));
    }

    private void clearForm() {
        editingReclamationId = null;
        if (titreField != null) titreField.clear();
        if (descriptionArea != null) descriptionArea.clear();
        if (typeCombo != null) typeCombo.setValue(TYPE_JOUEUR);
        if (etatCombo != null) etatCombo.setValue(ETAT_EN_COURS);
        if (playerIdCombo != null) playerIdCombo.setValue(null);
        if (attachmentField != null) attachmentField.clear();
        if (createdAtField != null) createdAtField.clear();
        if (updatedAtField != null) updatedAtField.clear();
        setStatusAndAuditVisibility(false);
        if (submitButton != null) submitButton.setText("➤");
    }

    private void loadPlayerIds() {
        if (playerIdCombo == null) return;
        try {
            List<PlayerChoice> choices = service.getAvailablePlayers().stream()
                    .map(player -> new PlayerChoice(player.getId(), player.getNickname()))
                    .toList();
            playerIdCombo.getItems().setAll(choices);
        } catch (Exception e) {
            showError("Load failed", "Unable to load player IDs: " + e.getMessage());
        }
    }

    private void setPlayerSelection(Integer playerId) {
        if (playerIdCombo == null) return;
        if (playerId == null) {
            playerIdCombo.setValue(null);
            return;
        }
        for (PlayerChoice choice : playerIdCombo.getItems()) {
            if (choice.getId().equals(playerId)) {
                playerIdCombo.setValue(choice);
                return;
            }
        }
        PlayerChoice fallback = new PlayerChoice(playerId, null);
        playerIdCombo.getItems().add(fallback);
        playerIdCombo.setValue(fallback);
    }

    private void syncPlayerAvailabilityWithType() {
        boolean isTechnique = TYPE_TECHNIQUE.equals(normalizeType(typeCombo.getValue()));
        playerIdCombo.setDisable(isTechnique);
        if (isTechnique) {
            playerIdCombo.setValue(null);
        }
    }

    private void setStatusAndAuditVisibility(boolean visible) {
        if (etatLabel != null) {
            etatLabel.setVisible(visible);
            etatLabel.setManaged(visible);
        }
        if (etatCombo != null) {
            etatCombo.setVisible(visible);
            etatCombo.setManaged(visible);
        }
        if (createdAtLabel != null) {
            createdAtLabel.setVisible(visible);
            createdAtLabel.setManaged(visible);
        }
        if (createdAtField != null) {
            createdAtField.setVisible(visible);
            createdAtField.setManaged(visible);
        }
        if (updatedAtLabel != null) {
            updatedAtLabel.setVisible(visible);
            updatedAtLabel.setManaged(visible);
        }
        if (updatedAtField != null) {
            updatedAtField.setVisible(visible);
            updatedAtField.setManaged(visible);
        }
    }

    private void loadData() {
        try {
            rows.setAll(service.getData());
            applyFilter();
        } catch (Exception e) {
            showError("Load failed", e.getMessage());
        }
    }

    private void applyFilter() {
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        filtered.setPredicate(row ->
                row.getTitre().toLowerCase().contains(q)
                        || row.getType().toLowerCase().contains(q)
                        || row.getEtat().toLowerCase().contains(q)
                        || (row.getDescription() != null && row.getDescription().toLowerCase().contains(q))
        );
        refreshReclamationCards();
    }

    private void refreshReclamationCards() {
        focusedReclamationId = RankUpApp.consumePendingReclamationFocusId();
        reclamationCardsBox.getChildren().clear();
        if (filtered.isEmpty()) {
            Label empty = new Label("No reclamations found.");
            empty.getStyleClass().add("muted");
            reclamationCardsBox.getChildren().add(empty);
            return;
        }
        for (Reclamation reclamation : filtered) {
            VBox card = createReclamationCard(reclamation);
            if (focusedReclamationId != null && focusedReclamationId == reclamation.getId()) {
                pulseHighlight(card);
            }
            reclamationCardsBox.getChildren().add(card);
        }
    }

    private VBox createReclamationCard(Reclamation reclamation) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("card", "reclamation-item-card");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label(reclamation.getTitre() == null || reclamation.getTitre().isBlank()
                ? "Reclamation"
                : reclamation.getTitre());
        title.getStyleClass().add("card-title");
        
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        
        Button aiAgent = new Button("✨ AI ANALYST");
        aiAgent.getStyleClass().addAll("btn-secondary", "ai-agent-btn");
        aiAgent.setTooltip(new Tooltip("Launch AI Sentiment & Urgency Analysis"));
        aiAgent.setOnAction(event -> handleAiAnalysis(reclamation, aiAgent));
        
        header.getChildren().addAll(title, headerSpacer, aiAgent);

        HBox metaRow = new HBox(12);
        metaRow.getStyleClass().add("reclamation-meta-row");

        Label typeBadge = new Label("Type: " + normalizeType(reclamation.getType()));
        typeBadge.getStyleClass().addAll("reclamation-meta-badge", "reclamation-meta-type");

        String etat = normalizeEtat(reclamation.getEtat());
        ComboBox<String> etatBadge = new ComboBox<>();
        etatBadge.getItems().setAll(List.of(ETAT_EN_COURS, ETAT_RESOLU, ETAT_APPROUVE, ETAT_REJETE));
        etatBadge.setValue(etat);
        etatBadge.getStyleClass().addAll("reclamation-meta-badge", getEtatBadgeClass(etat));
        etatBadge.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-background-insets: 0;");
        
        etatBadge.setOnAction(e -> {
            String newVal = etatBadge.getValue();
            if (newVal != null && !newVal.equals(reclamation.getEtat())) {
                try {
                    reclamation.setEtat(newVal);
                    service.updateEntity(reclamation.getId(), reclamation);
                    
                    // Update style classes
                    etatBadge.getStyleClass().removeAll("reclamation-meta-etat-en-cours", "reclamation-meta-etat-resolu", "reclamation-meta-etat-rejete");
                    etatBadge.getStyleClass().add(getEtatBadgeClass(newVal));
                } catch (SQLException ex) {
                    showError("Update failed", "Could not update status: " + ex.getMessage());
                    etatBadge.setValue(reclamation.getEtat()); // Revert on failure
                }
            }
        });

        Label playerBadge = new Label("Player: " + (reclamation.getPlayerId() == null ? "N/A" : reclamation.getPlayerId()));
        playerBadge.getStyleClass().addAll("reclamation-meta-badge", "reclamation-meta-player");

        metaRow.getChildren().addAll(typeBadge, etatBadge, playerBadge);

        Label description = new Label("Description: " + safe(reclamation.getDescription()));
        Label attachment = new Label("Attachment: " + safe(reclamation.getAttachmentFilename()));
        Label created = new Label("Created: " + formatDateTime(reclamation.getCreatedAt()));
        Label updated = new Label("Updated: " + formatDateTime(reclamation.getUpdatedAt()));

        HBox actions = new HBox(8);
        Button edit = new Button("✎");
        edit.getStyleClass().addAll("btn-primary", "icon-button");
        edit.setOnAction(event -> beginEdit(reclamation));

        Button delete = new Button("🗑");
        delete.getStyleClass().addAll("btn-danger", "icon-button");
        delete.setOnAction(event -> deleteReclamationCard(reclamation));

        Button response = new Button("Response");
        response.getStyleClass().add("btn-primary");
        response.setOnAction(event -> openResponsePage(reclamation));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        actions.getChildren().addAll(edit, delete, spacer, response);

        card.getChildren().addAll(header, metaRow, description, attachment, created, updated, actions);
        return card;
    }

    private void beginEdit(Reclamation reclamation) {
        editingReclamationId = reclamation.getId();
        openFormPopup("Edit Reclamation #" + reclamation.getId(), reclamation);
    }

    private void deleteReclamationCard(Reclamation reclamation) {
        try {
            service.deleteEntity(reclamation);
            if (editingReclamationId != null && editingReclamationId.equals(reclamation.getId())) {
                clearForm();
            }
            loadData();
            showInfo("Success", "Reclamation deleted successfully.");
        } catch (Exception e) {
            showError("Delete failed", e.getMessage());
        }
    }

    private void pulseHighlight(VBox card) {
        card.getStyleClass().add("card-focus-highlight");

        ScaleTransition pop = new ScaleTransition(Duration.millis(220), card);
        pop.setFromX(1.0);
        pop.setFromY(1.0);
        pop.setToX(1.03);
        pop.setToY(1.03);
        pop.setAutoReverse(true);
        pop.setCycleCount(2);
        pop.play();

        PauseTransition delay = new PauseTransition(Duration.seconds(2.6));
        delay.setOnFinished(event -> card.getStyleClass().remove("card-focus-highlight"));
        delay.play();
    }

    private String getEtatBadgeClass(String etat) {
        return switch (etat) {
            case ETAT_APPROUVE, ETAT_RESOLU -> "reclamation-meta-etat-resolu";
            case ETAT_REJETE -> "reclamation-meta-etat-rejete";
            default -> "reclamation-meta-etat-en-cours";
        };
    }

    private void openResponsePage(Reclamation reclamation) {
        if (reclamation == null) {
            return;
        }
        try {
            Integer responseId = service.getLatestAdminResponseId(reclamation.getId());
            if (responseId == null) {
                showInfo("Response", "No admin response for this reclamation yet.");
                return;
            }
            RankUpApp.setPendingAdminResponseFocusId(responseId);
            RankUpApp.loadInBase("/views/adminresponses/admin-responses.fxml");
        } catch (SQLException e) {
            showError("Response failed", "Failed to load response: " + e.getMessage());
        } catch (Exception e) {
            showError("Response failed", "Failed to load admin responses view: " + e.getMessage());
        }
    }

    private String safe(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "" : value.format(DATE_TIME_FORMATTER);
    }

    private String normalizeEtat(String etat) {
        String normalized = etat == null ? "" : etat.trim().toUpperCase();
        return switch (normalized) {
            case ETAT_APPROUVE -> ETAT_APPROUVE;
            case ETAT_RESOLU -> ETAT_RESOLU;
            case ETAT_REJETE -> ETAT_REJETE;
            default -> ETAT_EN_COURS;
        };
    }

    private String normalizeType(String type) {
        String normalized = type == null ? "" : type.trim().toUpperCase();
        return TYPE_TECHNIQUE.equals(normalized) ? TYPE_TECHNIQUE : TYPE_JOUEUR;
    }

    private String emptyToNull(String value) {
        String trimmed = value == null ? "" : value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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

    private boolean containsAlphabet(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        for (char c : value.toCharArray()) {
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    // ===== Notification Methods =====
    
    @FXML
    void onOpenNotifications(ActionEvent event) {
        if (notifications.isEmpty()) {
            showInfo("Notifications", "No new notifications.");
            return;
        }
        showNotificationsDialog();
    }

    private void startNotificationPolling() {
        if (notificationPollingThread != null && notificationPollingThread.isAlive()) {
            return;
        }
        
        notificationPollingThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    checkForNewAdminResponses();
                    checkForNewPunitions();
                    Thread.sleep(15000); // Check every 15 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error checking notifications: " + e.getMessage());
                }
            }
        });
        notificationPollingThread.setDaemon(true);
        notificationPollingThread.start();
    }

    private void checkForNewAdminResponses() throws SQLException {
        List<AdminResponse> allResponses = adminResponseService.getData();
        
        for (AdminResponse response : allResponses) {
            if (!seenAdminResponseIds.contains(response.getId())) {
                seenAdminResponseIds.add(response.getId());
                
                // Get reclamation details
                Reclamation reclamation = adminResponseService.getReclamationById(response.getReclamationId());
                String reclamationTitre = reclamation != null ? reclamation.getTitre() : "Reclamation #" + response.getReclamationId();
                
                ReclamationNotification notification = new ReclamationNotification(
                    response.getId() * 10, // Avoid ID collision
                    response.getId(),
                    response.getMessage(),
                    response.getReclamationId(),
                    reclamationTitre,
                    response.getCreatedAt(),
                    "RESPONSE"
                );
                
                Platform.runLater(() -> {
                    notifications.add(0, notification);
                    updateNotificationBadge();
                });
            }
        }
    }

    private void checkForNewPunitions() throws SQLException {
        List<Punition> allPunitions = punitionService.getDataWithReclamation();
        
        for (Punition punition : allPunitions) {
            if (!seenPunitionIds.contains(punition.getId())) {
                seenPunitionIds.add(punition.getId());
                
                String reclamationTitre = punition.getReclamation() != null ? punition.getReclamation().getTitre() : "Reclamation #" + punition.getReclamationId();
                
                ReclamationNotification notification = new ReclamationNotification(
                    punition.getId() * 10 + 1, // Avoid ID collision
                    punition.getId(),
                    "Action: " + punition.getPlayerStatus(),
                    punition.getReclamationId(),
                    reclamationTitre,
                    punition.getStartAt(),
                    "PUNITION"
                );
                
                Platform.runLater(() -> {
                    notifications.add(0, notification);
                    updateNotificationBadge();
                });
            }
        }
    }

    private void updateNotificationBadge() {
        if (notificationBadge == null) return;
        int count = notifications.size();
        if (count > 0) {
            notificationBadge.setText(String.valueOf(count));
            notificationBadge.setVisible(true);
            notificationBadge.setManaged(true);
        } else {
            notificationBadge.setVisible(false);
            notificationBadge.setManaged(false);
        }
    }


    private void showNotificationsDialog() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Recent Updates");

        VBox root = new VBox(20);
        root.getStyleClass().add("ai-popup-container");
        root.setPrefWidth(500);
        root.setPadding(new Insets(25));

        Label title = new Label("Recent Activity (" + notifications.size() + ")");
        title.getStyleClass().add("ai-header-title");
        root.getChildren().add(title);

        VBox list = new VBox(12);
        list.setStyle("-fx-background-color: transparent;");

        // Sort notifications: most recent first
        List<ReclamationNotification> sorted = notifications.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .toList();

        for (ReclamationNotification notif : sorted) {
            VBox card = new VBox(8);
            boolean isResponse = "RESPONSE".equals(notif.getType());
            String color = isResponse ? "#38bdf8" : "#f43f5e";
            String bg = isResponse ? "rgba(56, 189, 248, 0.08)" : "rgba(244, 63, 94, 0.08)";
            
            card.setStyle("-fx-background-color: " + bg + "; -fx-padding: 15; -fx-background-radius: 12; -fx-border-color: " + color + "44; -fx-border-width: 1; -fx-border-radius: 12;");
            
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            Label icon = new Label(isResponse ? "💬" : "⚖️");
            icon.setStyle("-fx-font-size: 16px;");
            Label type = new Label(isResponse ? "ADMIN RESPONSE" : "DISCIPLINARY ACTION");
            type.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: 900; -fx-font-size: 11px; -fx-text-transform: uppercase;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label time = new Label(notif.getFormattedTimestamp());
            time.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
            header.getChildren().addAll(icon, type, spacer, time);
            
            Label ref = new Label("Ref: " + notif.getReclamationTitre());
            ref.setStyle("-fx-text-fill: #f1f5f9; -fx-font-weight: 700; -fx-font-size: 13px;");
            
            Label msg = new Label(notif.getAdminMessage());
            msg.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px;");
            msg.setWrapText(true);
            
            card.getChildren().addAll(header, ref, msg);
            list.getChildren().add(card);
        }

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(450);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        scroll.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        
        Button close = new Button("CLOSE");
        close.getStyleClass().add("ai-close-btn");
        close.setPrefWidth(Double.MAX_VALUE);
        close.setOnAction(e -> stage.close());

        root.getChildren().addAll(scroll, close);

        Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void handleAiAnalysis(Reclamation reclamation, Button btn) {
        String originalText = btn.getText();
        btn.setText("⏳");
        btn.setDisable(true);

        Thread thread = new Thread(() -> {
            try {
                String result = AIApiClient.analyzeUrgency(reclamation.getDescription());
                Platform.runLater(() -> {
                    btn.setText(originalText);
                    btn.setDisable(false);
                    showAiResult(reclamation, result);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btn.setText(originalText);
                    btn.setDisable(false);
                    showError("AI Agent Error", "Failed to analyze: " + e.getMessage());
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void showAiResult(Reclamation reclamation, String result) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        
        VBox root = new VBox(25);
        root.getStyleClass().add("ai-popup-container");
        root.setPrefWidth(550);
        
        // Header
        HBox header = new HBox(15);
        header.getStyleClass().add("ai-header-box");
        Label icon = new Label("✨");
        icon.setStyle("-fx-font-size: 28px;");
        VBox titleBox = new VBox(2);
        Label title = new Label("AI Urgency Analysis");
        title.getStyleClass().add("ai-header-title");
        Label subtitle = new Label("Mistral-powered sentiment engine");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, subtitle);
        header.getChildren().addAll(icon, titleBox);
        
        // Parsing logic
        String urgency = parseField(result, "URGENCY LEVEL");
        String sentiment = parseField(result, "SENTIMENT");
        String summary = parseField(result, "SUMMARY");
        String recommendation = parseField(result, "RECOMMENDATION");
        
        // Metrics Grid
        GridPane grid = new GridPane();
        grid.getStyleClass().add("ai-analysis-grid");
        
        VBox urgencyCard = createMetricCard("Urgency", urgency);
        VBox sentimentCard = createMetricCard("Sentiment", sentiment);
        
        grid.add(urgencyCard, 0, 0);
        grid.add(sentimentCard, 1, 0);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);
        
        // Summary & Recommendation
        VBox detailsBox = new VBox(15);
        
        VBox summaryBox = new VBox(8);
        summaryBox.getStyleClass().add("ai-summary-area");
        Label summaryLabel = new Label("SUMMARY");
        summaryLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-font-size: 11px;");
        Label summaryText = new Label(summary);
        summaryText.getStyleClass().add("ai-summary-text");
        summaryText.setWrapText(true);
        summaryBox.getChildren().addAll(summaryLabel, summaryText);
        
        VBox recBox = new VBox(8);
        recBox.getStyleClass().add("ai-summary-area");
        recBox.setStyle("-fx-border-color: rgba(99, 102, 241, 0.2);");
        Label recLabel = new Label("ADVISORY");
        recLabel.setStyle("-fx-text-fill: #a855f7; -fx-font-weight: bold; -fx-font-size: 11px;");
        Label recText = new Label(recommendation);
        recText.getStyleClass().add("ai-summary-text");
        recText.setStyle("-fx-text-fill: #e2e8f0;");
        recText.setWrapText(true);
        recBox.getChildren().addAll(recLabel, recText);
        
        detailsBox.getChildren().addAll(summaryBox, recBox);
        
        // Footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button closeBtn = new Button("ACKNOWLEDGE");
        closeBtn.getStyleClass().add("ai-finish-btn");
        closeBtn.setOnAction(e -> stage.close());
        footer.getChildren().add(closeBtn);
        
        root.getChildren().addAll(header, grid, detailsBox, footer);
        
        Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        
        // Center on screen
        stage.show();
    }

    private VBox createMetricCard(String label, String value) {
        VBox card = new VBox(5);
        card.getStyleClass().add("ai-card");
        Label l = new Label(label);
        l.getStyleClass().add("ai-card-label");
        Label v = new Label(value);
        v.getStyleClass().add("ai-card-value");
        card.getChildren().addAll(l, v);
        return card;
    }

    private String parseField(String text, String field) {
        try {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(field + ".*?:\\s*(.*?)(?=\\d\\.|\\n|$)", java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher m = p.matcher(text);
            if (m.find()) {
                return m.group(1).trim().replaceAll("\\*\\*", "");
            }
        } catch (Exception e) {}
        return "Not detected";
    }

    private String truncateMessage(String message, int maxLength) {
        if (message == null) return "N/A";
        return message.length() > maxLength ? message.substring(0, maxLength) + "..." : message;
    }
}

