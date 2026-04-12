package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.entities.DemandeRecompense;
import edu.connexion3a36.rankup.services.DemandeRecompenseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;

public class DemandeDetailController {
    @FXML
    private Label nomLabel;
    @FXML
    private Label emailLabel;
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
    private DemandeRecompense demande;
    private DemandeRecompenseController parentController;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    void initialize() {
        service = new DemandeRecompenseService();
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
            dateLabel.setText(demande.getDateDemande().format(formatter));
            statutLabel.setText(demande.getStatut());
            motifArea.setText(demande.getMotif());

            // Désactiver les boutons selon le statut
            if (!demande.getStatut().equals("En attente")) {
                approveBtn.setDisable(true);
                rejectBtn.setDisable(true);
            }

            // Appliquer le style du statut
            applyStatusStyle();
        }
    }

    @FXML
    void onApprove() {
        demande.setStatut("Approuvée");
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
        demande.setStatut("Rejetée");
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
        String statut = demande.getStatut();
        statutLabel.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; " +
                (statut.equals("Approuvée") ? "-fx-text-fill: #22c55e;" :
                 statut.equals("Rejetée") ? "-fx-text-fill: #ef4444;" :
                 "-fx-text-fill: #f59e0b;")
        );
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

