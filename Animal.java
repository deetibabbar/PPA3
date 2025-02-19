import java.util.*;
/**
 * Common elements of wolves, deer, owls, cats and mice.
 *
 * @author David J. Barnes, Michael Kölling, Deeti Babbar and Hannan Nur
 * @version 7.0
 */
public abstract class Animal 
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position
    private Location location;

    // The likehood of animal born with a disease
    private static final double DISEASE_PROBABILITY = 0.07;
    // The likehood of contracting a disease
    private static final double CONTAGIOUS_PROBABILITY = 0.03;
    // A shared random number generator to assign gender to the animals.
    private final Random rand = new Random();
    // An integer that represents gender.
    private final int gender;
    // A disease instance.
    private Disease disease;
    // A counter to count how long an animal has been infected.
    private int infectedSince;

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
        gender = rand.nextInt(2);
    }
    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract public void act(Field currentField, Field nextFieldState);
    
    /**
     * Check whether the animal is alive or not.
     * @return true If the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }
    
    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the animal's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }

    /**
     * Check whether the animal is infected.
     * @return true If the animal is infected.
     */
    protected boolean isDiseased() {
        return disease!=null;
    }

    /**
     * Creates a disease when an animal is not infected
     * 
     */
    protected void disease(){
        if (!isDiseased() && (rand.nextDouble() <= DISEASE_PROBABILITY)){
            disease = new Disease();
        }
    }

    /**
     * Infected animals spread the disease to animals in close proximity
     * @param field
     */
    protected void spreadDisease(Field field){
        if(isDiseased()) {
            List<Location> adjacent = field.getAdjacentLocations(getLocation(), 1);
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

    /**
     * Spreads the disease to another animal
     */
    protected void passDisease() {
        this.disease = new Disease();
    }

    /**
     * Infected animal may die if they suffered with the disease 
     * for a long time and if the death probability is met.
     * Otherwise, they recover.
     * 
     */
    protected void diseaseDeath(){
        if (disease.diseaseExpired(this) && disease.animalDemise()){
            setDead();
        }
        else {
            disease = null;
        }
    }

    /**
     * Counts how long an infected animal contracted the disease
     * @return infectedSince How many steps since an animal got the disease.
     */
    public int getInfectedSince(){
        return infectedSince;
    }

    /**
     * Increse the duration of infection.
     */
    public void incrementDisease(){
        infectedSince++;
    }

    /**
     * Check whether the animal is a female or male.
     * Where a female animal = 1 and a male animal = 0.
     * @return true if female, otherwise false for male
     */
    public boolean getGender()
    {
        return gender == 1;
    }

    /**
     * Check if two ADJACENT animals are of opposite gender
     * @param currentField The current state of the field.
     * @return true if the two animals are of opposite gender
     */
    protected boolean genderCheck(Field currentField)
    {   
        // A list of adjacent locations
        List<Location> adjacent = currentField.getAdjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();

        // Iterates through the list to get the adjacent location
        while(it.hasNext())
        {
            Location loc = it.next();
            // finds the adjacent animal to this.animal
            Animal adjacentAnimal = currentField.getAnimalAt(loc);
            if (adjacentAnimal != null)
            {
                if (checkOppositeGender(adjacentAnimal)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if two animals are of the same class and the opposite gender
     * @param animal The animal in question
     * @return true If both animals are in the same class and opposite gender
     */
    protected boolean checkOppositeGender(Animal animal)
    {
        return (this.getClass().equals(animal.getClass())) && 
        (this.getGender() != animal.getGender());
    }

    /**
     * Check if an animal should not be asleep in the morning
     * @return true If the time of day is the morning or the afternoon
     */
    protected boolean isNotAsleep()
    {
        Time time = Simulator.getCurrentTime();
        int hour = time.getTime();
        return time.getTimeOfDay(hour) != TimeOfDay.NIGHT;
    }
}