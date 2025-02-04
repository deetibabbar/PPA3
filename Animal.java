public abstract class Animal {

    private boolean alive;
    private Location location;

    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
    }
    
    abstract public void act(Field currentField, Field nextFieldState);
    
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