
import java.util.*;

public class Plant{

    private Location location;
    private boolean consumed;
    private static final double GROWTH_RATE = 0.05;
    private static final Random rand = Randomizer.getRandom();

    public Plant(Location location){

        this.location = location;
        this.consumed = false;

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



}
