package edu.connexion3a36.rankup.controllers.punitions;

import edu.connexion3a36.entities.Punition;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.PunitionService;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PunitionsController {

    private static final String STATUS_MATCH = "banned from match";
    private static final String STATUS_TOURNAMENT = "banned from tournament";
    private static final String STATUS_GAME = "banned from game";
    private static final DateTimeFormatter PRETTY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

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

    @FXML private TextField searchField;
    @FXML private DatePicker startAtPicker;
    @FXML private DatePicker endAtPicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<ReclamationChoice> reclamationCombo;
    @FXML private VBox punitionCardsBox;
    @FXML private Button submitButton;

    private final PunitionService service = new PunitionService();
    private final ObservableList<Punition> rows = FXCollections.observableArrayList();
    private final FilteredList<Punition> filtered = new FilteredList<>(rows, item -> true);
    private final Map<Integer, String> playerNicknames = new HashMap<>();
    private Integer editingPunitionId;
    private Integer focusedPunitionId;

    @FXML
    void initialize() {
        statusCombo.getItems().setAll(List.of(STATUS_MATCH, STATUS_TOURNAMENT, STATUS_GAME));
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        loadPlayerNicknames();
        loadReclamationChoices();
        loadData();
        clearForm();
    }

    @FXML
    void onCreatePunition(ActionEvent event) {
        try {
            Punition punition = buildFromForm();
            boolean updateMode = editingPunitionId != null;
            if (updateMode) {
                service.updateEntity(editingPunitionId, punition);
            } else {
                service.addEntity(punition);
            }
            loadData();
            clearForm();
            showInfo("Success", updateMode ? "Punition updated successfully." : "Punition created successfully.");
        } catch (Exception e) {
            showError("Save failed", e.getMessage());
        }
    }

    @FXML
    void onUpdatePunition(ActionEvent event) {
        onCreatePunition(event);
    }

    @FXML
    void onDeletePunition(ActionEvent event) {
        if (editingPunitionId == null) {
            showError("Delete failed", "Click edit on a punishment card first.");
            return;
        }

        try {
            Punition selected = new Punition();
            selected.setId(editingPunitionId);
            service.deleteEntity(selected);
            loadData();
            clearForm();
            showInfo("Success", "Punition deleted successfully.");
        } catch (Exception e) {
            showError("Delete failed", e.getMessage());
        }
    }

    @FXML
    void onClearForm(ActionEvent event) {
        clearForm();
    }

    private Punition buildFromForm() {
        // Validate start date
        if (startAtPicker.getValue() == null) {
            throw new IllegalArgumentException("Start date is required.");
        }

        // Validate end date
        if (endAtPicker.getValue() == null) {
            throw new IllegalArgumentException("End date is required.");
        }

        // Validate that end date is after start date
        if (endAtPicker.getValue().isBefore(startAtPicker.getValue())) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        // Validate status
        String selectedStatus = statusCombo.getValue();
        if (selectedStatus == null) {
            throw new IllegalArgumentException("Status is required.");
        }

        // Validate reclamation selection
        ReclamationChoice choice = reclamationCombo.getValue();
        if (choice == null) {
            throw new IllegalArgumentException("Reclamation is required.");
        }

        Punition punition = new Punition();
        punition.setStartAt(LocalDateTime.of(startAtPicker.getValue(), java.time.LocalTime.MIDNIGHT));
        punition.setEndAt(LocalDateTime.of(endAtPicker.getValue(), java.time.LocalTime.MIDNIGHT));
        punition.setPlayerStatus(selectedStatus);
        punition.setReclamationId(choice.getId());
        return punition;
    }

    private void fillForm(Punition punition) {
        if (punition == null) {
            return;
        }

        editingPunitionId = punition.getId();
        startAtPicker.setValue(toDateValue(punition.getStartAt()));
        endAtPicker.setValue(toDateValue(punition.getEndAt()));
        statusCombo.setValue(normalizeStatus(punition.getPlayerStatus()));
        setSelectedReclamation(punition.getReclamationId());
        submitButton.setText("➤");
    }

    private void clearForm() {
        editingPunitionId = null;
        startAtPicker.setValue(LocalDate.now());
        endAtPicker.setValue(LocalDate.now().plusDays(1));
        statusCombo.setValue(null);
        reclamationCombo.setValue(null);
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

    private void loadData() {
        try {
            rows.setAll(service.getDataWithReclamation());
            applyFilter();
        } catch (SQLException e) {
            showError("Load failed", e.getMessage());
        }
    }

    private void applyFilter() {
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        filtered.setPredicate(row ->
                row.getPlayerStatus().toLowerCase().contains(q)
                        || String.valueOf(row.getReclamationId()).contains(q)
                        || resolveNickname(row).toLowerCase().contains(q)
        );
        refreshPunitionCards();
    }

    private void refreshPunitionCards() {
        focusedPunitionId = RankUpApp.consumePendingPunitionFocusId();
        punitionCardsBox.getChildren().clear();
        if (filtered.isEmpty()) {
            Label empty = new Label("No punishments found.");
            empty.getStyleClass().add("muted");
            punitionCardsBox.getChildren().add(empty);
            return;
        }
        for (Punition punition : filtered) {
            VBox card = createPunitionCard(punition);
            if (focusedPunitionId != null && focusedPunitionId == punition.getId()) {
                pulseHighlight(card);
            }
            punitionCardsBox.getChildren().add(card);
        }
    }

    private VBox createPunitionCard(Punition punition) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("card", "punition-item-card");

        Label title = new Label("Punishment #" + punition.getId());
        title.getStyleClass().add("section-title");

        HBox metaRow = new HBox(10);
        metaRow.getStyleClass().add("punition-meta-row");
        Label nicknameBadge = new Label("Player: " + resolveNickname(punition));
        nicknameBadge.getStyleClass().addAll("reclamation-meta-badge", "reclamation-meta-player");
        Label typeBadge = new Label(getDisplayStatus(punition.getPlayerStatus()));
        typeBadge.getStyleClass().addAll("reclamation-meta-badge", "punition-status-badge");
        typeBadge.setStyle(getStatusStyle(punition.getPlayerStatus()));
        metaRow.getChildren().addAll(nicknameBadge, typeBadge);

        HBox dateRow = new HBox(8);
        dateRow.getStyleClass().add("punition-date-range");
        Label fromChip = new Label("From " + prettyDate(punition.getStartAt()));
        fromChip.getStyleClass().add("punition-date-chip");
        Label toChip = new Label("To " + prettyDate(punition.getEndAt()));
        toChip.getStyleClass().add("punition-date-chip");
        dateRow.getChildren().addAll(fromChip, toChip);

        HBox actions = new HBox(8);
        Button edit = new Button("✎");
        edit.getStyleClass().addAll("btn-primary", "icon-button");
        edit.setOnAction(e -> fillForm(punition));

        Button delete = new Button("🗑");
        delete.getStyleClass().addAll("btn-danger", "icon-button");
        delete.setOnAction(e -> deletePunitionCard(punition));

        Button reclamationButton = new Button("Reclamation");
        reclamationButton.getStyleClass().add("btn-primary");
        reclamationButton.setOnAction(e -> openLinkedReclamation(punition));

        Button responseButton = new Button("Response");
        responseButton.getStyleClass().add("btn-primary");
        responseButton.setOnAction(e -> openLinkedResponse(punition));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        actions.getChildren().addAll(edit, delete, spacer, reclamationButton, responseButton);

        card.getChildren().addAll(title, metaRow, dateRow, actions);
        return card;
    }

    private void openLinkedReclamation(Punition punition) {
        if (punition == null || punition.getReclamationId() <= 0) {
            showError("Navigation failed", "This punition is not linked to a reclamation.");
            return;
        }
        RankUpApp.setPendingReclamationFocusId(punition.getReclamationId());
        RankUpApp.loadInBase("/views/reclamations/reclamations.fxml");
    }

    private void openLinkedResponse(Punition punition) {
        if (punition == null || punition.getReclamationId() <= 0) {
            showError("Navigation failed", "This punition is not linked to a reclamation.");
            return;
        }

        try {
            Integer responseId = service.getLatestAdminResponseIdByReclamationId(punition.getReclamationId());
            if (responseId == null) {
                showInfo("No response", "No admin response exists for this reclamation yet.");
                return;
            }
            RankUpApp.setPendingAdminResponseFocusId(responseId);
            RankUpApp.loadInBase("/views/adminresponses/admin-responses.fxml");
        } catch (SQLException e) {
            showError("Navigation failed", e.getMessage());
        }
    }

    private void deletePunitionCard(Punition punition) {
        try {
            service.deleteEntity(punition);
            if (editingPunitionId != null && editingPunitionId.equals(punition.getId())) {
                clearForm();
            }
            loadData();
            showInfo("Success", "Punition deleted successfully.");
        } catch (Exception e) {
            showError("Delete failed", e.getMessage());
        }
    }

    private void loadPlayerNicknames() {
        try {
            playerNicknames.clear();
            playerNicknames.putAll(service.getPlayerNicknamesById());
        } catch (SQLException ignored) {
            // Non-blocking fallback if nickname source differs by schema.
        }
    }

    private String resolveNickname(Punition punition) {
        if (punition == null || punition.getReclamation() == null || punition.getReclamation().getPlayerId() == null) {
            return "Unknown player";
        }
        int playerId = punition.getReclamation().getPlayerId();
        return playerNicknames.getOrDefault(playerId, "Player #" + playerId);
    }

    private String prettyDate(LocalDateTime value) {
        return value == null ? "N/A" : value.format(PRETTY_DATE_FORMATTER);
    }

    private LocalDate toDateValue(LocalDateTime dateTime) {
        return dateTime == null ? LocalDate.now() : dateTime.toLocalDate();
    }

    private String getDisplayStatus(String status) {
        return switch (normalizeStatus(status)) {
            case STATUS_MATCH -> "Match Ban";
            case STATUS_TOURNAMENT -> "Tournament Ban";
            case STATUS_GAME -> "Game Ban";
            default -> status;
        };
    }

    private String getStatusStyle(String status) {
        return switch (normalizeStatus(status)) {
            case STATUS_MATCH -> "-fx-text-fill: #f97316; -fx-font-weight: 700;";
            case STATUS_TOURNAMENT -> "-fx-text-fill: #eab308; -fx-font-weight: 700;";
            case STATUS_GAME -> "-fx-text-fill: #ef4444; -fx-font-weight: 700;";
            default -> "-fx-text-fill: #38bdf8; -fx-font-weight: 700;";
        };
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return STATUS_MATCH;
        }
        String normalized = status.trim().toLowerCase();
        if (normalized.contains("match")) {
            return STATUS_MATCH;
        } else if (normalized.contains("tournament")) {
            return STATUS_TOURNAMENT;
        } else if (normalized.contains("game")) {
            return STATUS_GAME;
        }
        return STATUS_MATCH;
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

        PauseTransition delay = new PauseTransition(Duration.seconds(2.8));
        delay.setOnFinished(event -> card.getStyleClass().remove("card-focus-highlight"));
        delay.play();
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

