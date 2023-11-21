package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.controller.ResultPanel;
import org.example.view.EntityPanel;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tour APP");


        BorderPane root = new BorderPane();

        ResultPanel resultPanel = new ResultPanel();
        root.setLeft(resultPanel);

        EntityPanel entityPanel = new EntityPanel(resultPanel);
        root.setRight(entityPanel);

        Scene scene = new Scene(root, 1920, 1080);

        primaryStage.setScene(scene);

        primaryStage.show();
    }
}