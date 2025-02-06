public class Time {
    
    private int time;
    private int day;
    
    public Time()
    {
        this.time = 0;
        this.day = 0;
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
        if (time < 24)
        {
            if (step % 6 == 0)
            {
                time += 6;
            }
            if (time == 24)
            {
                time = 0;
            } 
        }  
    }

    public void setDay(int step)
    {
        setTime(step);
        if (time == 0)
        {
            day++;
        }
    }

    public String timeOfDay()
    {
        String[] stages = {"Day", "Night"};
        if (time == 0 || time == 18)
        {
            return stages[1];
        }
        else 
        {
            return stages[0];
        }
    }
}
