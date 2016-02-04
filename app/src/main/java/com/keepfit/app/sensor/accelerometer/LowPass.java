package com.keepfit.app.sensor.accelerometer;

/*
    * time smoothing constant for low-pass filter
    * 0 ≤ alpha ≤ 1 ; a smaller value basically means more smoothing
    * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
    */
public class LowPass extends BaseGravityFilter {

    private float[] _cachedValues;

    public LowPass(float alpha) {
        super(alpha);
        _cachedValues = new float[VECTOR_SIZE];
    }

    @Override
    public float[] filter(float x, float y, float z) {
        _cachedValues[X] = _cachedValues[X] + getAlpha() * (x - _cachedValues[X]);
        _cachedValues[Y] = _cachedValues[Y] + getAlpha() * (y - _cachedValues[Y]);
        _cachedValues[Z] = _cachedValues[Z] + getAlpha() * (z - _cachedValues[Z]);
        return _cachedValues;
    }
}
