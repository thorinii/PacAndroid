package pacandroid.util;

public interface Timer {

    public boolean isTick();

    public boolean beforeTick();

    public boolean afterTick();
    
    public float getTime();
}
