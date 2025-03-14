package com.example.gridproject;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

import java.util.*;
import java.util.List;
import java.util.logging.*;

import java.awt.*;
import java.net.URL;
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

class InsectViewInfo {
    ImageView insectView;
    String insectType;

    public InsectViewInfo(ImageView insectView, String insectType) {
        this.insectView = insectView;
        this.insectType = insectType;
    }

    public ImageView getInsectView() {
        return insectView;
    }

    public String getInsectType() {
        return insectType;
    }

    public void setInsectType(String insectType) {
        this.insectType = insectType;
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
    private Button pesticideType1, pesticideType2, pesticideType3, pesticideType4;
    @FXML
    private ImageView pesticideImage1, pesticideImage2, pesticideImage3, pesticideImage4;
    @FXML
    private Button waterButton;
    @FXML
    private ImageView waterDrop;
    @FXML
    private Button fertilizerButton;
    @FXML
    private ImageView manure;
    @FXML
    private ComboBox<String> pesticideComboBox;
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
    private final Map<String, List<InsectViewInfo>> insectGridMap = new HashMap<>();
    private String selectedPesticide = "";
    private final String[] pesticideImages = new String[4];
    private boolean selectedWater = false;
    private boolean selectedFertilizer = false;

    public void initialize() {
        setupGrid();
        setupPlantSelection();
        pesticideComboBox.getItems().addAll("pest1", "pest2", "pest3", "pest4");
        setupPesticideSelection();
        waterTrigger();
        fertilizerTrigger();

        loadImage(plantImage1, "images/plant.jpg");
        loadImage(plantImage2, "images/plant2.jpg");
        loadImage(plantImage3, "images/plant3.jpg");
        loadImage(waterDrop, "images/waterdrop.jpg");
        loadImage(manure, "images/fertilizer.png");

//        pesticideImages[0] = "images/pesticide.png";
//        pesticideImages[1] = "images/pesticide1.png";
//        pesticideImages[2] = "images/pesticide2.png";
//        pesticideImages[3] = "images/pesticide3.png";
//
//        loadImage(pesticideImage1, pesticideImages[0]);
//        loadImage(pesticideImage2, pesticideImages[1]);
//        loadImage(pesticideImage3, pesticideImages[2]);
//        loadImage(pesticideImage4, pesticideImages[3]);

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
                tileContainer.setMinSize(0.11*screenWidth,0.11*screenWidth);
                tileContainer.setMaxSize(0.11*screenWidth,0.11*screenWidth);

                VBox tileBox = new VBox();
                tileBox.setSpacing(2);
                tileBox.setStyle("-fx-alignment: center;");

                ImageView imageView = new ImageView();
                imageView.setFitWidth(0.25*tileContainer.getMinHeight());
                imageView.setFitHeight(0.25*tileContainer.getMinHeight());

                // Health Bar
                HBox healthBarContainer = createBarContainer("GREEN", "♥:");
                Rectangle healthBar = (Rectangle) healthBarContainer.getChildren().get(1);
                Label healthLabel = (Label) healthBarContainer.getChildren().get(2);
                healthBarContainer.setVisible(false);

                // Water Level Bar
                HBox waterBarContainer = createBarContainer("LIGHTBLUE", "💧:");
                Rectangle waterBar = (Rectangle) waterBarContainer.getChildren().get(1);
                Label waterLabel = (Label) waterBarContainer.getChildren().get(2);
                waterBarContainer.setVisible(false);

                // Nutrient Level Bar
                HBox nutrientBarContainer = createBarContainer("BROWN", "🧬:");
                Rectangle nutrientBar = (Rectangle) nutrientBarContainer.getChildren().get(1);
                Label nutrientLabel = (Label) nutrientBarContainer.getChildren().get(2);
                nutrientBarContainer.setVisible(false);

                Button tileButton = new Button();
                tileButton.setMinSize(180, 180);
                tileButton.setStyle("-fx-background-color: LIGHTBLUE;");
                tileButton.setGraphic(imageView);

                tileButton.setOnAction(event -> plantSelectedPlant(finalRow, finalCol, imageView, healthBarContainer, healthBar, healthLabel, waterBarContainer, waterBar, waterLabel, nutrientBarContainer, nutrientBar, nutrientLabel));
                tileButton.setOnMouseClicked(event -> functionCumulative(finalRow, finalCol));

                // Create VBox for insects (right side)
                VBox insectBox = new VBox();
                insectBox.setSpacing(0);
                insectBox.setAlignment(Pos.CENTER_RIGHT);
                insectBox.setMaxWidth(50);

                // Shift more towards the right using translateX
                insectBox.setTranslateX(20); // Adjust this value if needed

                List<InsectViewInfo> insectList = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    ImageView insectView = new ImageView();
                    insectView.setFitWidth(24); // Keep them small
                    insectView.setFitHeight(24);
                    insectView.setVisible(false);

                    InsectViewInfo insectViewInfo = new InsectViewInfo(insectView, null); // Insect type initially null
                    insectList.add(insectViewInfo);

                    insectBox.getChildren().add(insectView);
                }

                insectGridMap.put(finalRow + "," + finalCol, insectList);

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

    private void functionCumulative(int row, int col) {
        sprayPesticide(row, col);
        addFertilizer(row, col);
        dripIrrigation(row, col);
    }

    // Setup plant selection buttons
    private void setupPlantSelection() {
        plantType1.setOnAction(event -> selectPlant("/images/plant.jpg", new PlantClass1(100, 100, 100)));
        plantType2.setOnAction(event -> selectPlant("/images/plant2.jpg", new PlantClass2(100, 50, 75)));
        plantType3.setOnAction(event -> selectPlant("/images/plant3.jpg", new PlantClass3(50, 50, 50)));
    }

    private void setupPesticideSelection() {
        pesticideComboBox.setOnAction(event -> {
            String selectedPesticide = pesticideComboBox.getValue();
            if (!Objects.equals(selectedPesticide, "")) {
                this.selectedPesticide = selectedPesticide;
                logger.log(Level.INFO, "Selected " + selectedPesticide);
            }
        });
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
            logger.log(Level.INFO, "Selected plant: " + plantObject.getClass().getSimpleName());
//            updateLog("Selected plant: {0}" + plantObject.getClass().getSimpleName());
        }

    }

