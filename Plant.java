import java.util.*;

/**
 * A simple model of plants.
 * Plants can grow, distribute to other locations and die.
 * 
 * @author Deeti Babbar and Hannan Nur.
 * @version 18.02.2025
 */
public class Plant{

    // A location instance.
    private Location location;
    // Checks if the plant was eaten.
    private boolean consumed;
    // The likelihood of a plant growing.
    private static final double GROWTH_RATE = 0.06;
    // Maximum number of plants that can be grown.
    private static final int MAX_CHILDREN = 3;
    // A shared random number generator to control growth.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Creates a plant at a location.
     * @param location Where in the field.
     */
    public Plant(Location location){

        this.location = location;
        this.consumed = false;

    }

    /**
     * This is what a plant does: it grows.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState){
        if(isAlive()){
            // Plant is added at a specific location.
            Location nextLocation = this.location;
            setLocation(nextLocation);
            nextFieldState.placePlant(this, nextLocation);
    
            // Plant can grow at other free adjacent locations.
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if(! freeLocations.isEmpty()) {
                reproduce(nextFieldState, freeLocations);
            }
        }
    }

    /**
     * Check whether this plant is to grow at another location at this step.
     * New growth will be made into free adjacent locations.
     * @param nextFieldState The field that stores the adjacent location and the new plant.
     * @param freeLocations The locations that are free in the current field.
     */
    public void reproduce(Field nextFieldState, List<Location> freeLocations)
    {
        // New plants are grown into adjacent locations
        // Get a list of adjacent free locations.
        int births = grow();
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                if (!nextFieldState.containsTrap(loc)){
                    Plant young = new Plant(loc);
                    nextFieldState.placePlant(young, loc);
                }
            }
        }
    }

    /**
     * Check whether the plant is alive or not.
     * @return true If the plant is still alive.
     */
    public boolean isAlive(){
        return !consumed;
    }

    /**
     * Indicate that the plant is no longer alive.
     */
    protected void setDead()
    {
        consumed = true;
        location = null;
    }
    
    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the plant's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }

    /**
     * Generate a number representing the number of births,
     * if it can grow.
     * @param field The field occupied by the plant.
     * @return The number of births (may be zero).
     */
    private int grow()
    {
        int births;
        if(rand.nextDouble() <= GROWTH_RATE) {
            births = rand.nextInt(MAX_CHILDREN) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }
}
