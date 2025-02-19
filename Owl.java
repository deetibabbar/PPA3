import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Owl extends Animal
{
    private static final int BREEDING_AGE = 6;
    private static final int MAX_AGE = 100;
    private static final double BREEDING_PROBABILITY = 0.10;
    private static final int MAX_LITTER_SIZE = 5;
    private static final int MOUSE_FOOD_VALUE = 5;
    private static final int CAT_FOOD_VALUE = 9;
    private static final Random rand = Randomizer.getRandom();
    private int age;
    private int foodLevel;

    public Owl(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = rand.nextInt(MOUSE_FOOD_VALUE);
    }
    
    @Override
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (isDiseased()){
                incrementDisease();
                diseaseDeath();
            }
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if(! freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations);
                disease();
                spreadDisease(currentField);
            }
      
            Location nextLocation = findFood(currentField);
            if(nextLocation == null && !freeLocations.isEmpty()) {
          
                nextLocation = freeLocations.remove(0);
            }
       
            if(nextLocation != null) {
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
        return "Owl{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
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
    
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
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
    
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Owl young = new Owl(false, loc);
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
}