    private void sprayPesticide(int r, int c){
        String key = r + "," + c;
        List<InsectViewInfo> insectViews = insectGridMap.get(key);
        selectedPesticide = pesticideComboBox.getValue();
        if(selectedPesticide == null){
            return;
        }
        if (insectViews != null) {
            for (InsectViewInfo insectViewInfo : insectViews) {
                // Assuming InsectViewInfo has an ImageView object and an insectType
                ImageView insectView = insectViewInfo.getInsectView(); // Get the ImageView object
                String insectType = insectViewInfo.getInsectType(); // Get the insect type (if needed)

                // Hide the insect by setting the ImageView's visibility to false
                if(selectedPesticide.equals(insectType)) {
                    // here, add backend object
                    insectView.setVisible(false);
                }

                // Log the pesticide spray action
                logger.log(Level.INFO, "Sprayed pesticide on "+ insectType+" at ("+ (r+1) +"," + (c+1) + ")");
            }
        } else {
            logger.info("No insects to spray at (" + (r+1) + "," + (c+1) + ")");
        }
    }

    private void fertilizerTrigger(){
        fertilizerButton.setOnMouseClicked(event -> {
            selectedFertilizer = !selectedFertilizer;
            logger.log(Level.INFO, "Fertilizer Nozzle: " + selectedFertilizer);
        });
    }

    private void addFertilizer(int r, int c){
        String key = r + "," + c;
        Plant selectedPlant = plantGridMap.get(key);

        if(selectedFertilizer) {
            if (selectedPlant == null) {
                logger.log(Level.INFO, "No plant at (" + (r+1) + "," + (c+1) + ")");
            } else {
                // update backend here
                selectedPlant.setNutrientsLevel(100); // Set nutrient level to 100

                logger.log(Level.INFO, "Fertilizer applied at (" + (r + 1) + "," + (c + 1) + ")");

                updateGrid(r, c);
            }
        }
    }

    private void waterTrigger(){
        waterButton.setOnMouseClicked(event -> {
            selectedWater = !selectedWater;
            logger.log(Level.INFO, "Water Nozzle: " + selectedWater);
        });
    }

