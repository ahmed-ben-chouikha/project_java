package edu.connexion3a36.rankup.controllers.reclamations;

import edu.connexion3a36.entities.Reclamation;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.ReclamationService;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
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
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    @FXML private ComboBox<String> etatCombo;
    @FXML private ComboBox<PlayerChoice> playerIdCombo;
    @FXML private TextField attachmentField;
    @FXML private Label createdAtLabel;
    @FXML private TextField createdAtField;
    @FXML private Label updatedAtLabel;
    @FXML private TextField updatedAtField;
    @FXML private VBox reclamationCardsBox;
    @FXML private Button submitButton;

    private final ReclamationService service = new ReclamationService();
    private final ObservableList<Reclamation> rows = FXCollections.observableArrayList();
    private final FilteredList<Reclamation> filtered = new FilteredList<>(rows, item -> true);
    private Integer editingReclamationId;
    private Integer focusedReclamationId;

    @FXML
    void initialize() {
        typeCombo.getItems().setAll(List.of(TYPE_JOUEUR, TYPE_TECHNIQUE));
        etatCombo.getItems().setAll(List.of(ETAT_EN_COURS, ETAT_APPROUVE, ETAT_REJETE));
        createdAtField.setEditable(false);
        updatedAtField.setEditable(false);
        typeCombo.valueProperty().addListener((obs, oldValue, newValue) -> syncPlayerAvailabilityWithType());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        loadPlayerIds();
        loadData();
        clearForm();
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
        titreField.setText(reclamation.getTitre());
        descriptionArea.setText(reclamation.getDescription());
        typeCombo.setValue(normalizeType(reclamation.getType()));
        etatCombo.setValue(normalizeEtat(reclamation.getEtat()));
        setPlayerSelection(reclamation.getPlayerId());
        attachmentField.setText(reclamation.getAttachmentFilename() == null ? "" : reclamation.getAttachmentFilename());
        createdAtField.setText(formatDateTime(reclamation.getCreatedAt()));
        updatedAtField.setText(formatDateTime(reclamation.getUpdatedAt()));
    }

    private void clearForm() {
        editingReclamationId = null;
        titreField.clear();
        descriptionArea.clear();
        typeCombo.setValue(TYPE_JOUEUR);
        etatCombo.setValue(ETAT_EN_COURS);
        playerIdCombo.setValue(null);
        attachmentField.clear();
        createdAtField.clear();
        updatedAtField.clear();
        setStatusAndAuditVisibility(false);
        submitButton.setText("➤");
    }

    private void loadPlayerIds() {
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
        etatLabel.setVisible(visible);
        etatLabel.setManaged(visible);
        etatCombo.setVisible(visible);
        etatCombo.setManaged(visible);
        createdAtLabel.setVisible(visible);
        createdAtLabel.setManaged(visible);
        createdAtField.setVisible(visible);
        createdAtField.setManaged(visible);
        updatedAtLabel.setVisible(visible);
        updatedAtLabel.setManaged(visible);
        updatedAtField.setVisible(visible);
        updatedAtField.setManaged(visible);
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

        Label title = new Label(reclamation.getTitre() == null || reclamation.getTitre().isBlank()
                ? "Reclamation"
                : reclamation.getTitre());
        title.getStyleClass().add("card-title");

        HBox metaRow = new HBox(12);
        metaRow.getStyleClass().add("reclamation-meta-row");

        Label typeBadge = new Label("Type: " + normalizeType(reclamation.getType()));
        typeBadge.getStyleClass().addAll("reclamation-meta-badge", "reclamation-meta-type");

        String etat = normalizeEtat(reclamation.getEtat());
        Label etatBadge = new Label("Etat: " + etat);
        etatBadge.getStyleClass().addAll("reclamation-meta-badge", getEtatBadgeClass(etat));

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

        card.getChildren().addAll(title, metaRow, description, attachment, created, updated, actions);
        return card;
    }

    private void beginEdit(Reclamation reclamation) {
        editingReclamationId = reclamation.getId();
        fillForm(reclamation);
        setStatusAndAuditVisibility(true);
        submitButton.setText("➤");
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
}

