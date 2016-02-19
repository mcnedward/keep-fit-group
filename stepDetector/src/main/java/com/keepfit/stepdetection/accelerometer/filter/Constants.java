package com.keepfit.stepdetection.accelerometer.filter;

public class Constants {
    public static final float HIGH_PASS_ALPHA = 0.8f;
    public static final float LOW_PASS_ALPHA = 0.15f;
    public static final int HIGH_PASS_MINIMUM = 10;
    public static final int ACCELEROMETER_VECTOR_SIZE = 3;
    public static final int SAMPLE_RATE = 10000; // (microsec -> sample rate = 100Hz)
}