    private void updateGrid(int r, int c){
        String key = r + "," + c;
        Plant selectedPlant = plantGridMap.get(key);

        // for water
        StackPane tileContainer = (StackPane) gridPane.getChildren().get(r * GRID_SIZE + c);
        HBox waterBarContainer = (HBox) ((VBox) ((HBox) tileContainer.getChildren().getFirst()).getChildren().getFirst()).getChildren().get(2);
        Rectangle waterBar = (Rectangle) waterBarContainer.getChildren().get(1);
        Label waterLabel = (Label) waterBarContainer.getChildren().get(2);

        // for nutrient
        HBox nutrientBarContainer = (HBox) ((VBox) ((HBox) tileContainer.getChildren().getFirst()).getChildren().getFirst()).getChildren().get(3);
        Rectangle nutrientBar = (Rectangle) nutrientBarContainer.getChildren().get(1);
        Label nutrientLabel = (Label) nutrientBarContainer.getChildren().get(2);

        updateBar(waterBar, waterLabel, selectedPlant.getWaterLevel());
        updateBar(nutrientBar, nutrientLabel, selectedPlant.getNutrientsLevel());
    }

    private void dripIrrigation(int r, int c){
        String key = r + "," + c;
        Plant selectedPlant = plantGridMap.get(key);

        if(selectedWater) {
            if (selectedPlant == null) {
                logger.log(Level.INFO, "No plant at (" + (r+1) + "," + (c+1) + ")");
            } else {
                selectedPlant.setWaterLevel(100);
                plantGridMap.put(key, selectedPlant);
                logger.log(Level.INFO, "Irrigation carried out at (" + (r+1) + "," + (c+1) +")");
                updateGrid(r, c);

            }
        }
    }

    private HBox createBarContainer(String color, String emoji) {
        HBox barContainer = new HBox();
        barContainer.setMinWidth(80);
        barContainer.setMaxWidth(80);
        barContainer.setSpacing(5);
        barContainer.setStyle("-fx-alignment: center-left;");

        // Add emoji
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-padding: 0 5 0 0;" +
                        "-fx-font-family: 'Segoe UI Emoji';"
        );
        emojiLabel.setMinWidth(30); // Increase width to prevent truncation

        // Create the progress bar rectangle
        Rectangle bar = new Rectangle(50, 5, Color.valueOf(color));
        bar.setArcHeight(2);
        bar.setArcWidth(2);

        // Create label for percentage value
        Label label = new Label("100%");
        label.setMinWidth(25);
        label.setStyle("-fx-text-fill: black; -fx-font-size: 10px; -fx-padding: 0;");

        // ✅ Add emoji, bar, and label in order
        barContainer.getChildren().addAll(emojiLabel, bar, label);

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
        String key = row + "," + col;

        if(plantGridMap.get(key) != null){
            return;
        }
        if (selectedPlantImage != null && selectedPlantObject != null) {
            imageView.setImage(selectedPlantImage);
            plantGridMap.put(key, selectedPlantObject);

            updateBar(healthBar, healthLabel, selectedPlantObject.getHealth());
            updateBar(waterBar, waterLabel, selectedPlantObject.getWaterLevel());
            updateBar(nutrientBar, nutrientLabel, selectedPlantObject.getNutrientsLevel());

            healthBarContainer.setVisible(true);
            waterBarContainer.setVisible(true);
            nutrientBarContainer.setVisible(true);
            System.out.println("Planted " + selectedPlantObject.getClass().getSimpleName() + " at (" + (row+1) + "," + (col+1) + ")");
            logger.log(Level.INFO, "Planted "+ selectedPlantObject.getClass().getSimpleName() +" at (" + (row+1) + "," + (col+1) + ")");
//            updateLog("Planted " + selectedPlantObject.getClass().getSimpleName() + " at (" + (row+1) + "," + (col+1) + ")");
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
        List<InsectViewInfo> insectList = insectGridMap.get(key);
        Plant selectedPlant = plantGridMap.get(key);
//        if(selectedPlant != null) {
            if (insectList != null) {
                for (InsectViewInfo insectInfo : insectList) {
                    if (insectInfo.getInsectView().getImage() == null) {
                        URL imageUrl = getClass().getResource("/images/" + insectType + ".png");
                        if (imageUrl != null) {
                            insectInfo.getInsectView().setImage(new Image(imageUrl.toExternalForm()));
                            insectInfo.getInsectView().setVisible(true);
                            insectInfo.setInsectType(insectType); // Set the insect type

                            logger.log(Level.WARNING, "WARNING: Insect attack: " + insectType + " at (" + (row+1) + "," + (col+1) +")");
                            return;
                        } else {
                            logger.warning("Error: Insect image not found: " + insectType);
                        }
                    }
                }
                logger.info("No available slot for insect at (" + (row+1) + "," + (col+1) +")");
            }
            else
                logger.log(Level.INFO,"No insect at (" + (row+1) + "," + (col+1) +")");
//        }
//        else
//            logger.log(Level.INFO,"No Plant at (" + (row+1) + "," + (col+1) +")");
    }

}
