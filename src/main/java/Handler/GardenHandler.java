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

    // Important Functions
    public boolean hasPlant(int coordinate){
        return grid.containsKey(coordinate);
    }

    public void removePlant(int coordinate){
        grid.remove(coordinate);
    }

    public Plant getPlant(int coordinate){
        return grid.get(coordinate);
    }

    public void addPlant(int coordinate, Plant plant){
        grid.put(coordinate,plant);

    }
    // public? for manual calling?
    public void waterPlant(Plant plant){
        if(plant.getSensor().needsWater()) {
            plant.getWaterSprinkler().waterPlant();
            logger.log(Level.INFO, "Irrigation carried out at ("+ plant.getCoordinate()+")th tile");
        }
    }

    public void pestInfection(Plant plant, Pest pest){
        if(pest.getID().equals(plant.ID)){
            plant.setPestInfected(true);
            plant.setPest(pest);
        }
    }

    public void addPesticide(Plant plant, String pesticideType){
        if(plant.ID.equals(pesticideType)){
            plant.addPesticide();
        }
    }

    public void addFertilizer(Plant plant){
        if(plant.getSensor().needsFertilizer()){
            plant.addFertilizer();
        }
    }

    // Automatic Functions

    public void autoWaterPlants(){
        for(Plant plant: grid.values()){
            waterPlant(plant);
        }
    }

    public void autoAddPesticide(){
        for(Plant plant: grid.values()){
            if(plant.getSensor().isInfected()) plant.addPesticide();
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
                if(Objects.equals(plant.ID, "Plant1")){
                    if(Math.random() > 0.3) pestInfection(plant, new Pest1(plant));
                }
            }
        }
        else if(randomNumber == 1){
            for(Plant plant : grid.values()){
                if(Objects.equals(plant.ID, "Plant2")){
                    if(Math.random() > 0.3) pestInfection(plant, new Pest2(plant));
                }
            }
        }
        else if(randomNumber == 2){
            for(Plant plant : grid.values()){
                if(Objects.equals(plant.ID, "Plant3")){
                    if(Math.random() > 0.3) pestInfection(plant, new Pest3(plant));
                }
            }
        }
        else if(randomNumber == 3){
            for(Plant plant : grid.values()){
                if(Objects.equals(plant.ID, "Plant4")){
                    if(Math.random() > 0.3) pestInfection(plant, new Pest4(plant));
                }
            }
        }
    }


    // Functions to call every second
    private void dryPlants(){
        for(Plant plant: grid.values()){
            plant.setWaterLevel((int) (plant.getWaterLevel()-(10*Math.random())));
        }
    }

    private void absorbFertilizer(){
        for(Plant plant: grid.values()){
            plant.setFertilizerLevel((int) (plant.getFertilizerLevel()-(2*Math.random())));
        }
    }

    private boolean deathCondition(Plant plant){
        if(plant.getFertilizerLevel() <=5 && plant.getWaterLevel() <= 2) return true;
        return false;
    }

    private void deadPlant(){
        for(Plant plant: grid.values()){
            if(deathCondition(plant)) removePlant(plant.getCoordinate());
        }
    }

    // Functions to call every second for Automation

    public void iteration(){
        //System.out.println(grid.size());
        dryPlants();
        absorbFertilizer();
        autoWaterPlants();
        autoAddFertilizer();
        pestInvasion();
        deadPlant();
        if(count == 0) {
            autoAddPesticide();
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

