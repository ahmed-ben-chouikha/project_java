package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.entities.Recompense;
import edu.connexion3a36.rankup.services.RecompenseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private TextField tournamentIdField;
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
    private static final int TYPE_MAX_LENGTH = 50;

    @FXML
    void initialize() {
        service = new RecompenseService();
        typeCombo.setItems(javafx.collections.FXCollections.observableArrayList("Or", "Argent", "Bronze"));

        // Ajout des validations en temps réel
        recompenseField.textProperty().addListener((obs, oldVal, newVal) -> validateRecompense(newVal));
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateType());
        classementField.textProperty().addListener((obs, oldVal, newVal) -> validateClassement(newVal));
        descriptionArea.textProperty().addListener((obs, oldVal, newVal) -> validateDescription(newVal));
        tournamentIdField.textProperty().addListener((obs, oldVal, newVal) -> validateTournamentId(newVal));
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
            tournamentIdField.setText(String.valueOf(currentRecompense.getTournamentId()));
        }
    }

    @FXML
    void onSave() {
        clearErrors();
        if (validateAllFields()) {
            currentRecompense.setRecompense(recompenseField.getText());
            currentRecompense.setType(typeCombo.getValue());
            currentRecompense.setClassement(Integer.parseInt(classementField.getText()));
            currentRecompense.setDescription(descriptionArea.getText());
            currentRecompense.setTournamentId(Integer.parseInt(tournamentIdField.getText()));

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
        if (value == null || value.isEmpty()) {
            setErrorStyle(recompenseField);
            recompenseError.setText("❌ Le nom est requis");
        } else if (value.length() > RECOMPENSE_MAX_LENGTH) {
            setErrorStyle(recompenseField);
            recompenseError.setText("❌ Dépasse " + RECOMPENSE_MAX_LENGTH + " caractères");
        } else {
            clearFieldError(recompenseField);
            recompenseError.setText("");
        }
    }

    private void validateType() {
        if (typeCombo.getValue() == null) {
            setErrorStyle(typeCombo);
            typeError.setText("❌ Sélectionner un type");
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
        if (value != null && value.length() > 500) {
            setErrorStyle(descriptionArea);
            descriptionError.setText("❌ Dépasse 500 caractères");
        } else {
            clearFieldError(descriptionArea);
            descriptionError.setText("");
        }
    }

    private void validateTournamentId(String value) {
        if (value == null || value.isEmpty()) {
            setErrorStyle(tournamentIdField);
            tournamentError.setText("❌ L'ID tournoi est requis");
        } else {
            try {
                int id = Integer.parseInt(value);
                if (id <= 0) {
                    setErrorStyle(tournamentIdField);
                    tournamentError.setText("❌ L'ID doit être positif");
                } else {
                    clearFieldError(tournamentIdField);
                    tournamentError.setText("");
                }
            } catch (NumberFormatException e) {
                setErrorStyle(tournamentIdField);
                tournamentError.setText("❌ Doit être un nombre");
            }
        }
    }

    private boolean validateAllFields() {
        boolean isValid = true;

        if (recompenseField.getText() == null || recompenseField.getText().isEmpty() ||
            recompenseField.getText().length() > RECOMPENSE_MAX_LENGTH) {
            isValid = false;
        }

        if (typeCombo.getValue() == null) {
            isValid = false;
        }

        if (classementField.getText() == null || classementField.getText().isEmpty()) {
            isValid = false;
        } else {
            try {
                if (Integer.parseInt(classementField.getText()) <= 0) {
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                isValid = false;
            }
        }

        if (tournamentIdField.getText() == null || tournamentIdField.getText().isEmpty()) {
            isValid = false;
        } else {
            try {
                if (Integer.parseInt(tournamentIdField.getText()) <= 0) {
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                isValid = false;
            }
        }

        return isValid;
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

