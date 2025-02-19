/**
 * Represents the time system in the animal simulation.
 * 
 * @author Deeti Baddar and Hannan Nur
 * @version 18.02.2025
 */
public class Time {
    // The hour and day of the animal simulation. 
    private static int hour;
    private static int day;
    
    /**
     * Reperesnt the hour and day of the animal simulation
     * Both starts at 0 to start the animal simulation at 0am on the
     * 0th day.
     */
    public Time()
    {
        hour = 0;
        day = 0;
    }
    
    /**
     * Set the hour and day.
     * The hour increments by 6 every 6th step and resets to 0
     * every 24th step.
     * @param step The current step of the animal simulation.
     */
    public void setTime(int step)
    {
        if (step % 6 == 0)
        {
            hour += 6;
        }
        if (hour == 24)
        {
            hour = 0;
            day++;
        }  
    }

    /**
     * Return the hour of the time.
     * @return The hour.
     */
    public int getTime()
    {
        return hour;
    }

    /**
     * Return the day of the animal simulation.
     * @return The day.
     */
    public int getDay()
    {
        return day;
    }

    /**
     * Return the time of day in the animal simulation.
     * @param hour The hour in the animal simulation.
     * @return Either MORNING, AFTERNOON or NIGHT.
     */
    public TimeOfDay getTimeOfDay(int hour)
    {
        if (hour >= 6 && hour <= 12)
        {
            return TimeOfDay.MORNING;
        }
        else if (hour > 12 && hour <= 18)
        {
            return TimeOfDay.AFTERNOON;
        }
        else
        {
            return TimeOfDay.NIGHT;
        }
    }

}
