import java.util.Random;

public class Weather
{
    private static final double SUN_PROBABILITY = 0.7;
    
    private static final Random random = Randomizer.getRandom();
    
    private boolean rain;
    private boolean sun;

    public Weather()
    {
        rain = false;
        sun = false;
    }

    public void weather()
    {
        double randValue = random.nextDouble();
        if(randValue <= SUN_PROBABILITY) {
            sun = true;
            rain = false;
        } else {
            sun = false;
            rain = true;
        }
    }

    public String getWeather()
    {
        weather();
        if(rain) {
            return "Rain";
        } else {
            return "Sun";
        }
    }
    
    public boolean getRaining()
    {
        return rain;
    }
    
    public boolean getSunny()
    {
        return sun;
    }
}
