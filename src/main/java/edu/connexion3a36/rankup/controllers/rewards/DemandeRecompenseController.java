package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.entities.DemandeRecompense;
import edu.connexion3a36.rankup.services.DemandeRecompenseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DemandeRecompenseController {
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> statutFilter;
    @FXML
    private TableView<DemandeRecompense> demandeTable;
    @FXML
    private TableColumn<DemandeRecompense, Integer> idCol;
    @FXML
    private TableColumn<DemandeRecompense, String> nomCol;
    @FXML
    private TableColumn<DemandeRecompense, String> emailCol;
    @FXML
    private TableColumn<DemandeRecompense, String> motifCol;
    @FXML
    private TableColumn<DemandeRecompense, String> dateCol;
    @FXML
    private TableColumn<DemandeRecompense, String> statutCol;
    @FXML
    private Button createBtn;
    @FXML
    private Button viewBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button refreshBtn;

    private DemandeRecompenseService service;
    private ObservableList<DemandeRecompense> demandes;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    void initialize() {
        service = new DemandeRecompenseService();
        demandes = FXCollections.observableArrayList();

        // Configurer les colonnes
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nomCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNomDemandeur()));
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        motifCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMotif() != null ? cellData.getValue().getMotif().substring(0, Math.min(30, cellData.getValue().getMotif().length())) + "..." : ""));
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateDemande().format(formatter)));
        statutCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut()));

        // Initialiser le filtre de statut
        statutFilter.setItems(FXCollections.observableArrayList("Tous", "En attente", "Approuvée", "Rejetée"));
        statutFilter.setValue("Tous");

        // Charger les données
        loadData();

        // Configurer les écouteurs
        searchField.textProperty().addListener((obs, old, newVal) -> filterData());
        statutFilter.valueProperty().addListener((obs, old, newVal) -> filterData());

        demandeTable.setItems(demandes);
    }

    @FXML
    void onCreateDemande() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/rewards/demande-form.fxml"));
            Node formNode = loader.load();
            DemandeFormController formController = loader.getController();
            formController.setMode("CREATE", null, this);
            RankUpApp.loadInBase(formNode);
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger le formulaire");
        }
    }

    @FXML
    void onViewDemande() {
        DemandeRecompense selected = demandeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Attention", "Veuillez sélectionner une demande");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/rewards/demande-detail.fxml"));
            Node detailNode = loader.load();
            DemandeDetailController detailController = loader.getController();
            detailController.setDemande(selected, this);
            RankUpApp.loadInBase(detailNode);
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger les détails");
        }
    }

    @FXML
    void onEditDemande() {
        DemandeRecompense selected = demandeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Attention", "Veuillez sélectionner une demande");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/rewards/demande-form.fxml"));
            Node formNode = loader.load();
            DemandeFormController formController = loader.getController();
            formController.setMode("EDIT", selected, this);
            RankUpApp.loadInBase(formNode);
        } catch (IOException e) {
            showError("Erreur", "Impossible de charger le formulaire");
        }
    }

    @FXML
    void onDeleteDemande() {
        DemandeRecompense selected = demandeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Attention", "Veuillez sélectionner une demande");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la demande");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette demande?");
        if (alert.showAndWait().orElse(Alert.ButtonType.CANCEL) == Alert.ButtonType.OK) {
            if (service.delete(selected.getId())) {
                showInfo("Succès", "Demande supprimée avec succès");
                loadData();
            } else {
                showError("Erreur", "Impossible de supprimer la demande");
            }
        }
    }

    @FXML
    void onRefresh() {
        loadData();
    }

    private void loadData() {
        List<DemandeRecompense> list = service.getAll();
        demandes.setAll(list);
    }

    private void filterData() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatut = statutFilter.getValue();

        List<DemandeRecompense> allDemandes = service.getAll();
        ObservableList<DemandeRecompense> filtered = FXCollections.observableArrayList();

        for (DemandeRecompense d : allDemandes) {
            boolean matchesSearch = searchText.isEmpty() ||
                    d.getNomDemandeur().toLowerCase().contains(searchText) ||
                    d.getEmail().toLowerCase().contains(searchText);

            boolean matchesStatut = selectedStatut.equals("Tous") || d.getStatut().equals(selectedStatut);

            if (matchesSearch && matchesStatut) {
                filtered.add(d);
            }
        }
        demandeTable.setItems(filtered);
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

