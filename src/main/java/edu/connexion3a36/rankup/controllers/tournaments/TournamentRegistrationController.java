package edu.connexion3a36.rankup.controllers.tournaments;

import edu.connexion3a36.entities.TournamentRegistration;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TournamentRegistrationService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TournamentRegistrationController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<RegistrationRow> registrationsTable;
    @FXML private TableColumn<RegistrationRow, String> playerNameCol;
    @FXML private TableColumn<RegistrationRow, String> teamNameCol;
    @FXML private TableColumn<RegistrationRow, String> tournamentCol;
    @FXML private TableColumn<RegistrationRow, String> registrationDateCol;
    @FXML private TableColumn<RegistrationRow, String> statusCol;
    @FXML private Pagination pagination;

    private FilteredList<RegistrationRow> filtered;
    private TournamentRegistrationService registrationService;
    private TournamentRegistration selectedRegistration;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    void initialize() {
        registrationService = new TournamentRegistrationService();

        playerNameCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        teamNameCol.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        tournamentCol.setCellValueFactory(new PropertyValueFactory<>("tournamentName"));
        registrationDateCol.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusFilter.setItems(FXCollections.observableArrayList("All", "pending", "confirmed"));
        statusFilter.setValue("All");

        loadRegistrations();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        registrationsTable.setItems(filtered);
        registrationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    selectedRegistration = registrationService.getRegistrationById(newVal.getId());
                } catch (SQLException ignored) {}
            }
        });

        pagination.setPageCount(1);
    }

    private void loadRegistrations() {
        try {
            List<TournamentRegistration> registrations = registrationService.getData();
            filtered = new FilteredList<>(FXCollections.observableArrayList(
                registrations.stream().map(RegistrationRow::fromRegistration).toArray(RegistrationRow[]::new)
            ));
        } catch (SQLException e) {
            showError("Failed to load registrations: " + e.getMessage());
            filtered = new FilteredList<>(FXCollections.observableArrayList());
        }
    }

    @FXML
    void onRegister(ActionEvent event) {
        RankUpApp.loadInBase("/views/tournaments/registration-form.fxml");
    }

    @FXML
    void onEditTeamName(ActionEvent event) {
        if (selectedRegistration == null) {
            showError("Please select a registration to edit");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedRegistration.getTeamName());
        dialog.setTitle("Edit Team Name");
        dialog.setHeaderText("Edit Team Name for " + selectedRegistration.getPlayerName());
        dialog.setContentText("New Team Name:");

        dialog.showAndWait().ifPresent(newTeamName -> {
            if (newTeamName.trim().isEmpty()) {
                showError("Team name cannot be empty");
                return;
            }

            try {
                selectedRegistration.setTeamName(newTeamName.trim());
                registrationService.updateEntity(selectedRegistration.getId(), selectedRegistration);
                showSuccess("Team name updated successfully");
                loadRegistrations();
                registrationsTable.setItems(filtered);
            } catch (SQLException e) {
                showError("Failed to update team name: " + e.getMessage());
            }
        });
    }

    @FXML
    void onViewStatus(ActionEvent event) {
        if (selectedRegistration != null) {
            showInfo("Registration Status", String.format(
                "Player: %s\nTeam: %s\nTournament: %s\nDate: %s\nStatus: %s",
                selectedRegistration.getPlayerName(),
                selectedRegistration.getTeamName(),
                selectedRegistration.getTournamentName(),
                selectedRegistration.getRegistrationDate().format(DATE_FORMATTER),
                selectedRegistration.getStatus()
            ));
        } else {
            showError("Please select a registration");
        }
    }

    @FXML
    void onCancelRegistration(ActionEvent event) {
        if (selectedRegistration == null) {
            showError("Please select a registration to cancel");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Cancel Registration");
        confirmDialog.setHeaderText("Are you sure?");
        confirmDialog.setContentText("Cancel registration for " + selectedRegistration.getPlayerName() + "?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            try {
                registrationService.deleteEntity(selectedRegistration);
                showSuccess("Registration cancelled successfully");
                loadRegistrations();
                registrationsTable.setItems(filtered);
            } catch (SQLException e) {
                showError("Failed to cancel registration: " + e.getMessage());
            }
        }
    }

    private void applyFilter() {
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        filtered.setPredicate(row -> {
            boolean statusOk = "All".equals(status) || row.getStatus().equalsIgnoreCase(status);
            boolean searchOk = row.getPlayerName().toLowerCase().contains(q) ||
                              row.getTeamName().toLowerCase().contains(q);
            return statusOk && searchOk;
        });
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class RegistrationRow {
        private final int id;
        private final String playerName;
        private final String teamName;
        private final String tournamentName;
        private final String registrationDate;
        private final String status;

        public RegistrationRow(int id, String playerName, String teamName, String tournamentName,
                              String registrationDate, String status) {
            this.id = id;
            this.playerName = playerName;
            this.teamName = teamName;
            this.tournamentName = tournamentName;
            this.registrationDate = registrationDate;
            this.status = status;
        }

        public static RegistrationRow fromRegistration(TournamentRegistration tr) {
            return new RegistrationRow(tr.getId(), tr.getPlayerName(), tr.getTeamName(),
                tr.getTournamentName(), tr.getRegistrationDate().format(DATE_FORMATTER), tr.getStatus());
        }

        public int getId() { return id; }
        public String getPlayerName() { return playerName; }
        public String getTeamName() { return teamName; }
        public String getTournamentName() { return tournamentName; }
        public String getRegistrationDate() { return registrationDate; }
        public String getStatus() { return status; }
    }
}
