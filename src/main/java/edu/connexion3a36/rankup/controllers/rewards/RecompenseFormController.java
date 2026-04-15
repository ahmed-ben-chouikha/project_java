package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.entities.Recompense;
import edu.connexion3a36.rankup.entities.Tournament;
import edu.connexion3a36.rankup.services.RecompenseService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.List;

public class RecompenseFormController {
    @FXML
    private Label formTitle;
    @FXML
    private TextField recompenseField;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField classementField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<Tournament> tournamentCombo;
    @FXML
    private Label recompenseError;
    @FXML
    private Label typeError;
    @FXML
    private Label classementError;
    @FXML
    private Label descriptionError;
    @FXML
    private Label tournamentError;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    private RecompenseService service;
    private Recompense currentRecompense;
    private RecompenseController parentController;
    private String mode;
    private static final int RECOMPENSE_MAX_LENGTH = 30;
    private List<String> allowedTypes;

    @FXML
    void initialize() {
        service = new RecompenseService();
        allowedTypes = service.getAllowedTypes();
        typeCombo.setItems(FXCollections.observableArrayList(allowedTypes));
        tournamentCombo.setItems(FXCollections.observableArrayList(service.getAllTournaments()));
        tournamentCombo.setConverter(new StringConverter<Tournament>() {
            @Override
            public String toString(Tournament tournament) {
                return tournament == null ? "" : tournament.getName();
            }

            @Override
            public Tournament fromString(String string) {
                return null;
            }
        });

        // Ajout des validations en temps réel
        recompenseField.textProperty().addListener((obs, oldVal, newVal) -> validateRecompense(newVal));
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateType());
        classementField.textProperty().addListener((obs, oldVal, newVal) -> validateClassement(newVal));
        descriptionArea.textProperty().addListener((obs, oldVal, newVal) -> validateDescription(newVal));
        tournamentCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateTournament());

        if (tournamentCombo.getItems().isEmpty()) {
            tournamentError.setText("❌ Aucun tournoi disponible");
            saveBtn.setDisable(true);
        }
    }

    public void setMode(String mode, Recompense recompense, RecompenseController parent) {
        this.mode = mode;
        this.currentRecompense = recompense;
        this.parentController = parent;

        if (mode.equals("CREATE")) {
            formTitle.setText("Ajouter une nouvelle récompense");
            currentRecompense = new Recompense();
        } else {
            formTitle.setText("Modifier la récompense");
            loadFormData();
        }
    }

    private void loadFormData() {
        if (currentRecompense != null) {
            recompenseField.setText(currentRecompense.getRecompense());
            typeCombo.setValue(currentRecompense.getType());
            classementField.setText(String.valueOf(currentRecompense.getClassement()));
            descriptionArea.setText(currentRecompense.getDescription());
            for (Tournament tournament : tournamentCombo.getItems()) {
                if (tournament.getId() == currentRecompense.getTournamentId()) {
                    tournamentCombo.setValue(tournament);
                    break;
                }
            }
        }
    }

    @FXML
    void onSave() {
        clearErrors();
        if (validateAllFields()) {
            currentRecompense.setRecompense(recompenseField.getText().trim());
            currentRecompense.setType(typeCombo.getValue());
            currentRecompense.setClassement(Integer.parseInt(classementField.getText().trim()));
            currentRecompense.setDescription(descriptionArea.getText() == null ? "" : descriptionArea.getText().trim());
            currentRecompense.setTournamentId(tournamentCombo.getValue().getId());

            boolean success;
            if (mode.equals("CREATE")) {
                success = service.add(currentRecompense);
            } else {
                success = service.update(currentRecompense);
            }

            if (success) {
                showInfo("Succès", "Récompense " + (mode.equals("CREATE") ? "ajoutée" : "modifiée") + " avec succès");
                parentController.refreshTable();
                onCancel();
            } else {
                showError("Erreur", "Erreur lors de l'enregistrement");
            }
        }
    }

    @FXML
    void onCancel() {
        RankUpApp.loadInBase("/views/rewards/recompense-list.fxml");
    }

    private void validateRecompense(String value) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            setErrorStyle(recompenseField);
            recompenseError.setText("❌ Le nom est requis");
        } else if (normalized.length() > RECOMPENSE_MAX_LENGTH) {
            setErrorStyle(recompenseField);
            recompenseError.setText("❌ Dépasse " + RECOMPENSE_MAX_LENGTH + " caractères");
        } else {
            clearFieldError(recompenseField);
            recompenseError.setText("");
        }
    }

    private void validateType() {
        if (typeCombo.getValue() == null || !allowedTypes.contains(typeCombo.getValue())) {
            setErrorStyle(typeCombo);
            typeError.setText("❌ Sélectionner un type valide");
        } else {
            clearFieldError(typeCombo);
            typeError.setText("");
        }
    }

    private void validateClassement(String value) {
        if (value == null || value.isEmpty()) {
            setErrorStyle(classementField);
            classementError.setText("❌ Le classement est requis");
        } else {
            try {
                int classement = Integer.parseInt(value);
                if (classement <= 0) {
                    setErrorStyle(classementField);
                    classementError.setText("❌ Le classement doit être positif");
                } else {
                    clearFieldError(classementField);
                    classementError.setText("");
                }
            } catch (NumberFormatException e) {
                setErrorStyle(classementField);
                classementError.setText("❌ Doit être un nombre");
            }
        }
    }

    private void validateDescription(String value) {
        String normalized = value == null ? "" : value.trim();
        if (!normalized.isEmpty() && normalized.length() <= 10) {
            setErrorStyle(descriptionArea);
            descriptionError.setText("❌ Laisser vide ou saisir plus de 10 caractères");
        } else if (normalized.length() > 500) {
            setErrorStyle(descriptionArea);
            descriptionError.setText("❌ Dépasse 500 caractères");
        } else {
            clearFieldError(descriptionArea);
            descriptionError.setText("");
        }
    }

    private void validateTournament() {
        if (tournamentCombo.getValue() == null) {
            setErrorStyle(tournamentCombo);
            tournamentError.setText("❌ Sélectionner un tournoi");
        } else if (!service.tournamentExists(tournamentCombo.getValue().getId())) {
            setErrorStyle(tournamentCombo);
            tournamentError.setText("❌ Tournoi introuvable");
        } else {
            clearFieldError(tournamentCombo);
            tournamentError.setText("");
        }
    }

    private boolean validateAllFields() {
        validateRecompense(recompenseField.getText());
        validateType();
        validateClassement(classementField.getText());
        validateDescription(descriptionArea.getText());
        validateTournament();

        return recompenseError.getText().isEmpty()
                && typeError.getText().isEmpty()
                && classementError.getText().isEmpty()
                && descriptionError.getText().isEmpty()
                && tournamentError.getText().isEmpty();
    }

    private void clearErrors() {
        recompenseError.setText("");
        typeError.setText("");
        classementError.setText("");
        descriptionError.setText("");
        tournamentError.setText("");
    }

    private void setErrorStyle(Control control) {
        control.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2; -fx-border-radius: 4;");
    }

    private void clearFieldError(Control control) {
        control.setStyle("-fx-border-color: transparent;");
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

