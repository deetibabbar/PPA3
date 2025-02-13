import java.util.*;

public class Field {
    private static final Random rand = Randomizer.getRandom();
    
    private final int depth, width;
    private final Map<Location, Animal> field = new HashMap<>();
    private final Map<Location, Plant> fieldPlant = new HashMap<>();
    private final Map<Location, Trap> fieldTrap = new HashMap<>();

    private final List<Animal> animals = new ArrayList<>();
    private final List<Plant> plants = new ArrayList<>();
    private final List<Trap> traps = new ArrayList<>();
    private final Set<Location> earthquakeAffected = new HashSet<>();
    private static final int MAX_EARTHQUAKE_RADIUS = 9;


    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
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
        List<Location> adjacent = getAdjacentLocations(location);
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

    public List<Location> getAdjacentLocations(Location location)
    {
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }

            Collections.shuffle(locations, rand);
        }
        return locations;
    }

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

    // public void triggerEarthquake(){
    //     earthquakeAffected.clear();

    //     Random rand = new Random();
    //     int epicenterX = rand.nextInt(width);
    //     int epicenterY = rand.nextInt(depth);
    //     Location epicenter = new Location(epicenterX, epicenterY);
    //     int radius = rand.nextInt(MAX_EARTHQUAKE_RADIUS) + 2;

    //     for (int row = 0; row < depth; row++) {
    //         for (int col = 0; col < width; col++) {
    //             Location loc = new Location(row, col);
    //             if (isWithinRadius(epicenter, loc, radius)) {
    //                 clear(loc); 
    //                 earthquakeAffected.add(loc);
    //             }
    //         }
    //     }   
    // }

    // private boolean isWithinRadius(Location epicenter, Location loc, int radius) {
    //     int dx = epicenter.row() - loc.row();
    //     int dy = epicenter.col() - loc.col();
    //     return Math.sqrt(dx * dx + dy * dy) <= radius;
    // }

    // public boolean wasHitByEarthquake(Location loc) {
    //     return earthquakeAffected.contains(loc);
    // }
    
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
}
