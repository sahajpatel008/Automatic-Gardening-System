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
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.util.logging.*;

import java.awt.*;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(PlantGridController.class.getName());
    private final int GRID_SIZE = 4;
    private final Button[][] buttons = new Button[GRID_SIZE][GRID_SIZE];
    private Image selectedPlantImage = null;
    private Plant selectedPlantObject = null;
    private final Map<String, Plant> plantGridMap = new HashMap<>();
    private final Map<String, ImageView[]> insectGridMap = new HashMap<>();

    public void initialize() {
        setupGrid();
        setupPlantSelection();

        loadImage(plantImage1, "images/plant.jpg");
        loadImage(plantImage2, "images/plant2.jpg");
        loadImage(plantImage3, "images/plant3.jpg");

        setupLogger();
    }

    private void setupLogger() {
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler); // Remove default console handler
        }

        Handler textAreaHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                Platform.runLater(() -> {
                    String message = record.getLevel() + ": " + record.getMessage() + "\n";
                    Text text = new Text(message);

                    if (record.getLevel().equals(Level.SEVERE)) {
                        text.setStyle("-fx-fill: red; -fx-font-weight: bold;");
                    } else {
                        text.setStyle("-fx-fill: black;");
                    }

                    logTextArea.appendText(text.getText());
                });
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };

        textAreaHandler.setLevel(Level.ALL);
        rootLogger.addHandler(textAreaHandler);
        logger.setLevel(Level.ALL);
    }

    private void updateLog(String message) {
        logTextArea.appendText(message + "\n");
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

                // Create VBox for insects (right side)
                VBox insectBox = new VBox();
                insectBox.setSpacing(2);
                insectBox.setStyle("-fx-alignment: center-right;");
                insectBox.setMaxWidth(50);

                ImageView[] insectViews = new ImageView[4];
                for (int i = 0; i < 4; i++) {
                    insectViews[i] = new ImageView();
                    insectViews[i].setFitWidth(24); // Keep them small
                    insectViews[i].setFitHeight(24);
                    insectViews[i].setVisible(false);
                    insectBox.getChildren().add(insectViews[i]);
                }

                // Store insect views in the map
                insectGridMap.put(finalRow + "," + finalCol, insectViews);

                tileBox.getChildren().addAll(imageView, tileButton, healthBarContainer, waterBarContainer, nutrientBarContainer);
                HBox container = new HBox(tileBox, insectBox);
                tileContainer.getChildren().add(container);
                gridPane.add(tileContainer, col, row);

                buttons[row][col] = tileButton;


            }
        }

        this.insectAttack("pest1", 0, 2);
        this.insectAttack("pest2", 0, 2);
        this.insectAttack("pest3", 0, 2);
        this.insectAttack("pest4", 0, 2);


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
            logger.log(Level.SEVERE, "Error: Couldn't retrieve image for selected plant: {0}", imagePath);
//            updateLog("Image not found in selectPlant: " + imagePath);
        } else {
            selectedPlantImage = new Image(imageUrl.toExternalForm());
            selectedPlantObject = plantObject;
            System.out.println("Selected plant: " + plantObject.getClass().getSimpleName());
            logger.log(Level.INFO, "Selected plant: {0}", plantObject.getClass().getSimpleName());
//            updateLog("Selected plant: {0}" + plantObject.getClass().getSimpleName());
        }

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
            logger.log(Level.SEVERE, "Error: Image not found: {0}", imagePath);
//            updateLog("Image not found: " + imagePath);
        } else {
            imageView.setImage(new Image(imageUrl.toExternalForm()));
            System.out.println("Loaded: " + imagePath);
            logger.log(Level.INFO, "Loaded: {0}", imagePath);
//            updateLog("Loaded: " + imagePath);
        }
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
            logger.log(Level.INFO, "Planted {0} at ({1})", new Object[]{selectedPlantObject.getClass().getSimpleName(), key});
//            updateLog("Planted " + selectedPlantObject.getClass().getSimpleName() + " at (" + key + ")");
        } else {
            System.out.println("No plant selected!");
            logger.log(Level.WARNING, "No plant selected!");
//            updateLog("No plant selected!");
        }
    }

    private void updateBar(Rectangle bar, Label label, int value) {
        double percentage = value / 100.0;
        bar.setWidth(50 * percentage);
        label.setText(value + "%");
        bar.setVisible(true);
        label.setVisible(true);
    }

    public void insectAttack(String insectType, int row, int col) {
        String key = row + "," + col;
        ImageView[] insectViews = insectGridMap.get(key);

        if (insectViews != null) {
            for (ImageView insectView : insectViews) {
                if (insectView.getImage() == null) {
                    URL imageUrl = getClass().getResource("/images/" + insectType + ".png");
                    if (imageUrl != null) {
                        insectView.setImage(new Image(imageUrl.toExternalForm()));
                        insectView.setVisible(true);

                        // Log warning in RED when an insect is added
                        logger.log(Level.WARNING, "WARNING: Insect attack: " + insectType + " at (" + row + "," + col);
//                        updateLog("\n\u001B[31mWARNING: Insect added: " + insectType + " at (" + row + "," + col + ")\u001B[0m\n");

                        return;
                    } else {
                        logger.warning("Error: Insect image not found: " + insectType);
//                        updateLog("Insect image not found: " + insectType);
                    }
                }
            }
            logger.info("No available slot for insect at (" + row + "," + col + ")");
//            updateLog("No available slot for insect at (" + row + "," + col + ")");
        }

    }

}
