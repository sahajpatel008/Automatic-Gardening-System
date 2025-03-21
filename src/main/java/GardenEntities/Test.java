package GardenEntities;

import Handler.GardenHandler;
import javafx.scene.control.TextArea;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test {

    private static GardenHandler handler;

    public static void main(String[] args) {
        handler = new GardenHandler(null);
        handler.addPlant(0,new Plant1(0));
        runFor24Hours();
    }

    public static void runFor24Hours() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable taskWrapper = () -> {
            try {
                // write here
//                task();
                handler.iteration(true);
                handler.getGrid().get(0).displayStatus();
            } catch (Exception e) {
                System.err.println("Error in task execution: " + e.getMessage());
            }
        };

        scheduler.scheduleAtFixedRate(taskWrapper, 0, 5, TimeUnit.SECONDS);

        // Schedule shutdown after 24 hours
        scheduler.schedule(() -> {
            System.out.println("24-hour period complete. Shutting down.");
            scheduler.shutdown();
        }, 120, TimeUnit.HOURS);
    }


}