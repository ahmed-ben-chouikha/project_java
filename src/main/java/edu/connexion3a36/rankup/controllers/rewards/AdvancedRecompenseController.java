package edu.connexion3a36.rankup.controllers.rewards;

import edu.connexion3a36.rankup.services.AutoApprovalService;
import edu.connexion3a36.rankup.services.ReportRecompenseService;
import edu.connexion3a36.rankup.services.RecompenseService;
import edu.connexion3a36.rankup.services.DemandeRecompenseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
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
        Map<String, Object> recompenseStats = recompenseService.getStatistics();
        totalRecompensesLabel.setText(String.valueOf(recompenseStats.getOrDefault("total_count", 0)));
        coutTotalLabel.setText(String.valueOf(recompenseStats.getOrDefault("total_cost", 0)) + " €");
        coutMoyenLabel.setText(String.format("%.2f €", recompenseStats.getOrDefault("average_cost", 0.0)));
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

