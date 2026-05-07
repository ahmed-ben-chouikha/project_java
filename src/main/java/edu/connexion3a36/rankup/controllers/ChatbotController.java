package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.services.BudgetService;
import edu.connexion3a36.services.DepenseService;
import edu.connexion3a36.services.GroqChatbotService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatbotController {

    @FXML private ScrollPane chatScrollPane;
    @FXML private VBox messagesVBox;
    @FXML private VBox suggestionsBox;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;
    @FXML private Button minimizeButton;
    @FXML private Button closeButton;
    @FXML private Label chatTitle;

    private DepenseService depenseService;
    private BudgetService budgetService;

    private static final String[] SUGGESTIONS = {
            "What is my budget status?",
            "Show recent expenses",
            "How to reduce costs?",
            "Am I at risk of overspending?"
    };

    @FXML
    private void initialize() {
        depenseService = new DepenseService();
        budgetService = new BudgetService();
        setupSuggestions();
        appendBotMessage("Hello! I'm your RankUp AI assistant. How can I help you today?");
    }

    private void setupSuggestions() {
        suggestionsBox.getChildren().clear();
        for (String suggestion : SUGGESTIONS) {
            Button btn = new Button(suggestion);
            btn.setStyle("-fx-font-size: 11; -fx-padding: 4 8; -fx-cursor: hand; -fx-background-color: #e9f0ff; -fx-border-color: #007bff; -fx-border-radius: 12; -fx-background-radius: 12;");
            btn.setOnAction(e -> {
                messageInput.setText(suggestion);
                handleSendMessage();
            });
            suggestionsBox.getChildren().add(btn);
        }
    }

    @FXML
    private void handleSendMessage() {
        String userMessage = messageInput.getText();
        if (userMessage == null || userMessage.trim().isEmpty()) return;

        appendUserMessage(userMessage.trim());
        messageInput.clear();
        sendButton.setDisable(true);

        float allocated = 0f, used = 0f, remaining = 0f;
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

        final float fAllocated = allocated, fUsed = used, fRemaining = remaining;
        final int fExpenseCount = expenseCount;

        new Thread(() -> {
            try {
                String response = GroqChatbotService.chat(
                        userMessage, "All Teams", fAllocated, fUsed, fRemaining, fExpenseCount
                );
                Platform.runLater(() -> {
                    appendBotMessage(response);
                    sendButton.setDisable(false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    appendBotMessage("Sorry, an error occurred: " + (ex.getMessage() != null ? ex.getMessage() : "Unknown error"));
                    sendButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void minimizeChatbot() {
        if (messagesVBox.isVisible()) {
            messagesVBox.setVisible(false);
            messagesVBox.setManaged(false);
            suggestionsBox.setVisible(false);
            suggestionsBox.setManaged(false);
        } else {
            messagesVBox.setVisible(true);
            messagesVBox.setManaged(true);
            suggestionsBox.setVisible(true);
            suggestionsBox.setManaged(true);
        }
    }

    @FXML
    private void closeChatbot() {
        if (closeButton.getScene() != null && closeButton.getScene().getWindow() != null) {
            closeButton.getScene().getWindow().hide();
        }
    }

    private void appendUserMessage(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 8 12; -fx-background-radius: 12;");
        label.setMaxWidth(280);

        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPadding(new Insets(2, 0, 2, 0));
        messagesVBox.getChildren().add(hbox);
        scrollToBottom();
    }

    private void appendBotMessage(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle("-fx-background-color: #f1f1f1; -fx-text-fill: #333; -fx-padding: 8 12; -fx-background-radius: 12;");
        label.setMaxWidth(280);

        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(2, 0, 2, 0));
        messagesVBox.getChildren().add(hbox);
        scrollToBottom();
    }

    private void scrollToBottom() {
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }
}