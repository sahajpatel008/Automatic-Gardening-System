package GardenEntities;

public class Sensor {
    Plant plant;

    public Sensor(Plant plant) {
        this.plant = plant;
    }

    public boolean isInfected(){
        return plant.isPestInfected();
    }

    public boolean needsWater(){
        return plant.getWaterLevel()<=20;
    }

    public boolean needsFertilizer(){
        return plant.getFertilizerLevel()<=10;
    }
}
