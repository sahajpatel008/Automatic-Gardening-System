package com.example.gridproject;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.application.Platform;
import javafx.scene.text.Text;

import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

import java.awt.*;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import GardenEntities.*;
import Handler.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;
// Parent Plant class with health, water, and nutrients properties



//class Plant {
//    int health;
//    int waterLevel;
//    int nutrientsLevel;
//
//    public Plant() {
//        this.health = 100;
//        this.waterLevel = 100;
//        this.nutrientsLevel = 100;
//    }
//
//    public Plant(int h, int w, int n){
//        this.health = h;
//        this.waterLevel = w;
//        this.nutrientsLevel = n;
//    }
//
//    public int getHealth() { return health; }
//    public void setHealth(int health) { this.health = health; }
//    public int getWaterLevel() { return waterLevel; }
//    public void setWaterLevel(int waterLevel) { this.waterLevel = waterLevel; }
//    public int getNutrientsLevel() { return nutrientsLevel; }
//    public void setNutrientsLevel(int nutrientsLevel) { this.nutrientsLevel = nutrientsLevel; }
//}
//
//// Child classes inheriting from Plant
//class PlantClass1 extends Plant {
//    PlantClass1(int h, int w, int n){
//        super(h, w, n);
//    }
//}
//class PlantClass2 extends Plant {
//    PlantClass2(int h, int w, int n){
//        super(h, w, n);
//    }
//}
//class PlantClass3 extends Plant {
//    PlantClass3(int h, int w, int n){
//        super(h, w, n);
//    }
//}

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
    @FXML
    private Button removePlantButton;
    @FXML
    private ImageView removePlantImage;

    // Added Weather, Temp, and Day
    @FXML
    private Label weatherLabel;
    @FXML
    private Label tempLabel;
    @FXML
    private Label dayLabel;

    private Timeline timeline;

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize = toolkit.getScreenSize();
    final int screenWidth = (int) screenSize.getWidth();
    final int screenHeight = (int) screenSize.getHeight();

    private static final Logger logger = Logger.getLogger(PlantGridController.class.getName());
    private final int GRID_SIZE = 4;
    private final Button[][] buttons = new Button[GRID_SIZE][GRID_SIZE];
    private Image selectedPlantImage = null;
//    private Plant selectedPlantObject = null;
    private String plantType = null;
//    private final Map<String, Plant> plantGridMap = new HashMap<>();
    GardenHandler gardenHandler = new GardenHandler(logTextArea, logger);
    private HashMap<Integer, Plant> plantGridMap = gardenHandler.getGrid();
