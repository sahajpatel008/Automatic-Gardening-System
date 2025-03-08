package com.example.gridproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/plant_grid.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/plant_grid.fxml"));
        System.out.println("Its working!??");
        if (fxmlLoader.getLocation() == null) {
            System.err.println("FXML file not found! Check path.");
        }
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Plant Grid!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}