import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Trackingfleet {
    private static Vehicles vehicles = new Vehicles();

    public static void main(String[] args) {
        System.out.print("enter number of cars");
        java.util.Scanner input = new java.util.Scanner(System.in);
        // ask user for number of threads

        int number = input.nextInt();
        ExecutorService executor = Executors.newCachedThreadPool();

        // create and launch number of threads created by user
        for (int i = 0; i < number; i++) {
            executor.execute(new UpdateLocation());
        }
        executor.shutdown();

        // wait till all tasks are done
        while (!executor.isTerminated()) {
            Map<Integer, Location> result = vehicles.getLocations();
            for (int regnum : result.keySet()) {
                System.out.println("resgistration number is " + regnum + " longitude is " + result.get(regnum).longit
                        + " latitude is " + result.get(regnum).lat);
            }
        }
    }

    // A thread for updating the position of a car,if the car exists we update its
    // location if
    // it doesn't we create a new car with the registration number and give it the
    // inputted location
    public static class UpdateLocation implements Runnable {
        public void run() {
            Random rand = new Random();
            int regnum = rand.nextInt(1000);
            double longi = rand.nextDouble() * 100;
            double lat = rand.nextDouble() * 100;
            vehicles.setLocation(regnum, longi, lat);
        }
    }

    // vehicles class for controlling the locations of cars
    public static class Vehicles {
        private static Lock lock = new ReentrantLock();
        private Map<Integer, Location> locations = new HashMap<>();

        // all methods in this class are synchronized so as to ensure thread safety
        // when multiple GPS devices are trying to update location simultaneously
        public void setLocation(int registration_number, double longi, double lat) {
            lock.lock();
            try {
                locations.put(registration_number, new Location(longi, lat));
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                // TODO: handle exception
            } finally {
                lock.unlock();
            }
        }

        public synchronized Location getLocation(int registration_number) {
            return locations.get(registration_number);
        }

        public synchronized Map<Integer, Location> getLocations() {
            Map<Integer, Location> result = new HashMap<Integer, Location>();
            for (int registration_number : locations.keySet()) {
                result.put(registration_number, new Location(locations.get(registration_number).longit,
                        locations.get(registration_number).lat));
            }
            return result;
        }
    }
}

// location class for creating a location object for a car
class Location {
    public double longit, lat;

    public Location(double longit, double lat) {
        this.longit = longit;
        this.lat = lat;
    }
}
