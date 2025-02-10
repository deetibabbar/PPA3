public class Trap{

    private Location location;
    
    public Trap(Location location){

        this.location = location;

    }

    public void act(Field currentField, Field nextFieldState){
            Location nextLocation = this.location;
            setLocation(nextLocation);
            nextFieldState.placeTrap(this, nextLocation);
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