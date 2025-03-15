package Handler;

import GardenEntities.*;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Handler;


public class GardenHandler {
    private HashMap<Integer, Plant> grid;
    private int count;
    private final Logger logger;
    private TextArea logTextArea;

    // Constructor
    public GardenHandler(TextArea logTextArea) {
        grid = new HashMap<>();
        count = 0;
        logger = Logger.getLogger("GardenLogs");
        this.logTextArea = logTextArea;
    }

    // Setup Logger

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

    // Weather Functions



    // Important Functions
    public boolean hasPlant(int coordinate){
        return grid.containsKey(coordinate);
    }

    public void removePlant(int coordinate){
        grid.remove(coordinate);
        logger.log(Level.INFO, "Removed plant at " + coordinate +"th tile");
    }

    public Plant getPlant(int coordinate){
        return grid.get(coordinate);
    }

    public void addPlant(int coordinate, Plant plant){
        grid.put(coordinate,plant);
        logger.log(Level.INFO, "Added"+ plant.ID+" plant at " + coordinate +"th tile");
    }

    // public? for manual calling?
    private void waterPlant(Plant plant){
        if(plant.getSensor().needsWater()) {
            plant.getWaterSprinkler().waterPlant();
            logger.log(Level.INFO, "Irrigation carried out at ("+ plant.getCoordinate() +")th tile");
        }
    }

    // for manually irrigating water ((because waterPlant checks for the threshold)
    public void manuallyWaterPlant(Plant plant){
        plant.getWaterSprinkler().waterPlant();
        logger.log(Level.INFO, "Irrigation carried out at ("+ plant.getCoordinate() +")th tile");
    }

    public void pestInfection(Plant plant, Pest pest){
        if(pest.getID().equals(plant.ID)){
            plant.setPestInfected(true);
            plant.setPest(pest);
            logger.log(Level.INFO, "Pest infection at ("+ plant.getCoordinate() +")th tile");
        }
    }

    public void addPesticide(Plant plant, String pesticideType){
        if(plant.ID.equals(pesticideType)){
            plant.addPesticide();
            logger.log(Level.INFO, "Anti-Pest infection at ("+ plant.getCoordinate() +")th tile");
        }
    }

    private void addFertilizer(Plant plant){
        if(plant.getSensor().needsFertilizer()){
            plant.addFertilizer();
            logger.log(Level.INFO, "Added fertilizer at ("+ plant.getCoordinate() +")th tile");
        }
    }

    // for manually adding fertilizer (because addFertilizer checks for the threshold)
    public void manuallyAddFertilizer(Plant plant){
        plant.addFertilizer();
        logger.log(Level.INFO, "Added fertilizer at ("+ plant.getCoordinate() +")th tile");
    }

    // Automatic Functions

    public void changeHealth(Plant plant){
        int health = plant.getWaterLevel()+plant.getFertilizerLevel();
        health = health/2;
        if(plant.getSensor().isInfected()) {
            health -= 7;
        }

        plant.setHealth(health);
    }

    public void autoWaterPlants(){
        for(Plant plant: grid.values()){
            waterPlant(plant);
        }
    }

    public void autoAddPesticide(){
        for(Plant plant: grid.values()){
            if(plant.getSensor().isInfected()) addPesticide(plant, plant.ID);
        }
    }

    public void autoAddFertilizer(){
        for(Plant plant: grid.values()){
            addFertilizer(plant);
        }
    }

    // To Complete
    public void pestInvasion(){
        int randomNumber = (int)(Math.random()*4);

        if (randomNumber == 0){
            for(Plant plant : grid.values()){
                if(!plant.isPestInfected() && Objects.equals(plant.ID, "Plant1")){
                    if(Math.random() > 0.3) pestInfection(plant, new Pest1(plant));
                }
            }
        }
        else if(randomNumber == 1){
            for(Plant plant : grid.values()){
                if(!plant.isPestInfected() && Objects.equals(plant.ID, "Plant2")){
                    if(Math.random() > 0.3) pestInfection(plant, new Pest2(plant));
                }
            }
        }
        else if(randomNumber == 2){
            for(Plant plant : grid.values()){
                if(!plant.isPestInfected() && Objects.equals(plant.ID, "Plant3")){
                    if(Math.random() > 0.3) pestInfection(plant, new Pest3(plant));
                }
            }
        }
        else if(randomNumber == 3){
            for(Plant plant : grid.values()){
                if(!plant.isPestInfected() && Objects.equals(plant.ID, "Plant4")){
                    if(Math.random() > 0.3) pestInfection(plant, new Pest4(plant));
                }
            }
        }
    }


    // Functions to call every second
    private void dryPlants(){
        for(Plant plant: grid.values()){
            if(plant.getWaterLevel()> 10)plant.setWaterLevel((int) (plant.getWaterLevel()-(10*Math.random())));
        }
    }

    private void absorbFertilizer(){
        for(Plant plant: grid.values()){
            if(plant.getFertilizerLevel()>2) plant.setFertilizerLevel((int) (plant.getFertilizerLevel()-(2*Math.random())));
        }
    }

    private void updateHealth(){
        for(Plant plant: grid.values()){
            changeHealth(plant);
        }
    }


    private void deadPlant(){
        for(Plant plant: grid.values()){
            if(plant.getHealth() <= 0) {
                removePlant(plant.getCoordinate());
                logger.log(Level.INFO, "Dead plant at ("+ plant.getCoordinate() +")");
            }
        }
    }

    // Functions to call every second for Automation

    private void essentialFunctions(){
        dryPlants();
        absorbFertilizer();
        pestInvasion();
        updateHealth();
        deadPlant();
    }

    private void automaticFunctions(){
        autoWaterPlants();
        autoAddFertilizer();
    }

    public void iteration(boolean isAutomatic){
        //System.out.println(grid.size());
        essentialFunctions();
        if(isAutomatic){
            automaticFunctions();
            if(count == 0) {
                autoAddPesticide();
            }
        }
        count++;
        count%=15;
    }

    public HashMap<Integer, Plant> getGrid() {
        return grid;
    }

    public Logger getLogger() {
        return logger;
    }
}

