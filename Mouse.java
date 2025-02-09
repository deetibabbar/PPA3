import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Mouse extends Animal
{
    private static final int BREEDING_AGE = 4;
    private static final int MAX_AGE = 16;
    private static final double BREEDING_PROBABILITY = 0.25;
    private static final int MAX_LITTER_SIZE = 6;
    private static final int PLANT_FOOD_LEVEL = 9;
    private static final Random rand = Randomizer.getRandom();
    private int age;
    private int foodLevel;

    public Mouse(boolean randomAge, Location location)
    {
        super(location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        foodLevel = rand.nextInt(PLANT_FOOD_LEVEL);
    }
    
    @Override
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            List<Location> freeLocations = 
                nextFieldState.getFreeAdjacentLocations(getLocation());
            if(!freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations);
            }

            Location nextLocation = findFood(currentField);
            if(nextLocation == null && ! freeLocations.isEmpty()) {
                nextLocation = freeLocations.remove(0);
            }
            if(! freeLocations.isEmpty()) {
                nextLocation = freeLocations.get(0);
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
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

    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Mouse young = new Mouse(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
        
    private int breed()
    {
        int births;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    public Location findFood(Field field){
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
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
}
