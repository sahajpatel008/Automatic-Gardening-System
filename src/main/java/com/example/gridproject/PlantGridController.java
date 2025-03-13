package com.example.gridproject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// Parent Plant class with health, water, and nutrients properties



class Plant {
    int health;
    int waterLevel;
    int nutrientsLevel;

    public Plant() {
        this.health = 100;
        this.waterLevel = 100;
        this.nutrientsLevel = 100;
    }

    public Plant(int h, int w, int n){
        this.health = h;
        this.waterLevel = w;
        this.nutrientsLevel = n;
    }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public int getWaterLevel() { return waterLevel; }
    public void setWaterLevel(int waterLevel) { this.waterLevel = waterLevel; }
    public int getNutrientsLevel() { return nutrientsLevel; }
    public void setNutrientsLevel(int nutrientsLevel) { this.nutrientsLevel = nutrientsLevel; }
}

// Child classes inheriting from Plant
class PlantClass1 extends Plant {
    PlantClass1(int h, int w, int n){
        super(h, w, n);
    }
}
class PlantClass2 extends Plant {
    PlantClass2(int h, int w, int n){
        super(h, w, n);
    }
}
class PlantClass3 extends Plant {
    PlantClass3(int h, int w, int n){
        super(h, w, n);
    }
}

public class PlantGridController {
    @FXML
    private GridPane gridPane;
    @FXML
    private Button plantType1, plantType2, plantType3;
    @FXML
    private ImageView plantImage1, plantImage2, plantImage3;
    @FXML
    private TextArea logTextArea;

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();
    final int screenWidth = (int) screenSize.getWidth();
    final int screenHeight = (int) screenSize.getHeight();

    private final Logger logger = new Logger();
    private final int GRID_SIZE = 4;
    private final Button[][] buttons = new Button[GRID_SIZE][GRID_SIZE];
    private Image selectedPlantImage = null;
    private Plant selectedPlantObject = null;
    private final Map<String, Plant> plantGridMap = new HashMap<>();

    public void initialize() {
        setupGrid();
        setupPlantSelection();

        loadImage(plantImage1, "images/plant.jpg");
        loadImage(plantImage2, "images/plant2.jpg");
        loadImage(plantImage3, "images/plant3.jpg");

        updateLog();
    }

    private void updateLog() {
        logTextArea.setText(logger.getLog());
    }

    private void setupGrid() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                final int finalRow = row;
                final int finalCol = col;

                StackPane tileContainer = new StackPane();
                tileContainer.setStyle("-fx-background-color: lightgray; -fx-padding: 10px; -fx-border-color: black;");
                double boxWidth = 0.08*screenWidth;
                tileContainer.setMinSize(0.08*screenWidth,0.08*screenWidth);
                tileContainer.setMaxSize(0.08*screenWidth,0.08*screenWidth);

                VBox tileBox = new VBox();
                tileBox.setSpacing(2);
                tileBox.setStyle("-fx-alignment: center;");

                ImageView imageView = new ImageView();
                imageView.setFitWidth(0.25*tileContainer.getMinHeight());
                imageView.setFitHeight(0.25*tileContainer.getMinHeight());

                // Health Bar
                HBox healthBarContainer = createBarContainer("GREEN");
                Rectangle healthBar = (Rectangle) healthBarContainer.getChildren().get(0);
                Label healthLabel = (Label) healthBarContainer.getChildren().get(1);
                healthBarContainer.setVisible(false);

                // Water Level Bar
                HBox waterBarContainer = createBarContainer("LIGHTBLUE");
                Rectangle waterBar = (Rectangle) waterBarContainer.getChildren().get(0);
                Label waterLabel = (Label) waterBarContainer.getChildren().get(1);
                waterBarContainer.setVisible(false);

                // Nutrient Level Bar
                HBox nutrientBarContainer = createBarContainer("BROWN");
                Rectangle nutrientBar = (Rectangle) nutrientBarContainer.getChildren().get(0);
                Label nutrientLabel = (Label) nutrientBarContainer.getChildren().get(1);
                nutrientBarContainer.setVisible(false);

                Button tileButton = new Button();
                tileButton.setMinSize(100, 100);
                tileButton.setStyle("-fx-background-color: transparent;");
                tileButton.setGraphic(imageView);

