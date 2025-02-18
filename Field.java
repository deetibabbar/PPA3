import java.util.*;

public class Field {
    private static final Random rand = Randomizer.getRandom();
    
    private static int depth = 0, width = 0;
    private int currentDepth, currentWidth;
    private static final int DEFORESTATION_RATE = 1;

    private final Map<Location, Animal> field = new HashMap<>();
    private final Map<Location, Plant> fieldPlant = new HashMap<>();
    private final Map<Location, Trap> fieldTrap = new HashMap<>();

    private final List<Animal> animals = new ArrayList<>();
    private final List<Plant> plants = new ArrayList<>();
    private final List<Trap> traps = new ArrayList<>();


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

    public void placePlant(Plant plant, Location location){
        assert location != null;
        Object other = field.get(location);
        if (other != null && other instanceof Trap){
            plants.remove(plant);
        }
        fieldPlant.put(location, plant);
        plants.add(plant);
    }

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
    
    public Animal getAnimalAt(Location location)
    {
        return field.get(location);
    }

    public Plant getPlantAt(Location location){
        return fieldPlant.get(location);
    }

    public Trap getTrapAt(Location location){
        return fieldTrap.get(location);
    }

    public boolean containsTrap(Location location){
        return fieldTrap.containsKey(location);
    }

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

    public List<Location> getAdjacentLocations(Location location, int n)
    {
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -n; roffset <= n; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < currentDepth) {
                    for(int coffset = -n; coffset <= n; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < currentWidth && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }

            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    // public List<Location> getLocationsFromNCellsAway(Location location, int n)
    // {
    //     List<Location> locations = new ArrayList<>();
    //     if(location != null) {
    //         int row = location.row();
    //         int col = location.col();
    //         for(int roffset = -n; roffset <= n; roffset++) {
    //             int nextRow = row + roffset;
    //             if(nextRow >= 0 && nextRow < depth) {
    //                 for(int coffset = -n; coffset <= n; coffset++) {
    //                     int nextCol = col + coffset;
    //                     // Exclude invalid locations and the original location.
    //                     if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
    //                         locations.add(new Location(nextRow, nextCol));
    //                     }
    //                 }
    //             }
    //         }

    //         Collections.shuffle(locations, rand);
    //     }
    //     return locations;
    // }
    

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

    public void clear()
    {
        field.clear();
    }

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
    
    public List<Animal> getAnimals()
    {
        return animals;
    }

    public List<Plant> getPlants()
    {
        return plants;
    }

    public List<Trap> getTraps()
    {
        return traps;
    }

    public int getDepth()
    {
        return depth;
    }
    
    public int getWidth()
    {
        return width;
    }

    public int getCurrentDepth()
    {
        return currentDepth;
    }
    
    public int getCurrentWidth()
    {
        return currentWidth;
    }

    public void triggerDeforestation(){
        if (currentDepth > 2 * DEFORESTATION_RATE && currentWidth > 2 * DEFORESTATION_RATE){
            currentDepth -= DEFORESTATION_RATE;
            currentWidth -= DEFORESTATION_RATE;
        }

        animals.removeIf(animal -> !isInsideBounds(animal.getLocation()));
        plants.removeIf(plant -> !isInsideBounds(plant.getLocation()));
        traps.removeIf(trap -> !isInsideBounds(trap.getLocation()));
        
    }

    public boolean isInsideBounds(Location location){
        if (location == null){
            return false;
        }
        else{
        return location.row() >= 0 && location.row() < currentDepth && location.col() >= 0 && location.col() < currentWidth;
        }
    }
}
