package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.rankup.entities.Recompense;
import edu.connexion3a36.rankup.services.RewardPdfService;
import edu.connexion3a36.rankup.services.RecompenseService;
import edu.connexion3a36.rankup.tools.RewardSearchUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private TableColumn<Recompense, String> tournamentCol;
    @FXML
    private Button createBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private Button exportPdfBtn;

    private RecompenseService service;
    private RewardPdfService pdfService;
    private ObservableList<Recompense> recompenses;
    private Map<Integer, String> tournamentNameMap;

    @FXML
    void initialize() {
        service = new RecompenseService();
        pdfService = new RewardPdfService();
        recompenses = FXCollections.observableArrayList();
        tournamentNameMap = service.getTournamentNameMap();

        // Configurer les colonnes
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        recompenseCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRecompense()));
        typeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        classementCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getClassement()).asObject());
        descriptionCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(safeText(cellData.getValue().getDescription())));
        tournamentCol.setCellValueFactory(cellData -> {
            int tournamentId = cellData.getValue().getTournamentId();
            String tournamentName = tournamentNameMap.getOrDefault(tournamentId, "Tournoi #" + tournamentId);
            return new javafx.beans.property.SimpleStringProperty(tournamentName);
        });

        // Initialiser le filtre de type
        typeFilter.setItems(FXCollections.observableArrayList("Tous", "Medaille", "Argent", "Trophee", "Accessoir PC"));
        typeFilter.setValue("Tous");

        // Charger les données
        loadData();

        // Configurer les écouteurs
        searchField.textProperty().addListener((obs, old, newVal) -> filterData());
        typeFilter.valueProperty().addListener((obs, old, newVal) -> filterData());

        editBtn.disableProperty().bind(recompenseTable.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.disableProperty().bind(recompenseTable.getSelectionModel().selectedItemProperty().isNull());
        recompenseTable.setRowFactory(table -> {
            TableRow<Recompense> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    onEditRecompense();
                }
            });
            return row;
        });

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
                String detail = safeText(service.getLastErrorMessage());
                showError("Erreur", detail.isEmpty() ? "Impossible de supprimer la récompense" : "Suppression impossible: " + detail);
            }
        }
    }

    @FXML
    void onRefresh() {
        loadData();
    }

    @FXML
    void onExportPdf() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter les récompenses en PDF");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        chooser.setInitialFileName("recompenses.pdf");

        File file = chooser.showSaveDialog(recompenseTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        List<Recompense> exportItems = new ArrayList<>(recompenseTable.getItems());
        if (pdfService.exportRecompenses(exportItems, tournamentNameMap, file)) {
            showInfo("Succès", "PDF exporté avec succès");
        } else {
            showError("Erreur", pdfService.getLastErrorMessage());
        }
    }

    private void loadData() {
        tournamentNameMap = service.getTournamentNameMap();
        List<Recompense> list = service.getAll();
        recompenses.setAll(list);
    }

    private void filterData() {
        String searchText = safeText(searchField.getText());
        String selectedType = typeFilter.getValue();

        List<Recompense> allRecompenses = service.getAll();
        ObservableList<Recompense> filtered = FXCollections.observableArrayList();

        for (Recompense r : allRecompenses) {
            boolean matchesSearch = RewardSearchUtil.matches(searchText,
                    r.getRecompense(),
                    r.getType(),
                    r.getDescription(),
                    tournamentNameMap.getOrDefault(r.getTournamentId(), "Tournoi #" + r.getTournamentId()));

            boolean matchesType = selectedType == null || selectedType.equals("Tous") || selectedType.equals(r.getType());

            if (matchesSearch && matchesType) {
                filtered.add(r);
            }
        }
        recompenseTable.setItems(filtered);
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
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

