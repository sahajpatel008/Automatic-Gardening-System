package Handler;

import GardenEntities.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GardenHandler {
    private HashMap<Integer, Plant> grid;
    private int count;
    private final Logger logger;
    private String weather;
    private int seconds;
    private int minutes;
    private int hours;
    private int days;
    private int temperature;
    private int currHour;
        // Constructor
    public GardenHandler(Logger logger) {
        grid = new HashMap<>();
        count = 0;
        seconds = 0;
        minutes = 0;
        hours = 0;
        days = 0;
        weather = "Sunny";
        temperature = 0;
        updateTemperature();
        this.logger = logger;
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
        int delta = 8;
        if(weather.equals("Sunny")){
            delta = 14;
        }
        else if(weather.equals("Rainy")){
            delta = -5;
        }

        for(Plant plant: grid.values()){
            if(plant.getWaterLevel()> 10) {
                plant.setWaterLevel((int) (plant.getWaterLevel() - (delta * Math.random()))%100);
            }
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

    private void updateTime(){
        seconds++;
        if(seconds == 60) minutes++;
        seconds %= 60;
        if(minutes == 60) hours++;
        minutes %= 60;
        if(hours == 24) days++;
        hours %= 24;
    }

    public String getTime(){
        return Integer.toString(days)+ " Days : " + Integer.toString(hours)+ " Hours : " + Integer.toString(minutes) + " Minutes : " + Integer.toString(seconds) + " Seconds";
    }

    private void updateWeather(){
        if(hours%2 == 0){
            int rand = (int) (Math.random() * 4);
            if(rand == 0){
                weather = "Sunny";
            }
            if(rand == 1){
                weather = "Rainy";
            }
            if(rand == 2){
                weather = "Cloudy";

            }
            if (rand == 3){
                weather = "Clear";

            }
        }
        updateTemperature();
    }

    private void updateTemperature(){
        if(weather.equals("Sunny")){
            temperature = 35+ (int) (Math.random()*10);
        }
        if(weather.equals("Rainy")){
            temperature = 15+ (int) (Math.random()*10);
        }
        if(weather.equals("Cloudy")){
            temperature = 20+ (int) (Math.random()*10);
        }
        if(weather.equals("Clear")){
            temperature = 25+ (int) (Math.random()*10);
        }
    }


    private void essentialFunctions(int count){
        if(count%30 == 0) {
            dryPlants();
            absorbFertilizer();
            pestInvasion();
            updateHealth();
            updateTemperature();
        }
        if(count == 0)updateWeather();
        deadPlant();
        updateTime();
    }

    private void automaticFunctions(){
        autoWaterPlants();
        autoAddFertilizer();
    }

    public void iteration(boolean isAutomatic){
        //System.out.println(grid.size());
        essentialFunctions(count);
        if(isAutomatic){
            automaticFunctions();
            if(count%50 == 0) {
                autoAddPesticide();
            }
        }
        count++;
        count%=1000;
    }

    public HashMap<Integer, Plant> getGrid() {
        return grid;
    }

    public String getWeather() {
        return weather;
    }

    public int getTemperature() {
        return temperature;
    }
}

