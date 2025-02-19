import java.util.*;

/**
 * A disease class to track when to kill the infected animals
 * 
 * @author Deeti Babbar and Hannan Nur
 * @version 18.02.2025
 */
public class Disease
{

    // Class constants

    // How long the disease lasts for.
    private static final int DISEASE_LIFETIME = 2;
    // The likelihood of dying from the disease.
    private static final double DEATH_PROBABILITY = 0.5;
    // A randomiser to generate a number and compare with 
    // the disease lifetime and death probability.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Disease.
     */
    public Disease(){
    }

    /**
     * Checks if the animal has been infected
     * for longer than the disease lifetime.
     * @param animal The infected animal.
     * @return true If the animal has been infected longer than the disease lifetime.
     */
    public boolean diseaseExpired(Animal animal){
        return animal.getInfectedSince() >= DISEASE_LIFETIME;
    }

    /**
     * Checks if the infected animal should die from the disease.
     * @return true If the randomiser generates a number less than 
     *  or equal to the death probability.
     */
    public boolean animalDemise(){
        return rand.nextDouble() <= DEATH_PROBABILITY;
    }
}