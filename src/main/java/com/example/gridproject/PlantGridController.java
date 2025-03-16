package com.example.gridproject;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
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

import java.io.IOException;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

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
    @FXML
    private HBox weatherPanel;
    @FXML
    private ImageView weatherGifSunny;
    @FXML
    private ImageView weatherGifRainy;
    @FXML
    private ImageView weatherGifCloudy;
    @FXML
    private ImageView weatherGifClear;
    @FXML
    private ImageView currentWeatherGif;

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
    @FXML
    final int screenWidth = (int) screenSize.getWidth();
    @FXML
    final int screenHeight = (int) screenSize.getHeight();



    private static Logger logger = Logger.getLogger(PlantGridController.class.getName());
    private static final int LOG_EVENT_MAX_CHAR = 1000;
    private final int GRID_SIZE = 4;
    private final Button[][] buttons = new Button[GRID_SIZE][GRID_SIZE];
    private Image selectedPlantImage = null;
    private String plantType = null;
    GardenHandler gardenHandler = new GardenHandler(logTextArea, logger);
    private HashMap<Integer, Plant> plantGridMap = gardenHandler.getGrid();
    private String selectedPesticide = "";
    private boolean selectedWater = false;
    private boolean selectedFertilizer = false;
    private boolean isAutoPilot = false;
    private boolean isRemovePlant = false;
    private String currentWeather = "Rainy";
    private Image gifSunny, gifRainy, gifCloudy, gifClear;
    private ScheduledExecutorService scheduler;


    public void initialize() {
        gifSunny = new Image(Objects.requireNonNull(getClass().getResource("/images/sunImage.png")).toExternalForm(), true);
        gifRainy = new Image(Objects.requireNonNull(getClass().getResource("/images/rainImage.png")).toExternalForm(), true);
        gifCloudy = new Image(Objects.requireNonNull(getClass().getResource("/images/cloudyImage.png")).toExternalForm(), true);
        gifClear = new Image(Objects.requireNonNull(getClass().getResource("/images/clearImage.png")).toExternalForm(), true);

        // Set initial weather image
        if (currentWeatherGif != null) {
            currentWeatherGif.setImage(gifClear);
        } else {
            System.err.println("⚠ currentWeatherGif is null — check your FXML binding.");
        }

        setupWeatherPanel();
        setupGrid();
        setupPlantSelection();
        pesticideComboBox.getItems().addAll("Plant1", "Plant2", "Plant3", "Plant4");
        setupPesticideSelection();
        waterTrigger();
        fertilizerTrigger();
        createAutoPilotButton();
        removeTrigger();
        weatherPanel = new HBox();

        loadImage(plantImage1, "images/plant.jpg");
        loadImage(plantImage2, "images/plant2.jpg");
        loadImage(plantImage3, "images/plant3.jpg");
        loadImage(waterDrop, "images/waterdrop.jpg");
        loadImage(manure, "images/fertilizer.png");
        loadImage(removePlantImage, "images/remove.jpg");

        setWeatherGifWidth();
        runFor24Hours();
        setupLogger();
        startRefreshTimer();
    }

    public void onClose() {
        System.out.println("Application is closing...");
        if (timeline != null) timeline.stop();
        if (scheduler != null && !scheduler.isShutdown()) scheduler.shutdownNow();
    }


    public void runFor24Hours() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable taskWrapper = () -> {
            try {
                // write here
//                task();
                gardenHandler.iteration(isAutoPilot);
//                for (Plant plant : gardenHandler.getGrid().values()){
//                    plant.displayStatus();
//                }
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

    private void refreshGUI() {
        Platform.runLater(() -> {
            try {
                Random random = new Random();
                int newTemp = 50 + random.nextInt(30); // 50°F to 80°F
                String[] weatherTypes = {"Clear", "Rainy", "Cloudy", "Sunny"};
                String weather = weatherTypes[random.nextInt(weatherTypes.length)];

                int day;
                try {
                    day = Integer.parseInt(dayLabel.getText().split(":")[1].trim()) + 1;
                } catch (NumberFormatException ex) {
                    logger.log(Level.WARNING, "Invalid day value in label: " + dayLabel.getText());
                    day = 1; // Default to day 1
                }

                updateWeather(weather, newTemp, day);
                simulateBackendLogic();

            } catch (NullPointerException e) {
                logger.log(Level.SEVERE, "Null pointer exception in refreshGUI: " + e.getMessage(), e);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during refresh: " + e.getMessage(), e);
            }
        });
    }


    private int getKey(int row, int col){
        return row*GRID_SIZE+col;
    }

    // Example backend logic
    private void simulateBackendLogic() {
        try {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    try {
                        int key = getKey(i, j);

                        if (gardenHandler.hasPlant(key)) {
                            Plant plant = gardenHandler.getPlant(key);

                            if (plant.getSensor().isInfected()) {
                                switch (plant.ID) {
                                    case "Plant1" -> insectAttack("pest1", i, j);
                                    case "Plant2" -> insectAttack("pest2", i, j);
                                    case "Plant3" -> insectAttack("pest3", i, j);
                                    case "Plant4" -> insectAttack("pest4", i, j);
                                    default -> logger.log(Level.INFO, "Unknown plant ID");
                                }
                            } else {
                                insectAttack("", i, j);
                            }

                            updateGrid(i, j);
                        } else {
                            emptyGrid(i, j);
                        }
                    } catch (NullPointerException e) {
                        logger.log(Level.WARNING, "Null value in simulateBackendLogic at [" + i + "][" + j + "]", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "simulateBackendLogic failed: " + e.getMessage(), e);
        }
    }

    private void setWeatherGifWidth(){
        currentWeatherGif.setFitWidth(0.98*screenWidth);
    }


    private void setupWeatherPanel() {
        weatherPanel.setSpacing(20);
        weatherPanel.setAlignment(Pos.CENTER);
        weatherPanel.setStyle("-fx-background-color: transparent; -fx-padding: 10px;");

        // Styling labels to stand out
        String labelStyle = "-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-effect: dropshadow( gaussian , black , 5 , 0 , 0 , 0 );";

        weatherLabel.setStyle(labelStyle);
        tempLabel.setStyle(labelStyle);
        dayLabel.setStyle(labelStyle);
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
        if (Objects.equals(currentWeather, weather)) {
            return;
        }
        currentWeather = weather;
        weatherLabel.setText("Weather: " + weather);
        tempLabel.setText("Temp: " + temperature + "°F");
        dayLabel.setText("Day: " + day);
        switch (weather) {
            case "Clear" -> currentWeatherGif.setImage(gifClear);
            case "Sunny" -> currentWeatherGif.setImage(gifSunny);
            case "Rainy" -> currentWeatherGif.setImage(gifRainy);
            case "Cloudy" -> currentWeatherGif.setImage(gifCloudy);
        }
    }

    private void setupLogger() {
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();

        // Remove existing handlers (including default console handler)
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        // FileHandler to write logs to a file
        try {

            Path logDirectory = Paths.get("src", "main", "resources", "logs");
            if (!Files.exists(logDirectory)) {
                Files.createDirectories(logDirectory);
            }

            Path logFilePath = logDirectory.resolve("LOG.log");
            FileHandler fileHandler = new FileHandler(logFilePath.toString(), true); // true = append mode
            fileHandler.setLevel(Level.CONFIG);
            fileHandler.setFormatter(new SimpleFormatter()); // Use a simple format

            rootLogger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Handler textAreaHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                Platform.runLater(() -> {
                    String message = record.getLevel() + ": " + record.getMessage() + "\n";
                    logTextArea.appendText(message);
                    String[] lines = logTextArea.getText().split("\n");
                    if (lines.length > LOG_EVENT_MAX_CHAR) {
                        logTextArea.setText(String.join("\n", Arrays.copyOfRange(lines, lines.length - LOG_EVENT_MAX_CHAR, lines.length)));
                    }
                    logTextArea.setScrollTop(Double.MAX_VALUE);
                });
            }

            @Override
            public void flush() {}

            @Override
            public void close() throws SecurityException {}
        };

        textAreaHandler.setLevel(Level.CONFIG);
        rootLogger.addHandler(textAreaHandler);

        // Set logger level to get all messages
        rootLogger.setLevel(Level.CONFIG);

        logger.log(Level.INFO, "Logger setup. Program started... "+logger);
    }


    private void setupGrid() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                final int finalRow = row;
                final int finalCol = col;

                StackPane tileContainer = new StackPane();
                tileContainer.setStyle("-fx-background-color: #81854e; -fx-padding: 0px; -fx-border-width: 0;");
                tileContainer.setPadding(new Insets(0, 0, 0, 0));
                double boxWidth = 0.08*screenWidth;
                tileContainer.setMinSize(0.11*screenWidth,0.11*screenWidth);
                tileContainer.setMaxSize(0.11*screenWidth,0.11*screenWidth);

                VBox tileBox = new VBox();
                tileBox.setSpacing(2);
                tileBox.setStyle("-fx-alignment: center; -fx-background-color: transparent");

                ImageView imageView = new ImageView();
//                imageView.setFitWidth(0.25*tileContainer.getMinHeight());
//                imageView.setFitHeight(0.25*tileContainer.getMinHeight());


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
                tileButton.setMinSize(105, 105);
                tileButton.setMaxSize(105,105);
                tileButton.setStyle("-fx-background-color: transparent;");
                tileButton.setGraphic(imageView);

                tileButton.setOnAction(event -> plantSelectedPlant(finalRow, finalCol, imageView, healthBarContainer, healthBar, healthLabel, waterBarContainer, waterBar, waterLabel, nutrientBarContainer, nutrientBar, nutrientLabel));
                tileButton.setOnMouseClicked(event -> functionCumulative(finalRow, finalCol));

                // Create VBox for insects (right side)
                VBox insectBox = new VBox();
                insectBox.setSpacing(0);
                insectBox.setAlignment(Pos.CENTER_RIGHT);
                insectBox.setMaxWidth(50);

                // Shift more towards the right using translateX
                insectBox.setTranslateX(0);
                insectBox.setStyle("-fx-background-color: transparent;");

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

                imageView.setFitWidth(0.7*tileButton.getMinHeight());
                imageView.setFitHeight(0.7*tileButton.getMinHeight());
                tileBox.setAlignment(Pos.CENTER);
                tileButton.setAlignment(Pos.CENTER);


                tileBox.getChildren().addAll(imageView, tileButton, healthBarContainer, waterBarContainer, nutrientBarContainer);
                HBox container = new HBox(tileBox, insectBox);
                container.setSpacing(5);
                container.setAlignment(Pos.CENTER_LEFT);
                tileBox.setAlignment(Pos.CENTER);
                insectBox.setAlignment(Pos.CENTER);
                tileContainer.getChildren().add(container);
                tileContainer.setMinWidth(Region.USE_COMPUTED_SIZE);
                tileContainer.setMaxWidth(Region.USE_PREF_SIZE);
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
            updateGridButtonColor(row, col);
            updateRemovePlantButtonColor();
        }
    }

    private void updateGridButtonColor(int row, int col) {
        int key = getKey(row, col);
        StackPane tileContainer = (StackPane) gridPane.getChildren().get(key);
        tileContainer.setStyle("-fx-background-color: #81854e; -fx-padding: 0px; -fx-border-width: 0;");
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
            removePlantButton.setStyle("-fx-background-color: #d58282;");
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
        } else {
            selectedPlantImage = new Image(imageUrl.toExternalForm());
            plantType = type;
            logger.log(Level.INFO, "Selected plant: " + type);
        }

    }

    private void sprayPesticide(int r, int c){
        int key = getKey(r,c);
        selectedPesticide = pesticideComboBox.getValue();
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

    private void updateGrid(int r, int c) {
        try {
            int key = getKey(r, c);
            StackPane tileContainer = (StackPane) gridPane.getChildren().get(r * GRID_SIZE + c);

            if (gardenHandler.hasPlant(key)) {
                Plant selectedPlant = gardenHandler.getPlant(key);

                // Wrap UI updates in a try-catch
                try {
                    HBox waterBarContainer = (HBox) ((VBox) ((HBox) tileContainer.getChildren().getFirst()).getChildren().getFirst()).getChildren().get(2);
                    Rectangle waterBar = (Rectangle) waterBarContainer.getChildren().get(1);
                    Label waterLabel = (Label) waterBarContainer.getChildren().get(2);

                    HBox nutrientBarContainer = (HBox) ((VBox) ((HBox) tileContainer.getChildren().getFirst()).getChildren().getFirst()).getChildren().get(3);
                    Rectangle nutrientBar = (Rectangle) nutrientBarContainer.getChildren().get(1);
                    Label nutrientLabel = (Label) nutrientBarContainer.getChildren().get(2);

                    HBox healthBarContainer = (HBox) ((VBox) ((HBox) tileContainer.getChildren().getFirst()).getChildren().getFirst()).getChildren().get(1);
                    Rectangle healthBar = (Rectangle) healthBarContainer.getChildren().get(1);
                    Label healthLabel = (Label) healthBarContainer.getChildren().get(2);

                    updateBar(waterBar, waterLabel, selectedPlant.getWaterLevel());
                    updateBar(nutrientBar, nutrientLabel, selectedPlant.getFertilizerLevel());
                    updateBar(healthBar, healthLabel, selectedPlant.getHealth());
                } catch (IndexOutOfBoundsException e) {
                    logger.log(Level.WARNING, "Grid update failed due to index issue: " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update grid: " + e.getMessage(), e);
        }
    }


    private void emptyGrid(int row, int col){
        int key = getKey(row, col);

        // Access the StackPane at the grid location
        StackPane tileContainer = (StackPane) gridPane.getChildren().get(key);
        HBox container = (HBox) tileContainer.getChildren().get(0);
        VBox tileBox = (VBox) container.getChildren().get(0);

        for (Node node : tileBox.getChildren()) {
            if (node instanceof Button) {
                Button tileButton = (Button) node;
                if (tileButton.getGraphic() instanceof ImageView) {
                    ((ImageView) tileButton.getGraphic()).setVisible(false);
                }
                break; // Stop searching once the button is found
            }
        }

        for (Node node : tileBox.getChildren()) {
            if (node instanceof HBox) {
                HBox barContainer = (HBox) node;
                barContainer.setVisible(false);
            }
        }

        insectAttack("", row, col);
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
        emojiLabel.setMinWidth(30);
        Rectangle bar = new Rectangle(50, 5, Color.valueOf(color));
        bar.setArcHeight(2);
        bar.setArcWidth(2);

        Label label = new Label("100%");
        label.setMinWidth(25);
        label.setStyle("-fx-text-fill: black; -fx-font-size: 10px; -fx-padding: 0;");
        barContainer.getChildren().addAll(emojiLabel, bar, label);
        return barContainer;
    }



    private void loadImage(ImageView imageView, String imagePath) {
        try {
            if (imageView == null) {
                logger.log(Level.SEVERE, "ImageView is null for path: " + imagePath);
                return;
            }

            URL imageUrl = getClass().getResource("/" + imagePath);
            if (imageUrl == null) {
                logger.log(Level.SEVERE, "Image not found: " + imagePath);
            } else {
                imageView.setImage(new Image(imageUrl.toExternalForm()));
                logger.log(Level.INFO, "Loaded image: " + imagePath);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading image: " + imagePath + " - " + e.getMessage(), e);
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
            if(plantType != null){
                if(plantType.equals("Plant1")) gardenHandler.addPlant(key, new Plant1(key));
                if(plantType.equals("Plant2")) gardenHandler.addPlant(key, new Plant2(key));
                if (plantType.equals("Plant3")) gardenHandler.addPlant(key, new Plant3(key));
            }

            updateBar(healthBar, healthLabel, gardenHandler.getPlant(key).getHealth());
            updateBar(waterBar, waterLabel, gardenHandler.getPlant(key).getWaterLevel());
            updateBar(nutrientBar, nutrientLabel, gardenHandler.getPlant(key).getFertilizerLevel());

            healthBarContainer.setVisible(true);
            waterBarContainer.setVisible(true);
            nutrientBarContainer.setVisible(true);
            System.out.println("Planted " + plantType + " at (" + (row+1) + "," + (col+1) + ")");
            StackPane tileContainer = (StackPane) gridPane.getChildren().get(key);
            tileContainer.setStyle("-fx-background-color: #7c9a5f; -fx-padding: 0px; -fx-border-width: 0;");
//            logger.log(Level.INFO, "Planted "+ selectedPlantObject.getClass().getSimpleName() +" at (" + (row+1) + "," + (col+1) + ")");
//            printGridDetails();

        } else {
            System.out.println("No plant selected!");
            logger.log(Level.WARNING, "No plant selected!");
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
        try {
            int key = getKey(row, col);
            StackPane tileContainer = (StackPane) gridPane.getChildren().get(key);
            HBox container = (HBox) tileContainer.getChildren().get(0);
            VBox insectBox = (VBox) container.getChildren().get(1);

            if (!insectBox.getChildren().isEmpty()) {
                ImageView insectView = (ImageView) insectBox.getChildren().get(0);

                if (insectType.isEmpty()) {
                    insectView.setVisible(false);
                    return;
                }

                URL imageUrl = getClass().getResource("/images/" + insectType + ".png");
                if (imageUrl != null) {
                    insectView.setImage(new Image(imageUrl.toExternalForm()));
                    insectView.setVisible(true);
                } else {
                    logger.log(Level.WARNING, "Insect image not found: " + insectType);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in insectAttack: " + e.getMessage(), e);
        }
    }


}
