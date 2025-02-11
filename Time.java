public class Time {
    
    private int time;
    private int day;
    
    public Time(int time, int day)
    {
        this.time = time;
        this.day = day;
    }

    public int getTime()
    {
        return time;
    }

    public int getDay()
    {
        return day;
    }
    
    public void setTime(int step)
    {
        if (step % 6 == 0)
        {
            time += 6;
        }
        if (time == 24)
        {
            time = 0;
            day++;
        }  
    }

    public timeOfDay timeOfDay()
    {
        if (time == 0 || time == 18)
        {
            return timeOfDay.NIGHT;
        }
        else 
        {
            return timeOfDay.DAY;
        }
    }

    public enum timeOfDay {
        DAY, NIGHT;
    }
}
