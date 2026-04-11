package edu.connexion3a36.rankup.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserManagementController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;
    @FXML private TableView<UserRow> usersTable;
    @FXML private TableColumn<UserRow, Integer> idCol;
    @FXML private TableColumn<UserRow, String> emailCol;
    @FXML private TableColumn<UserRow, String> usernameCol;
    @FXML private TableColumn<UserRow, String> roleCol;
    @FXML private TableColumn<UserRow, String> statusCol;

    private FilteredList<UserRow> filtered;

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        filtered = new FilteredList<>(FXCollections.observableArrayList(
                new UserRow(1, "admin@rankup.gg", "admin", "ADMIN", "ACTIVE"),
                new UserRow(2, "manager@rankup.gg", "teamlead", "MANAGER", "ACTIVE"),
                new UserRow(3, "player1@rankup.gg", "falconx", "PLAYER", "ACTIVE"),
                new UserRow(4, "player2@rankup.gg", "vortex7", "PLAYER", "BANNED")
        ));

        roleFilter.setItems(FXCollections.observableArrayList("All", "PLAYER", "MANAGER", "ADMIN"));
        roleFilter.setValue("All");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        roleFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        usersTable.setItems(filtered);
    }

    @FXML
    void onBanUser(ActionEvent event) { showInfo("Placeholder", "Ban user handler placeholder."); }

    @FXML
    void onUnbanUser(ActionEvent event) { showInfo("Placeholder", "Unban user handler placeholder."); }

    @FXML
    void onDeleteUser(ActionEvent event) { showInfo("Placeholder", "Delete user handler placeholder."); }

    @FXML
    void onEditRole(ActionEvent event) { showInfo("Placeholder", "Edit role handler placeholder."); }

    private void applyFilter() {
        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String role = roleFilter.getValue();

        filtered.setPredicate(row -> {
            boolean roleOk = "All".equals(role) || row.getRole().equalsIgnoreCase(role);
            boolean searchOk = row.getEmail().toLowerCase().contains(q) || row.getUsername().toLowerCase().contains(q);
            return roleOk && searchOk;
        });
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class UserRow {
        private final Integer id;
        private final String email;
        private final String username;
        private final String role;
        private final String status;

        public UserRow(Integer id, String email, String username, String role, String status) {
            this.id = id;
            this.email = email;
            this.username = username;
            this.role = role;
            this.status = status;
        }

        public Integer getId() { return id; }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
    }
}

