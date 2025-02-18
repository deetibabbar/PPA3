
import java.util.*;

public class Plant{

    private Location location;
    private boolean consumed;
    private static final double GROWTH_RATE = 0.05;
    private static final int MAX_CHILDREN = 3;
    private static final Random rand = Randomizer.getRandom();

    public Plant(Location location){

        this.location = location;
        this.consumed = false;

    }

    public void act(Field currentField, Field nextFieldState){
        if(isAlive()){
            Location nextLocation = this.location;
            setLocation(nextLocation);
            nextFieldState.placePlant(this, nextLocation);
    
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if(! freeLocations.isEmpty()) {
                reproduce(nextFieldState, freeLocations);
            }
        }
    }

    public void reproduce(Field nextFieldState, List<Location> freeLocations){
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

    public boolean isAlive(){
        return !consumed;
    }

    protected void setDead()
    {
        consumed = true;
        location = null;
    }
    
    public Location getLocation()
    {
        return location;
    }
    
    protected void setLocation(Location location)
    {
        this.location = location;
    }

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
