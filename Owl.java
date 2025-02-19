import java.util.*;

/**
 * A simple model of a owl
 * Owl age, move, breed, and die.
 * 
 * @author Deeti Babbar and Hannan Nur
 * @version 18.02.2025
 */
public class Owl extends Animal
{
    // Characteristics shared by all owls (class variables).
    // The age at which a owl can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which an owl can live.
    private static final int MAX_AGE = 50;
    // The likelihood of an owl breeding.
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single mouse. In effect, this is the
    // number of steps a owl can go before it has to eat again.
    private static final int MOUSE_FOOD_VALUE = 10;
    // The food value of a single cat. In effect, this is the
    // number of steps a owl can go before it has to eat again.
    private static final int CAT_FOOD_VALUE = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields)

    // The owl's age
    private int age;
    // The owl's food level, which is increased by eating mice and cats.
    private int foodLevel;

    /**
     * Create a owl. An owl can be created as a new born (age zero
     * and not hungry) or with a random age and hunger level of a cat's food value.
     * 
     * @param randomAge If true, the owl will have random age and a hunger level.
     * @param location The location within the field.
     */
    public Owl(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = CAT_FOOD_VALUE;
    }
    
    /**
     * This is what the owl does most of the time: it hunts for
     * mice and cats. In the process, it might breed, die of hunger,
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
            // Increment how long the owl has been infected 
            // and whether they should die from the disease.
            if (isDiseased()){
                incrementDisease();
                diseaseDeath();
            }
            // A list of free adjacent locations.
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
            if(nextLocation == null && !freeLocations.isEmpty()) {
                // No food found - try to move to a free location.
                nextLocation = freeLocations.remove(0);
            }

            // At night, owls move 2 cells away.
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
        return "Owl{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    /**
     * Increase the age. This could result in the owl's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this owl more hungry. This could result in the cat's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for cat and mice adjacent to the current location.
     * Only the first live mouse or the first live cat is eaten.
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
            if (animal == null){
                continue;
            }
            switch (animal) {
                case Mouse mouse -> {
                    if(mouse.isAlive()) {
                        mouse.setDead();
                        foodLevel = MOUSE_FOOD_VALUE;
                        foodLocation = loc;
                    }
                }
                case Cat cat -> {
                    if(cat.isAlive()) {
                        cat.setDead();
                        foodLevel = CAT_FOOD_VALUE;
                        foodLocation = loc;
                    }
                }
                default -> {
                }
            }
        }
        return foodLocation;
    }
    
    /**
     * Check whether this owl is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param currentField The field currently occupied
     * @param nextFieldState The field that stores the adjacent location and the newborn owl.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // New owls are born into adjacent locations
        // Get a list of adjacent free locations.
        int births = breed(currentField);
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Owl young = new Owl(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @param field The field occupied by the owl.
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
            // No breeding occurs
            births = 0;
        }
        return births;
    }

    /**
     * A owl can breed if it has reached the breeding age
     * and of opposite gender
     * @param field The field occupied by the owl.
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
     * An owl is active at night.
     * @return true If it is night time.
     */
    protected boolean isActive()
    {
        Time time = Simulator.getCurrentTime();
        int hour = time.getTime();
        return time.getTimeOfDay(hour) == TimeOfDay.NIGHT;
    }

    /**
     * When the owl is active, they move two cells away from their
     * original location.
     * 
     * @param currentField The field the owl is occupied in
     * @param nextFieldState The field that stores the owl and the new location.
     */
    public void specialMovement(Field currentField, Field nextFieldState)
    {
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
