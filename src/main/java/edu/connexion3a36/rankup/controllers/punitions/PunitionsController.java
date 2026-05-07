package edu.connexion3a36.rankup.controllers.punitions;

import edu.connexion3a36.entities.Punition;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.PunitionService;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    /**
     * Model class for leaderboard table rows
     */
    public static class LeaderboardRow {
        private final String playerName;
        private final long banCount;

        public LeaderboardRow(String playerName, long banCount) {
            this.playerName = playerName;
            this.banCount = banCount;
        }

        public String getPlayerName() {
            return playerName;
        }

        public long getBanCount() {
            return banCount;
        }
    }

    @FXML private TextField searchField;
    @FXML private DatePicker startAtPicker;
    @FXML private DatePicker endAtPicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<ReclamationChoice> reclamationCombo;
    @FXML private VBox punitionCardsBox;
    @FXML private Button submitButton;
    @FXML private Button chatbotIconButton;
    @FXML private Button gamesNewsButton;
    @FXML private Label matchBanCount;
    @FXML private Label tournamentBanCount;
    @FXML private Label gameBanCount;
    @FXML private TableView<LeaderboardRow> bannedPlayersTable;

    private final PunitionService service = new PunitionService();
    private final ObservableList<Punition> rows = FXCollections.observableArrayList();
    private final FilteredList<Punition> filtered = new FilteredList<>(rows, item -> true);
    private final Map<Integer, String> playerNicknames = new HashMap<>();
    private Integer editingPunitionId;
    private Integer focusedPunitionId;
    private ScheduledExecutorService leaderboardRefreshExecutor;

    @FXML
    void initialize() {
        if (statusCombo != null) {
            statusCombo.getItems().setAll(List.of(STATUS_MATCH, STATUS_TOURNAMENT, STATUS_GAME));
        }
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        }

        loadPlayerNicknames();
        loadReclamationChoices();
        loadData();
        
        if (statusCombo != null) { // Only clear if we have the form fields
            clearForm();
        }

        // Initialize leaderboard table
        if (bannedPlayersTable != null) {
            initializeLeaderboardTable();
            startLeaderboardAutoRefresh();
        }
    }

    /**
     * Open the chatbot in a popup dialog window
     */
    @FXML
    void onOpenChatbot(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/punitions/chatbot-pane.fxml"));
            VBox chatbotPane = loader.load();
            
            // Create a new stage (popup window) for the chatbot
            Stage chatbotStage = new Stage();
            chatbotStage.setTitle("Ban Recommendation Chatbot");
            chatbotStage.setWidth(500);
            chatbotStage.setHeight(700);
            chatbotStage.setResizable(true);
            
            // Style the chatbot window
            Scene scene = new Scene(chatbotPane);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            chatbotStage.setScene(scene);
            
            // Center the window on parent
            javafx.stage.Window owner = chatbotIconButton.getScene().getWindow();
            if (owner != null) {
                chatbotStage.initOwner(owner);
                chatbotStage.initModality(Modality.WINDOW_MODAL);
                chatbotStage.setX(owner.getX() + (owner.getWidth() - chatbotStage.getWidth()) / 2);
                chatbotStage.setY(owner.getY() + (owner.getHeight() - chatbotStage.getHeight()) / 2);
            }
            
            chatbotStage.show();
        } catch (IOException e) {
            showError("Chatbot Error", "Unable to open chatbot: " + e.getMessage());
        }
    }

    /**
     * Open the Games News popup
     */
    @FXML
    void onOpenGamesNews(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/punitions/games-news.fxml"));
            VBox newsPane = loader.load();
            
            Stage newsStage = new Stage();
            newsStage.setTitle("Latest Gaming News");
            newsStage.setWidth(600);
            newsStage.setHeight(800);
            
            Scene scene = new Scene(newsPane);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            newsStage.setScene(scene);
            
            // Center on parent
            javafx.stage.Window owner = gamesNewsButton.getScene().getWindow();
            if (owner != null) {
                newsStage.initOwner(owner);
                newsStage.initModality(Modality.WINDOW_MODAL);
                newsStage.setX(owner.getX() + (owner.getWidth() - newsStage.getWidth()) / 2);
                newsStage.setY(owner.getY() + (owner.getHeight() - newsStage.getHeight()) / 2);
            }
            
            newsStage.show();
        } catch (IOException e) {
            showError("News Error", "Unable to open games news: " + e.getMessage());
            e.printStackTrace();
        }
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
            // Advanced: Apply ban escalation after save
            applyBanEscalation(punition);
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
    void onOpenCreatePopup(ActionEvent event) {
        clearForm();
        openFormPopup("Configure Punition", null, null);
    }

    private void openFormPopup(String title, Punition punitionToEdit, Integer preSelectedReclamationId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/punitions/punition-form.fxml"));
            loader.setController(this);
            VBox root = loader.load();
            
            if (punitionToEdit != null) {
                fillForm(punitionToEdit);
            } else if (preSelectedReclamationId != null) {
                // For "Add Additional Ban"
                editingPunitionId = null;
                startAtPicker.setValue(LocalDate.now());
                endAtPicker.setValue(LocalDate.now().plusDays(1));
                statusCombo.setValue(null);
                setSelectedReclamation(preSelectedReclamationId);
            }
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle(title);
            
            Scene scene = new Scene(root);
            scene.setFill(null);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            
            Button popupSubmit = (Button) root.lookup("#submitButton");
            Button popupCancel = (Button) root.lookup("#cancelButton");
            Label titleLabel = (Label) root.lookup("#formTitle");
            if (titleLabel != null) titleLabel.setText(title);

            popupCancel.setOnAction(e -> stage.close());
            popupSubmit.setOnAction(e -> {
                if (editingPunitionId != null) {
                    onUpdatePunition(null);
                } else {
                    onCreatePunition(null);
                }
                
                if (editingPunitionId == null) {
                    stage.close();
                }
            });

            if (editingPunitionId != null) {
                popupSubmit.setText("UPDATE PUNITION");
            }

            stage.show();
        } catch (Exception e) {
            showError("Popup Error", "Unable to open form: " + e.getMessage());
            e.printStackTrace();
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
        if (startAtPicker != null) startAtPicker.setValue(toDateValue(punition.getStartAt()));
        if (endAtPicker != null) endAtPicker.setValue(toDateValue(punition.getEndAt()));
        if (statusCombo != null) statusCombo.setValue(normalizeStatus(punition.getPlayerStatus()));
        setSelectedReclamation(punition.getReclamationId());
        if (submitButton != null) submitButton.setText("➤");
    }

    private void clearForm() {
        editingPunitionId = null;
        if (startAtPicker != null) startAtPicker.setValue(LocalDate.now());
        if (endAtPicker != null) endAtPicker.setValue(LocalDate.now().plusDays(1));
        if (statusCombo != null) statusCombo.setValue(null);
        if (reclamationCombo != null) reclamationCombo.setValue(null);
        if (submitButton != null) submitButton.setText("➤");
    }

    /**
     * Pre-fill the form to add another ban to the same reclamation.
     * This sets create mode (not edit) so it inserts a new punition row.
     */
    private void prepareAddBan(Punition existingPunition) {
        openFormPopup("Add Additional Ban to #" + existingPunition.getReclamationId(), null, existingPunition.getReclamationId());
    }

    private void loadReclamationChoices() {
        if (reclamationCombo == null) return;
        try {
            List<ReclamationChoice> choices = service.getReclamationChoices().stream()
                    .map(item -> new ReclamationChoice(item.getId(), item.getTitre()))
                    .sorted((a, b) -> Integer.compare(b.getId(), a.getId()))
                    .toList();
            reclamationCombo.getItems().setAll(choices);
        } catch (SQLException e) {
            showError("Load failed", "Unable to load reclamation list: " + e.getMessage());
        }
    }

    private void setSelectedReclamation(int reclamationId) {
        if (reclamationCombo == null) return;
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

        // Group by reclamationId to show all related bans in one card
        Map<Integer, List<Punition>> groups = filtered.stream()
                .collect(Collectors.groupingBy(Punition::getReclamationId, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<Integer, List<Punition>> entry : groups.entrySet()) {
            VBox card = createGroupedPunitionCard(entry.getKey(), entry.getValue());
            if (focusedPunitionId != null && entry.getValue().stream().anyMatch(p -> p.getId() == focusedPunitionId)) {
                pulseHighlight(card);
            }
            punitionCardsBox.getChildren().add(card);
        }
    }

    private VBox createGroupedPunitionCard(int reclamationId, List<Punition> group) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("card", "punition-item-card");

        // Header: Reclamation ID and Player Nickname
        Punition first = group.get(0);
        Label title = new Label("Reclamation #" + reclamationId);
        title.getStyleClass().add("section-title");

        Label nicknameBadge = new Label("Player: " + resolveNickname(first));
        nicknameBadge.getStyleClass().addAll("reclamation-meta-badge", "reclamation-meta-player");

        VBox bansContainer = new VBox(8);
        bansContainer.getStyleClass().add("punition-bans-list");
        
        for (Punition p : group) {
            HBox banRow = new HBox(12);
            banRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            banRow.getStyleClass().add("punition-ban-row");

            Label typeBadge = new Label(getDisplayStatus(p.getPlayerStatus()));
            typeBadge.getStyleClass().addAll("reclamation-meta-badge", "punition-status-badge");
            typeBadge.setStyle(getStatusStyle(p)); // Pass full object to check isAutomatic
            typeBadge.setMinWidth(140);

            Label dateRange = new Label(prettyDate(p.getStartAt()) + " ➔ " + prettyDate(p.getEndAt()));
            dateRange.getStyleClass().add("punition-date-chip");
            HBox.setHgrow(dateRange, Priority.ALWAYS);

            if (p.isAutomatic()) {
                Label autoLabel = new Label("[SYSTEM AUTOMATIC]");
                autoLabel.setStyle("-fx-text-fill: #22d3ee; -fx-font-size: 11px; -fx-font-weight: 800; -fx-padding: 0 0 0 10;");
                banRow.getChildren().add(autoLabel);
            }

            Button edit = new Button("✎");
            edit.getStyleClass().addAll("btn-primary", "icon-button-small");
            edit.setOnAction(e -> {
                openFormPopup("Edit Punition #" + p.getId(), p, null);
            });

            Button delete = new Button("🗑");
            delete.getStyleClass().addAll("btn-danger", "icon-button-small");
            delete.setOnAction(e -> deletePunitionCard(p));

            banRow.getChildren().addAll(typeBadge, dateRange, edit, delete);
            bansContainer.getChildren().add(banRow);
        }

        // Footer Actions
        HBox actions = new HBox(8);
        actions.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button addBan = new Button("➕ Add Ban");
        addBan.getStyleClass().add("btn-primary");
        addBan.setOnAction(e -> prepareAddBan(first));

        Button reclamationButton = new Button("Reclamation");
        reclamationButton.getStyleClass().add("btn-primary");
        reclamationButton.setOnAction(e -> openLinkedReclamation(first));

        Button responseButton = new Button("Response");
        responseButton.getStyleClass().add("btn-primary");
        responseButton.setOnAction(e -> openLinkedResponse(first));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        actions.getChildren().addAll(addBan, spacer, reclamationButton, responseButton);

        card.getChildren().addAll(title, nicknameBadge, bansContainer, actions);
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

    private String getStatusStyle(Punition p) {
        String baseStyle = switch (normalizeStatus(p.getPlayerStatus())) {
            case STATUS_MATCH -> "-fx-text-fill: #f97316; -fx-font-weight: 700;";
            case STATUS_TOURNAMENT -> "-fx-text-fill: #eab308; -fx-font-weight: 700;";
            case STATUS_GAME -> "-fx-text-fill: #ef4444; -fx-font-weight: 700;";
            default -> "-fx-text-fill: #38bdf8; -fx-font-weight: 700;";
        };
        
        if (p.isAutomatic()) {
            // Brighter cyan for automatic bans to make them pop
            return baseStyle + "-fx-text-fill: #22d3ee; -fx-border-color: #22d3ee; -fx-border-width: 1.5; -fx-border-radius: 999; -fx-background-color: rgba(34, 211, 238, 0.05);";
        }
        return baseStyle;
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

    /**
     * Initialize the leaderboard table with columns
     */
    private void initializeLeaderboardTable() {
        try {
            TableColumn<LeaderboardRow, String> playerColumn = new TableColumn<>("Player");
            playerColumn.setCellValueFactory(new PropertyValueFactory<>("playerName"));
            playerColumn.setPrefWidth(200);

            TableColumn<LeaderboardRow, Long> banCountColumn = new TableColumn<>("Ban Count");
            banCountColumn.setCellValueFactory(new PropertyValueFactory<>("banCount"));
            banCountColumn.setPrefWidth(100);

            bannedPlayersTable.getColumns().setAll(playerColumn, banCountColumn);
            
            // Load initial data asynchronously to avoid blocking UI
            Thread initialLoadThread = new Thread(() -> {
                try {
                    Thread.sleep(500); // Small delay to ensure UI is ready
                    refreshLeaderboard();
                } catch (Exception e) {
                    System.err.println("Failed to load initial leaderboard: " + e.getMessage());
                }
            });
            initialLoadThread.setDaemon(true);
            initialLoadThread.start();
        } catch (Exception e) {
            System.err.println("Failed to initialize leaderboard table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Start background thread to refresh leaderboard every 15 seconds
     */
    private void startLeaderboardAutoRefresh() {
        try {
            leaderboardRefreshExecutor = Executors.newScheduledThreadPool(1, r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });

            leaderboardRefreshExecutor.scheduleAtFixedRate(
                    this::refreshLeaderboard,
                    15,  // Initial delay of 15 seconds to let UI stabilize
                    15,
                    TimeUnit.SECONDS
            );
        } catch (Exception e) {
            System.err.println("Failed to start leaderboard auto-refresh: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Refresh leaderboard data from the database
     */
    private void refreshLeaderboard() {
        try {
            // Get ban counts
            long matchBans = service.getBanCountByStatus("match");
            long tournamentBans = service.getBanCountByStatus("tournament");
            long gameBans = service.getBanCountByStatus("game");

            // Get most banned players
            List<Object[]> mostBanned = service.getMostBannedPlayers(10);

            // Update UI on JavaFX thread
            Platform.runLater(() -> {
                try {
                    if (matchBanCount != null) {
                        matchBanCount.setText(String.valueOf(matchBans));
                    }
                    if (tournamentBanCount != null) {
                        tournamentBanCount.setText(String.valueOf(tournamentBans));
                    }
                    if (gameBanCount != null) {
                        gameBanCount.setText(String.valueOf(gameBans));
                    }

                    // Populate table
                    if (bannedPlayersTable != null) {
                        ObservableList<LeaderboardRow> tableData = FXCollections.observableArrayList();
                        if (mostBanned != null && !mostBanned.isEmpty()) {
                            for (Object[] row : mostBanned) {
                                int playerId = (Integer) row[0];
                                long banCount = (Long) row[1];
                                String playerName = playerNicknames.getOrDefault(playerId, "Player #" + playerId);
                                tableData.add(new LeaderboardRow(playerName, banCount));
                            }
                        }
                        bannedPlayersTable.setItems(tableData);
                    }
                } catch (Exception e) {
                    System.err.println("Error updating leaderboard UI: " + e.getMessage());
                }
            });
        } catch (SQLException e) {
            // Log silently to avoid disrupting the main UI
            System.err.println("Failed to refresh leaderboard data: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error in refreshLeaderboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stop the leaderboard refresh executor when controller is destroyed
     */
    public void shutdown() {
        if (leaderboardRefreshExecutor != null && !leaderboardRefreshExecutor.isShutdown()) {
            leaderboardRefreshExecutor.shutdown();
            try {
                if (!leaderboardRefreshExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    leaderboardRefreshExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                leaderboardRefreshExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Advanced: Escalate bans for a player if thresholds are reached.
     * 2 match bans → automatic tournament ban.
     * 2 tournament bans → automatic game ban.
     *
     * The escalation uses thresholds: for every 2 match bans a player has,
     * they should have at least 1 tournament ban. For every 2 tournament bans,
     * they should have at least 1 game ban. Missing bans are created automatically.
     */
    private void applyBanEscalation(Punition punition) {
        try {
            // Look up the playerId from the reclamation, since the form doesn't populate the Reclamation object
            Integer playerId = service.getPlayerIdByReclamationId(punition.getReclamationId());
            System.out.println("[Escalation] Checking for player ID: " + playerId + " linked to rec: " + punition.getReclamationId());
            
            if (playerId == null || playerId <= 0) {
                System.out.println("[Escalation] No player found. Skipping.");
                return; 
            }

            StringBuilder escalationMessages = new StringBuilder();

            // Count current bans for this player
            long matchBans = service.getBanCountByPlayerAndStatus(playerId, STATUS_MATCH);
            long tournamentBans = service.getBanCountByPlayerAndStatus(playerId, STATUS_TOURNAMENT);
            long gameBans = service.getBanCountByPlayerAndStatus(playerId, STATUS_GAME);
            
            System.out.println("[Escalation] Player counts - Match: " + matchBans + ", Tournament: " + tournamentBans + ", Game: " + gameBans);

            // Escalate: every 2 match bans → 1 tournament ban required
            long requiredTournamentBans = matchBans / 2;
            if (requiredTournamentBans > tournamentBans) {
                long bansToCreate = requiredTournamentBans - tournamentBans;
                for (long i = 0; i < bansToCreate; i++) {
                    Punition tournamentBan = new Punition();
                    tournamentBan.setStartAt(LocalDateTime.now());
                    tournamentBan.setEndAt(LocalDateTime.now().plusDays(7));
                    tournamentBan.setPlayerStatus(STATUS_TOURNAMENT);
                    tournamentBan.setReclamationId(punition.getReclamationId());
                    tournamentBan.setAutomatic(true); // Mark as system-generated
                    service.addEntity(tournamentBan);
                }
                tournamentBans += bansToCreate; // Update count for the next escalation check
                escalationMessages.append("⚠ ").append(bansToCreate)
                        .append(" automatic TOURNAMENT ban(s) created (player has ")
                        .append(matchBans).append(" match bans).\n");
            }

            // Escalate: every 2 tournament bans → 1 game ban required
            long requiredGameBans = tournamentBans / 2;
            if (requiredGameBans > gameBans) {
                long bansToCreate = requiredGameBans - gameBans;
                for (long i = 0; i < bansToCreate; i++) {
                    Punition gameBan = new Punition();
                    gameBan.setStartAt(LocalDateTime.now());
                    gameBan.setEndAt(LocalDateTime.now().plusDays(30));
                    gameBan.setPlayerStatus(STATUS_GAME);
                    gameBan.setReclamationId(punition.getReclamationId());
                    gameBan.setAutomatic(true); // Mark as system-generated
                    service.addEntity(gameBan);
                }
                escalationMessages.append("🚫 ").append(bansToCreate)
                        .append(" automatic GAME ban(s) created (player has ")
                        .append(tournamentBans).append(" tournament bans).\n");
            }

            // Notify user if any escalation happened
            if (!escalationMessages.isEmpty()) {
                String playerName = playerNicknames.getOrDefault(playerId, "Player #" + playerId);
                showInfo("Ban Escalation",
                        "Automatic bans applied for " + playerName + ":\n\n" + escalationMessages);
            }
        } catch (Exception e) {
            System.err.println("Ban escalation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
