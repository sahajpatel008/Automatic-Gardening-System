package GardenEntities;

import java.time.Instant;

public class Plant {
    private int waterLevel; // Water level percentage
    private boolean isPestInfected;
    private int health;
    private int fertilizerLevel; // Hours of sunlight received
    private final Instant timeCreated;
    private final WaterSprinkler waterSprinkler;
    private Pest pest;
    private Sensor sensor;
    private int coordinate;
    public final String ID;

    public Plant(int coordinate, String ID) {
        this.waterLevel = 100;
        this.isPestInfected = false;
        this.fertilizerLevel = 100;
        this.health = 100;
        this.timeCreated = Instant.now();
        this.coordinate = coordinate;
        this.waterSprinkler = new WaterSprinkler(this, coordinate);
        this.sensor = new Sensor(this);
        this.ID = ID;
    }

    // Functionalities
    public void displayStatus() {
        System.out.println("ID: " + ID);
        System.out.println("Health: " + health);
        System.out.println("Water Level: " + waterLevel + "%");
        System.out.println("Pest Infected: " + (isPestInfected ? "Yes" : "No"));
        System.out.println("Sunlight Hours: " + fertilizerLevel);
    }

    public void waterPlant(){
        this.waterLevel = 100;
    }

    public void addPesticide(){
        this.isPestInfected = false;
        pest.destroyPest();
    }

    public void addFertilizer(){
        this.fertilizerLevel = 100;
    }

    // Getters
    public int getHealth() { return health; }
    public int getCoordinate() {
        return coordinate;
    }
    public int getWaterLevel() {
        return waterLevel;
    }
    public boolean isPestInfected() {
        return isPestInfected;
    }
    public int getFertilizerLevel() {
        return fertilizerLevel;
    }
    public Instant getTimeCreated() {
        return timeCreated;
    }
    public Pest getPest() {
        return pest;
    }

    // Setters
    public void setCoordinate(int coordinate) { this.coordinate = coordinate; }
    public void setHealth(int health) { this.health = health; }
    public void setPestInfected(boolean pestInfected) {
        isPestInfected = pestInfected;
    }
    public WaterSprinkler getWaterSprinkler() {
        return waterSprinkler;
    }
    public void setPest(Pest pest) {
        this.pest = pest;
    }
    public Sensor getSensor() {
        return sensor;
    }
    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }
    public void setFertilizerLevel(int fertilizerLevel) {
        this.fertilizerLevel = fertilizerLevel;
    }
}


