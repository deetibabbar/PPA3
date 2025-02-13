public class Time {
    
    private int hour;
    private int day;
    
    public Time()
    {
        this.hour = 0;
        this.day = 1;
    }

    public int getHour()
    {
        return hour;
    }

    public int getDay()
    {
        return day;
    }
    
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

    public TimeOfDay getTimeOfDay()
    {
        if (hour >= 6 || hour <= 12)
        {
            return TimeOfDay.MORNING;
        }
        else if (hour > 12 || hour <= 18)
        {
            return TimeOfDay.AFTERNOON;
        }
        else
        {
            return TimeOfDay.NIGHT;
        }
    }

    public enum TimeOfDay {
        MORNING, AFTERNOON, NIGHT;
    }
}
