package edu.connexion3a36.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPersonne.fxml"));
        Parent root = loader.load();
        Scene sc =new Scene(root, 1440, 960);
        sc.getStylesheets().add(getClass().getResource("/styles/esports.css").toExternalForm());
        primaryStage.setTitle("EsportDev Arena");
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(820);
        primaryStage.setScene(sc);
        primaryStage.show();


    }
}
