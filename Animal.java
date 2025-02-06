import java.util.List;
import java.util.Random;
public abstract class Animal {
    private boolean alive;
    private Location location;
    
    // an integer that represents gender, male = 0 and female = 1
    private int gender;

    private Random rand = new Random();
        
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
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
    
    protected boolean genderCheck(Field field)
    {   
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        for (Location beside : adjacent)
        {
            Animal adjacentAnimal = (Animal) field.getAnimalAt(beside);
            if (adjacentAnimal != null && this.getGender() != adjacentAnimal.getGender())
            {
                return true; 
            }
        }
        return false;
    }

    protected void specialMovement()
    {
        
    }
}