package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.entities.DemandeRecompense;
import edu.connexion3a36.rankup.entities.Recompense;
import edu.connexion3a36.rankup.services.DemandeRecompenseService;
import edu.connexion3a36.rankup.services.RecompenseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;

public class DemandeDetailController {
    @FXML
    private Label nomLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label recompenseLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label statutLabel;
    @FXML
    private TextArea motifArea;
    @FXML
    private Button approveBtn;
    @FXML
    private Button rejectBtn;
    @FXML
    private Button backBtn;

    private DemandeRecompenseService service;
    private RecompenseService recompenseService;
    private DemandeRecompense demande;
    private DemandeRecompenseController parentController;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    void initialize() {
        service = new DemandeRecompenseService();
        recompenseService = new RecompenseService();
    }

    public void setDemande(DemandeRecompense demande, DemandeRecompenseController parent) {
        this.demande = demande;
        this.parentController = parent;
        loadDemande();
    }

    private void loadDemande() {
        if (demande != null) {
            nomLabel.setText(demande.getNomDemandeur());
            emailLabel.setText(demande.getEmail());
            dateLabel.setText(demande.getDateDemande() == null ? "-" : demande.getDateDemande().format(formatter));
            statutLabel.setText(toUiStatus(demande.getStatut()));
            recompenseLabel.setText(resolveRecompenseLabel());
            motifArea.setText(demande.getMotif());

            // Désactiver les boutons selon le statut
            if (!"en_attente".equals(normalizeStatus(demande.getStatut()))) {
                approveBtn.setDisable(true);
                rejectBtn.setDisable(true);
            }

            // Appliquer le style du statut
            applyStatusStyle();
        }
    }

    @FXML
    void onApprove() {
        demande.setStatut("approuvee");
        if (service.update(demande)) {
            showInfo("Succès", "Demande approuvée avec succès");
            statutLabel.setText("Approuvée");
            applyStatusStyle();
            approveBtn.setDisable(true);
            rejectBtn.setDisable(true);
            parentController.refreshTable();
        } else {
            showError("Erreur", "Impossible d'approuver la demande");
        }
    }

    @FXML
    void onReject() {
        demande.setStatut("rejetee");
        if (service.update(demande)) {
            showInfo("Succès", "Demande rejetée");
            statutLabel.setText("Rejetée");
            applyStatusStyle();
            approveBtn.setDisable(true);
            rejectBtn.setDisable(true);
            parentController.refreshTable();
        } else {
            showError("Erreur", "Impossible de rejeter la demande");
        }
    }

    @FXML
    void onBack() {
        RankUpApp.loadInBase("/views/rewards/demande-list.fxml");
    }

    private void applyStatusStyle() {
        String statut = normalizeStatus(demande.getStatut());
        statutLabel.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; " +
                (statut.equals("approuvee") ? "-fx-text-fill: #22c55e;" :
                 statut.equals("rejetee") ? "-fx-text-fill: #ef4444;" :
                 "-fx-text-fill: #f59e0b;")
        );
    }

    private String resolveRecompenseLabel() {
        if (demande.getRecompenseId() == null || demande.getRecompenseId() <= 0) {
            return "-";
        }
        Recompense recompense = recompenseService.getById(demande.getRecompenseId());
        return recompense == null ? "#" + demande.getRecompenseId() : recompense.getRecompense() + " (#" + recompense.getId() + ")";
    }

    private String toUiStatus(String dbStatus) {
        String normalized = normalizeStatus(dbStatus);
        if ("approuvee".equals(normalized)) {
            return "Approuvée";
        }
        if ("rejetee".equals(normalized)) {
            return "Rejetée";
        }
        return "En attente";
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "en_attente";
        }
        String normalized = status.trim().toLowerCase();
        if (normalized.equals("en attente") || normalized.equals("en_attente")) {
            return "en_attente";
        }
        if (normalized.equals("approuvee") || normalized.equals("approuvée")) {
            return "approuvee";
        }
        if (normalized.equals("rejetee") || normalized.equals("rejetée")) {
            return "rejetee";
        }
        return "en_attente";
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

