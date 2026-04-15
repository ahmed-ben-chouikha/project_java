package edu.connexion3a36.rankup.controllers.admin;

import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.entities.TournamentRegistration;
import edu.connexion3a36.services.TournamentRegistrationService;
import edu.connexion3a36.services.TournamentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminRegistrationController {

    // Header and Counter
    @FXML private Label pendingCountLabel;
    @FXML private Label infoLabel;

    // Filters
    @FXML private ComboBox<TournamentFilterItem> tournamentFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TextField searchField;

    // Table
    @FXML private TableView<AdminRegistrationRow> registrationsTable;
    @FXML private TableColumn<AdminRegistrationRow, String> playerCol;
    @FXML private TableColumn<AdminRegistrationRow, String> teamCol;
    @FXML private TableColumn<AdminRegistrationRow, String> tournamentCol;
    @FXML private TableColumn<AdminRegistrationRow, String> dateCol;
    @FXML private TableColumn<AdminRegistrationRow, String> statusCol;

    // Action Buttons
    @FXML private Button acceptButton;
    @FXML private Button rejectButton;
    @FXML private Label actionErrorLabel;

    private FilteredList<AdminRegistrationRow> filteredRegistrations;
    private TournamentRegistrationService registrationService;
    private TournamentService tournamentService;
    private AdminRegistrationRow selectedRegistration;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    void initialize() {
        registrationService = new TournamentRegistrationService();
        tournamentService = new TournamentService();

        infoLabel.setStyle("-fx-text-fill: #00BCD4;");
        actionErrorLabel.setStyle("-fx-text-fill: #ef4444;");

        // Setup table columns
        setupTable();

        // Load data
        loadAllRegistrations();
        loadTournamentFilter();

        // Disable buttons initially
        acceptButton.setDisable(true);
        rejectButton.setDisable(true);

        // Setup listeners
        registrationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedRegistration = newVal;
            acceptButton.setDisable(newVal == null || !"pending".equalsIgnoreCase(newVal.getStatus()));
            rejectButton.setDisable(newVal == null || !"pending".equalsIgnoreCase(newVal.getStatus()));
        });

        tournamentFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void setupTable() {
        playerCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        teamCol.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        tournamentCol.setCellValueFactory(new PropertyValueFactory<>("tournamentName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusFilter.setItems(FXCollections.observableArrayList("All", "pending", "confirmed", "rejected"));
        statusFilter.setValue("All");
    }

    private void loadAllRegistrations() {
        try {
            List<TournamentRegistration> registrations = registrationService.getAllRegistrations();
            filteredRegistrations = new FilteredList<>(FXCollections.observableArrayList(
                registrations.stream().map(AdminRegistrationRow::fromRegistration).toArray(AdminRegistrationRow[]::new)
            ));
            registrationsTable.setItems(filteredRegistrations);
            updatePendingCount();
        } catch (SQLException e) {
            showError("Failed to load registrations: " + e.getMessage());
        }
    }

    private void loadTournamentFilter() {
        try {
            List<Tournament> tournaments = tournamentService.getData();
            List<TournamentFilterItem> items = tournaments.stream()
                .map(t -> new TournamentFilterItem(t.getId(), t.getName()))
                .toList();

            ObservableList<TournamentFilterItem> filterItems = FXCollections.observableArrayList(
                new TournamentFilterItem(-1, "All Tournaments")
            );
            filterItems.addAll(items);
            tournamentFilter.setItems(filterItems);
            tournamentFilter.setValue(filterItems.get(0));
        } catch (SQLException e) {
            showError("Failed to load tournaments: " + e.getMessage());
        }
    }

    @FXML
    void onAccept(ActionEvent event) {
        if (selectedRegistration == null) {
            showError("Please select a registration to accept");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Accept Registration");
        confirmDialog.setHeaderText("Confirm acceptance");
        confirmDialog.setContentText("Accept registration for " + selectedRegistration.getPlayerName() +
                                    " in " + selectedRegistration.getTournamentName() + "?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                registrationService.acceptRegistration(getRegistrationId(selectedRegistration));
                showSuccess("Registration accepted successfully");
                loadAllRegistrations();
                selectedRegistration = null;
                acceptButton.setDisable(true);
                rejectButton.setDisable(true);
            } catch (SQLException e) {
                showError("Failed to accept registration: " + e.getMessage());
            }
        }
    }

    @FXML
    void onReject(ActionEvent event) {
        if (selectedRegistration == null) {
            showError("Please select a registration to reject");
            return;
        }

        // Show dialog with optional rejection reason
        Dialog<String> reasonDialog = new Dialog<>();
        reasonDialog.setTitle("Reject Registration");
        reasonDialog.setHeaderText("Reject registration for " + selectedRegistration.getPlayerName());

        TextArea reasonArea = new TextArea();
        reasonArea.setWrapText(true);
        reasonArea.setPrefRowCount(5);
        reasonArea.setPromptText("Optional: Enter rejection reason (e.g., duplicate account)");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(
            new Label("Rejection Reason (optional):"),
            reasonArea
        );

        reasonDialog.getDialogPane().setContent(content);
        reasonDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        reasonDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return reasonArea.getText().trim();
            }
            return null;
        });

        reasonDialog.showAndWait().ifPresent(reason -> {
            try {
                int registrationId = getRegistrationId(selectedRegistration);
                registrationService.rejectRegistration(registrationId, reason.isEmpty() ? "Rejected by admin" : reason);
                showSuccess("Registration rejected successfully");
                loadAllRegistrations();
                selectedRegistration = null;
                acceptButton.setDisable(true);
                rejectButton.setDisable(true);
            } catch (SQLException e) {
                showError("Failed to reject registration: " + e.getMessage());
            }
        });
    }

    private void applyFilters() {
        String searchText = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        TournamentFilterItem tournament = tournamentFilter.getValue();

        filteredRegistrations.setPredicate(row -> {
            boolean statusOk = "All".equals(status) || row.getStatus().equalsIgnoreCase(status);
            boolean tournamentOk = tournament == null || tournament.getId() == -1 ||
                                   row.getTournamentName().equalsIgnoreCase(tournament.getName());
            boolean searchOk = row.getPlayerName().toLowerCase().contains(searchText) ||
                              row.getTeamName().toLowerCase().contains(searchText);
            return statusOk && tournamentOk && searchOk;
        });
    }

    private void updatePendingCount() {
        try {
            long pendingCount = registrationService.getPendingCount();
            pendingCountLabel.setText("Pending Registrations: " + pendingCount);
            infoLabel.setText(pendingCount > 0 ?
                "⚠ " + pendingCount + " registration(s) waiting for review" :
                "✓ All registrations reviewed");
        } catch (SQLException e) {
            pendingCountLabel.setText("Pending: Error");
        }
    }

    private int getRegistrationId(AdminRegistrationRow row) throws SQLException {
        List<TournamentRegistration> registrations = registrationService.getAllRegistrations();
        for (TournamentRegistration reg : registrations) {
            if (reg.getPlayerName().equals(row.getPlayerName()) &&
                reg.getTeamName().equals(row.getTeamName()) &&
                reg.getTournamentName().equals(row.getTournamentName())) {
                return reg.getId();
            }
        }
        throw new SQLException("Registration not found");
    }

    private void showError(String message) {
        actionErrorLabel.setText("❌ " + message);
    }

    private void showSuccess(String message) {
        actionErrorLabel.setText("✓ " + message);
    }

    // Inner classes
    public static class TournamentFilterItem {
        private final int id;
        private final String name;

        public TournamentFilterItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() { return name; }
    }

    public static class AdminRegistrationRow {
        private final String playerName;
        private final String teamName;
        private final String tournamentName;
        private final String registrationDate;
        private final String status;

        public AdminRegistrationRow(String playerName, String teamName, String tournamentName,
                                   String registrationDate, String status) {
            this.playerName = playerName;
            this.teamName = teamName;
            this.tournamentName = tournamentName;
            this.registrationDate = registrationDate;
            this.status = status;
        }

        public static AdminRegistrationRow fromRegistration(TournamentRegistration tr) {
            return new AdminRegistrationRow(tr.getPlayerName(), tr.getTeamName(), tr.getTournamentName(),
                tr.getRegistrationDate().format(DATE_FORMATTER), tr.getStatus());
        }

        public String getPlayerName() { return playerName; }
        public String getTeamName() { return teamName; }
        public String getTournamentName() { return tournamentName; }
        public String getRegistrationDate() { return registrationDate; }
        public String getStatus() { return status; }
    }
}
