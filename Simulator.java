import java.util.*;

/**
 * Survival of the Fittest simulator: a simple animal simulator, 
 * based on a rectangular field containing cats, deer, mice, owls and wolves
 * 
 * @author David J. Barnes, Michael Kölling, Deeti Babbar and Hannan Nur
 * @version 7.1
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 130;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 130;

    // The probability that each animal, plant and trap will be created in any given grid position.
    private static final double OWL_CREATION_PROBABILITY = 0.02;
    private static final double MOUSE_CREATION_PROBABILITY = 0.06;   
    private static final double CAT_CREATION_PROBABILITY = 0.05;
    private static final double WOLF_CREATION_PROBABILITY = 0.06;
    private static final double DEER_CREATION_PROBABILITY = 0.09; 
    private static final double PLANT_CREATION_PROBABILITY = 1;
    private static final double TRAP_CREATION_PROBABILITY = 0.0007;

    // The likelihood of an earthquake created.
    private static final double EARTHQUAKE_CREATION_PROBABILITY = 0.08;
    
    // Used to check when deforestation should occurs.
    private static final int DEFORESTATION_INTERVAL = 10;

    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private static int step;
    // A graphical view of the simulation.
    private final SimulatorView view;

    // A randomiser.
    private final Random rand;
    
    // A new time instance.
    private static final Time time = new Time();

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        field = new Field(depth, width);
        view = new SimulatorView(depth, width);
        rand = new Random();

        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(700);
    }
    
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(50); // delayed by 50 milliseconds 
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each animal.
     */
    public void simulateOneStep()
    {
        step++;
        //setting the time
        time.setTime(step);
        // Start deforestation
        if (step % DEFORESTATION_INTERVAL == 0){
            field.triggerDeforestation();
        }
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getCurrentDepth(), field.getCurrentWidth());
        Earthquake earthquake = null;

        // Create an earthquake
        if (rand.nextDouble() < EARTHQUAKE_CREATION_PROBABILITY) {
            int x = rand.nextInt(field.getCurrentDepth());
            int y = rand.nextInt(field.getCurrentWidth());
            earthquake = new Earthquake(new Location(x, y));
            System.out.println("--------Earthquake triggered at step " + step + ". At location: " + x + ", " + y);
        }

        // Kill animals affected by the earthquake.
        List<Animal> animals = field.getAnimals();
        for (Animal anAnimal : animals) {
            if(earthquake != null && earthquake.locationWithinCalamity(anAnimal.getLocation())) {
                anAnimal.setDead();
            }else{
                anAnimal.act(field, nextFieldState);
            }
        }

        // Add traps.
        List<Trap> traps = field.getTraps();
        for (Trap aTrap : traps) {
            aTrap.act(field, nextFieldState);
        }

        // Kill plants affected by the earthquake.
        List<Plant> plants = field.getPlants();
        for (Plant aPlant : plants) {
            if(earthquake != null && earthquake.locationWithinCalamity(aPlant.getLocation())) {
                aPlant.setDead();
            }else{
                aPlant.act(field, nextFieldState);
            }
        }
        // Replace the old state with the new one.
        field = nextFieldState;
        reportStats();
        view.showStatus(step, field, earthquake, time.getTime(), time.getDay());
    }
    
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        populate();
        view.showStatus(step, field, null, time.getTime(), time.getDay());
    }
    
    /**
     * Randomly populate the field with all animals, plants and traps.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= TRAP_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Trap trap = new Trap(location);
                    field.placeTrap(trap, location);
                }
                else if(rand.nextDouble() <= OWL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Owl owl = new Owl(true, location);
                    field.placeAnimal(owl, location);
                }
                else if(rand.nextDouble() <= MOUSE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Mouse mouse = new Mouse(true, location);
                    field.placeAnimal(mouse, location);
                }
                else if(rand.nextDouble() <= CAT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Cat cat = new Cat(true, location);
                    field.placeAnimal(cat, location);
                }
                else if(rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Wolf wolf = new Wolf(true, location);
                    field.placeAnimal(wolf, location);
                }
                else if(rand.nextDouble() <= DEER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Deer deer = new Deer(true, location);
                    field.placeAnimal(deer, location);
                }
                else if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(location);
                    field.placePlant(plant, location);
                }
            }
        }
    }

    /**
     * Report on the number of each type of animal in the field.
     */
    public void reportStats()
    {
        field.fieldStats();
    }
    
    /**
     * Pause for a given time.
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
        }
    }

    /**
     * Returns the current time of the simulation
     * @return The current time
     */
    public static Time getCurrentTime()
    {
        return time;
    }

    /**
     * Returns the iteration step of the simulation
     * @return The iteration step
     */
    public static int getStep()
    {
        return step;
    }
}


