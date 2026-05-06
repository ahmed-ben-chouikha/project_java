package edu.connexion3a36.rankup.controllers.adminresponses;
import edu.connexion3a36.entities.AdminResponse;
import edu.connexion3a36.entities.Punition;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.AdminResponseService;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
public class AdminResponsesController {
    private static class ReclamationChoice {
        private final int id;
        private final String title;
        private ReclamationChoice(int id, String title) {
            this.id = id;
            this.title = title;
        }
        int getId() {
            return id;
        }
        @Override
        public String toString() {
            if (title == null || title.isBlank()) {
                return String.valueOf(id);
            }
            return id + " - " + title;
        }
    }
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @FXML private TextField searchField;
    @FXML private ComboBox<ReclamationChoice> reclamationCombo;
    @FXML private Label createdAtLabel;
    @FXML private TextField createdAtField;
    @FXML private Label updatedAtLabel;
    @FXML private TextField updatedAtField;
    @FXML private TextArea messageArea;
    @FXML private VBox responseCardsBox;
    @FXML private Button submitButton;
    private final AdminResponseService service = new AdminResponseService();
    private final ObservableList<AdminResponse> rows = FXCollections.observableArrayList();
    private final FilteredList<AdminResponse> filtered = new FilteredList<>(rows, item -> true);
    private Integer editingResponseId;
    private Integer focusedResponseId;
    @FXML
    void initialize() {
        createdAtField.setEditable(false);
        updatedAtField.setEditable(false);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        loadReclamationChoices();
        loadData();
        clearForm();
    }
    @FXML
    void onCreateResponse() {
        try {
            AdminResponse response = buildFromForm();
            boolean updateMode = editingResponseId != null;
            if (updateMode) {
                service.updateEntity(editingResponseId, response);
            } else {
                service.addEntity(response);
            }
            loadData();
            clearForm();
            showInfo("Success", updateMode ? "Admin response updated successfully." : "Admin response created successfully.");
        } catch (Exception e) {
            showError("Save failed", e.getMessage());
        }
    }
    @FXML
    void onUpdateResponse() {
        onCreateResponse();
    }
    @FXML
    void onDeleteResponse() {
        if (editingResponseId == null) {
            showError("Delete failed", "Click edit on a response card first.");
            return;
        }
        try {
            AdminResponse selected = new AdminResponse();
            selected.setId(editingResponseId);
            service.deleteEntity(selected);
            loadData();
            clearForm();
            showInfo("Success", "Admin response deleted successfully.");
        } catch (Exception e) {
            showError("Delete failed", e.getMessage());
        }
    }
    @FXML
    void onClearForm() {
        clearForm();
    }
    private AdminResponse buildFromForm() {
        // Validate message
        String message = messageArea.getText().trim();
        if (message.isEmpty()) {
            throw new IllegalArgumentException("Message is required.");
        }
        if (countAlphabets(message) < 10) {
            throw new IllegalArgumentException("Message must contain at least 10 alphabetic characters (not only numbers).");
        }
        if (message.length() > 5000) {
            throw new IllegalArgumentException("Message must not exceed 5000 characters.");
        }
        // Validate reclamation selection
        ReclamationChoice choice = reclamationCombo.getValue();
        if (choice == null) {
            throw new IllegalArgumentException("Reclamation is required.");
        }
        AdminResponse response = new AdminResponse();
        response.setMessage(message);
        response.setReclamationId(choice.getId());
        response.setCreatedAt(null);
        response.setUpdatedAt(null);
        return response;
    }
    private void fillForm(AdminResponse response) {
        if (response == null) {
            return;
        }
        editingResponseId = response.getId();
        messageArea.setText(response.getMessage());
        setSelectedReclamation(response.getReclamationId());
        createdAtField.setText(formatDateTime(response.getCreatedAt()));
        updatedAtField.setText(formatDateTime(response.getUpdatedAt()));
        setAuditFieldsVisible(true);
        submitButton.setText("➤");
    }
    private void clearForm() {
        editingResponseId = null;
        messageArea.clear();
        reclamationCombo.setValue(null);
        createdAtField.clear();
        updatedAtField.clear();
        setAuditFieldsVisible(false);
        submitButton.setText("➤");
    }
    private void loadReclamationChoices() {
        try {
            List<ReclamationChoice> choices = service.getReclamationChoices().stream()
                    .map(item -> new ReclamationChoice(item.getId(), item.getTitre()))
                    .toList();
            reclamationCombo.getItems().setAll(choices);
        } catch (SQLException e) {
            showError("Load failed", "Unable to load reclamation list: " + e.getMessage());
        }
    }
    private void setSelectedReclamation(int reclamationId) {
        for (ReclamationChoice choice : reclamationCombo.getItems()) {
            if (choice.getId() == reclamationId) {
                reclamationCombo.setValue(choice);
                return;
            }
        }
        ReclamationChoice fallback = new ReclamationChoice(reclamationId, null);
        reclamationCombo.getItems().add(fallback);
        reclamationCombo.setValue(fallback);
    }
    private void setAuditFieldsVisible(boolean visible) {
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
            rows.setAll(service.getDataWithReclamation());
            applyFilter();
        } catch (SQLException e) {
            showError("Load failed", e.getMessage());
        }
    }
    private void applyFilter() {
        if (focusedResponseId == null) {
            focusedResponseId = RankUpApp.consumePendingAdminResponseFocusId();
        }
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        filtered.setPredicate(row ->
                (focusedResponseId != null && row.getId() == focusedResponseId)
                        || row.getMessage().toLowerCase().contains(q)
                        || String.valueOf(row.getReclamationId()).contains(q)
        );
        refreshResponseCards();
    }
    private void refreshResponseCards() {
        responseCardsBox.getChildren().clear();
        if (filtered.isEmpty()) {
            Label empty = new Label("No admin responses found.");
            empty.getStyleClass().add("muted");
            responseCardsBox.getChildren().add(empty);
            return;
        }
        boolean focusMatched = false;
        for (AdminResponse response : filtered) {
            VBox card = createResponseCard(response);
            if (focusedResponseId != null && focusedResponseId == response.getId()) {
                pulseHighlight(card);
                focusMatched = true;
            }
            responseCardsBox.getChildren().add(card);
        }
        if (focusMatched) {
            focusedResponseId = null;
        }
    }
    private VBox createResponseCard(AdminResponse response) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("card", "admin-response-item-card");
        Label title = new Label("Response #" + response.getId());
        title.getStyleClass().add("section-title");
        Label message = new Label("Message: " + safe(response.getMessage()));
        message.setWrapText(true);
        HBox metaRow = new HBox(10);
        metaRow.getStyleClass().add("admin-response-meta-row");
        Label reclamationBadge = new Label("Reclamation: #" + response.getReclamationId());
        reclamationBadge.getStyleClass().addAll("reclamation-meta-badge", "reclamation-meta-type");
        Label created = new Label("Created: " + formatDateTime(response.getCreatedAt()));
        created.getStyleClass().add("muted");
        Label updated = new Label("Updated: " + formatDateTime(response.getUpdatedAt()));
        updated.getStyleClass().add("muted");
        metaRow.getChildren().addAll(reclamationBadge, created, updated);
        HBox actions = new HBox(8);
        Button edit = new Button("✎");
        edit.getStyleClass().addAll("btn-primary", "icon-button");
        edit.setOnAction(e -> fillForm(response));
        Button delete = new Button("🗑");
        delete.getStyleClass().addAll("btn-danger", "icon-button");
        delete.setOnAction(e -> deleteResponseCard(response));
        Button viewReclamation = new Button("Reclamation");
        viewReclamation.getStyleClass().add("btn-primary");
        viewReclamation.setOnAction(e -> openReclamationPage(response));
        Button viewPunition = new Button("Punition");
        viewPunition.getStyleClass().add("btn-primary");
        viewPunition.setOnAction(e -> openPunitionPage(response));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        actions.getChildren().addAll(edit, delete, spacer, viewReclamation, viewPunition);
        card.getChildren().addAll(title, message, metaRow, actions);
        return card;
    }
    private void deleteResponseCard(AdminResponse response) {
        try {
            service.deleteEntity(response);
            if (editingResponseId != null && editingResponseId == response.getId()) {
                clearForm();
            }
            loadData();
            showInfo("Success", "Admin response deleted successfully.");
        } catch (Exception e) {
            showError("Delete failed", e.getMessage());
        }
    }
    private void openReclamationPage(AdminResponse response) {
        if (response == null || response.getReclamationId() <= 0) {
            showInfo("Reclamation", "No linked reclamation found.");
            return;
        }
        RankUpApp.setPendingReclamationFocusId(response.getReclamationId());
        RankUpApp.loadInBase("/views/reclamations/reclamations.fxml");
    }
    private void openPunitionPage(AdminResponse response) {
        try {
            Punition punition = service.getLatestPunitionByReclamationId(response.getReclamationId());
            if (punition == null) {
                showInfo("Punition", "No punition linked to this reclamation.");
                return;
            }
            RankUpApp.setPendingPunitionFocusId(punition.getId());
            RankUpApp.loadInBase("/views/punitions/punitions.fxml");
        } catch (SQLException e) {
            showError("Load failed", e.getMessage());
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
    private String formatDateTime(LocalDateTime value) {
        return value == null ? "" : value.format(DATE_TIME_FORMATTER);
    }
    private String safe(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
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
    private int countAlphabets(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (char c : value.toCharArray()) {
            if (Character.isLetter(c)) {
                count++;
            }
        }
        return count;
    }
}
