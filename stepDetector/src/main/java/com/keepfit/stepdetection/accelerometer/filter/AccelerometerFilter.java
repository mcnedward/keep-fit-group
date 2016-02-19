package com.keepfit.stepdetection.accelerometer.filter;

public interface AccelerometerFilter {
    float[] filter(float x, float y, float z);
}
