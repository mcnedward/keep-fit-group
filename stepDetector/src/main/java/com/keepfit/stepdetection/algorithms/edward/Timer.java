package com.keepfit.stepdetection.algorithms.edward;

import android.util.Log;

/**
 * Created by Edward on 3/1/2016.
 */
public class Timer {
    // Last time in nanoseconds
    private static long halfSecond = 500000000;
    private long lastTime;

    // Returns change in time as double
    private long getDeltaTime() {
        return (System.nanoTime() - lastTime);
    }

    // Updates lastTime
    private void updateTime() {
        this.lastTime = System.nanoTime();
    }

    // Public constructor for Timer object
    public Timer() {
        this.lastTime = System.nanoTime();
    }

    // Returns true if a half halfSecond has passed and updates time,
    // otherwise returns false and does nothing.
    public boolean hasHalfSecondPassed() {
        if (getDeltaTime() >= halfSecond) {
            return true;
        } else return false;
    }
}