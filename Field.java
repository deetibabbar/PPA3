import java.util.*;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal/object.
 * 
 * @author David J. Barnes, Michael Kölling, Deeti Babbar and Hannan Nur
 * @version 7.0
 */
public class Field {
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The dimensions of the field.
    private static int depth = 0, width = 0;
    private int currentDepth, currentWidth;

    // The rate of deforestation.
    private static final int DEFORESTATION_RATE = 1;

    // Animals mapped by location.
    private final Map<Location, Animal> field = new HashMap<>();
    // Plants mapped by location.
    private final Map<Location, Plant> fieldPlant = new HashMap<>();
    // Traps mapped by location.
    private final Map<Location, Trap> fieldTrap = new HashMap<>();

    // A new list of animals, plants and traps.
    private final List<Animal> animals = new ArrayList<>();
    private final List<Plant> plants = new ArrayList<>();
    private final List<Trap> traps = new ArrayList<>();

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        if (Field.depth == 0){
            Field.depth = depth;
        }
        if (Field.width == 0){
            Field.width = width;
        }
        this.currentDepth = depth;
        this.currentWidth = width;
    }

    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param anAnimal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void placeAnimal(Animal anAnimal, Location location)
    {
        assert location != null;
        Object other = field.get(location);
        if(other != null && other instanceof Animal animal) {
            animals.remove(animal);
        }
        else if (other == null || other instanceof Plant){
            field.put(location, anAnimal);
            animals.add(anAnimal);
        }
        else if (other instanceof Trap){
            animals.remove(anAnimal);
            anAnimal.setDead();
        }
    }

    /**
     * Place a plant at the given location.
     * If there is already a plant at the location it will
     * be lost.
     * @param plant The plant to be placed.
     * @param location Where to place the plant.
     */
    public void placePlant(Plant plant, Location location){
        assert location != null;
        if(!fieldPlant.containsKey(location))
        {
            fieldPlant.put(location, plant);
            plants.add(plant);
        } 
        else {
            fieldPlant.put(location, plant);
        }
    }

    /**
     * Place a trap at the given location.
     * If there is already a trap at the location it will
     * be lost.
     * @param anAnimal The trap to be placed.
     * @param location Where to place the trap.
     */
    public void placeTrap(Trap trap, Location location){
        assert location != null;
        Object other = field.get(location);
        if (other != null && other instanceof Animal animal){
            animals.remove(animal);
        }
        else if (other != null && other instanceof Plant plant){
            plants.remove(plant);
        }
        fieldTrap.put(location, trap);
        traps.add(trap);
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Animal getAnimalAt(Location location)
    {
        return field.get(location);
    }

    /**
     * Return the plant at the given location, if any.
     * @param location Where in the field.
     * @return The plant at the given location, or null if there is none.
     */
    public Plant getPlantAt(Location location){
        return fieldPlant.get(location);
    }

    /**
     * Return the trap at the given location, if any.
     * @param location Where in the field.
     * @return The trap at the given location, or null if there is none.
     */
    public Trap getTrapAt(Location location){
        return fieldTrap.get(location);
    }

    /**
     * Checks if a location has a trap.
     * @param location Where in the field
     * @return true If there is a trap at that location.
     */
    public boolean containsTrap(Location location){
        return fieldTrap.containsKey(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location, 1);
        for(Location next : adjacent) {
            Object item = field.get(next);
            if(item instanceof Plant || item == null) {
                free.add(next);
            }
            else if(item instanceof Animal anAnimal && !anAnimal.isAlive()) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @param numCells How many cells away from the given location.
     * @return A list of locations adjacent by a certain number of cells to that given.
     */
    public List<Location> getAdjacentLocations(Location location, int numCells)
    {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -numCells; roffset <= numCells; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -numCells; coffset <= numCells; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Print out the number of animals in the field.
     */
    public void fieldStats()
    {
        int numOwls = 0, numMice = 0, numDeers = 0, numCats = 0, numWolves = 0;
        for(Animal anAnimal : field.values()) {
            switch (anAnimal) {
                case Owl owl -> {
                    if(owl.isAlive()) {
                        numOwls++;
                    }
                }
                case Mouse mouse -> {
                    if(mouse.isAlive()) {
                        numMice++;
                    }
                }
                case Deer deer -> {
                    if(deer.isAlive()) {
                        numDeers++;
                    }
                }
                case Cat cat -> {
                    if(cat.isAlive()) {
                        numCats++;
                    }
                }
                case Wolf wolf -> {
                    if(wolf.isAlive()) {
                        numWolves++;
                    }
                }
                default -> {
                }
            }
        }
        System.out.println("Mice: " + numMice +
                           " Owls: " + numOwls +
                           " Cats: " + numCats +
                           " Wolves: " + numWolves +
                           " Deers: " + numDeers);
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        field.clear();
    }

    /**
     * Empty any objects from a specific location.
     * @param location Where in the field.
     */
    public void clear(Location location) {
        // Remove any animal, plant, or trap at this location
        field.remove(location);
        fieldPlant.remove(location);
        fieldTrap.remove(location);

        // Remove from the lists, checking for null locations
        animals.removeIf(animal -> {
            Location loc = animal.getLocation();
            return loc != null && loc.equals(location);
        });

        plants.removeIf(plant -> {
            Location loc = plant.getLocation();
            return loc != null && loc.equals(location);
        });

        traps.removeIf(trap -> {
            Location loc = trap.getLocation();
            return loc != null && loc.equals(location);
        });
    }

    /**
     * Return whether there is at least one mouse, owl, deer, cat and wolf in the field.
     * @return true if there is at least each animal in the field.
     */
    public boolean isViable()
    {
        boolean mouseFound = false; 
        boolean owlFound = false;
        boolean deerFound = false;
        boolean catFound = false;
        boolean wolfFound = false;
        Iterator<Animal> it = animals.iterator();
        while(it.hasNext() && ! (mouseFound && owlFound && deerFound && catFound && wolfFound)) {
            Animal anAnimal = it.next();
            if(anAnimal instanceof Mouse mouse) {
                if(mouse.isAlive()) {
                    mouseFound = true;
                }
            }
            else if(anAnimal instanceof Owl owl) {
                if(owl.isAlive()) {
                    owlFound = true;
                }
            }
            else if(anAnimal instanceof Wolf wolf) {
                if(wolf.isAlive()) {
                    wolfFound = true;
                }
            }
            else if(anAnimal instanceof Deer deer) {
                if(deer.isAlive()) {
                    deerFound = true;
                }
            }
            else if(anAnimal instanceof Cat cat) {
                if(cat.isAlive()) {
                    catFound = true;
                }
            }
        }
        return mouseFound && owlFound && deerFound && catFound && wolfFound;
    }   
    
    /**
     * Get the list of animals.
     */
    public List<Animal> getAnimals()
    {
        return animals;
    }

    /**
     * Get the list of plants.
     */
    public List<Plant> getPlants()
    {
        return plants;
    }

    /**
     * Get the list of traps.
     */
    public List<Trap> getTraps()
    {
        return traps;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Return the updated depth of the field.
     * @return The updated depth of the field.
     */
    public int getCurrentDepth()
    {
        return currentDepth;
    }
    
    /**
     * Return the updated width of the field.
     * @return The updated width of the field.
     */
    public int getCurrentWidth()
    {
        return currentWidth;
    }

    /**
     * Starts the deforestation.
     */
    public void triggerDeforestation(){
        if (currentDepth > 2 * DEFORESTATION_RATE && currentWidth > 2 * DEFORESTATION_RATE){
            currentDepth -= DEFORESTATION_RATE;
            currentWidth -= DEFORESTATION_RATE;
        }

        // Remove objects in the deforested field.
        animals.removeIf(animal -> !isInsideBounds(animal.getLocation()));
        plants.removeIf(plant -> !isInsideBounds(plant.getLocation()));
        traps.removeIf(trap -> !isInsideBounds(trap.getLocation()));
        
    }

    /**
     * Checks if a location is in the deforested field.
     * @param location Where in the field.
     * @return true If the location has been removed by deforestation.
     */
    public boolean isInsideBounds(Location location){
        if (location == null){
            return false;
        }
        else{
            return location.row() >= 0 && location.row() < currentDepth && 
            location.col() >= 0 && location.col() < currentWidth;
        }
    }
}
