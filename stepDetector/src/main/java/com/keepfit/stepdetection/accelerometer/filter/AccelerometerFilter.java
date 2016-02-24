package com.keepfit.stepdetection.accelerometer.filter;

public interface AccelerometerFilter {
    double[] filter(double x, double y, double z);
}
