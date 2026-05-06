package edu.connexion3a36.rankup.controllers.tickets;

import edu.connexion3a36.entities.Ticket;

public final class TicketFormState {

    private static Ticket editingTicket;

    private TicketFormState() {
    }

    public static void setEditingTicket(Ticket ticket) {
        editingTicket = ticket;
    }

    public static Ticket getEditingTicket() {
        return editingTicket;
    }

    public static void clear() {
        editingTicket = null;
    }
}

