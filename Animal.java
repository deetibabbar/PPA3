
import java.util.*;

public abstract class Animal {
    private boolean alive;
    private Location location;
    private static final double DISEASE_PROBABILITY = 0.07;
    private static final double CONTAGIOUS_PROBABILITY = 0.03;
    private Random rand = new Random();
    private Disease disease;
    private int infectedSince;
    
    // an integer that represents gender, male = 0 and female = 1
    private int gender;
        
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

    protected boolean isDiseased() {
        return disease!=null;
    }

    protected void disease(){
        if (!isDiseased() && (rand.nextDouble() <= DISEASE_PROBABILITY)){
            disease = new Disease();
        }
    }

    protected void spreadDisease(Field field){
        if(isDiseased()) {
            List<Location> adjacent = field.getAdjacentLocations(getLocation());
            for (Location loc : adjacent) {
                Animal animal = field.getAnimalAt(loc);
                if(animal!=null && this.getClass().equals(animal.getClass())) {
                    if (rand.nextDouble() <= CONTAGIOUS_PROBABILITY ) {
                        animal.passDisease();
                    }
                }
            }
        }
    }

    protected void passDisease() {
        this.disease = new Disease();
    }

    protected void diseaseDeath(){
        if (disease.diseaseExpired(this) && disease.animalDemise()){
            setDead();
        }
        else {
            disease = null;
        }
    }


    public int getInfectedSince(){
        return infectedSince;
    }

    public void incrementDisease(){
        infectedSince++;
    }

    
    
    protected boolean genderCheck(Field field)
    {   
        List<Location> adjacent = field.getAdjacentLocations(getLocation(), 1);
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
}