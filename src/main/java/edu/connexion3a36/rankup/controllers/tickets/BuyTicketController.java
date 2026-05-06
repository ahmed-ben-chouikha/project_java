package edu.connexion3a36.rankup.controllers.tickets;

import edu.connexion3a36.entities.Match;
import edu.connexion3a36.entities.Payment;
import edu.connexion3a36.entities.Ticket;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.MatchService;
import edu.connexion3a36.services.PaymentService;
import edu.connexion3a36.services.TicketService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.Desktop;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class BuyTicketController {

    @FXML private ComboBox<MatchChoice> matchCombo;
    @FXML private TableView<Ticket> availableTicketsTable;
    @FXML private TableColumn<Ticket, String> ticketNumberCol;
    @FXML private TableColumn<Ticket, String> typeCol;
    @FXML private TableColumn<Ticket, Double> priceCol;
    @FXML private TableColumn<Ticket, Integer> quantityCol;
    @FXML private TableColumn<Ticket, Integer> soldCol;
    @FXML private TableColumn<Ticket, String> statusCol;
    @FXML private TextField quantityField;
    @FXML private TextField customerNameField;
    @FXML private TextField customerEmailField;
    @FXML private TextField customerPhoneField;
    @FXML private TextArea notesArea;
    @FXML private Label statusLabel;
    @FXML private Label paymentIntentLabel;
    @FXML private ImageView qrImageView;

    private final MatchService matchService = new MatchService();
    private final TicketService ticketService = new TicketService();
    private final PaymentService paymentService = new PaymentService();

    @FXML
    void initialize() {
        ticketNumberCol.setCellValueFactory(new PropertyValueFactory<>("ticketNumber"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        soldCol.setCellValueFactory(new PropertyValueFactory<>("sold"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        customerNameField.setText(RankUpApp.getCurrentPlayerName());
        customerEmailField.setText(RankUpApp.getCurrentEmail());
        quantityField.setText("1");
        notesArea.setText("Match ticket purchase");

        loadMatches();

        matchCombo.valueProperty().addListener((obs, oldVal, newVal) -> refreshTicketsForSelectedMatch());
        availableTicketsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateStatusFromSelection(newVal));
    }

    @FXML
    void onRefresh(ActionEvent event) {
        loadMatches();
    }

    @FXML
    void onBuyTicket(ActionEvent event) {
        MatchChoice matchChoice = matchCombo.getValue();
        Ticket selectedTicket = availableTicketsTable.getSelectionModel().getSelectedItem();

        if (matchChoice == null) {
            showError("Selection required", "Choose a match first.");
            return;
        }
        if (selectedTicket == null) {
            showError("Selection required", "Choose a ticket to buy.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (Exception e) {
            showError("Invalid quantity", "Enter a valid quantity.");
            return;
        }

        String customerEmail = customerEmailField.getText() == null ? "" : customerEmailField.getText().trim();
        String customerName = customerNameField.getText() == null ? "" : customerNameField.getText().trim();
        String customerPhone = customerPhoneField.getText() == null ? "" : customerPhoneField.getText().trim();
        String notes = notesArea.getText() == null ? "" : notesArea.getText().trim();

        try {
            Payment payment = paymentService.createPaymentForTicket(
                    selectedTicket,
                    quantity,
                    customerName,
                    customerEmail,
                    customerPhone,
                    notes,
                    RankUpApp.getCurrentUserId() > 0 ? RankUpApp.getCurrentUserId() : null
            );

            Image qrImage = paymentService.generateQrImage(payment.getQrCode());
            qrImageView.setImage(qrImage);
            paymentIntentLabel.setText("Stripe checkout session: " + payment.getPaymentIntentId());
            statusLabel.setText("Payment created. Scan the QR code or continue in your browser.");
            openCheckoutUrl(payment.getQrCode());
            refreshTicketsForSelectedMatch();
        } catch (IllegalArgumentException e) {
            showError("Validation", e.getMessage());
        } catch (SQLException e) {
            showError("Database error", e.getMessage());
        }
    }

    @FXML
    void onBack(ActionEvent event) {
        RankUpApp.loadInBase("/views/matches/matches.fxml");
    }

    private void loadMatches() {
        try {
            List<Match> matches = matchService.getAllMatches();
            List<MatchChoice> matchChoices = matches.stream()
                    .map(match -> new MatchChoice(match.getId(), match.getTeam1() + " vs " + match.getTeam2() + " | " + match.getDate() + " | " + match.getStatus()))
                    .collect(Collectors.toList());
            matchCombo.setItems(FXCollections.observableArrayList(matchChoices));
            if (!matchChoices.isEmpty() && matchCombo.getValue() == null) {
                matchCombo.setValue(matchChoices.get(0));
            }
            refreshTicketsForSelectedMatch();
        } catch (SQLException e) {
            showError("Database error", "Could not load matches: " + e.getMessage());
        }
    }

    private void refreshTicketsForSelectedMatch() {
        MatchChoice matchChoice = matchCombo.getValue();
        if (matchChoice == null) {
            availableTicketsTable.getItems().clear();
            return;
        }

        try {
            List<Ticket> tickets = ticketService.getAllTickets().stream()
                    .filter(ticket -> ticket.getGameId() == matchChoice.getMatchId())
                    .filter(ticket -> ticket.getQuantity() > ticket.getSold())
                    .collect(Collectors.toList());
            availableTicketsTable.setItems(FXCollections.observableArrayList(tickets));
            if (!tickets.isEmpty()) {
                availableTicketsTable.getSelectionModel().selectFirst();
                updateStatusFromSelection(tickets.get(0));
            } else {
                statusLabel.setText("No available tickets for the selected match.");
                paymentIntentLabel.setText("");
                qrImageView.setImage(null);
            }
        } catch (SQLException e) {
            showError("Database error", "Could not load tickets: " + e.getMessage());
        }
    }

    private void updateStatusFromSelection(Ticket ticket) {
        if (ticket == null) {
            return;
        }
        int remaining = ticket.getQuantity() - ticket.getSold();
        statusLabel.setText("Selected ticket: " + ticket.getTicketNumber() + " | Remaining: " + remaining);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openCheckoutUrl(String url) {
        if (url == null || url.isBlank()) {
            return;
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(url));
            }
        } catch (Exception ignored) {
            // If the browser cannot be opened automatically, the QR code still works.
        }
    }

    public static final class MatchChoice {
        private final int matchId;
        private final String label;

        public MatchChoice(int matchId, String label) {
            this.matchId = matchId;
            this.label = label;
        }

        public int getMatchId() {
            return matchId;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}