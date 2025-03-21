package GardenEntities;

import java.time.Instant;

public class Pest {
    private final Instant timeCreated;
    private final Plant plant;
    private final String ID;

    public Pest(Plant plant, String ID) {
        this.timeCreated = Instant.now();
        this.plant = plant;
        plant.setPestInfected(true);
        plant.setPest(this);
        this.ID = ID;
    }

    public void destroyPest(){
        plant.setPestInfected(false);
        plant.setPest(null);
    }

    public String getID() {
        return ID;
    }
}
