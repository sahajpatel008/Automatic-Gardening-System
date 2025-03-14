package com.example.gridproject;

import GardenEntities.Plant;
import GardenEntities.Plant1;
import Handler.GardenHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    private static GardenHandler gardenHandler;

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

    @Override
    public void stop() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/plant_grid.fxml"));
        try {
            loader.load();
            PlantGridController controller = loader.getController();
            controller.printGridDetails();
        } catch (Exception e) {
            System.err.println("Failed to load controller: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        gardenHandler = new GardenHandler(new TextArea());
//        gardenHandler.addPlant(0, new Plant1(0));
//        runFor24Hours();
        launch();
    }

    public static void runFor24Hours() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable taskWrapper = () -> {
            try {
                // write here
//                task();
                gardenHandler.iteration();
                for (Plant plant : gardenHandler.getGrid().values()){
                    plant.displayStatus();
                }
            } catch (Exception e) {
                System.err.println("Error in task execution: " + e.getMessage());
            }
        };

        scheduler.scheduleAtFixedRate(taskWrapper, 0, 1, TimeUnit.SECONDS);

        // Schedule shutdown after 24 hours
        scheduler.schedule(() -> {
            System.out.println("24-hour period complete. Shutting down.");
            scheduler.shutdown();
        }, 24, TimeUnit.HOURS);
    }
}