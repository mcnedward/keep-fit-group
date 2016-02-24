package com.keepfit.stepdetection.accelerometer.filter;

public class HighPass extends BaseAccelerometerFilter {

    private final double[] _gravity;

    public HighPass(float alpha) {
        super(alpha);
        _gravity = new double[VECTOR_SIZE];
    }

    /**
     * This method derived from the Android documentation and is available under
     * the Apache 2.0 license.
     *
     * @see "http://developer.android.com/reference/android/hardware/SensorEvent.html"
     */
    public double[] filter(double x, double y, double z) {
        double[] filteredValues = new double[VECTOR_SIZE];

        _gravity[X] = getAlpha() * _gravity[X] + (1 - getAlpha()) * x;
        _gravity[Y] = getAlpha() * _gravity[Y] + (1 - getAlpha()) * y;
        _gravity[Z] = getAlpha() * _gravity[Z] + (1 - getAlpha()) * z;

        filteredValues[X] = x - _gravity[X];
        filteredValues[Y] = y - _gravity[Y];
        filteredValues[Z] = z - _gravity[Z];

        return filteredValues;
    }

}
