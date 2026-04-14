package edu.connexion3a36.services;

import edu.connexion3a36.entities.Ticket;
import edu.connexion3a36.tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketService {

    public static class GameOption {
        private final int id;
        private final String label;

        public GameOption(int id, String label) {
            this.id = id;
            this.label = label;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public List<Ticket> getAllTickets() throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM ticket ORDER BY id DESC";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                tickets.add(new Ticket(
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
                ));
            }
        }

        return tickets;
    }

    public void createTicket(Ticket ticket) throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        String query = "INSERT INTO ticket (game_id, ticket_number, type, price, quantity, sold, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, ticket.getGameId());
            pst.setString(2, ticket.getTicketNumber());
            pst.setString(3, ticket.getType());
            pst.setDouble(4, ticket.getPrice());
            pst.setInt(5, ticket.getQuantity());
            pst.setInt(6, ticket.getSold());
            pst.setString(7, ticket.getStatus());
            pst.setTimestamp(8, now);
            pst.setTimestamp(9, now);
            pst.executeUpdate();
        }
    }

    public void updateTicket(Ticket ticket) throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        String query = "UPDATE ticket SET game_id = ?, ticket_number = ?, type = ?, price = ?, quantity = ?, sold = ?, status = ?, updated_at = ? WHERE id = ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, ticket.getGameId());
            pst.setString(2, ticket.getTicketNumber());
            pst.setString(3, ticket.getType());
            pst.setDouble(4, ticket.getPrice());
            pst.setInt(5, ticket.getQuantity());
            pst.setInt(6, ticket.getSold());
            pst.setString(7, ticket.getStatus());
            pst.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            pst.setInt(9, ticket.getId());
            pst.executeUpdate();
        }
    }

    public void deleteTicketById(int ticketId) throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        try (PreparedStatement pst = connection.prepareStatement("DELETE FROM ticket WHERE id = ?")) {
            pst.setInt(1, ticketId);
            pst.executeUpdate();
        }
    }

    public List<GameOption> getGameOptions() throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        List<GameOption> games = new ArrayList<>();
        String query = "SELECT id, matchdate FROM game ORDER BY id DESC";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String label = "Game #" + id + " - " + rs.getString("matchdate");
                games.add(new GameOption(id, label));
            }
        }

        return games;
    }
}

