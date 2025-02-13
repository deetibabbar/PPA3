public class Earthquake implements Calamity {

    private Location epicenter;
    private int radius = 9;

    public Earthquake(Location epicenter) {
        this.epicenter = epicenter;
    }

    public Earthquake(Location epicenter, int radius) {
        this(epicenter);
        this.radius = radius;
    }

    // @Override
    // public void getAffectedLocation() {
    // }

    @Override
    public boolean locationWithinCalamity(Location location) {
        if(location == null) {
            return false;
        }
        // System.out.println("Checking location: " + "<" + location.row() + ", " + location.col() + ">");
        int x = location.row();
        int y = location.col();
        int ex = epicenter.row();
        int ey = epicenter.col();
        return Math.sqrt((x - ex) * (x - ex) + (y - ey) * (y - ey)) <= radius;
    }
    
}
