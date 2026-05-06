package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.entities.Budget;
import edu.connexion3a36.entities.Depense;
import edu.connexion3a36.rankup.app.RankUpApp;
import edu.connexion3a36.services.BudgetService;
import edu.connexion3a36.services.DepenseService;
import edu.connexion3a36.services.TeamService;
import edu.connexion3a36.entities.Team;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BudgetStatsController {

    @FXML private Label allocatedLabel;
    @FXML private Label usedLabel;
    @FXML private Label remainingLabel;
    @FXML private Label utilizationLabel;
    @FXML private Label overspentTeamsLabel;
    @FXML private Label totalDepensesLabel;
    @FXML private Label depensesApproveesLabel;
    @FXML private Label depensesAttenteLabel;
    @FXML private ComboBox<String> teamCombo;
    @FXML private StackPane budgetByTeamChartContainer;
    @FXML private StackPane depensesByTeamChartContainer;
    @FXML private StackPane depensesByCategoryChartContainer;

    private BarChart<String, Number> budgetByTeamChart;
    private BarChart<String, Number> depensesByTeamChart;
    private PieChart depensesByCategoryChart;
    private Timeline autoRefreshTimeline;

    private BudgetService budgetService;
    private DepenseService depenseService;
    private TeamService teamService;
    private Map<Integer, String> teamNamesById = new HashMap<>();
    private Map<String, Integer> teamIdsByName = new HashMap<>();
    private final Set<Integer> managerTeamIds = new HashSet<>();
    private int loggedManagerId;

    @FXML
    private void initialize() {
        budgetService = new BudgetService();
        depenseService = new DepenseService();
        teamService = new TeamService();
        loggedManagerId = RankUpApp.getCurrentUserId();

        createCharts();
        setupTeamSelector();
        refreshStats();
        startAutoRefresh();
    }

    @FXML
    private void onRefresh(ActionEvent event) {
        refreshStats();
    }

    private void refreshStats() {
        try {
            List<Budget> budgets = budgetService.getAllBudgets();
            List<Depense> depenses = depenseService.getAllDepenses();
            List<Team> teams = loggedManagerId > 0
                ? teamService.getTeamsByCreatorId(loggedManagerId)
                : new ArrayList<>();

            if (budgets == null) {
                budgets = new ArrayList<>();
            }
            if (depenses == null) {
                depenses = new ArrayList<>();
            }
            if (teams == null) {
                teams = new ArrayList<>();
            }

            managerTeamIds.clear();
            for (Team team : teams) {
                if (team != null) {
                    managerTeamIds.add(team.getId());
                }
            }

            budgets = budgets.stream()
                .filter(budget -> budget != null && managerTeamIds.contains(budget.getTeamId()))
                .toList();
            depenses = depenses.stream()
                .filter(depense -> depense != null && depense.getTeamId() != null && managerTeamIds.contains(depense.getTeamId()))
                .toList();

            rebuildTeamMaps(teams, budgets, depenses);

            String selectedTeam = teamCombo != null && teamCombo.getValue() != null
                ? teamCombo.getValue()
                : "Toutes les équipes";
            boolean allTeams = "Toutes les équipes".equals(selectedTeam);
            Integer selectedTeamId = allTeams ? null : teamIdsByName.get(selectedTeam);

            float allocated = 0f;
            float used = 0f;
            if (allTeams) {
                for (Budget budget : budgets) {
                    if (budget != null) {
                        allocated += budget.getMontantAlloue();
                        used += budget.getMontantUtilise();
                    }
                }
            } else {
                Budget teamBudget = getBudgetForTeam(budgets, selectedTeamId);
                if (teamBudget != null) {
                    allocated = teamBudget.getMontantAlloue();
                    used = teamBudget.getMontantUtilise();
                }
            }

            float remaining = allocated - used;
            float utilization = allocated > 0f ? (used / allocated) * 100f : 0f;

            float totalDepenses = 0f;
            float depensesApprouvees = 0f;
            long depensesAttente = 0;
            for (Depense depense : depenses) {
                if (depense == null) {
                    continue;
                }

                if (!allTeams && (depense.getTeamId() == null || !depense.getTeamId().equals(selectedTeamId))) {
                    continue;
                }

                totalDepenses += depense.getMontant();
                if ("approuvé".equalsIgnoreCase(depense.getStatut()) || "payée".equalsIgnoreCase(depense.getStatut())) {
                    depensesApprouvees += depense.getMontant();
                }
                if ("en attente".equalsIgnoreCase(depense.getStatut())) {
                    depensesAttente++;
                }
            }

            long overspentTeams = budgets.stream()
                .filter(b -> b != null && b.getMontantUtilise() > b.getMontantAlloue())
                .count();

            allocatedLabel.setText(formatEuro(allocated));
            usedLabel.setText(formatEuro(used));
            remainingLabel.setText(formatEuro(remaining));
            utilizationLabel.setText(String.format(Locale.FRANCE, "%.2f%%", utilization));
            overspentTeamsLabel.setText(String.valueOf(overspentTeams));

            totalDepensesLabel.setText(formatEuro(totalDepenses));
            depensesApproveesLabel.setText(formatEuro(depensesApprouvees));
            depensesAttenteLabel.setText(String.valueOf(depensesAttente));

            updateBudgetByTeamChart(budgets, selectedTeamId, allTeams);
            updateDepensesByTeamChart(depenses);
            updateDepensesByCategoryChart(depenses, selectedTeamId, allTeams);

        } catch (Exception e) {
            System.err.println("[STATS] Failed to refresh stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTeamSelector() {
        try {
            if (teamCombo == null) {
                return;
            }

            List<Team> teams = loggedManagerId > 0
                ? teamService.getTeamsByCreatorId(loggedManagerId)
                : new ArrayList<>();
            teamCombo.getItems().clear();

            if (teams != null) {
                for (Team t : teams) {
                    if (t != null && t.getName() != null && !t.getName().isBlank()) {
                        teamCombo.getItems().add(t.getName());
                    }
                }
            }

            if (!teamCombo.getItems().isEmpty()) {
                teamCombo.setValue(teamCombo.getItems().get(0));
            } else {
                teamCombo.setValue(null);
            }
            teamCombo.setOnAction(e -> refreshStats());
        } catch (Exception ex) {
            System.err.println("[STATS] Failed to setup team selector: " + ex.getMessage());
        }
    }

    private void updateBudgetByTeamChart(List<Budget> budgets, Integer selectedTeamId, boolean allTeams) {
        if (budgetByTeamChart == null) {
            return;
        }

        budgetByTeamChart.getData().clear();

        XYChart.Series<String, Number> allocatedSeries = new XYChart.Series<>();
        allocatedSeries.setName("Budget alloué");

        XYChart.Series<String, Number> usedSeries = new XYChart.Series<>();
        usedSeries.setName("Budget utilisé");

        XYChart.Series<String, Number> remainingSeries = new XYChart.Series<>();
        remainingSeries.setName("Budget restant");

        for (Budget budget : budgets) {
            if (budget == null) {
                continue;
            }

            if (!allTeams && (selectedTeamId == null || budget.getTeamId() != selectedTeamId)) {
                continue;
            }

            String teamName = teamNamesById.getOrDefault(budget.getTeamId(), "Equipe " + budget.getTeamId());
            double allocated = budget.getMontantAlloue();
            double used = budget.getMontantUtilise();
            double remaining = allocated - used;

            XYChart.Data<String, Number> allocatedData = new XYChart.Data<>(teamName, allocated);
            XYChart.Data<String, Number> usedData = new XYChart.Data<>(teamName, used);
            XYChart.Data<String, Number> remainingData = new XYChart.Data<>(teamName, remaining);

            allocatedSeries.getData().add(allocatedData);
            usedSeries.getData().add(usedData);
            remainingSeries.getData().add(remainingData);

            addBarTooltip(allocatedData, teamName, "Alloué", allocated);
            addBarTooltip(usedData, teamName, "Utilisé", used);
            addBarTooltip(remainingData, teamName, "Restant", remaining);
        }

        budgetByTeamChart.getData().addAll(allocatedSeries, usedSeries, remainingSeries);
    }

    private void updateDepensesByTeamChart(List<Depense> depenses) {
        if (depensesByTeamChart == null) {
            return;
        }

        Map<String, Double> totalsByTeam = new HashMap<>();
        for (Depense depense : depenses) {
            if (depense == null || depense.getTeamId() == null) {
                continue;
            }
            String teamName = teamNamesById.getOrDefault(depense.getTeamId(), "Equipe " + depense.getTeamId());
            totalsByTeam.merge(teamName, (double) depense.getMontant(), Double::sum);
        }

        depensesByTeamChart.getData().clear();
        XYChart.Series<String, Number> depensesSeries = new XYChart.Series<>();
        depensesSeries.setName("Dépenses par équipe");

        for (Map.Entry<String, Double> entry : totalsByTeam.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            depensesSeries.getData().add(data);
            addBarTooltip(data, entry.getKey(), "Dépenses", entry.getValue());
        }

        depensesByTeamChart.getData().add(depensesSeries);
    }

    private void updateDepensesByCategoryChart(List<Depense> depenses, Integer selectedTeamId, boolean allTeams) {
        if (depensesByCategoryChart == null) {
            return;
        }

        Map<String, Double> totalsByCategory = new HashMap<>();
        for (Depense depense : depenses) {
            if (depense == null) {
                continue;
            }
            if (!allTeams && (depense.getTeamId() == null || !depense.getTeamId().equals(selectedTeamId))) {
                continue;
            }

            String category = depense.getCategorie() == null || depense.getCategorie().isBlank()
                ? "Autre"
                : depense.getCategorie();
            totalsByCategory.merge(category, (double) depense.getMontant(), Double::sum);
        }

        depensesByCategoryChart.getData().clear();
        List<PieChart.Data> pieData = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totalsByCategory.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
            pieData.add(data);
        }
        depensesByCategoryChart.getData().addAll(pieData);

        for (PieChart.Data data : pieData) {
            addPieTooltip(data);
        }
    }

    private void addBarTooltip(XYChart.Data<String, Number> data, String label, String metric, double value) {
        String tooltipText = label + "\n" + metric + " : " + formatEuro(value);
        Tooltip tooltip = new Tooltip(tooltipText);

        if (data.getNode() != null) {
            Tooltip.install(data.getNode(), tooltip);
            return;
        }

        data.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                Tooltip.install(newNode, tooltip);
            }
        });
    }

    private void addPieTooltip(PieChart.Data data) {
        String text = data.getName() + " : " + formatEuro(data.getPieValue());
        Tooltip tooltip = new Tooltip(text);

        if (data.getNode() != null) {
            Tooltip.install(data.getNode(), tooltip);
            return;
        }

        data.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                Tooltip.install(newNode, tooltip);
            }
        });
    }

    private Budget getBudgetForTeam(List<Budget> budgets, Integer teamId) {
        if (teamId == null) {
            return null;
        }
        for (Budget budget : budgets) {
            if (budget != null && budget.getTeamId() == teamId) {
                return budget;
            }
        }
        return null;
    }

    private void rebuildTeamMaps(List<Team> teams, List<Budget> budgets, List<Depense> depenses) {
        teamNamesById.clear();
        teamIdsByName.clear();

        for (Team team : teams) {
            if (team == null || team.getName() == null || team.getName().isBlank()) {
                continue;
            }
            teamNamesById.put(team.getId(), team.getName());
            teamIdsByName.put(team.getName(), team.getId());
        }

        for (Budget budget : budgets) {
            if (budget == null) {
                continue;
            }
            if (!teamNamesById.containsKey(budget.getTeamId())) {
                String fallbackName = budget.getTeamName() != null && !budget.getTeamName().isBlank()
                    ? budget.getTeamName()
                    : "Equipe " + budget.getTeamId();
                teamNamesById.put(budget.getTeamId(), fallbackName);
                teamIdsByName.put(fallbackName, budget.getTeamId());
            }
        }

        for (Depense depense : depenses) {
            if (depense == null || depense.getTeamId() == null) {
                continue;
            }
            if (!teamNamesById.containsKey(depense.getTeamId())) {
                String fallbackName = depense.getTeamName() != null && !depense.getTeamName().isBlank()
                    ? depense.getTeamName()
                    : "Equipe " + depense.getTeamId();
                teamNamesById.put(depense.getTeamId(), fallbackName);
                teamIdsByName.put(fallbackName, depense.getTeamId());
            }
        }
    }

    private String formatEuro(double value) {
        return String.format(Locale.FRANCE, "%.2f€", value);
    }

    private void startAutoRefresh() {
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> refreshStats()));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    private void createCharts() {
        CategoryAxis budgetXAxis = new CategoryAxis();
        NumberAxis budgetYAxis = new NumberAxis();
        budgetXAxis.setLabel("Équipe");
        budgetYAxis.setLabel("Montant (€)");
        budgetByTeamChart = new BarChart<>(budgetXAxis, budgetYAxis);
        budgetByTeamChart.setTitle("Budget alloué / utilisé / restant par équipe");
        budgetByTeamChart.setAnimated(false);
        budgetByTeamChart.setLegendVisible(true);
        budgetByTeamChart.setCategoryGap(14);
        budgetByTeamChart.setBarGap(4);
        budgetByTeamChart.setPrefHeight(320.0);
        if (budgetByTeamChartContainer != null) {
            budgetByTeamChartContainer.getChildren().setAll(budgetByTeamChart);
        }

        CategoryAxis depenseTeamXAxis = new CategoryAxis();
        NumberAxis depenseTeamYAxis = new NumberAxis();
        depenseTeamXAxis.setLabel("Équipe");
        depenseTeamYAxis.setLabel("Montant (€)");
        depensesByTeamChart = new BarChart<>(depenseTeamXAxis, depenseTeamYAxis);
        depensesByTeamChart.setTitle("Dépenses par équipe");
        depensesByTeamChart.setAnimated(false);
        depensesByTeamChart.setLegendVisible(true);
        depensesByTeamChart.setCategoryGap(14);
        depensesByTeamChart.setBarGap(4);
        depensesByTeamChart.setPrefHeight(300.0);
        if (depensesByTeamChartContainer != null) {
            depensesByTeamChartContainer.getChildren().setAll(depensesByTeamChart);
        }

        depensesByCategoryChart = new PieChart();
        depensesByCategoryChart.setTitle("Dépenses par catégorie");
        depensesByCategoryChart.setLegendVisible(true);
        depensesByCategoryChart.setLabelsVisible(true);
        depensesByCategoryChart.setClockwise(true);
        depensesByCategoryChart.setPrefHeight(300.0);
        if (depensesByCategoryChartContainer != null) {
            depensesByCategoryChartContainer.getChildren().setAll(depensesByCategoryChart);
        }
    }
}
