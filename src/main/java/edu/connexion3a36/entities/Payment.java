package edu.connexion3a36.entities;

public class Payment {

    private final int id;
    private final int ticketId;
    private final String paymentIntentId;
    private final String status;
    private final String customerEmail;
    private final double amount;
    private final int quantityPurchased;
    private final String customerName;
    private final String customerPhone;
    private final String notes;
    private final String refundedAt;
    private final Double refundAmount;
    private final String createdAt;
    private final String updatedAt;
    private final Integer playerId;
    private final String qrCode;

    public Payment(int id,
                   int ticketId,
                   String paymentIntentId,
                   String status,
                   String customerEmail,
                   double amount,
                   int quantityPurchased,
                   String customerName,
                   String customerPhone,
                   String notes,
                   String refundedAt,
                   Double refundAmount,
                   String createdAt,
                   String updatedAt,
                   Integer playerId,
                   String qrCode) {
        this.id = id;
        this.ticketId = ticketId;
        this.paymentIntentId = paymentIntentId;
        this.status = status;
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.quantityPurchased = quantityPurchased;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.notes = notes;
        this.refundedAt = refundedAt;
        this.refundAmount = refundAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.playerId = playerId;
        this.qrCode = qrCode;
    }

    public int getId() {
        return id;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public double getAmount() {
        return amount;
    }

    public int getQuantityPurchased() {
        return quantityPurchased;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getNotes() {
        return notes;
    }

    public String getRefundedAt() {
        return refundedAt;
    }

    public Double getRefundAmount() {
        return refundAmount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public String getQrCode() {
        return qrCode;
    }
}