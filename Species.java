public class Species{

    private Location location;
    private boolean alive;

    public Species(Location location){
        this.location = location;
        this.alive = true;
    }

    public boolean isAlive()
    {
        return alive;
    }

    protected void setDead()
    {
        alive = false;
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