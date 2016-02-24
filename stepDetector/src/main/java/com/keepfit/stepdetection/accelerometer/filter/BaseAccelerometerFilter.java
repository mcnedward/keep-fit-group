package com.keepfit.stepdetection.accelerometer.filter;


public abstract class BaseAccelerometerFilter implements AccelerometerFilter {

    protected static final int VECTOR_SIZE = Constants.ACCELEROMETER_VECTOR_SIZE;
    protected static final int X = 0;
    protected static final int Y = 1;
    protected static final int Z = 2;
    private final double _alpha;

    public BaseAccelerometerFilter(double alpha) {
        if (alpha < 0.0f || alpha > 1.0f)
            throw new IllegalArgumentException("alpha must be between 0 and 1");
        _alpha = alpha;
    }

        public double getAlpha() {
        return _alpha;
    }

}
