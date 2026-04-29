package edu.connexion3a36.rankup.controllers.chatbot;

import edu.connexion3a36.rankup.services.ChatbotService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;

public class ChatbotController {

    @FXML private VBox messagesBox;
    @FXML private TextField inputField;
    @FXML private Button sendButton;

    private final ChatbotService chatbotService = new ChatbotService();

    @FXML
    void initialize() {
        // initial greeting
        appendBotMessage("Hello — I am the Punition Advisor. Tell me about the incident (offense type, repeat history, evidence) and I will suggest a suitable punishment.");
    }

    @FXML
    void onSend() {
        String text = inputField.getText();
        if (text == null || text.isBlank()) return;
        appendUserMessage(text);
        inputField.clear();
        sendButton.setDisable(true);

        CompletableFuture.supplyAsync(() -> chatbotService.ask(text))
                .thenAccept(response -> Platform.runLater(() -> {
                    appendBotMessage(response);
                    sendButton.setDisable(false);
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        appendBotMessage("Error: " + ex.getMessage());
                        sendButton.setDisable(false);
                    });
                    return null;
                });
    }

    private void appendUserMessage(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().addAll("badge");
        messagesBox.getChildren().add(lbl);
    }

    private void appendBotMessage(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().addAll("muted");
        messagesBox.getChildren().add(lbl);
    }
}
