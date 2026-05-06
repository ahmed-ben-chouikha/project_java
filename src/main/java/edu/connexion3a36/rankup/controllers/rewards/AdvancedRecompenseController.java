package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.services.AutoApprovalService;
import edu.connexion3a36.rankup.services.ReportRecompenseService;
import edu.connexion3a36.rankup.services.RecompenseService;
import edu.connexion3a36.rankup.services.DemandeRecompenseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour les fonctionnalités avancées de gestion des récompenses
 */
public class AdvancedRecompenseController {

    @FXML
    private TabPane tabPane;

    // Tab Statistiques
    @FXML
    private Label totalDemandesLabel;
    @FXML
    private Label approuveesLabel;
    @FXML
    private Label rejeteesLabel;
    @FXML
    private Label enAttenteLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label totalRecompensesLabel;
    @FXML
    private Label coutTotalLabel;
    @FXML
    private Label coutMoyenLabel;
    @FXML
    private PieChart demandStatusChart;
    @FXML
    private BarChart<String, Number> recompenseTypeChart;
    @FXML
    private CategoryAxis recompenseTypeXAxis;
    @FXML
    private NumberAxis recompenseTypeYAxis;

    // Tab Rapports
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> reportTypeCombo;
    @FXML
    private TextArea reportOutput;
    @FXML
    private Button generateReportBtn;
    @FXML
    private Button exportCSVBtn;

    // Tab Auto Approval
    @FXML
    private Button autoApproveBtn;
    @FXML
    private Label autoApproveResultLabel;
    @FXML
    private ListView<String> rulesListView;

    private ReportRecompenseService reportService;
    private AutoApprovalService approvalService;
    private RecompenseService recompenseService;
    private DemandeRecompenseService demandeService;

    @FXML
    void initialize() {
        reportService = new ReportRecompenseService();
        approvalService = new AutoApprovalService();
        recompenseService = new RecompenseService();
        demandeService = new DemandeRecompenseService();

        // Initialiser les date pickers
        startDatePicker.setValue(LocalDate.now().minusMonths(1));
        endDatePicker.setValue(LocalDate.now());

        // Initialiser les types de rapports
        reportTypeCombo.setItems(FXCollections.observableArrayList(
                "Rapport des Demandes",
                "Rapport des Récompenses",
                "Rapport Complet"
        ));
        reportTypeCombo.setValue("Rapport des Demandes");

        // Charger les statistiques
        loadStatistics();

        // Charger les règles d'approbation
        loadApprovalRules();
    }

    @FXML
    void onGenerateReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String reportType = reportTypeCombo.getValue();

        if (startDate == null || endDate == null) {
            showError("Erreur", "Veuillez sélectionner une période");
            return;
        }

        if (startDate.isAfter(endDate)) {
            showError("Erreur", "La date de début doit être avant la date de fin");
            return;
        }

        StringBuilder report = new StringBuilder();

        if ("Rapport des Demandes".equals(reportType) || "Rapport Complet".equals(reportType)) {
            Map<String, Object> demandsReport = reportService.generateDemandReport(startDate, endDate);
            report.append("=== RAPPORT DES DEMANDES ===\n\n");
            report.append("Période: ").append(startDate).append(" à ").append(endDate).append("\n\n");
            report.append("Total des demandes: ").append(demandsReport.get("total_demandes")).append("\n");
            report.append("Approuvées: ").append(demandsReport.get("approuvees")).append("\n");
            report.append("Rejetées: ").append(demandsReport.get("rejetees")).append("\n");
            report.append("En attente: ").append(demandsReport.get("en_attente")).append("\n");
            report.append("Score d'approbation moyen: ").append(
                    String.format("%.2f", demandsReport.getOrDefault("score_approbation_moyen", 0.0))
            ).append("\n");
            report.append("Jours moyen pour approbation: ").append(
                    String.format("%.2f", demandsReport.getOrDefault("jours_moyen_approbation", 0.0))
            ).append("\n\n");
        }

