package edu.connexion3a36.rankup.app;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        RankUpApp.init(primaryStage);
        RankUpApp.showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

