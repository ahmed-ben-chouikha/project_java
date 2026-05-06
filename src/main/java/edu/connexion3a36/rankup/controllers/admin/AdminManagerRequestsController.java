package edu.connexion3a36.rankup.controllers.admin;

import edu.connexion3a36.entities.ManagerRequest;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.app.SessionManager;
import edu.connexion3a36.services.ManagerRequestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdminManagerRequestsController {

    @FXML private Label pendingCountLabel;
    @FXML private Label approvedCountLabel;
    @FXML private Label rejectedCountLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<ManagerRequest> requestsTable;
    @FXML private TableColumn<ManagerRequest, Integer> idCol;
    @FXML private TableColumn<ManagerRequest, String> playerCol;
    @FXML private TableColumn<ManagerRequest, String> teamCol;
    @FXML private TableColumn<ManagerRequest, String> motivationCol;
    @FXML private TableColumn<ManagerRequest, String> createdAtCol;
    @FXML private TableColumn<ManagerRequest, String> statusCol;
    @FXML private TableColumn<ManagerRequest, String> reviewedAtCol;
    @FXML private TableColumn<ManagerRequest, Void> actionsCol;

    private final ManagerRequestService service = new ManagerRequestService();

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        playerCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPlayerDisplayName()));
        teamCol.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        motivationCol.setCellValueFactory(new PropertyValueFactory<>("motivation"));
        createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        reviewedAtCol.setCellValueFactory(new PropertyValueFactory<>("reviewedAt"));
        configureActionsColumn();
        loadRequests();
    }

    @FXML
    void onRefresh() {
        loadRequests();
    }

    @FXML
    void onBack() {
        RankUpApp.loadInBase("/views/admin/admin-dashboard.fxml");
    }

    private void loadRequests() {
        try {
            List<ManagerRequest> pending = service.getPendingRequests();
            requestsTable.setItems(FXCollections.observableArrayList(pending));
            pendingCountLabel.setText(String.valueOf(service.countRequestsByStatus("pending")));
            approvedCountLabel.setText(String.valueOf(service.countRequestsByStatus("approved")));
            rejectedCountLabel.setText(String.valueOf(service.countRequestsByStatus("rejected")));
            statusLabel.setText("Loaded " + pending.size() + " pending manager request(s).");
        } catch (SQLException e) {
            statusLabel.setText("Could not load manager requests: " + e.getMessage());
            requestsTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void configureActionsColumn() {
        actionsCol.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");
            private final HBox box = new HBox(8, approveButton, rejectButton);

            {
                approveButton.setStyle("-fx-background-color: #34A853; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
                rejectButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
                approveButton.setOnAction(e -> handleApprove(getTableView().getItems().get(getIndex())));
                rejectButton.setOnAction(e -> handleReject(getTableView().getItems().get(getIndex())));
                box.setAlignment(javafx.geometry.Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                ManagerRequest request = getTableView().getItems().get(getIndex());
                setGraphic("pending".equalsIgnoreCase(request.getStatus()) ? box : null);
            }
        });
    }

    private void handleApprove(ManagerRequest request) {
        try {
            service.approveRequest(request.getId(), SessionManager.getCurrentUserId());
            loadRequests();
        } catch (SQLException e) {
            statusLabel.setText("Could not approve request: " + e.getMessage());
        }
    }

    private void handleReject(ManagerRequest request) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Reject Manager Request");
        dialog.setHeaderText("Enter a rejection reason");
        dialog.setContentText("Comment:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        try {
            service.rejectRequest(request.getId(), SessionManager.getCurrentUserId(), result.get().trim());
            loadRequests();
        } catch (SQLException e) {
            statusLabel.setText("Could not reject request: " + e.getMessage());
        }
    }
}
