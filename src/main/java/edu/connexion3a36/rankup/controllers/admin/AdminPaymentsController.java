package edu.connexion3a36.rankup.controllers.admin;

import edu.connexion3a36.entities.Payment;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.PaymentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class AdminPaymentsController {

    @FXML private Label totalPaymentsLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label averageIncomeLabel;
    @FXML private Label paidCountLabel;
    @FXML private Label pendingCountLabel;
    @FXML private Label failedCountLabel;
    @FXML private Label totalTicketsLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<Payment> paymentsTable;
    @FXML private TableColumn<Payment, Integer> idCol;
    @FXML private TableColumn<Payment, String> customerCol;
    @FXML private TableColumn<Payment, Double> amountCol;
    @FXML private TableColumn<Payment, Integer> quantityCol;
    @FXML private TableColumn<Payment, String> statusCol;
    @FXML private TableColumn<Payment, String> createdAtCol;
    @FXML private TableColumn<Payment, String> paymentIntentCol;

    private final PaymentService paymentService = new PaymentService();

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerCol.setCellValueFactory(cell -> {
            Payment payment = cell.getValue();
            String customer = payment.getCustomerName();
            if (customer == null || customer.isBlank()) {
                customer = payment.getCustomerEmail();
            } else if (payment.getCustomerEmail() != null && !payment.getCustomerEmail().isBlank()) {
                customer = customer + " (" + payment.getCustomerEmail() + ")";
            }
            return new javafx.beans.property.SimpleStringProperty(customer == null ? "" : customer);
        });
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantityPurchased"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        paymentIntentCol.setCellValueFactory(new PropertyValueFactory<>("paymentIntentId"));

        loadPayments();
    }

    @FXML
    void onRefresh() {
        loadPayments();
    }

    @FXML
    void onBack() {
        RankUpApp.loadInBase("/views/admin/admin-dashboard.fxml");
    }

    private void loadPayments() {
        try {
            PaymentService.PaymentStats stats = paymentService.getPaymentStats();
            List<Payment> payments = paymentService.getRecentPayments(12);

            totalPaymentsLabel.setText(String.valueOf(stats.totalPayments()));
            totalIncomeLabel.setText(formatCurrency(stats.totalIncome()));
            averageIncomeLabel.setText(formatCurrency(stats.averageIncome()));
            paidCountLabel.setText(String.valueOf(stats.paidCount()));
            pendingCountLabel.setText(String.valueOf(stats.pendingCount()));
            failedCountLabel.setText(String.valueOf(stats.failedCount()));
            totalTicketsLabel.setText(String.valueOf(stats.totalTickets()));
            statusLabel.setText("Loaded " + payments.size() + " recent payment(s).");

            ObservableList<Payment> rows = FXCollections.observableArrayList(payments);
            paymentsTable.setItems(rows);
        } catch (SQLException e) {
            statusLabel.setText("Could not load payments: " + e.getMessage());
            paymentsTable.setItems(FXCollections.observableArrayList());
        }
    }

    private String formatCurrency(double amount) {
        return String.format(java.util.Locale.US, "$%,.2f", amount);
    }
}