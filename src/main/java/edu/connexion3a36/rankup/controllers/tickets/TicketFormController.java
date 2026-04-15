package edu.connexion3a36.rankup.controllers.tickets;

import edu.connexion3a36.entities.Ticket;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.TicketService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;

public class TicketFormController {

    @FXML private Label titleLabel;
    @FXML private ComboBox<TicketService.GameOption> gameCombo;
    @FXML private TextField ticketNumberField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private TextField soldField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label feedbackLabel;

    private final TicketService ticketService = new TicketService();
    private Ticket editingTicket;

    @FXML
    void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList("regular", "vip", "student"));
        statusCombo.setItems(FXCollections.observableArrayList("available", "sold_out", "cancelled"));
        statusCombo.setValue("available");

        loadGames();

        editingTicket = TicketFormState.getEditingTicket();
        if (editingTicket != null) {
            titleLabel.setText("Edit Ticket");
            bindTicket(editingTicket);
        } else {
            titleLabel.setText("Create Ticket");
            soldField.setText("0");
            quantityField.setText("100");
            priceField.setText("25");
        }
    }

    @FXML
    void onSave(ActionEvent event) {
        TicketService.GameOption game = gameCombo.getValue();
        if (game == null) {
            feedbackLabel.setText("Please select a game.");
            return;
        }

        String ticketNumber = valueOrEmpty(ticketNumberField.getText());
        if (ticketNumber.isBlank()) {
            feedbackLabel.setText("Ticket number is required.");
            return;
        }

        int quantity = parseIntSafe(quantityField.getText());
        int sold = parseIntSafe(soldField.getText());
        double price = parseDoubleSafe(priceField.getText());

        if (quantity < 0 || sold < 0 || sold > quantity) {
            feedbackLabel.setText("Quantity/sold values are invalid.");
            return;
        }

        String type = typeCombo.getValue() == null ? "regular" : typeCombo.getValue();
        String status = statusCombo.getValue() == null ? "available" : statusCombo.getValue();

        Ticket payload = new Ticket(
                editingTicket == null ? -1 : editingTicket.getId(),
                game.getId(),
                ticketNumber,
                type,
                price,
                quantity,
                sold,
                status,
                editingTicket == null ? null : editingTicket.getCreatedAt(),
                null
        );

        try {
            if (editingTicket == null) {
                ticketService.createTicket(payload);
            } else {
                ticketService.updateTicket(payload);
            }
            TicketFormState.clear();
            RankUpApp.loadInBase("/views/tickets/tickets.fxml");
        } catch (SQLException e) {
            feedbackLabel.setText("Could not save ticket: " + e.getMessage());
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        TicketFormState.clear();
        RankUpApp.loadInBase("/views/tickets/tickets.fxml");
    }

    private void loadGames() {
        try {
            List<TicketService.GameOption> games = ticketService.getGameOptions();
            gameCombo.setItems(FXCollections.observableArrayList(games));
        } catch (SQLException e) {
            feedbackLabel.setText("Could not load games: " + e.getMessage());
        }
    }

    private void bindTicket(Ticket ticket) {
        ticketNumberField.setText(ticket.getTicketNumber());
        priceField.setText(String.valueOf(ticket.getPrice()));
        quantityField.setText(String.valueOf(ticket.getQuantity()));
        soldField.setText(String.valueOf(ticket.getSold()));
        typeCombo.setValue(ticket.getType());
        statusCombo.setValue(ticket.getStatus());

        for (TicketService.GameOption option : gameCombo.getItems()) {
            if (option.getId() == ticket.getGameId()) {
                gameCombo.setValue(option);
                break;
            }
        }
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(valueOrEmpty(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(valueOrEmpty(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}

