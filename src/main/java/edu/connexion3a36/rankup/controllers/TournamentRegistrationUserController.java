package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.entities.TournamentRegistration;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.app.SessionManager;
import edu.connexion3a36.rankup.controllers.tournaments.TournamentRegistrationState;
import edu.connexion3a36.services.TournamentRegistrationService;
import edu.connexion3a36.services.TournamentService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TournamentRegistrationUserController {

    // Registration Form Section
    @FXML private TextField playerNameField;
    @FXML private TextField teamNameField;
    @FXML private TextArea teamMembersField;
    @FXML private TextField contactInfoField;
    @FXML private ComboBox<TournamentComboItem> tournamentComboBox;
    @FXML private Label formErrorLabel;
    @FXML private Label successLabel;

    // My Registrations Section
    @FXML private TableView<RegistrationRow> myRegistrationsTable;
    @FXML private TableColumn<RegistrationRow, String> tournamentCol;
    @FXML private TableColumn<RegistrationRow, String> teamCol;
    @FXML private TableColumn<RegistrationRow, String> teamMembersCol;
    @FXML private TableColumn<RegistrationRow, String> contactInfoCol;
    @FXML private TableColumn<RegistrationRow, String> dateCol;
    @FXML private TableColumn<RegistrationRow, String> statusCol;
    @FXML private ComboBox<String> myStatusFilter;
    @FXML private TextField searchField;
    @FXML private Label cancelErrorLabel;

    private TournamentRegistrationService registrationService;
    private TournamentService tournamentService;
    private FilteredList<RegistrationRow> filteredRegistrations;
    private RegistrationRow selectedRegistration;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    void initialize() {
        registrationService = new TournamentRegistrationService();
        tournamentService = new TournamentService();

        // Style error labels
        formErrorLabel.setStyle("-fx-text-fill: #ef4444;");
        cancelErrorLabel.setStyle("-fx-text-fill: #ef4444;");
        successLabel.setStyle("-fx-text-fill: #10b981;");

        // Load planned/upcoming tournaments for registration form
        loadOpenTournaments();
        playerNameField.setText(SessionManager.getCurrentPlayerName());
        playerNameField.setEditable(false);

        // Pre-select tournament if coming from tournament list
        Tournament selectedTournament = TournamentRegistrationState.getSelectedTournament();
        if (selectedTournament != null) {
            selectTournamentInCombo(selectedTournament.getId());
            TournamentRegistrationState.clear();
        }

        // Setup my registrations table
        setupRegistrationsTable();
        loadMyRegistrations();

        // Add listener for table selection
        myRegistrationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedRegistration = newVal;
        });

        // Add listener for status filter
        myStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
    }

    private void selectTournamentInCombo(int tournamentId) {
        for (TournamentComboItem item : tournamentComboBox.getItems()) {
            if (item.getId() == tournamentId) {
                tournamentComboBox.setValue(item);
                break;
            }
        }
    }

    private void loadOpenTournaments() {
        try {
            List<Tournament> openTournaments = tournamentService.getPlannedOrUpcomingTournaments();
            tournamentComboBox.setItems(FXCollections.observableArrayList(
                openTournaments.stream()
                    .map(t -> new TournamentComboItem(t.getId(), t.getName()))
                    .toArray(TournamentComboItem[]::new)
            ));
        } catch (SQLException e) {
            showErrorForm("Failed to load tournaments: " + e.getMessage());
        }
    }

    private void setupRegistrationsTable() {
        tournamentCol.setCellValueFactory(new PropertyValueFactory<>("tournamentName"));
        teamCol.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        teamMembersCol.setCellValueFactory(new PropertyValueFactory<>("teamMembers"));
        contactInfoCol.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        myStatusFilter.setItems(FXCollections.observableArrayList("All", "pending", "confirmed", "rejected"));
        myStatusFilter.setValue("All");
    }

    private void loadMyRegistrations() {
        try {
            String playerName = SessionManager.getCurrentPlayerName();
            List<TournamentRegistration> registrations = registrationService.getPlayerRegistrations(playerName);
            filteredRegistrations = new FilteredList<>(FXCollections.observableArrayList(
                registrations.stream().map(RegistrationRow::fromRegistration).toArray(RegistrationRow[]::new)
            ));
            myRegistrationsTable.setItems(filteredRegistrations);
        } catch (SQLException e) {
            showErrorCancel("Failed to load registrations: " + e.getMessage());
            filteredRegistrations = new FilteredList<>(FXCollections.observableArrayList());
        }
    }

    @FXML
    void onRegister(ActionEvent event) {
        if (validateRegistrationForm()) {
            try {
                String playerName = SessionManager.getCurrentPlayerName();
                String teamName = teamNameField.getText().trim();
                String teamMembers = teamMembersField.getText().trim();
                String contactInfo = contactInfoField.getText().trim();
                int tournamentId = tournamentComboBox.getValue().getId();

                Tournament selectedTournament = tournamentService.getTournamentById(tournamentId);
                if (selectedTournament == null || !"planned".equalsIgnoreCase(selectedTournament.getStatus())) {
                    showErrorForm("Registration is only allowed for tournaments in planned status.");
                    return;
                }

                TournamentRegistration registration = new TournamentRegistration(playerName, teamName, teamMembers, contactInfo, tournamentId, "pending");
                registrationService.addEntity(registration);

                showSuccessForm("Registration submitted. Waiting for admin approval.");
                clearRegistrationFormInternal();
                loadMyRegistrations();
            } catch (SQLException e) {
                showErrorForm(e.getMessage());
            }
        }
    }

    @FXML
    void onCancelRegistration(ActionEvent event) {
        if (selectedRegistration == null) {
            showErrorCancel("Please select a registration to cancel");
            return;
        }

        if (!"pending".equalsIgnoreCase(selectedRegistration.getStatus())) {
            showErrorCancel("Can only cancel pending registrations");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Cancel Registration");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("Cancel registration for " + selectedRegistration.getTournamentName() + "?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                TournamentRegistration reg = new TournamentRegistration();
                reg.setId(selectedRegistration.getId());
                registrationService.deleteEntity(reg);
                showSuccessForm("Registration cancelled successfully");
                loadMyRegistrations();
                selectedRegistration = null;
            } catch (SQLException e) {
                showErrorCancel("Failed to cancel registration: " + e.getMessage());
            }
        }
    }

    private boolean validateRegistrationForm() {
        formErrorLabel.setText("");

        if (playerNameField.getText().trim().isEmpty()) {
            formErrorLabel.setText("Player name cannot be empty");
            return false;
        }

        if (teamNameField.getText().trim().isEmpty()) {
            formErrorLabel.setText("Team name cannot be empty");
            return false;
        }

        if (teamNameField.getText().trim().length() < 3) {
            formErrorLabel.setText("Team name must be at least 3 characters");
            return false;
        }

        if (tournamentComboBox.getValue() == null) {
            formErrorLabel.setText("Please select a tournament");
            return false;
        }

        return true;
    }

    private void applyFilter() {
        String searchText = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String status = myStatusFilter.getValue();

        filteredRegistrations.setPredicate(row -> {
            boolean statusOk = "All".equals(status) || row.getStatus().equalsIgnoreCase(status);
            boolean searchOk = row.getTournamentName().toLowerCase().contains(searchText) ||
                              row.getTeamName().toLowerCase().contains(searchText);
            return statusOk && searchOk;
        });
    }

    private void showErrorForm(String message) {
        formErrorLabel.setText(message);
        successLabel.setText("");
    }

    private void showSuccessForm(String message) {
        successLabel.setText(message);
        formErrorLabel.setText("");
    }

    private void showErrorCancel(String message) {
        cancelErrorLabel.setText(message);
    }

    @FXML
    void clearRegistrationForm(ActionEvent event) {
        playerNameField.setText(SessionManager.getCurrentPlayerName());
        teamNameField.clear();
        teamMembersField.clear();
        contactInfoField.clear();
        tournamentComboBox.setValue(null);
        formErrorLabel.setText("");
        successLabel.setText("");
    }

    private void clearRegistrationFormInternal() {
        playerNameField.setText(SessionManager.getCurrentPlayerName());
        teamNameField.clear();
        teamMembersField.clear();
        contactInfoField.clear();
        tournamentComboBox.setValue(null);
        formErrorLabel.setText("");
        successLabel.setText("");
    }

    // Inner classes
    public static class TournamentComboItem {
        private final int id;
        private final String name;

        public TournamentComboItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class RegistrationRow {
        private final int id;
        private final String tournamentName;
        private final String teamName;
        private final String teamMembers;
        private final String contactInfo;
        private final String registrationDate;
        private final String status;

        public RegistrationRow(int id, String tournamentName, String teamName, String teamMembers,
                               String contactInfo, String registrationDate, String status) {
            this.id = id;
            this.tournamentName = tournamentName;
            this.teamName = teamName;
            this.teamMembers = teamMembers;
            this.contactInfo = contactInfo;
            this.registrationDate = registrationDate;
            this.status = status;
        }

        public static RegistrationRow fromRegistration(TournamentRegistration tr) {
            return new RegistrationRow(tr.getId(), tr.getTournamentName(), tr.getTeamName(),
                tr.getTeamMembers(), tr.getContactInfo(), tr.getRegistrationDate().format(DATE_FORMATTER), tr.getStatus());
        }

        public int getId() { return id; }
        public String getTournamentName() { return tournamentName; }
        public String getTeamName() { return teamName; }
        public String getTeamMembers() { return teamMembers; }
        public String getContactInfo() { return contactInfo; }
        public String getRegistrationDate() { return registrationDate; }
        public String getStatus() { return status; }
    }
}
