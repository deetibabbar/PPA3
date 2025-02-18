import java.awt.*;
import java.util.*;
import javax.swing.*;

public class SimulatorView extends JFrame {

    private static final Color EMPTY_COLOR = Color.white;
    private static final Color UNKNOWN_COLOR = Color.gray;
    private static final Color EARTHQUAKE_COLOR = Color.red;  // Color for affected area

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String TIME_PREFIX = "Time: ";
    private final String DAY_PREFIX = "Day: ";
    
    private final JLabel stepLabel;
    private final JLabel population;
    private final JLabel timeLabel;
    private final JLabel dayLabel;
    private final FieldView fieldView;
    
    private final Map<Class<?>, Color> colors;
    private final FieldStats stats;

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

        setTitle("Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        timeLabel = new JLabel(TIME_PREFIX, JLabel.CENTER);
        dayLabel = new JLabel(DAY_PREFIX, JLabel.CENTER);


        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        
        contents.add(stepLabel, BorderLayout.NORTH);
        contents.add(timeLabel, BorderLayout.EAST);
        contents.add(dayLabel, BorderLayout.WEST);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
    
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

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

    public void showStatus(int step, Field field, Earthquake earthquake, int time, int day)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        timeLabel.setText(TIME_PREFIX + time + ":00");
        stats.reset();
        dayLabel.setText(DAY_PREFIX + day);
        stats.reset();
        
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

    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }

    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private final int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

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
        
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

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
