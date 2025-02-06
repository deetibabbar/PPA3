import java.util.*;

public class Simulator
{
    private static final int DEFAULT_WIDTH = 130;
    private static final int DEFAULT_DEPTH = 130;
    private static final double OWL_CREATION_PROBABILITY = 0.02;
    private static final double MOUSE_CREATION_PROBABILITY = 0.06;   
    private static final double CAT_CREATION_PROBABILITY = 0.05;
    private static final double WOLF_CREATION_PROBABILITY = 0.07;
    private static final double DEER_CREATION_PROBABILITY = 0.05; 

    private Field field;
    private int step;
    private final SimulatorView view;
    
    private  int time;
    private int day;

    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
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

        reset();
    }
    
    public void runLongSimulation()
    {
        simulate(700);
    }
         
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(50);       
        }
    }
    
    public void simulateOneStep()
    {
        //step++;
        stepTime();
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Animal> animals = field.getAnimals();
        for (Animal anAnimal : animals) {
            anAnimal.act(field, nextFieldState);
        }
        
        field = nextFieldState;

        reportStats();
        view.showStatus(step, field, time, day);
    }
        
    public void reset()
    {
        step = 0;
        populate();
        view.showStatus(step, field, time, day);
    }
    
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= OWL_CREATION_PROBABILITY) {
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
            }
        }
    }

    public void reportStats()
    {
        field.fieldStats();
    }
    
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
        }
    }
    
    public int getTime()
    {
        return time;
    }
    
    public void stepTime()
    {
        step++;
        time = step%4;
        day = 0;
        if (time == 0)
        {
            day++;
        }
    }

    public static void main(String[] args) {
        Simulator sim1 = new Simulator();
        sim1.simulate(2500);
    }
}


