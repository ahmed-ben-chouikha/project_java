package edu.connexion3a36.rankup.controllers.notifications;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class NotificationsController {

    @FXML private ComboBox<String> filterBox;
    @FXML private TableView<NotificationRow> notificationsTable;
    @FXML private TableColumn<NotificationRow, String> typeCol;
    @FXML private TableColumn<NotificationRow, String> messageCol;
    @FXML private TableColumn<NotificationRow, String> timestampCol;
    @FXML private TableColumn<NotificationRow, String> readCol;

    private FilteredList<NotificationRow> filtered;

    @FXML
    void initialize() {
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        readCol.setCellValueFactory(new PropertyValueFactory<>("read"));

        filtered = new FilteredList<>(FXCollections.observableArrayList(
                new NotificationRow("INFO", "Match Falcons vs Nova starts in 1 hour", "2026-04-11 17:00", "No"),
                new NotificationRow("WARNING", "Budget reached 75% threshold", "2026-04-11 10:20", "No"),
                new NotificationRow("SUCCESS", "Reward request approved", "2026-04-10 21:10", "Yes")
        ));

        filterBox.setItems(FXCollections.observableArrayList("All", "Unread only"));
        filterBox.setValue("All");
        filterBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        notificationsTable.setItems(filtered);
    }

    @FXML
    void onMarkAsRead(ActionEvent event) {
        showInfo("Placeholder", "Mark as read handler placeholder.");
    }

    @FXML
    void onClearAll(ActionEvent event) {
        showInfo("Placeholder", "Clear all handler placeholder.");
    }

    private void applyFilter() {
        String filter = filterBox.getValue();
        filtered.setPredicate(row -> "All".equals(filter) || "No".equals(row.getRead()));
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class NotificationRow {
        private final String type;
        private final String message;
        private final String timestamp;
        private final String read;

        public NotificationRow(String type, String message, String timestamp, String read) {
            this.type = type;
            this.message = message;
            this.timestamp = timestamp;
            this.read = read;
        }

        public String getType() { return type; }
        public String getMessage() { return message; }
        public String getTimestamp() { return timestamp; }
        public String getRead() { return read; }
    }
}

