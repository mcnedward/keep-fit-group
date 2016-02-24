package com.keepfit.stepdetection.accelerometer.filter;

/*
    * time smoothing constant for low-pass filter
    * 0 ≤ alpha ≤ 1 ; a smaller value basically means more smoothing
    * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
    */
public class LowPass extends BaseAccelerometerFilter {

    private double[] _cachedValues;

    public LowPass(float alpha) {
        super(alpha);
        _cachedValues = new double[VECTOR_SIZE];
    }

    @Override
    public double[] filter(double x, double y, double z) {
        _cachedValues[X] = _cachedValues[X] + getAlpha() * (x - _cachedValues[X]);
        _cachedValues[Y] = _cachedValues[Y] + getAlpha() * (y - _cachedValues[Y]);
        _cachedValues[Z] = _cachedValues[Z] + getAlpha() * (z - _cachedValues[Z]);
        return _cachedValues;
    }
}
