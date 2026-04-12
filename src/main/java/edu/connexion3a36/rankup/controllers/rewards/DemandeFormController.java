package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.entities.DemandeRecompense;
import edu.connexion3a36.rankup.services.DemandeRecompenseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;

public class DemandeFormController {
    @FXML
    private Label formTitle;
    @FXML
    private TextField nomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea motifArea;
    @FXML
    private ComboBox<String> statutCombo;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    private DemandeRecompenseService service;
    private DemandeRecompense currentDemande;
    private DemandeRecompenseController parentController;
    private String mode;

    @FXML
    void initialize() {
        service = new DemandeRecompenseService();
        statutCombo.setItems(javafx.collections.FXCollections.observableArrayList("En attente", "Approuvée", "Rejetée"));
    }

    public void setMode(String mode, DemandeRecompense demande, DemandeRecompenseController parent) {
        this.mode = mode;
        this.currentDemande = demande;
        this.parentController = parent;

        if (mode.equals("CREATE")) {
            formTitle.setText("Créer une nouvelle demande");
            currentDemande = new DemandeRecompense();
            statutCombo.setValue("En attente");
        } else {
            formTitle.setText("Modifier la demande");
            loadFormData();
        }
    }

    private void loadFormData() {
        if (currentDemande != null) {
            nomField.setText(currentDemande.getNomDemandeur());
            emailField.setText(currentDemande.getEmail());
            motifArea.setText(currentDemande.getMotif());
            statutCombo.setValue(currentDemande.getStatut());
        }
    }

    @FXML
    void onSave() {
        if (validateFields()) {
            currentDemande.setNomDemandeur(nomField.getText());
            currentDemande.setEmail(emailField.getText());
            currentDemande.setMotif(motifArea.getText());
            currentDemande.setStatut(statutCombo.getValue());
            if (mode.equals("CREATE")) {
                currentDemande.setDateDemande(LocalDateTime.now());
            }

            boolean success;
            if (mode.equals("CREATE")) {
                success = service.add(currentDemande);
            } else {
                success = service.update(currentDemande);
            }

            if (success) {
                showInfo("Succès", "Demande " + (mode.equals("CREATE") ? "ajoutée" : "modifiée") + " avec succès");
                parentController.refreshTable();
                onCancel();
            } else {
                showError("Erreur", "Erreur lors de l'enregistrement");
            }
        }
    }

    @FXML
    void onCancel() {
        RankUpApp.loadInBase("/views/rewards/demande-list.fxml");
    }

    private boolean validateFields() {
        if (nomField.getText().isEmpty()) {
            showWarning("Validation", "Veuillez entrer le nom du demandeur");
            return false;
        }
        if (emailField.getText().isEmpty()) {
            showWarning("Validation", "Veuillez entrer l'email");
            return false;
        }
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showWarning("Validation", "Veuillez entrer une adresse email valide");
            return false;
        }
        if (motifArea.getText().isEmpty()) {
            showWarning("Validation", "Veuillez entrer le motif de la demande");
            return false;
        }
        if (statutCombo.getValue() == null) {
            showWarning("Validation", "Veuillez sélectionner un statut");
            return false;
        }
        return true;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
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

