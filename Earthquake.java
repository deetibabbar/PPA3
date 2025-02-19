/**
 * A simple model of an earthquake.
 * 
 * @author Deeti Babbar and Hannan Nur
 * @version 18.02.2025
 */
public class Earthquake implements Calamity 
{

    // A location instance to represent the centre of the earthquake.
    private Location epicenter;
    // The size of the radius of the earthquake.
    private int radius = 20;

    /**
     * Creates an earthquake.
     * @param epicenter The centre of the earthquake.
     */
    public Earthquake(Location epicenter) 
    {
        this.epicenter = epicenter;
    }

    /**
     * Creates an earthquake.
     * @param epicenter The centre of the earthquake.
     * @param radius The radius of the earthquake.
     */
    public Earthquake(Location epicenter, int radius) 
    {
        this(epicenter);
        this.radius = radius;
    }

    /**
     * Checks if the location is in the earthquake.
     * @param location The location in question.
     */
    @Override
    public boolean locationWithinCalamity(Location location) 
    {
        if(location == null) {
            return false;
        }
        int x = location.row();
        int y = location.col();
        int ex = epicenter.row();
        int ey = epicenter.col();
        return Math.sqrt((x - ex) * (x - ex) + (y - ey) * (y - ey)) <= radius;
    }
    
}
