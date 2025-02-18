import java.util.*;

public class Disease{

    private static final int DISEASE_LIFETIME = 2;
    private static final double DEATH_PROBABILITY = 0.5;
    private static final Random rand = Randomizer.getRandom();

    public Disease(){
    }

    public boolean diseaseExpired(Animal animal){
        return animal.getInfectedSince() >= DISEASE_LIFETIME;
    }

    public boolean animalDemise(){
        return rand.nextDouble() <= DEATH_PROBABILITY;
    }
}