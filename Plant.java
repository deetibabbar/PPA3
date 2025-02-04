public class Plant extends Species{

    public static final double GROWTH_RATE = 0.10;
    public static final double REPRODUCTION_PROBABILITY = 0.25;
    private boolean consumed;

    public Plant(Location location){
        super(location);
        this.consumed = false;
    }
}