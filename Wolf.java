import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Wolf extends Animal
{
    // Characteristics shared by all wolves (class variables).
    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 50;
    // The likelihood of wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.07;
    // The maximum number of births
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single deer. In effect, this is the
    // number of steps a wolf can go before it has to eat again.
    private static final int DEER_FOOD_VALUE = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The wolf's age.
    private int age;
    // The wolf's food level, which is increased by eating deer.
    private int foodLevel;

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level (the deer food value).
     * 
     * @param randomAge If true, the wolf will have random age and a hunger level.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = DEER_FOOD_VALUE;
    }
    
    /**
     * This is what the wolf does most of the time: it hunts for
     * deer. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    @Override
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            // Increment how long the wolf has been infected 
            // and whether they should die from the disease.
            if (isDiseased()){
                incrementDisease();
                diseaseDeath();
            }
            // A list of free adjacent locations
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            // Newborn wolf with or without the disease, can spread to neighbouring animals.
            if(! freeLocations.isEmpty()) {
                giveBirth(currentField, nextFieldState, freeLocations);
                disease();
                spreadDisease(currentField);
            }
            // Move towards a source of food if found.
            Location nextLocation = findFood(currentField);
            if(nextLocation == null && ! freeLocations.isEmpty()) {
                // No food found - try to move to a free location.
                nextLocation = freeLocations.remove(0);
            }
            // Move to a free location
            if(nextLocation != null) {
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            }
            else {
                // Overcrowding
                setDead();
            }
        }
    }

    @Override
    public String toString() {
        return "Wolf{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    /**
     * Increase the age. This could result in the wolf's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this wolf more hungry. This could result in the wolf's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for deer adjacent to the current location.
     * Only the first live deer is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof Deer deer) {
                if(deer.isAlive()) {
                    deer.setDead();
                    foodLevel = DEER_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }
    
    /**
     * Check whether this wolf is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param currentField The field currently occupied
     * @param nextFieldState The field that stores the adjacent location and the newborn wolf.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // New wolves are born into adjacent locations
        // Get a list of adjacent free locations.
        int births = breed(currentField);
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Wolf young = new Wolf(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @param field The field occupied by the wolf.
     * @return The number of births (may be zero).
     */
    private int breed(Field field)
    {
        int births;
        if(canBreed(field) && rand.nextDouble() <= BREEDING_PROBABILITY) 
            {
                births = rand.nextInt(MAX_LITTER_SIZE) + 1;
            }
        else {
            // no breeding occurs.
            births = 0;
        }
        return births;
    }

    /**
     * A wolf can breed if it has reached the breeding age
     * and of opposite gender
     * @param field The field occupied by the wolf.
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
}
