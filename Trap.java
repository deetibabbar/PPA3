/**
 * A simple model of a trap.
 * Traps kill any animal and plant.
 * 
 * @author Deeti Babbar and Hannan Nur
 * @version 18.02.2025
 */
public class Trap
{

    // Location given.
    private Location location;
    
    /**
     * Constructor of class Trap
     * Creates a trap at the given location.
     * @param location The given location.
     */
    public Trap(Location location){

        this.location = location;

    }

    /**
     * The trap is placed in the field.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        // Set the trap at the location given.
        Location nextLocation = this.location;
        setLocation(nextLocation);
        nextFieldState.placeTrap(this, nextLocation);
    }

    /**
     * Return the trap's location.
     * @return The trap's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the trap's location.
     * @param location The new location.
     */
    public void setLocation(Location location)
    {
        this.location = location;
    }

    /**
     * Remove a trap from the given location.
     * @param location The given location.
     */
    public void removeTrap(Location location){
        this.location = null;
    }
}