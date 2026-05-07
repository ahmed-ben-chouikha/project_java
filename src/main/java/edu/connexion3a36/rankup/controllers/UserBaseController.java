package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class UserBaseController implements ContentController {

    @FXML
    private StackPane contentPane;

    @FXML
    private StackPane rootStack;

    @FXML
    void initialize() {
        RankUpApp.registerContentController(this);
        loadCenter("/views/dashboard/user-dashboard.fxml");
        addChatbotButton();
    }

    private void addChatbotButton() {
        try {
            ChatbotFloatingButton chatbotBtn = new ChatbotFloatingButton();
            chatbotBtn.setMaxSize(60, 60);
            chatbotBtn.setMinSize(60, 60);
            StackPane.setAlignment(chatbotBtn, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(chatbotBtn, new Insets(0, 24, 24, 0));
            rootStack.getChildren().add(chatbotBtn);
        } catch (Exception e) {
            System.err.println("Could not add chatbot button: " + e.getMessage());
        }
    }

    @Override
    public void loadCenter(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load content: " + fxmlPath, e);
        }
    }
}