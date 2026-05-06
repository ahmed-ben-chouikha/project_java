package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.entities.DemandeRecompense;
import edu.connexion3a36.rankup.services.DemandeRecompenseService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class KanbanController {

    @FXML private VBox pendingColumn;
    @FXML private VBox approvedColumn;
    @FXML private VBox rejectedColumn;
    @FXML private Label pendingCount;
    @FXML private Label approvedCount;
    @FXML private Label rejectedCount;
    @FXML private TextField searchField;
    @FXML private Button refreshBtn;

    private DemandeRecompenseService service;
    private List<DemandeRecompense> allDemandes;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    void initialize() {
        service = new DemandeRecompenseService();
        loadKanban();

        searchField.textProperty().addListener((obs, old, newVal) -> filterKanban(newVal));
    }

    @FXML
    void onRefresh() {
        loadKanban();
    }

    private void loadKanban() {
        allDemandes = service.getAll();
        filterKanban(searchField.getText());
    }

    private void filterKanban(String search) {
        String keyword = (search == null) ? "" : search.toLowerCase().trim();

        List<DemandeRecompense> filtered = allDemandes.stream()
                .filter(d -> keyword.isEmpty()
                        || safe(d.getNomDemandeur()).toLowerCase().contains(keyword)
                        || safe(d.getEmail()).toLowerCase().contains(keyword)
                        || safe(d.getMotif()).toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        List<DemandeRecompense> pending  = filtered.stream().filter(d -> isStatus(d, "en_attente")).collect(Collectors.toList());
        List<DemandeRecompense> approved = filtered.stream().filter(d -> isStatus(d, "approuvee")).collect(Collectors.toList());
        List<DemandeRecompense> rejected = filtered.stream().filter(d -> isStatus(d, "rejetee")).collect(Collectors.toList());

        populateColumn(pendingColumn,  pending);
        populateColumn(approvedColumn, approved);
        populateColumn(rejectedColumn, rejected);

        pendingCount.setText(String.valueOf(pending.size()));
        approvedCount.setText(String.valueOf(approved.size()));
        rejectedCount.setText(String.valueOf(rejected.size()));
    }

    private void populateColumn(VBox column, List<DemandeRecompense> demandes) {
        column.getChildren().clear();
        for (DemandeRecompense d : demandes) {
            column.getChildren().add(buildCard(d));
        }
    }

    private VBox buildCard(DemandeRecompense d) {
        VBox card = new VBox(6);
        card.getStyleClass().add("kanban-card");
        card.setPadding(new Insets(12));

        Label nom = new Label(safe(d.getNomDemandeur()));
        nom.getStyleClass().add("kanban-card-title");
        nom.setWrapText(true);

        Label email = new Label(safe(d.getEmail()));
        email.getStyleClass().add("kanban-card-email");

        String motif = safe(d.getMotif());
        if (motif.length() > 80) motif = motif.substring(0, 80) + "…";
        Label motifLabel = new Label(motif);
        motifLabel.getStyleClass().add("kanban-card-motif");
        motifLabel.setWrapText(true);

        String dateStr = d.getDateDemande() == null ? "-" : d.getDateDemande().format(formatter);
        Label date = new Label("📅 " + dateStr);
        date.getStyleClass().add("kanban-card-date");

        Label badge = new Label(toUiStatus(d.getStatut()));
        badge.getStyleClass().addAll("kanban-badge", getBadgeClass(d.getStatut()));

        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER_RIGHT);

        String normalStatus = normalizeStatus(d.getStatut());

        if (!"approuvee".equals(normalStatus)) {
            Button approveBtn = new Button("✅");
            approveBtn.getStyleClass().add("kanban-action-btn");
            approveBtn.setTooltip(new Tooltip("Approuver"));
            approveBtn.setOnAction(e -> changeStatus(d, "approuvee"));
            actions.getChildren().add(approveBtn);
        }
        if (!"rejetee".equals(normalStatus)) {
            Button rejectBtn = new Button("❌");
            rejectBtn.getStyleClass().add("kanban-action-btn");
            rejectBtn.setTooltip(new Tooltip("Rejeter"));
            rejectBtn.setOnAction(e -> changeStatus(d, "rejetee"));
            actions.getChildren().add(rejectBtn);
        }
        if (!"en_attente".equals(normalStatus)) {
            Button pendingBtn = new Button("⏳");
            pendingBtn.getStyleClass().add("kanban-action-btn");
            pendingBtn.setTooltip(new Tooltip("Remettre en attente"));
            pendingBtn.setOnAction(e -> changeStatus(d, "en_attente"));
            actions.getChildren().add(pendingBtn);
        }

        card.getChildren().addAll(nom, email, motifLabel, date, badge, actions);
        return card;
    }

    private void changeStatus(DemandeRecompense d, String newStatus) {
        d.setStatut(newStatus);
        if (service.update(d)) {
            loadKanban();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de mettre à jour le statut.");
            alert.showAndWait();
        }
    }

    private boolean isStatus(DemandeRecompense d, String status) {
        return status.equals(normalizeStatus(d.getStatut()));
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) return "en_attente";
        String n = status.trim().toLowerCase();
        if (n.equals("en attente") || n.equals("en_attente")) return "en_attente";
        if (n.equals("approuvee") || n.equals("approuvée")) return "approuvee";
        if (n.equals("rejetee") || n.equals("rejetée")) return "rejetee";
        return "en_attente";
    }

    private String toUiStatus(String status) {
        return switch (normalizeStatus(status)) {
            case "approuvee" -> "Approuvée";
            case "rejetee"   -> "Rejetée";
            default          -> "En attente";
        };
    }

    private String getBadgeClass(String status) {
        return switch (normalizeStatus(status)) {
            case "approuvee" -> "badge-approved";
            case "rejetee"   -> "badge-rejected";
            default          -> "badge-pending";
        };
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
