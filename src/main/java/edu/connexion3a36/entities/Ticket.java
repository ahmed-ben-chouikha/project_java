package edu.connexion3a36.entities;

public class Ticket {

    private final int id;
    private final int gameId;
    private final String ticketNumber;
    private final String type;
    private final double price;
    private final int quantity;
    private final int sold;
    private final String status;
    private final String createdAt;
    private final String updatedAt;

    public Ticket(int id,
                  int gameId,
                  String ticketNumber,
                  String type,
                  double price,
                  int quantity,
                  int sold,
                  String status,
                  String createdAt,
                  String updatedAt) {
        this.id = id;
        this.gameId = gameId;
        this.ticketNumber = ticketNumber;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.sold = sold;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public int getGameId() {
        return gameId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSold() {
        return sold;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}

