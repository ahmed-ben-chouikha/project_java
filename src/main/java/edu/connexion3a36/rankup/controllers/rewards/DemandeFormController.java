package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.entities.DemandeRecompense;
import edu.connexion3a36.rankup.entities.Recompense;
import edu.connexion3a36.rankup.services.DemandeRecompenseService;
import edu.connexion3a36.rankup.services.RecompenseService;
import edu.connexion3a36.rankup.session.SessionContext;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

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
    private ComboBox<Recompense> recompenseCombo;
    @FXML
    private Label nomError;
    @FXML
    private Label emailError;
    @FXML
    private Label motifError;
    @FXML
    private Label recompenseError;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    private DemandeRecompenseService service;
    private RecompenseService recompenseService;
    private DemandeRecompense currentDemande;
    private DemandeRecompenseController parentController;
    private String mode;

    @FXML
    void initialize() {
        service = new DemandeRecompenseService();
        recompenseService = new RecompenseService();
        recompenseCombo.setItems(javafx.collections.FXCollections.observableArrayList(recompenseService.getAll()));
        recompenseCombo.setConverter(new StringConverter<Recompense>() {
            @Override
            public String toString(Recompense recompense) {
                return recompense == null ? "" : recompense.getRecompense() + " (#" + recompense.getId() + ")";
            }

            @Override
            public Recompense fromString(String string) {
                return null;
            }
        });
    }

    public void setMode(String mode, DemandeRecompense demande, DemandeRecompenseController parent) {
        this.mode = mode;
        this.currentDemande = demande;
        this.parentController = parent;

        if (mode.equals("CREATE")) {
            formTitle.setText("Créer une nouvelle demande");
            currentDemande = new DemandeRecompense();
            currentDemande.setStatut("en_attente");
            if (SessionContext.isLoggedIn()) {
                currentDemande.setUserId(SessionContext.getCurrentUser().getId());
                nomField.setText(SessionContext.getCurrentUser().getFullName());
                emailField.setText(SessionContext.getCurrentUser().getEmail());
            }
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
            if (currentDemande.getRecompenseId() != null) {
                for (Recompense recompense : recompenseCombo.getItems()) {
                    if (recompense.getId() == currentDemande.getRecompenseId()) {
                        recompenseCombo.setValue(recompense);
                        break;
                    }
                }
            }
        }
    }

    @FXML
    void onSave() {
        if (validateFields()) {
            currentDemande.setNomDemandeur(nomField.getText().trim());
            currentDemande.setEmail(emailField.getText().trim().toLowerCase());
            currentDemande.setMotif(motifArea.getText().trim());
            Recompense selectedRecompense = recompenseCombo.getValue();
            currentDemande.setRecompenseId(selectedRecompense == null ? null : selectedRecompense.getId());

            if (mode.equals("CREATE")) {
                currentDemande.setStatut("en_attente");
            } else {
                currentDemande.setStatut(normalizeStatus(currentDemande.getStatut()));
            }

            if (mode.equals("CREATE")) {
                currentDemande.setDateDemande(LocalDateTime.now());
                if (SessionContext.isLoggedIn()) {
                    currentDemande.setUserId(SessionContext.getCurrentUser().getId());
                }
            } else if (currentDemande.getDateDemande() == null) {
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
                String message = service.getLastErrorMessage();
                showError("Erreur", message == null || message.isBlank() ? "Erreur lors de l'enregistrement" : message);
            }
        }
    }

    @FXML
    void onCancel() {
        RankUpApp.loadInBase("/views/rewards/demande-list.fxml");
    }

    private boolean validateFields() {
        clearErrors();
        boolean valid = true;

        String nom = nomField.getText() == null ? "" : nomField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String motif = motifArea.getText() == null ? "" : motifArea.getText().trim();

        if (nom.isEmpty()) {
            nomError.setText("Nom requis");
            valid = false;
        } else if (!nom.matches("^[A-Za-z][A-Za-z'\\-]*\\s+[A-Za-z][A-Za-z'\\-]*$")) {
            nomError.setText("Format attendu: prenom nom");
            valid = false;
        } else if (nom.length() > 255) {
            nomError.setText("Max 255 caracteres");
            valid = false;
        }

        if (email.isEmpty()) {
            emailError.setText("Email requis");
            valid = false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            emailError.setText("Email invalide");
            valid = false;
        }

        if (motif.isEmpty()) {
            motifError.setText("Description requise");
            valid = false;
        } else if (motif.length() < 50) {
            motifError.setText("Minimum 50 caracteres");
            valid = false;
        } else if (motif.length() > 1500) {
            motifError.setText("Max 1500 caracteres");
            valid = false;
        }

        if (recompenseCombo.getValue() == null) {
            recompenseError.setText("Recompense requise");
            valid = false;
        }

        return valid;
    }

    private void clearErrors() {
        nomError.setText("");
        emailError.setText("");
        motifError.setText("");
        recompenseError.setText("");
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

