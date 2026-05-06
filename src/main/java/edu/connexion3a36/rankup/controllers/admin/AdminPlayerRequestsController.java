package edu.connexion3a36.rankup.controllers.admin;

import edu.connexion3a36.entities.PlayerApplication;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.PlayerApplicationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;

public class AdminPlayerRequestsController {

    @FXML private Label pendingCountLabel;
    @FXML private Label approvedCountLabel;
    @FXML private Label rejectedCountLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<PlayerApplication> requestsTable;
    @FXML private TableColumn<PlayerApplication, Integer> idCol;
    @FXML private TableColumn<PlayerApplication, String> nicknameCol;
    @FXML private TableColumn<PlayerApplication, String> firstNameCol;
    @FXML private TableColumn<PlayerApplication, String> lastNameCol;
    @FXML private TableColumn<PlayerApplication, String> birthDateCol;
    @FXML private TableColumn<PlayerApplication, String> roleCol;
    @FXML private TableColumn<PlayerApplication, String> createdAtCol;
    @FXML private TableColumn<PlayerApplication, String> statusCol;
    @FXML private TableColumn<PlayerApplication, Void> actionsCol;

    private final PlayerApplicationService service = new PlayerApplicationService();

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nicknameCol.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        birthDateCol.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("playerStatus"));
        configureActionsColumn();
        loadApplications();
    }

    @FXML
    void onRefresh() {
        loadApplications();
    }

    @FXML
    void onBack() {
        RankUpApp.loadInBase("/views/admin/admin-dashboard.fxml");
    }

    private void loadApplications() {
        try {
            List<PlayerApplication> pending = service.getPendingApplications();
            requestsTable.setItems(FXCollections.observableArrayList(pending));
            pendingCountLabel.setText(String.valueOf(service.countApplicationsByStatus("pending")));
            approvedCountLabel.setText(String.valueOf(service.countApplicationsByStatus("approved")));
            rejectedCountLabel.setText(String.valueOf(service.countApplicationsByStatus("rejected")));
            statusLabel.setText("Loaded " + pending.size() + " pending player application(s).");
        } catch (SQLException e) {
            statusLabel.setText("Could not load player applications: " + e.getMessage());
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
                setGraphic(empty ? null : box);
            }
        });
    }

    private void handleApprove(PlayerApplication application) {
        try {
            service.approveApplication(application.getId());
            loadApplications();
        } catch (SQLException e) {
            statusLabel.setText("Could not approve application: " + e.getMessage());
        }
    }

    private void handleReject(PlayerApplication application) {
        try {
            service.rejectApplication(application.getId());
            loadApplications();
        } catch (SQLException e) {
            statusLabel.setText("Could not reject application: " + e.getMessage());
        }
    }
}
