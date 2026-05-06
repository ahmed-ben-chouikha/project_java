package edu.connexion3a36.services;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import edu.connexion3a36.entities.Payment;
import edu.connexion3a36.entities.Ticket;
import edu.connexion3a36.tools.MyConnection;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PaymentService {

    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String DEFAULT_CURRENCY = "usd";
    private static final String DEFAULT_SUCCESS_URL = "https://example.com/stripe/success?session_id={CHECKOUT_SESSION_ID}";
    private static final String DEFAULT_CANCEL_URL = "https://example.com/stripe/cancel";

    public Payment createPaymentForTicket(Ticket ticket,
                                          int quantity,
                                          String customerName,
                                          String customerEmail,
                                          String customerPhone,
                                          String notes,
                                          Integer playerId) throws SQLException {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket is required.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }
        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException("Customer email is required.");
        }

        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        String ticketTable = resolveTicketTable(connection);
        String paymentTable = resolvePaymentTable(connection);
        Integer resolvedPlayerId = resolvePlayerId(connection, customerEmail, customerName, playerId);

        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try {
            Ticket freshTicket = loadTicketForUpdate(connection, ticketTable, ticket.getId());
            if (freshTicket == null) {
                throw new SQLException("Selected ticket no longer exists.");
            }

            int remaining = freshTicket.getQuantity() - freshTicket.getSold();
            if (remaining < quantity) {
                throw new SQLException("Only " + remaining + " ticket(s) are still available for this match.");
            }

            double amount = freshTicket.getPrice() * quantity;
            StripeCheckout checkout = createStripeCheckoutSession(freshTicket, quantity, customerName, customerEmail, customerPhone, notes, resolvedPlayerId);
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            String insertSql = "INSERT INTO " + paymentTable + " (ticket_id, payment_intent_id, status, customer_email, amount, quantity_purchased, customer_name, customer_phone, notes, created_at, updated_at, player_id, qr_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int generatedId;

            try (PreparedStatement pst = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                pst.setInt(1, freshTicket.getId());
                pst.setString(2, checkout.sessionId());
                pst.setString(3, "pending");
                pst.setString(4, customerEmail.trim());
                pst.setDouble(5, amount);
                pst.setInt(6, quantity);
                pst.setString(7, customerName == null || customerName.isBlank() ? null : customerName.trim());
                pst.setString(8, customerPhone == null || customerPhone.isBlank() ? null : customerPhone.trim());
                pst.setString(9, notes == null || notes.isBlank() ? null : notes.trim());
                pst.setTimestamp(10, now);
                pst.setTimestamp(11, now);
                if (resolvedPlayerId != null && resolvedPlayerId > 0) {
                    pst.setInt(12, resolvedPlayerId);
                } else {
                    pst.setNull(12, java.sql.Types.INTEGER);
                }
                pst.setString(13, checkout.checkoutUrl());
                pst.executeUpdate();

                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Payment insert did not return a generated key.");
                    }
                    generatedId = keys.getInt(1);
                }
            }

            int newSold = freshTicket.getSold() + quantity;
            String newStatus = newSold >= freshTicket.getQuantity() ? "sold_out" : freshTicket.getStatus();
            String updateTicketSql = "UPDATE " + ticketTable + " SET sold = ?, status = ?, updated_at = ? WHERE id = ?";

            try (PreparedStatement pst = connection.prepareStatement(updateTicketSql)) {
                pst.setInt(1, newSold);
                pst.setString(2, newStatus == null ? "available" : newStatus.toLowerCase());
                pst.setTimestamp(3, now);
                pst.setInt(4, freshTicket.getId());
                pst.executeUpdate();
            }

            connection.commit();
            try {
                String subject = "Ticket purchase created — Order #" + generatedId;
                String body = "Hello " + (customerName == null ? "" : customerName.trim()) + ",\n\n"
                        + "Your order #" + generatedId + " has been created. Complete payment at: " + checkout.checkoutUrl() + "\n\n"
                        + "Thank you for your purchase.";
                try {
                    MailService.sendEmail(customerEmail.trim(), subject, body);
                } catch (Exception mailEx) {
                    System.err.println("Failed to send confirmation email: " + mailEx.getMessage());
                }
            } catch (Exception ignored) {
            }
            return new Payment(
                    generatedId,
                    freshTicket.getId(),
                    checkout.sessionId(),
                    "pending",
                    customerEmail.trim(),
                    amount,
                    quantity,
                    customerName,
                    customerPhone,
                    notes,
                    null,
                    null,
                    now.toString(),
                    now.toString(),
                    resolvedPlayerId,
                    checkout.checkoutUrl()
            );
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    public Image generateQrImage(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("QR payload is required.");
        }

        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = new QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, 320, 320, hints);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return new Image(new ByteArrayInputStream(out.toByteArray()));
        } catch (WriterException | java.io.IOException e) {
            throw new IllegalStateException("Could not generate QR code image.", e);
        }
    }

    public List<Payment> getRecentPayments(int limit) throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        int safeLimit = Math.max(1, limit);
        String paymentTable = resolvePaymentTable(connection);
        String sql = "SELECT id, ticket_id, payment_intent_id, status, customer_email, amount, quantity_purchased, customer_name, customer_phone, notes, refunded_at, refund_amount, created_at, updated_at, player_id, qr_code FROM " + paymentTable + " ORDER BY created_at DESC, id DESC LIMIT ?";

        List<Payment> payments = new ArrayList<>();
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, safeLimit);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapPayment(rs));
                }
            }
        }
        return payments;
    }

    public PaymentStats getPaymentStats() throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        String paymentTable = resolvePaymentTable(connection);
        String sql = "SELECT COUNT(*) AS total_payments, " +
                "COALESCE(SUM(amount), 0) AS total_income, " +
                "COALESCE(AVG(amount), 0) AS average_income, " +
                "COALESCE(SUM(quantity_purchased), 0) AS total_tickets, " +
                "SUM(CASE WHEN LOWER(status) IN ('paid', 'succeeded', 'completed') THEN 1 ELSE 0 END) AS paid_count, " +
                "SUM(CASE WHEN LOWER(status) = 'pending' THEN 1 ELSE 0 END) AS pending_count, " +
                "SUM(CASE WHEN LOWER(status) IN ('failed', 'canceled', 'cancelled') THEN 1 ELSE 0 END) AS failed_count " +
                "FROM " + paymentTable;

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (!rs.next()) {
                return new PaymentStats(0, 0, 0, 0, 0, 0, 0);
            }

            return new PaymentStats(
                    rs.getInt("total_payments"),
                    rs.getDouble("total_income"),
                    rs.getDouble("average_income"),
                    rs.getInt("total_tickets"),
                    rs.getInt("paid_count"),
                    rs.getInt("pending_count"),
                    rs.getInt("failed_count")
            );
        }
    }

    private StripeCheckout createStripeCheckoutSession(Ticket ticket,
                                                       int quantity,
                                                       String customerName,
                                                       String customerEmail,
                                                       String customerPhone,
                                                       String notes,
                                                       Integer playerId) throws SQLException {
        String secretKey = resolveStripeSecretKey();
        Stripe.apiKey = secretKey;

        long unitAmountInCents = Math.max(1L, Math.round(ticket.getPrice() * 100.0));
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(resolveStripeSuccessUrl())
                .setCancelUrl(resolveStripeCancelUrl())
                .setCustomerEmail(customerEmail.trim())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) quantity)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(resolveStripeCurrency())
                                                .setUnitAmount(unitAmountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(buildStripeProductName(ticket))
                                                                .setDescription(buildStripeDescription(ticket, quantity, customerName, customerPhone, notes))
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("ticket_id", String.valueOf(ticket.getId()))
                .putMetadata("quantity", String.valueOf(quantity))
                .putMetadata("customer_email", customerEmail.trim());

        if (playerId != null && playerId > 0) {
            paramsBuilder.putMetadata("player_id", String.valueOf(playerId));
        }

        try {
            Session session = Session.create(paramsBuilder.build());
            if (session.getUrl() == null || session.getUrl().isBlank()) {
                throw new SQLException("Stripe checkout session did not return a URL.");
            }
            return new StripeCheckout(session.getId(), session.getUrl());
        } catch (Exception e) {
            throw new SQLException("Could not create Stripe checkout session: " + e.getMessage(), e);
        }
    }

    private String resolveStripeSecretKey() {
        String value = firstNonBlank(
                System.getProperty("stripe.secretKey"),
                System.getenv("STRIPE_SECRET_KEY")
        );
        if (value.isBlank()) {
            throw new IllegalStateException("Stripe secret key is missing. Set STRIPE_SECRET_KEY or -Dstripe.secretKey.");
        }
        return value.trim();
    }

    private String resolveStripeCurrency() {
        String value = firstNonBlank(System.getProperty("stripe.currency"), System.getenv("STRIPE_CURRENCY"));
        return value.isBlank() ? DEFAULT_CURRENCY : value.trim().toLowerCase();
    }

    private String resolveStripeSuccessUrl() {
        String value = firstNonBlank(System.getProperty("stripe.successUrl"), System.getenv("STRIPE_SUCCESS_URL"));
        return value.isBlank() ? DEFAULT_SUCCESS_URL : value.trim();
    }

    private String resolveStripeCancelUrl() {
        String value = firstNonBlank(System.getProperty("stripe.cancelUrl"), System.getenv("STRIPE_CANCEL_URL"));
        return value.isBlank() ? DEFAULT_CANCEL_URL : value.trim();
    }

    private String buildStripeProductName(Ticket ticket) {
        return "Match ticket " + ticket.getTicketNumber();
    }

    private String buildStripeDescription(Ticket ticket, int quantity, String customerName, String customerPhone, String notes) {
        StringBuilder builder = new StringBuilder("Match ID: ").append(ticket.getGameId())
                .append(" | Quantity: ").append(quantity);
        if (customerName != null && !customerName.isBlank()) {
            builder.append(" | Customer: ").append(customerName.trim());
        }
        if (customerPhone != null && !customerPhone.isBlank()) {
            builder.append(" | Phone: ").append(customerPhone.trim());
        }
        if (notes != null && !notes.isBlank()) {
            builder.append(" | Notes: ").append(notes.trim());
        }
        return builder.toString();
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private Ticket loadTicketForUpdate(Connection connection, String tableName, int ticketId) throws SQLException {
        String sql = "SELECT id, game_id, ticket_number, type, price, quantity, sold, status, created_at, updated_at FROM " + tableName + " WHERE id = ? FOR UPDATE";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, ticketId);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Ticket(
                        rs.getInt("id"),
                        rs.getInt("game_id"),
                        rs.getString("ticket_number"),
                        rs.getString("type"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getInt("sold"),
                        rs.getString("status"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
            }
        }
    }

    private String buildQrPayload(String paymentIntentId, int ticketId, int quantity, double amount, String customerEmail) {
        return "payment_intent_id=" + paymentIntentId
                + "|ticket_id=" + ticketId
                + "|quantity=" + quantity
                + "|amount=" + amount
                + "|customer_email=" + customerEmail;
    }

    private String resolvePaymentTable(Connection connection) throws SQLException {
        SQLException lastError = null;
        for (String tableName : new String[]{"payment", "payments"}) {
            try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery("SELECT id FROM " + tableName + " LIMIT 1")) {
                return tableName;
            } catch (SQLException e) {
                lastError = e;
            }
        }
        throw new SQLException("Could not find payment table 'payment' or 'payments'.", lastError);
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        return new Payment(
                rs.getInt("id"),
                rs.getInt("ticket_id"),
                rs.getString("payment_intent_id"),
                rs.getString("status"),
                rs.getString("customer_email"),
                rs.getDouble("amount"),
                rs.getInt("quantity_purchased"),
                rs.getString("customer_name"),
                rs.getString("customer_phone"),
                rs.getString("notes"),
                rs.getString("refunded_at"),
                rs.getObject("refund_amount") == null ? null : rs.getDouble("refund_amount"),
                formatTimestamp(rs.getTimestamp("created_at")),
                formatTimestamp(rs.getTimestamp("updated_at")),
                rs.getObject("player_id") == null ? null : rs.getInt("player_id"),
                rs.getString("qr_code")
        );
    }

    private String formatTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : DISPLAY_DATE_TIME.format(timestamp.toLocalDateTime());
    }

    public record PaymentStats(int totalPayments,
                               double totalIncome,
                               double averageIncome,
                               int totalTickets,
                               int paidCount,
                               int pendingCount,
                               int failedCount) {
    }

    private Integer resolvePlayerId(Connection connection, String customerEmail, String customerName, Integer fallbackPlayerId) {
        if (fallbackPlayerId != null && fallbackPlayerId > 0) {
            if (playerExists(connection, fallbackPlayerId)) {
                return fallbackPlayerId;
            }
        }

        Integer byEmail = findPlayerId(connection, "SELECT id FROM player WHERE email = ? LIMIT 1", customerEmail);
        if (byEmail != null) {
            return byEmail;
        }

        Integer byUsername = findPlayerId(connection, "SELECT id FROM player WHERE username = ? LIMIT 1", customerName);
        if (byUsername != null) {
            return byUsername;
        }

        Integer byNickname = findPlayerId(connection, "SELECT id FROM player WHERE nickname = ? LIMIT 1", customerName);
        if (byNickname != null) {
            return byNickname;
        }

        return null;
    }

    private boolean playerExists(Connection connection, int playerId) {
        try (PreparedStatement pst = connection.prepareStatement("SELECT id FROM player WHERE id = ? LIMIT 1")) {
            pst.setInt(1, playerId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ignored) {
            return false;
        }
    }

    private Integer findPlayerId(Connection connection, String sql, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, value.trim());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException ignored) {
            return null;
        }

        return null;
    }

    private String resolveTicketTable(Connection connection) throws SQLException {
        SQLException lastError = null;
        for (String tableName : new String[]{"ticket", "tickets"}) {
            try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery("SELECT id FROM " + tableName + " LIMIT 1")) {
                return tableName;
            } catch (SQLException e) {
                lastError = e;
            }
        }
        throw new SQLException("Could not find ticket table 'ticket' or 'tickets'.", lastError);
    }

    private record StripeCheckout(String sessionId, String checkoutUrl) {
    }
}