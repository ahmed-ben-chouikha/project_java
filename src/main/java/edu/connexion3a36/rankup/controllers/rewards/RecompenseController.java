package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.entities.Recompense;
import edu.connexion3a36.rankup.services.RecompenseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.List;

public class RecompenseController {
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private TableView<Recompense> recompenseTable;
    @FXML
    private TableColumn<Recompense, Integer> idCol;
    @FXML
    private TableColumn<Recompense, String> recompenseCol;
    @FXML
    private TableColumn<Recompense, String> typeCol;
    @FXML
    private TableColumn<Recompense, Integer> classementCol;
    @FXML
    private TableColumn<Recompense, String> descriptionCol;
    @FXML
    private Button createBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button refreshBtn;

    private RecompenseService service;
    private ObservableList<Recompense> recompenses;

    @FXML
    void initialize() {
        service = new RecompenseService();
        recompenses = FXCollections.observableArrayList();

        // Configurer les colonnes
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        recompenseCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRecompense()));
        typeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        classementCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getClassement()).asObject());
        descriptionCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        // Initialiser le filtre de type
        typeFilter.setItems(FXCollections.observableArrayList("Tous", "Or", "Argent", "Bronze"));
        typeFilter.setValue("Tous");

        // Charger les données
        loadData();

        // Configurer les écouteurs
        searchField.textProperty().addListener((obs, old, newVal) -> filterData());
        typeFilter.valueProperty().addListener((obs, old, newVal) -> filterData());

        recompenseTable.setItems(recompenses);
    }

    @FXML
    void onCreateRecompense() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/rewards/recompense-form.fxml"));
            Node formNode = loader.load();
            RecompenseFormController formController = loader.getController();
            formController.setMode("CREATE", null, this);
            RankUpApp.loadInBase(formNode);
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger le formulaire");
        }
    }

    @FXML
    void onEditRecompense() {
        Recompense selected = recompenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Attention", "Veuillez sélectionner une récompense");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/rewards/recompense-form.fxml"));
            Node formNode = loader.load();
            RecompenseFormController formController = loader.getController();
            formController.setMode("EDIT", selected, this);
            RankUpApp.loadInBase(formNode);
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger le formulaire");
        }
    }

    @FXML
    void onDeleteRecompense() {
        Recompense selected = recompenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Attention", "Veuillez sélectionner une récompense");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la récompense");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette récompense?");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (service.delete(selected.getId())) {
                showInfo("Succès", "Récompense supprimée avec succès");
                loadData();
            } else {
                showError("Erreur", "Impossible de supprimer la récompense");
            }
        }
    }

    @FXML
    void onRefresh() {
        loadData();
    }

    private void loadData() {
        List<Recompense> list = service.getAll();
        recompenses.setAll(list);
    }

    private void filterData() {
        String searchText = searchField.getText().toLowerCase();
        String selectedType = typeFilter.getValue();

        List<Recompense> allRecompenses = service.getAll();
        ObservableList<Recompense> filtered = FXCollections.observableArrayList();

        for (Recompense r : allRecompenses) {
            boolean matchesSearch = searchText.isEmpty() ||
                    r.getRecompense().toLowerCase().contains(searchText) ||
                    r.getDescription().toLowerCase().contains(searchText);

            boolean matchesType = selectedType.equals("Tous") || r.getType().equals(selectedType);

            if (matchesSearch && matchesType) {
                filtered.add(r);
            }
        }
        recompenseTable.setItems(filtered);
    }

    public void refreshTable() {
        loadData();
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

