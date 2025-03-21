package GardenEntities;

public class WaterSprinkler {
    private Plant plant;
    private final int coordinate;

    public WaterSprinkler(Plant plant, int coordinate) {
        this.plant = plant;
        this.coordinate = coordinate;
    }

    public void waterPlant(){
        plant.waterPlant();
    }
}
