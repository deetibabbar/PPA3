import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author David J. Barnes, Michael Kölling, Deeti Babbar and Hannan Nur
 * @version 7.0
 */
public class SimulatorView extends JFrame {

    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;
    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;
    private static final Color EARTHQUAKE_COLOR = Color.red;  // Color for affected area

    // The prefixes displayed on the GUI.
    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String TIME_PREFIX = "Time: ";
    private final String DAY_PREFIX = "Day: ";

    // The JLabel of the step, population, time and day.
    private final JLabel stepLabel;
    private final JLabel population;
    private final JLabel timeLabel;
    private final JLabel dayLabel;

    // The field's appearance.
    private final FieldView fieldView;
    
    // A map for storing colors for participants in the simulation.
    private final Map<Class<?>, Color> colors;
    // A statistics object computing and storing simulation information.
    private final FieldStats stats;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();
        setColor(Trap.class, Color.black);
        setColor(Mouse.class, Color.orange);
        setColor(Owl.class, Color.magenta);
        setColor(Cat.class, Color.cyan);
        setColor(Deer.class, Color.yellow);
        setColor(Wolf.class, Color.lightGray);
        setColor(Plant.class, Color.green);

        setTitle("Survival of the Fittest");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        timeLabel = new JLabel(TIME_PREFIX, JLabel.CENTER);
        dayLabel = new JLabel(DAY_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);

        fieldView = new FieldView(height, width);
        
        Container contents = getContentPane();

        // The top bar of the GUI.
        JPanel topPanel = new JPanel(new GridLayout(1, 2));

        topPanel.add(stepLabel);
        topPanel.add(timeLabel);

        // The bottom bar of the GUI.
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.add(population);
        bottomPanel.add(dayLabel);

        contents.add(topPanel, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(bottomPanel, BorderLayout.SOUTH);
        
        pack();
        setVisible(true);
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class<?> animalClass)
    {
        Color col = colors.get(animalClass);
        if(col == null) {
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     * @param time The current time of the simulation.
     * @param day The current day of the simulation.
     */
    public void showStatus(int step, Field field, Earthquake earthquake, int time, int day)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        timeLabel.setText(TIME_PREFIX + time + ":00");
        dayLabel.setText(DAY_PREFIX + day);
        
        fieldView.preparePaint();

        // Loop through all locations in the field
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location loc = new Location(row, col);
                Object animal = field.getAnimalAt(loc);
                Plant plant = field.getPlantAt(loc);
                Trap trap = field.getTrapAt(loc);
                if (row > field.getCurrentDepth() || col > field.getCurrentWidth()){
                    fieldView.drawMark(col, row, Color.BLACK);
                }
                else if (earthquake != null && earthquake.locationWithinCalamity(loc)) {
                    fieldView.drawMark(col, row, EARTHQUAKE_COLOR);  // Mark affected locations in red
                } else {
                    if(trap != null){
                        fieldView.drawMark(col, row, getColor(Trap.class));
                    }
                    else if(animal != null) {
                        stats.incrementCount(animal.getClass());
                        fieldView.drawMark(col, row, getColor(animal.getClass()));
                    }
                    else if(plant != null){
                        fieldView.drawMark(col, row, getColor(Plant.class));
                    }
                    else {
                        fieldView.drawMark(col, row, EMPTY_COLOR);
                    }
                }
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }

    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private final int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        @Override
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }

}