                tileButton.setOnAction(event -> plantSelectedPlant(finalRow, finalCol, imageView, healthBarContainer, healthBar, healthLabel, waterBarContainer, waterBar, waterLabel, nutrientBarContainer, nutrientBar, nutrientLabel));

                tileBox.getChildren().addAll(imageView, tileButton, healthBarContainer, waterBarContainer, nutrientBarContainer);
                tileContainer.getChildren().add(tileBox);
                gridPane.add(tileContainer, col, row);

                buttons[row][col] = tileButton;
            }
        }
    }

    // Setup plant selection buttons
    private void setupPlantSelection() {
        plantType1.setOnAction(event -> selectPlant("/images/plant.jpg", new PlantClass1(100, 100, 100)));
        plantType2.setOnAction(event -> selectPlant("/images/plant2.jpg", new PlantClass2(100, 50, 75)));
        plantType3.setOnAction(event -> selectPlant("/images/plant3.jpg", new PlantClass3(50, 50, 50)));
    }

    private void selectPlant(String imagePath, Plant plantObject) {
        URL imageUrl = getClass().getResource(imagePath);
        if (imageUrl == null) {
            System.err.println("Image not found in selectPlant: " + imagePath);
            logger.addLog("Error: Couldn't retrieve image for selected plant: " + imagePath);
        } else {
            selectedPlantImage = new Image(imageUrl.toExternalForm());
            selectedPlantObject = plantObject;
            System.out.println("Selected plant: " + plantObject.getClass().getSimpleName());
            logger.addLog("Selected plant: " + plantObject.getClass().getSimpleName());
        }
        updateLog();
    }


    private HBox createBarContainer(String color) {
        HBox barContainer = new HBox();
        barContainer.setMinWidth(80);
        barContainer.setMaxWidth(80);
        barContainer.setSpacing(2); // Reduce spacing

        Rectangle bar = new Rectangle(50, 5, Color.valueOf(color));
        Label label = new Label("100%");

        // Remove extra padding/margin from label
        label.setMinWidth(25);
        label.setStyle("-fx-text-fill: black; -fx-font-size: 10px; -fx-padding: 0; -fx-margin: 0;");

        barContainer.getChildren().addAll(bar, label);
        return barContainer;
    }


    private void loadImage(ImageView imageView, String imagePath) {
        URL imageUrl = getClass().getResource("/" + imagePath);
        if (imageUrl == null) {
            System.err.println("Image not found: " + imagePath);
            logger.addLog("Error: Image not found: " + imagePath);
        } else {
            imageView.setImage(new Image(imageUrl.toExternalForm()));
            System.out.println("Loaded: " + imagePath);
        }
        updateLog();
    }

    private void plantSelectedPlant(int row, int col, ImageView imageView, HBox healthBarContainer, Rectangle healthBar, Label healthLabel, HBox waterBarContainer, Rectangle waterBar, Label waterLabel, HBox nutrientBarContainer, Rectangle nutrientBar, Label nutrientLabel) {
        if (selectedPlantImage != null && selectedPlantObject != null) {
            imageView.setImage(selectedPlantImage);
            String key = row + "," + col;
            plantGridMap.put(key, selectedPlantObject);

            updateBar(healthBar, healthLabel, selectedPlantObject.getHealth());
            updateBar(waterBar, waterLabel, selectedPlantObject.getWaterLevel());
            updateBar(nutrientBar, nutrientLabel, selectedPlantObject.getNutrientsLevel());

            healthBarContainer.setVisible(true);
            waterBarContainer.setVisible(true);
            nutrientBarContainer.setVisible(true);
            System.out.println("Planted " + selectedPlantObject.getClass().getSimpleName() + " at (" + key + ")");
            logger.addLog("Planted " + selectedPlantObject.getClass().getSimpleName() + " at (" + key + ")");
        } else {
            System.out.println("No plant selected!");
            logger.addLog("No plant selected!");
        }
        updateLog();
    }

    private void updateBar(Rectangle bar, Label label, int value) {
        double percentage = value / 100.0;
        bar.setWidth(50 * percentage);
        label.setText(value + "%");
        bar.setVisible(true);
        label.setVisible(true);
    }
}
