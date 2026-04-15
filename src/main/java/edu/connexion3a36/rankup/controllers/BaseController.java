package edu.connexion3a36.rankup.controllers;

import edu.connexion3a36.rankup.app.RankUpApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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
        } catch (IOException | RuntimeException e) {
            System.err.println("Cannot load content: " + fxmlPath);
            e.printStackTrace();
            showNavigationError(fxmlPath, e);
        }
    }

    private void showNavigationError(String fxmlPath, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Navigation Error");
        alert.setHeaderText("Unable to open page");
        alert.setContentText("Could not load: " + fxmlPath + "\n" + e.getMessage());
        alert.show();
    }
}
