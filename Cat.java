import java.util.*;

/**
 * A simple model of a cat
 * Cats age, move, breed, and die.
 * 
 * @author Deeti Babbar and Hannan Nur
 * @version 18.02.2025
 */
public class Cat extends Animal
{
    // Characteristics shared by all cats (class variables).
    // The age at which a cat can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a cat can live.
    private static final int MAX_AGE = 35;
    // The likelihood of a cat breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The maximum number of births
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single mouse. In effect, this is the
    // number of steps a cat can go before it has to eat again.
    private static final int MOUSE_FOOD_VALUE = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The cat's age.
    private int age;
    // The cat's food level, which is increased by eating mice.
    private int foodLevel;

    /**
     * Create a cat. A cat can be created as a new born (age zero
     * and not hungry) or with a random age and food level of a mouse food value.
     * 
     * @param randomAge If true, the cat will have random age and a hunger level.
     * @param location The location within the field.
     */
    public Cat(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = MOUSE_FOOD_VALUE;
    }
    
    /**
     * This is what the cat does most of the time: it hunts for
     * mice. In the process, it might breed, die of hunger,
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
            // Increment how long the cat has been infected 
            // and whether they should die from the disease.
            if (isDiseased()){
                incrementDisease();
                diseaseDeath();
            }
            // A list of free adjacent locations
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            // Newborn cat with or without the disease, can spread to neighbouring animals.
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
            // If it is the morning, then move two cells away.
            if (isActive()) {
                specialMovement(currentField, nextFieldState);
            }
            // Else, move only one cell away.
            else if (!isActive()){
                if(nextLocation != null) {
                    setLocation(nextLocation);
                    nextFieldState.placeAnimal(this, nextLocation);
                }
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    @Override
    public String toString() {
        return "Cat{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    /**
     * Increase the age. This could result in the cat's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this cat more hungry. This could result in the cat's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for mice adjacent to the current location.
     * Only the first live mouse is eaten.
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
            if(animal instanceof Mouse mouse) {
                if(mouse.isAlive()) {
                    mouse.setDead();
                    foodLevel = MOUSE_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }
    
    /**
     * Check whether this cat is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param currentField The field currently occupied
     * @param nextFieldState The field that stores the adjacent location and the newborn kitten.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // New cats are born into adjacent locations
        // Get a list of adjacent free locations.
        int births = breed(currentField);
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Cat young = new Cat(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @param field The field occupied by the cat
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
            // no breeding occurs
            births = 0;
        }
        return births;
    }

    /**
     * A cat can breed if it has reached the breeding age
     * and of opposite gender
     * @param field The field occupied by the cat
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
     * A cat is active in the morning.
     * @return true If it is the morning.
     */
    protected boolean isActive()
    {
        Time time = Simulator.getCurrentTime();
        int hour = time.getTime();
        return time.getTimeOfDay(hour) == TimeOfDay.MORNING;
    }

    /**
     * When the cat is active, they move two cells away from their
     * original location.
     * 
     * @param currentField The field the cat is occupied in
     * @param nextFieldState The field that stores the cat and the new location
     */
    public void specialMovement(Field currentField, Field nextFieldState)
    {
        // Get a list of adjacent locations that are two cells away from the cat
        List<Location> potentialLocations = currentField.getAdjacentLocations(getLocation(), 2);
        // Filter free locations in potentialLocations
        List<Location> freeLocations = new LinkedList<>();
        for (Location location : potentialLocations) 
        {
            // Check if there are no animals in the free locations.
            if (nextFieldState.getAnimalAt(location) == null) 
            {
                freeLocations.add(location);
            }
        }

        // Move to a random free location
        if (!freeLocations.isEmpty()) 
        {
            int randomLocation = rand.nextInt(freeLocations.size());
            Location nextLocation = freeLocations.get(randomLocation);
            setLocation(nextLocation);
            nextFieldState.placeAnimal(this, nextLocation);
        }
    }
}
