package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.services.BudgetService;
import edu.connexion3a36.services.DepenseService;
import edu.connexion3a36.services.GroqChatbotService;
import edu.connexion3a36.services.TeamService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

public class ChatbotController {

    @FXML private TextArea chatDisplayArea;
    @FXML private TextField chatInputField;
    @FXML private Button chatSendBtn;

    private DepenseService depenseService;
    private BudgetService budgetService;
    private TeamService teamService;

    @FXML
    private void initialize() {
        depenseService = new DepenseService();
        budgetService = new BudgetService();
        teamService = new TeamService();
    }

    @FXML
    private void onChatSend(ActionEvent event) {
        String userMessage = chatInputField.getText();
        if (userMessage == null || userMessage.trim().isEmpty()) return;

        appendToChatDisplay("Vous: " + userMessage.trim());
        chatInputField.clear();
        chatSendBtn.setDisable(true);

        // Aggregate global stats
        float allocated = 0f;
        float used = 0f;
        float remaining = 0f;
        int expenseCount = 0;

        try {
            var budgets = budgetService.getAllBudgets();
            if (budgets != null) {
                for (var b : budgets) {
                    if (b != null) {
                        allocated += b.getMontantAlloue();
                        used += b.getMontantUtilise();
                    }
                }
                remaining = allocated - used;
            }

            var depenses = depenseService.getAllDepenses();
            expenseCount = depenses == null ? 0 : depenses.size();
        } catch (Exception e) {
            System.err.println("[CHATBOT] Error gathering stats: " + e.getMessage());
        }

        final float finalAllocated = allocated;
        final float finalUsed = used;
        final float finalRemaining = remaining;
        final int finalExpenseCount = expenseCount;

        new Thread(() -> {
            try {
                String response = GroqChatbotService.chat(
                    userMessage,
                    "Toutes les équipes",
                    finalAllocated,
                    finalUsed,
                    finalRemaining,
                    finalExpenseCount
                );

                javafx.application.Platform.runLater(() -> {
                    appendToChatDisplay("Assistant IA: " + response);
                    chatSendBtn.setDisable(false);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    appendToChatDisplay("Assistant IA: Désolé, une erreur est survenue. " + (ex.getMessage() != null ? ex.getMessage() : ""));
                    chatSendBtn.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void onClearChat(ActionEvent event) {
        if (chatDisplayArea != null) chatDisplayArea.clear();
    }

    @FXML
    private void onShowExamples(ActionEvent event) {
        String examples = "Exemples:\n- Comment réduire les coûts de voyage ?\n- Quel poste consomme le plus de budget ?\n- Ai-je un risque de dépassement ce mois-ci ?";
        appendToChatDisplay(examples);
    }

    private void appendToChatDisplay(String text) {
        if (chatDisplayArea == null) return;
        String current = chatDisplayArea.getText();
        String sep = current == null || current.isEmpty() ? "" : "\n\n";
        chatDisplayArea.appendText(sep + text);
        chatDisplayArea.setScrollTop(Double.MAX_VALUE);
    }
}
