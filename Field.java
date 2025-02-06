import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Field {
    private static final Random rand = Randomizer.getRandom();
    
    private final int depth, width;
    private final Map<Location, Animal> field = new HashMap<>();
    private final Map<Location, Plant> fieldPlant = new HashMap<>();

    private final List<Animal> animals = new ArrayList<>();
    private final List<Plant> plants = new ArrayList<>();

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
        field.put(location, anAnimal);
        animals.add(anAnimal);
    }

    public void placePlant(Plant plant, Location location){
        assert location != null;
        fieldPlant.put(location, plant);
        plants.add(plant);
    }
    
    public Animal getAnimalAt(Location location)
    {
        return field.get(location);
    }

    public Plant getPlantAt(Location location){
        return fieldPlant.get(location);
    }

    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location);
        for(Location next : adjacent) {
            Animal anAnimal = field.get(next);
            if(anAnimal == null) {
                free.add(next);
            }
            else if(!anAnimal.isAlive()) {
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
            if(anAnimal instanceof Owl owl) {
                if(owl.isAlive()) {
                    numOwls++;
                }
            }
            else if(anAnimal instanceof Mouse mouse) {
                if(mouse.isAlive()) {
                    numMice++;
                }
            }
            else if(anAnimal instanceof Deer deer) {
                if(deer.isAlive()) {
                    numDeers++;
                }
            }
            else if(anAnimal instanceof Cat cat) {
                if(cat.isAlive()) {
                    numCats++;
                }
            }
            else if(anAnimal instanceof Wolf wolf) {
                if(wolf.isAlive()) {
                    numWolves++;
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

    public int getDepth()
    {
        return depth;
    }
    
    public int getWidth()
    {
        return width;
    }
}