//    private HashMap<Integer, List<InsectViewInfo>> insectGridMap = new HashMap<>();
    private String selectedPesticide = "";
    private boolean selectedWater = false;
    private final String[] pesticideImages = new String[4];
    private boolean selectedFertilizer = false;
    private boolean isAutoPilot = false;
    private boolean isRemovePlant = false;


    public void initialize() {
        setupWeatherPanel();
        setupGrid();
        setupPlantSelection();
        pesticideComboBox.getItems().addAll("Plant1", "Plant2", "Plant3", "Plant4");
        setupPesticideSelection();
        waterTrigger();
        fertilizerTrigger();
        createAutoPilotButton();
        removeTrigger();

        loadImage(plantImage1, "images/plant.jpg");
        loadImage(plantImage2, "images/plant2.jpg");
        loadImage(plantImage3, "images/plant3.jpg");
        loadImage(waterDrop, "images/waterdrop.jpg");
        loadImage(manure, "images/fertilizer.png");
        loadImage(removePlantImage, "images/remove.jpg");
        runFor24Hours();

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
        startRefreshTimer();
    }

    public void runFor24Hours() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable taskWrapper = () -> {
            try {
                // write here
//                task();
                gardenHandler.iteration(isAutoPilot);
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

    private void startRefreshTimer(){
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> refreshGUI()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Refresh GUI and backend data
    private void refreshGUI() {
        Platform.runLater(() -> {
            try {
                // Example: Update weather, temperature, and day
                Random random = new Random();
                int newTemp = 50 + random.nextInt(30); // 50°F to 80°F
                String[] weatherTypes = {"Clear", "Rainy", "Cloudy", "Sunny"};
                String weather = weatherTypes[random.nextInt(weatherTypes.length)];
                int day = Integer.parseInt(dayLabel.getText().split(":")[1].trim()) + 1;

                updateWeather(weather, newTemp, day);

                // Example: Call backend code here
                simulateBackendLogic();

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during refresh: " + e.getMessage());
            }
        });
    }

    private int getKey(int row, int col){
        return row*GRID_SIZE+col;
    }

    // Example backend logic
    private void simulateBackendLogic() {


        for(int i = 0; i < GRID_SIZE; i++) {
            for(int j = 0; j < GRID_SIZE; j++) {
                int key = getKey(i, j);
                if(gardenHandler.hasPlant(key)){
                    Plant plant = gardenHandler.getPlant(key);

                    // has insect
                    if(plant.getSensor().isInfected()){
                        if(plant.ID.equals("Plant1")){
                            insectAttack("pest1", i, j);
                        }
                        else if(plant.ID.equals("Plant2")){
                            insectAttack("pest2", i, j);
                        }
                        else if(plant.ID.equals("Plant3")){
                            insectAttack("pest3", i, j);
                        }
                        else if(plant.ID.equals("Plant4")){
                            insectAttack("pest4", i, j);
                        }
                    }

                    else{
                        insectAttack("", i, j);
                    }

                    // healthbar, nutrientbar
                    updateGrid(i, j);
                }
                else{
                    if(i == 0 && j == 2){
                        System.out.println("************************************");
                    }
                    emptyGrid(i,j);
                }
            }
        }
    }

    private void setupWeatherPanel() {
        HBox weatherPanel = new HBox();
        weatherPanel.setSpacing(20);
        weatherPanel.setAlignment(Pos.CENTER);
        weatherPanel.setStyle("-fx-background-color: lightblue; -fx-padding: 10px;");

        weatherLabel = new Label("Weather: Clear");
        tempLabel = new Label("Temp: 59°F");
        dayLabel = new Label("Day: 1");

        weatherLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        tempLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
        dayLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");

        weatherPanel.getChildren().addAll(weatherLabel, tempLabel, dayLabel);

        // Add panel at the top of the existing layout
        BorderPane root = (BorderPane) gridPane.getParent();
        root.setTop(weatherPanel);
    }

    private void createAutoPilotButton() {
        Button autoPilotButton = new Button("AutoPilot");
        autoPilotButton.setMinSize(120, 40);
        autoPilotButton.setStyle("-fx-text-fill: black; -fx-font-size: 14px;  -fx-border-color: black");

        autoPilotButton.setOnAction(event -> {
            startAutoPilot();
            updateAutoPilotButtonColor(autoPilotButton);
        });

        VBox parent = (VBox) fertilizerButton.getParent();
        parent.getChildren().add(autoPilotButton);
    }

    private void updateAutoPilotButtonColor(Button button) {
        if (isAutoPilot) {
            button.setStyle("-fx-background-color: GREEN; -fx-text-fill: white; -fx-font-size: 14px;");
        } else {
            button.setStyle("-fx-text-fill: black; -fx-font-size: 14px; -fx-border-color: black");
        }
    }

    private void startAutoPilot() {
        this.isAutoPilot = !this.isAutoPilot;
        logger.log(Level.INFO, "AutoPilot status: "+isAutoPilot);
    }

    public void printGridDetails() {
        System.out.println("----- Grid Details -----");
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int key = getKey(row,col);
                Plant plant = plantGridMap.get(key);
                System.out.println(plant);
                if (plant != null) {
                    System.out.println("Position: (" + row + "," + col + ")");
                    System.out.println("Plant Type: " + plant.getClass().getSimpleName());
                    System.out.println("Health: " + plant.getHealth());
                    System.out.println("Water Level: " + plant.getWaterLevel());
                    System.out.println("-----------------------");
                }
            }
        }
    }

    private void updateWeather(String weather, int temperature, int day) {
        weatherLabel.setText("Weather: " + weather);
        tempLabel.setText("Temp: " + temperature + "°F");
        dayLabel.setText("Day: " + day);
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
                tileButton.setMinSize(150, 150);
                tileButton.setMaxSize(150,150);
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


                tileBox.getChildren().addAll(imageView, tileButton, healthBarContainer, waterBarContainer, nutrientBarContainer);
                HBox container = new HBox(tileBox, insectBox);
                tileContainer.getChildren().add(container);
                gridPane.add(tileContainer, col, row);

                buttons[row][col] = tileButton;


            }
        }

        updateWeather("Sunny🌞", 70, 28);
    }

    private void functionCumulative(int row, int col) {
        sprayPesticide(row, col);
        fertilise(row, col);
        dripIrrigation(row, col);
        removePlant(row, col);
    }

    private void removePlant(int row, int col) {
        int key = getKey(row, col);
        if(isRemovePlant && gardenHandler.hasPlant(key)){
            gardenHandler.removePlant(key);
            isRemovePlant = false;
            updateRemovePlantButtonColor();
        }
    }

    private void removeTrigger(){
        removePlantButton.setOnMouseClicked(event -> {
            isRemovePlant = !isRemovePlant;
            updateRemovePlantButtonColor();
            logger.log(Level.INFO, "Remove Plant: " + isRemovePlant);
        });
    }

    private void updateRemovePlantButtonColor() {
        if(isRemovePlant){
            removePlantButton.setStyle("-fx-background-color: #ff9393;");
        }
        else
            removePlantButton.setStyle("");
    }

    // Setup plant selection buttons
    private void setupPlantSelection() {
        plantType1.setOnAction(event -> {
            selectPlant("/images/plant.jpg", "Plant1");
            updatePlantButtonColor();
        });
        plantType2.setOnAction(event -> {
            selectPlant("/images/plant2.jpg", "Plant2");
            updatePlantButtonColor();
        });
        plantType3.setOnAction(event -> {
            selectPlant("/images/plant3.jpg", "Plant3");
            updatePlantButtonColor();
        });
    }

    private void updatePlantButtonColor() {
        if(plantType.equals("Plant1")) {
            plantType1.setStyle("-fx-background-color: #f4f2a1;");
            plantType2.setStyle("");
            plantType3.setStyle("");
        }
        else if(plantType.equals("Plant2")) {
            plantType2.setStyle("-fx-background-color: #f4f2a1;");
            plantType3.setStyle("");
            plantType1.setStyle("");
        }
        else if(plantType.equals("Plant3")) {
            plantType3.setStyle("-fx-background-color: #f4f2a1;");
            plantType2.setStyle("");
            plantType1.setStyle("");
        }
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

    private void selectPlant(String imagePath, String type) {
        URL imageUrl = getClass().getResource(imagePath);
        if (imageUrl == null) {
            System.err.println("Image not found in selectPlant: " + imagePath);
            logger.log(Level.SEVERE, "Error: Couldn't retrieve image for selected plant: {0}", imagePath);

//            updateLog("Image not found in selectPlant: " + imagePath);
        } else {
            selectedPlantImage = new Image(imageUrl.toExternalForm());
            plantType = type;
//            selectedPlantObject = plantObject;
//            System.out.println("Selected plant: " + plantObject.getClass().getSimpleName());
            logger.log(Level.INFO, "Selected plant: " + type);
        }

    }

    private void sprayPesticide(int r, int c){
        int key = getKey(r,c);
        selectedPesticide = pesticideComboBox.getValue();
        // Expecting this call
        if(selectedPesticide!=null && gardenHandler.hasPlant(key)){
            gardenHandler.addPesticide(gardenHandler.getPlant(key), selectedPesticide);
            updateGrid(r, c);
        }
    }

    private void fertilizerTrigger(){
        fertilizerButton.setOnMouseClicked(event -> {
            selectedFertilizer = !selectedFertilizer;
            updateFertilizerButtonColor(fertilizerButton);
            logger.log(Level.INFO, "Fertilizer Nozzle: " + selectedFertilizer);
        });
    }

    private void updateFertilizerButtonColor(Button button){
        if(selectedFertilizer){
            button.setStyle("-fx-background-color: #a5992a;; -fx-text-fill: black; -fx-font-size: 14px;");
        }
        else{
            button.setStyle("");
        }
    }

    private void fertilise(int r, int c){
        int key = getKey(r,c);

        if(selectedFertilizer && gardenHandler.hasPlant(key)){
            gardenHandler.manuallyAddFertilizer(gardenHandler.getPlant(key));
            updateGrid(r, c);
        }
    }

    private void waterTrigger(){
        waterButton.setOnMouseClicked(event -> {
            selectedWater = !selectedWater;
            updateWaterTriggerColor(waterButton);
            logger.log(Level.INFO, "Water Nozzle: " + selectedWater);
        });
    }

    private void updateWaterTriggerColor(Button button){
        if(selectedWater){
            button.setStyle("-fx-background-color: LIGHTBLUE; -fx-text-fill: black; -fx-font-size: 14px;");
        }
        else{
            button.setStyle("");
        }
    }

    private void dripIrrigation(int r, int c){
        int key = getKey(r,c);

        if(selectedWater && gardenHandler.hasPlant(key)){
            gardenHandler.manuallyWaterPlant(gardenHandler.getPlant(key));
            updateGrid(r, c);
        }
    }

    private void updateGrid(int r, int c){
        int key = getKey(r,c);

        if(gardenHandler.hasPlant(key)){

            Plant selectedPlant = gardenHandler.getPlant(key);

            // for water
            StackPane tileContainer = (StackPane) gridPane.getChildren().get(r * GRID_SIZE + c);
            HBox waterBarContainer = (HBox) ((VBox) ((HBox) tileContainer.getChildren().getFirst()).getChildren().getFirst()).getChildren().get(2);
            Rectangle waterBar = (Rectangle) waterBarContainer.getChildren().get(1);
            Label waterLabel = (Label) waterBarContainer.getChildren().get(2);

            // for nutrient
            HBox nutrientBarContainer = (HBox) ((VBox) ((HBox) tileContainer.getChildren().getFirst()).getChildren().getFirst()).getChildren().get(3);
            Rectangle nutrientBar = (Rectangle) nutrientBarContainer.getChildren().get(1);
            Label nutrientLabel = (Label) nutrientBarContainer.getChildren().get(2);

            // for health
            HBox healthBarContainer = (HBox) ((VBox) ((HBox) tileContainer.getChildren().getFirst()).getChildren().getFirst()).getChildren().get(1);
            Rectangle healthBar = (Rectangle) healthBarContainer.getChildren().get(1);
            Label healthLabel = (Label) healthBarContainer.getChildren().get(2);

            updateBar(waterBar, waterLabel, selectedPlant.getWaterLevel());
            updateBar(nutrientBar, nutrientLabel, selectedPlant.getFertilizerLevel());
            updateBar(healthBar, healthLabel, selectedPlant.getHealth());
        }



    }

    private void emptyGrid(int row, int col){
        int key = getKey(row, col);

        // Access the StackPane at the grid location
        StackPane tileContainer = (StackPane) gridPane.getChildren().get(key);
        HBox container = (HBox) tileContainer.getChildren().get(0);
        VBox tileBox = (VBox) container.getChildren().get(0);

        // ✅ Find the button by type-checking to avoid casting errors
        for (Node node : tileBox.getChildren()) {
            if (node instanceof Button) {
                Button tileButton = (Button) node;
                if (tileButton.getGraphic() instanceof ImageView) {
                    ((ImageView) tileButton.getGraphic()).setVisible(false);
                }
                break; // Stop searching once the button is found
            }
        }

        // ✅ Hide the Health Bar
        for (Node node : tileBox.getChildren()) {
            if (node instanceof HBox) {
                HBox barContainer = (HBox) node;
                barContainer.setVisible(false);
            }
        }

        insectAttack("", row, col);

        System.out.println("Cell at (" + row + ", " + col + ") hidden.");

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
        int key = getKey(row,col);
        if(plantGridMap.get(key) != null){
            return;
        }
        if (selectedPlantImage != null && plantType != null) {
            imageView.setImage(selectedPlantImage);
            imageView.setVisible(true);
//            gardenHandler.put(key, selectedPlantObject);
            if(plantType != null){
                if(plantType.equals("Plant1")) gardenHandler.addPlant(key, new Plant1(key));
                if(plantType.equals("Plant2")) gardenHandler.addPlant(key, new Plant2(key));
                if (plantType.equals("Plant3")) gardenHandler.addPlant(key, new Plant3(key));
            }
            gardenHandler.getPlant(key).displayStatus();

            updateBar(healthBar, healthLabel, gardenHandler.getPlant(key).getHealth());
            updateBar(waterBar, waterLabel, gardenHandler.getPlant(key).getWaterLevel());
            updateBar(nutrientBar, nutrientLabel, gardenHandler.getPlant(key).getFertilizerLevel());

            healthBarContainer.setVisible(true);
            waterBarContainer.setVisible(true);
            nutrientBarContainer.setVisible(true);
            System.out.println("Planted " + plantType + " at (" + (row+1) + "," + (col+1) + ")");
//            logger.log(Level.INFO, "Planted "+ selectedPlantObject.getClass().getSimpleName() +" at (" + (row+1) + "," + (col+1) + ")");
            printGridDetails();
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
        int key = getKey(row,col);


        StackPane tileContainer = (StackPane) gridPane.getChildren().get(key);
        HBox container = (HBox) tileContainer.getChildren().get(0);
        VBox insectBox = (VBox) container.getChildren().get(1);



        if (insectBox.getChildren().size() > 0) {
            // Get the first insect view (or loop for multiple insects)
            ImageView insectView = (ImageView) insectBox.getChildren().get(0);

            // Load new image


            if(insectType.isEmpty()){
                insectView.setVisible(false);
                return;
            }

            URL imageUrl = getClass().getResource("/images/" + insectType + ".png");
            if (imageUrl != null) {
                insectView.setImage(new Image(imageUrl.toExternalForm()));
                insectView.setVisible(true);
            }
            else{
                System.out.println("cno");
            }
        }

    }

}
