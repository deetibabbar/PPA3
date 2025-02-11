import java.util.List;
import java.util.Random;

public class Deer extends Animal
{
    private static final int BREEDING_AGE = 6;
    private static final int MAX_AGE = 35;
    private static final double BREEDING_PROBABILITY = 0.08;
    private static final int MAX_LITTER_SIZE = 2;
    private static final Random rand = Randomizer.getRandom();
    private int age;

    private Time time = new Time(0,0);

    public Deer(boolean randomAge, Location location)
    {
        super(location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    @Override
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        if(isAlive() && time.timeOfDay() == Time.timeOfDay.DAY) {
            List<Location> freeLocations = 
                nextFieldState.getFreeAdjacentLocations(getLocation());
            if(!freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations);
            }
            if(! freeLocations.isEmpty()) {
                Location nextLocation = freeLocations.get(0);
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
        return "Deer{" +
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

    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        int births = breed(nextFieldState);
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Deer young = new Deer(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
    
    // private int breed()
    // {
    //     int births;
    //     if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
    //         births = rand.nextInt(MAX_LITTER_SIZE) + 1;
    //     }
    //     else {
    //         births = 0;
    //     }
    //     return births;
    // }

    private int breed(Field field)
    {
        int births = 0;
        if (!genderCheck(field))
        {
            if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) 
            {
                births = rand.nextInt(MAX_LITTER_SIZE) + 1;
            }   
        }
        return births;
    }

    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