        if ("Rapport des Récompenses".equals(reportType) || "Rapport Complet".equals(reportType)) {
            Map<String, Object> recompenseReport = reportService.generateRecompenseReport(startDate, endDate);
            report.append("=== RAPPORT DES RÉCOMPENSES ===\n\n");
            report.append("Total des récompenses: ").append(recompenseReport.get("total_recompenses")).append("\n");
            report.append("Actives: ").append(recompenseReport.get("actives")).append("\n");
            report.append("Inactives: ").append(recompenseReport.get("inactives")).append("\n");
            report.append("Coût total: ").append(recompenseReport.get("cout_total")).append("\n");
            report.append("Coût moyen: ").append(
                    String.format("%.2f", recompenseReport.getOrDefault("cout_moyen", 0.0))
            ).append("\n\n");
        }

        reportOutput.setText(report.toString());
        showInfo("Succès", "Rapport généré avec succès");
    }

    @FXML
    void onExportCSV() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showError("Erreur", "Veuillez sélectionner une période");
            return;
        }

        String csvContent = reportService.generateCSVReport(startDate, endDate);
        reportOutput.setText(csvContent);
        showInfo("Succès", "Rapport CSV généré. Vous pouvez copier le contenu.");
    }

    @FXML
    void onAutoApprove() {
        int approvedCount = approvalService.autoApproveDemands();
        autoApproveResultLabel.setText("✓ " + approvedCount + " demandes ont été approuvées automatiquement.");
        loadStatistics();
        showInfo("Succès", approvedCount + " demandes approuvées automatiquement");
    }

    private void loadStatistics() {
        Map<String, Object> completeStats = reportService.getCompleteStatistics();

        // Statistiques des demandes
        totalDemandesLabel.setText(String.valueOf(completeStats.getOrDefault("total_all_requests", 0)));
        scoreLabel.setText(String.format("%.1f%%", completeStats.getOrDefault("taux_approbation", 0.0)));

        // Récupérer les statistiques détaillées
        Map<String, Object> demandsReport = reportService.generateDemandReport(
                LocalDate.now().minusMonths(1),
                LocalDate.now()
        );
        approuveesLabel.setText(String.valueOf(demandsReport.getOrDefault("approuvees", 0)));
        rejeteesLabel.setText(String.valueOf(demandsReport.getOrDefault("rejetees", 0)));
        enAttenteLabel.setText(String.valueOf(demandsReport.getOrDefault("en_attente", 0)));

        // Statistiques des récompenses
        List<edu.connexion3a36.rankup.entities.Recompense> allRecompenses = recompenseService.getAll();
        totalRecompensesLabel.setText(String.valueOf(allRecompenses.size()));
        
        // Calcul du coût total et moyen non disponible dans l'entité actuelle -> affichage simple
        double totalCost = 0;
        coutTotalLabel.setText(String.format("%.2f €", totalCost));
        double averageCost = allRecompenses.isEmpty() ? 0 : totalCost / allRecompenses.size();
        coutMoyenLabel.setText(String.format("%.2f €", averageCost));

        Map<String, Integer> recompenseTypeCounts = new HashMap<>();
        for (edu.connexion3a36.rankup.entities.Recompense recompense : allRecompenses) {
            String type = recompense.getType() == null ? "Inconnu" : recompense.getType();
            recompenseTypeCounts.put(type, recompenseTypeCounts.getOrDefault(type, 0) + 1);
        }

        updateCharts(demandsReport, recompenseTypeCounts);
    }

    private void updateCharts(Map<String, Object> demandReport, Map<String, Integer> recompenseTypeCounts) {
        if (demandStatusChart != null) {
            demandStatusChart.setData(FXCollections.observableArrayList(
                    new PieChart.Data("Approuvées", toInt(demandReport.getOrDefault("approuvees", 0))),
                    new PieChart.Data("Rejetées", toInt(demandReport.getOrDefault("rejetees", 0))),
                    new PieChart.Data("En attente", toInt(demandReport.getOrDefault("en_attente", 0)))
            ));
            demandStatusChart.setTitle("Répartition des demandes");
        }

        if (recompenseTypeChart != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Types de récompenses");
            if (recompenseTypeCounts != null) {
                for (Map.Entry<String, Integer> entry : recompenseTypeCounts.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
            }
            recompenseTypeChart.getData().setAll(series);
            recompenseTypeChart.setTitle("Répartition des récompenses par type");
        }
    }

    private int toInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            return 0;
        }
    }

    private void loadApprovalRules() {
        java.util.List<String> rules = approvalService.getActiveRules();
        ObservableList<String> rulesList = FXCollections.observableArrayList(rules);
        rulesListView.setItems(rulesList);
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

