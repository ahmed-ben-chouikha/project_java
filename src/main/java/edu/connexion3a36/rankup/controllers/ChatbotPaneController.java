package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.services.BanRecommendationChatbot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Ban Recommendation Chatbot pane.
 * Allows admins to discuss and decide on suitable bans for various eSports violations.
 * Now with improved conversation history and real AI integration!
 */
public class ChatbotPaneController {

    @FXML private TextField inputField;
    @FXML private VBox messagesBox;

    private static final String BOT_PREFIX = "🤖 Assistant";
    private static final String ADMIN_PREFIX = "👨‍💼 You";
    
    // Conversation history for better context
    private List<String> conversationHistory = new ArrayList<>();

    @FXML
    void initialize() {
        // Display welcome message
        displaySystemMessage("Hello! 👋 I'm your punishment discussion assistant.\n\n" +
                "I can help you:\n" +
                "• Discuss violations and appropriate punishments\n" +
                "• Explore different punishment options\n" +
                "• Understand severity levels\n" +
                "• Review guidelines\n\n" +
                "Ask me about any violation type (cheating, cussing, toxicity, etc.) or type 'help' to see all options.");
    }

    @FXML
    void onSendMessage(ActionEvent event) {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) {
            return;
        }

        // Display user message
        displayAdminMessage(userInput);
        inputField.clear();

        // Get chatbot response
        String response = BanRecommendationChatbot.chat(userInput);
        displaySystemMessage(response);
    }

    @FXML
    void onHelpClicked(ActionEvent event) {
        String helpText = BanRecommendationChatbot.chat("help");
        displayAdminMessage("Show all violations");
        displaySystemMessage(helpText);
    }

    @FXML
    void onQuickViolation(ActionEvent event) {
        if (event.getSource() instanceof Button button) {
            String violationType = button.getText();
            String query = "What about " + violationType + "?";
            displayAdminMessage(query);
            String response = BanRecommendationChatbot.chat(violationType);
            displaySystemMessage(response);
        }
    }

    /**
     * Display a message from the system/bot.
     */
    private void displaySystemMessage(String message) {
        VBox messageCard = createMessageCard(message, BOT_PREFIX, "chatbot-system-message");
        messagesBox.getChildren().add(messageCard);
        scrollToBottom();
    }

    /**
     * Display a message from the admin/user.
     */
    private void displayAdminMessage(String message) {
        VBox messageCard = createMessageCard(message, ADMIN_PREFIX, "chatbot-admin-message");
        messagesBox.getChildren().add(messageCard);
        scrollToBottom();
    }

    /**
     * Create a styled message card with better contrast and readable colors.
     */
    private VBox createMessageCard(String message, String prefix, String styleClass) {
        VBox card = new VBox(4);
        card.getStyleClass().add(styleClass);
        card.setPadding(new Insets(10, 12, 10, 12));
        card.setStyle("-fx-border-radius: 6; -fx-background-radius: 6;");

        // Header with prefix
        Label headerLabel = new Label(prefix);
        headerLabel.getStyleClass().add("chatbot-message-header");
        // Use lighter colors for better readability
        headerLabel.setStyle("-fx-text-fill: " + (styleClass.contains("system") ? "#64d8ff" : "#e0aaff") + "; -fx-font-weight: bold; -fx-font-size: 11px;");

        // Message content with text wrapping and READABLE colors
        Label contentLabel = new Label(message);
        contentLabel.setWrapText(true);
        contentLabel.getStyleClass().add("chatbot-message-content");
        // Use light colors that contrast with dark background
        if (styleClass.contains("system")) {
            contentLabel.setStyle("-fx-text-fill: #e0e7ff; -fx-font-size: 13px; -fx-line-spacing: 2;");
        } else {
            contentLabel.setStyle("-fx-text-fill: #f3f4f6; -fx-font-size: 13px; -fx-line-spacing: 2;");
        }

        card.getChildren().addAll(headerLabel, contentLabel);
        return card;
    }

    /**
     * Scroll to the bottom of the messages area to show the latest message.
     */
    private void scrollToBottom() {
        messagesBox.layout();
        // Scroll to bottom
        if (messagesBox.getParent() instanceof javafx.scene.control.ScrollPane scrollPane) {
            scrollPane.setVvalue(1.0);
        }
    }
}


