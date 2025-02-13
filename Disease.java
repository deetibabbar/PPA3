import java.util.Random;

public class Disease {
    private final double INFECTION_PROBABILITY = 0.5;
    private final double DEATH_PROBABILITY = 0.5;
    
    private static final Random random = Randomizer.getRandom();
    
    private boolean infected;
    private boolean dead;
    
    public Disease()
    {
        infected = false;
        dead = false;
    }
    
    public void disease()
    {
        double randValue = random.nextDouble();
        if(randValue <= INFECTION_PROBABILITY) {
            infected = true;
        } else {
            infected = false;
        }
        if(randValue <= DEATH_PROBABILITY) {
            dead = true;
        } else {
            dead = false;
        }
    }
    
    public boolean getInfected()
    {
        return infected;
    }
    
    public boolean getDead()
    {
        return dead;
    }
    
    public void setInfected(boolean infected)
    {
        this.infected = infected;
    }
}
