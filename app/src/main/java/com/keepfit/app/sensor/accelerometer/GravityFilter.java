package com.keepfit.app.sensor.accelerometer;

public interface GravityFilter {
    float[] filter(float x, float y, float z);
}
