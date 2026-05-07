package edu.connexion3a36.rankup.controllers;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.io.IOException;

/**
 * Chatbot floating button component
 * Can be added to any scene to provide quick access to the chatbot
 */
public class ChatbotFloatingButton extends StackPane {

    private Button chatbotButton;
    private Popup chatbotPopup;
    private boolean isOpen = false;

    public ChatbotFloatingButton() {
        setupUI();
    }

    /**
     * Setup the floating chatbot button
     */
    private void setupUI() {
        // Create floating button
        chatbotButton = new Button("💬");
        chatbotButton.setStyle(
                "-fx-font-size: 24; " +
                "-fx-padding: 12; " +
                "-fx-background-color: #007bff; " +
                "-fx-text-fill: white; " +
                "-fx-border-radius: 50; " +
                "-fx-background-radius: 50; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,123,255,0.3), 10, 0, 0, 5);"
        );

        chatbotButton.setOnMouseEntered(e -> {
            chatbotButton.setStyle(
                    "-fx-font-size: 24; " +
                    "-fx-padding: 12; " +
                    "-fx-background-color: #0056b3; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-radius: 50; " +
                    "-fx-background-radius: 50; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,86,179,0.5), 15, 0, 0, 8);"
            );
        });

        chatbotButton.setOnMouseExited(e -> {
            chatbotButton.setStyle(
                    "-fx-font-size: 24; " +
                    "-fx-padding: 12; " +
                    "-fx-background-color: #007bff; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-radius: 50; " +
                    "-fx-background-radius: 50; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,123,255,0.3), 10, 0, 0, 5);"
            );
        });

        chatbotButton.setOnAction(event -> toggleChatbot());

        this.getChildren().add(chatbotButton);
    }

    /**
     * Toggle chatbot popup visibility
     */
    private void toggleChatbot() {
        if (isOpen) {
            closeChatbot();
        } else {
            openChatbot();
        }
    }

    /**
     * Open chatbot popup
     */
    private void openChatbot() {
        try {
            if (chatbotPopup == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/common/chatbot.fxml"));
                Parent chatbotUI = loader.load();

                chatbotPopup = new Popup();
                chatbotPopup.getContent().add(chatbotUI);
                chatbotPopup.setWidth(400);
                chatbotPopup.setHeight(600);
                chatbotPopup.setAutoFix(true);
                chatbotPopup.setAutoHide(false);

                // Close chatbot when popup loses focus
                chatbotPopup.setOnHidden(e -> {
                    isOpen = false;
                    chatbotButton.setText("💬");
                });
            }

            if (chatbotPopup != null && !chatbotPopup.isShowing()) {
                // Position popup near the button
                Bounds bounds = chatbotButton.localToScreen(chatbotButton.getBoundsInLocal());
                chatbotPopup.show(chatbotButton.getScene().getWindow(),
                        bounds.getCenterX() - 200,
                        bounds.getCenterY() - 300);
                isOpen = true;
                chatbotButton.setText("✕");
            }
        } catch (IOException e) {
            System.err.println("Failed to load chatbot UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Close chatbot popup
     */
    private void closeChatbot() {
        if (chatbotPopup != null && chatbotPopup.isShowing()) {
            chatbotPopup.hide();
            isOpen = false;
            chatbotButton.setText("💬");
        }
    }

    /**
     * Check if chatbot is open
     */
    public boolean isChatbotOpen() {
        return isOpen;
    }
}

