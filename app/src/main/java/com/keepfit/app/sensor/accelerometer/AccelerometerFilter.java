package com.keepfit.app.sensor.accelerometer;

public interface AccelerometerFilter {
    float[] filter(float x, float y, float z);
}
