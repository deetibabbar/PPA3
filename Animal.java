import java.util.Random;
public abstract class Animal {
    private boolean alive;
    private Location location;
    
    // an integer that represents gender, male = 0 and female = 1
    private int gender;

    private Random rand = new Random();
    
    //private Field field;
    
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
        //this.field = field;
        
        gender = rand.nextInt(2);
    }
    
    abstract public void act(Field currentField, Field nextFieldState);
    
    public boolean isAlive()
    {
        return alive;
    }
    
    /**
     * @return true if female, otherwise false for male
     * female = 1
     * male = 0
     */
    public boolean getGender()
    {
        if (gender == 1){
            return true;
        }
        return false;
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
    
    // protected Field getField()
    // {
        // return field;
    // }
}