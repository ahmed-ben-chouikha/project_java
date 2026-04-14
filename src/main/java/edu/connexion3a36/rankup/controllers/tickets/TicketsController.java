package edu.connexion3a36.rankup.controllers.tickets;

import edu.connexion3a36.entities.Ticket;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TicketService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class TicketsController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Ticket> ticketsTable;
    @FXML private TableColumn<Ticket, Integer> gameIdCol;
    @FXML private TableColumn<Ticket, String> ticketNumberCol;
    @FXML private TableColumn<Ticket, String> typeCol;
    @FXML private TableColumn<Ticket, Double> priceCol;
    @FXML private TableColumn<Ticket, Integer> quantityCol;
    @FXML private TableColumn<Ticket, Integer> soldCol;
    @FXML private TableColumn<Ticket, String> statusCol;
    @FXML private Pagination pagination;

    private final TicketService ticketService = new TicketService();
    private FilteredList<Ticket> filtered;

    @FXML
    void initialize() {
        gameIdCol.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        ticketNumberCol.setCellValueFactory(new PropertyValueFactory<>("ticketNumber"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        soldCol.setCellValueFactory(new PropertyValueFactory<>("sold"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        filtered = new FilteredList<>(FXCollections.observableArrayList());

        statusFilter.setItems(FXCollections.observableArrayList("All", "available", "sold_out", "cancelled"));
        statusFilter.setValue("All");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        loadTickets();
        ticketsTable.setItems(filtered);
        pagination.setPageCount(1);
    }

    @FXML
    void onCreateTicket(ActionEvent event) {
        TicketFormState.clear();
        RankUpApp.loadInBase("/views/tickets/ticket-form.fxml");
    }

    @FXML
    void onEditTicket(ActionEvent event) {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a ticket first.");
            return;
        }

        TicketFormState.setEditingTicket(selected);
        RankUpApp.loadInBase("/views/tickets/ticket-form.fxml");
    }

    @FXML
    void onDeleteTicket(ActionEvent event) {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("No selection", "Select a ticket first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Ticket");
        confirm.setHeaderText("Delete selected ticket?");
        confirm.setContentText(selected.getTicketNumber());

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    ticketService.deleteTicketById(selected.getId());
                    loadTickets();
                } catch (SQLException e) {
                    showError("Database Error", "Could not delete ticket.\n" + e.getMessage());
                }
            }
        });
    }

    private void applyFilter() {
        if (filtered == null) {
            return;
        }

        String q = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String status = statusFilter.getValue();

        filtered.setPredicate(row -> {
            boolean statusOk = "All".equals(status) || row.getStatus().equalsIgnoreCase(status);
            boolean searchOk = row.getTicketNumber().toLowerCase().contains(q)
                    || row.getType().toLowerCase().contains(q)
                    || String.valueOf(row.getGameId()).contains(q);
            return statusOk && searchOk;
        });
    }

    private void loadTickets() {
        try {
            List<Ticket> rows = ticketService.getAllTickets();
            filtered = new FilteredList<>(FXCollections.observableArrayList(rows));
            ticketsTable.setItems(filtered);
            applyFilter();
        } catch (SQLException e) {
            filtered = new FilteredList<>(FXCollections.observableArrayList());
            ticketsTable.setItems(filtered);
            showError("Database Error", "Could not load tickets from the database.\n" + e.getMessage());
        }
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

