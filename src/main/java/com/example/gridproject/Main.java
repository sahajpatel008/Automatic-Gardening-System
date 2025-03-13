package com.example.gridproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
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
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setTitle("Plant Grid!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}