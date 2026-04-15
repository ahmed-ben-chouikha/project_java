package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class BaseController {

    @FXML
    private StackPane contentPane;

    @FXML
    void initialize() {
        RankUpApp.registerBaseController(this);
        loadCenter("/views/dashboard/home.fxml");
    }

    public void loadCenter(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load content: " + fxmlPath, e);
        }
    }
}

