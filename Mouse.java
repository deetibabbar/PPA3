import java.util.*;

/**
 * A simple model of a mouse
 * Mouse age, move, breed, and die.
 * 
 * @author Deeti Babbar and Hannan Nur
 * @version 18.02.2025
 */
public class Mouse extends Animal
{
    // Characteristics shared by all mice (class variables).
    // The age at which a mouse can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a mouse can live.
    private static final int MAX_AGE = 18;
    // The likelihood of a mouse breeding.
    private static final double BREEDING_PROBABILITY = 0.16;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single plant. In effect, this is the
    // number of steps a mouse can go before it has to eat again.
    private static final int PLANT_FOOD_LEVEL = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The mouse's age.
    private int age;
    // The mouse's food level, which is increased by eating plants.
    private int foodLevel;

    /**
     * Create a mouse. A mouse can be created as a new born (age zero
     * and not hungry) or with a random age and a hunger level of a plant's food value.
     * 
     * @param randomAge If true, the mouse will have random age and a hunger level.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Location location)
    {
        super(location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        foodLevel = PLANT_FOOD_LEVEL;
    }
    
    /**
     * This is what the mouse does most of the time - it runs 
     * around in the morning and in the afternoon.
     * But running is restricted when it is the afternoon.
     * Sometimes it will breed or die of old age.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    @Override
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            // Increment how long the mouse has been infected 
            // and whether they should die from the disease.
            if (isDiseased()){
                incrementDisease();
                diseaseDeath();
            }
            List<Location> freeLocations = 
                nextFieldState.getFreeAdjacentLocations(getLocation());
            
            if(!freeLocations.isEmpty()) {
                giveBirth(currentField, nextFieldState, freeLocations);
                disease();
                spreadDisease(currentField);
                // There is a 50% chance of moving to a free location in the afternoon
                if (!isActive() && rand.nextBoolean()) {
                    setLocation(getLocation());
                    nextFieldState.placeAnimal(this, getLocation());
                }
            }
            // Try to move into a free location.
            if(!freeLocations.isEmpty()) {
                Location nextLocation = freeLocations.get(0);
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            }
            // Move towards a source of food if found.
            if(!freeLocations.isEmpty()) {
                Location nextLocation = findFood(currentField);
                if(nextLocation == null && ! freeLocations.isEmpty()) {
                    // No food found - try to move to a free location.
                    nextLocation = freeLocations.remove(0);
                }
            }
            else {
                setDead();
            }
        }
    }

    @Override
    public String toString() {
        return "Mouse{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Increase the age. This could result in the mouse's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this mouse more hungry. This could result in the mouse's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Check whether this mouse is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param currentField The field currently occupied
     * @param nextFieldState The field that stores the adjacent location and the newborn mouse.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // New mouse are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(currentField);
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Mouse young = new Mouse(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @param field The field currently occupied
     * @return The number of births (may be zero).
     */
    private int breed(Field currentField)
    {
        int births;
        if(canBreed(currentField) && rand.nextDouble() <= BREEDING_PROBABILITY) 
        {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            // no breeding occurs
            births = 0;
        }
        return births;
    }

    /**
     * A mouse can breed if it has reached the breeding age
     * and of opposite gender
     * @param field The field occupied by the mouse
     */
    private boolean canBreed(Field field)
    {
        if (age >= BREEDING_AGE) {
            if (genderCheck(field)){
                return true;
            }
        }
        return false;
    }

    /**
     * Look for plant adjacent to the current location.
     * Only the first live plant is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    public Location findFood(Field field){
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Plant plant = field.getPlantAt(loc);  
            if(plant != null && plant.isAlive()) {
                plant.setDead();
                foodLevel = PLANT_FOOD_LEVEL;
                foodLocation = loc;
            }
        }
        return foodLocation;
    }

    /**
     * A mouse is active in the morning and at night.
     * @return true If it is the morning and night.
     */
    protected boolean isActive()
    {
        Time time = Simulator.getCurrentTime();
        int hour = time.getTime();
        return time.getTimeOfDay(hour) != TimeOfDay.AFTERNOON;
    }
}
