package pacandroid.util;

import java.util.ArrayList;
import java.util.List;

public class Timers {

    private static final List<UpdateableTimer> TIMERS = new ArrayList<UpdateableTimer>();

    public static Timer getRecurringTimer(float tickTime) {
        RecurringTimer t = new RecurringTimer(tickTime);
        TIMERS.add(t);

        return t;
    }

    public static Timer getSingleTimer(float tickTime) {
        SingleTimer t = new SingleTimer(tickTime);
        TIMERS.add(t);

        return t;
    }

    public static void update(float delta) {
        for (UpdateableTimer t : TIMERS)
            t.update(delta);
    }

    private interface UpdateableTimer extends Timer {

        public void update(float delta);
    }

    private static class RecurringTimer implements UpdateableTimer {

        private final float tickTime;
        private float time;

        public RecurringTimer(float tickTime) {
            this.tickTime = tickTime;
            this.time = 0;
        }

        @Override
        public boolean isTick() {
            if (time >= tickTime) {
                time = 0;
                return true;
            }

            return false;
        }

        @Override
        public boolean beforeTick() {
            return time < tickTime;
        }

        @Override
        public boolean afterTick() {
            return false;
        }

        @Override
        public float getTime() {
            return time;
        }

        @Override
        public void update(float delta) {
            time += delta;
        }
    }

    private static class SingleTimer implements UpdateableTimer {

        private final float tickTime;
        private float time;
        private boolean ticked;

        public SingleTimer(float tickTime) {
            this.tickTime = tickTime;
            this.time = 0;
            this.ticked = false;
        }

        @Override
        public boolean isTick() {
            if (time >= tickTime && !ticked) {
                time = 0;
                ticked = true;
                return true;
            }

            return false;
        }

        @Override
        public boolean beforeTick() {
            return time < tickTime;
        }

        @Override
        public boolean afterTick() {
            return ticked && time > tickTime;
        }

        @Override
        public float getTime() {
            return time;
        }

        @Override
        public void update(float delta) {
            time += delta;
        }
    }
}
