package com.keepfit.stepdetection.algorithms;

import com.keepfit.stepdetection.accelerometer.filter.AccelerometerFilter;
import com.keepfit.stepdetection.accelerometer.filter.Constants;
import com.keepfit.stepdetection.accelerometer.filter.HighPass;
import com.keepfit.stepdetection.accelerometer.filter.Util;

public class AccelerationData {

    private final double x;
    private final double y;
    private final double z;
    private final Double acceleration;
    private final long timeStamp;
    private static AccelerometerFilter filter;

    public AccelerationData(double x, double y, double z, long timeStamp) {
        this(x, y, z, null, timeStamp);
    }

    public AccelerationData(double x, double y, double z, Double acceleration, long timeStamp) {
        this.x =  x;
        this.y = y;
        this.z = z;
        this.acceleration = acceleration;
        this.timeStamp = timeStamp;
    }

    public double getAcceleration() {
        if (acceleration != null)
            return acceleration;
        if (filter == null)
            filter = new HighPass(Constants.HIGH_PASS_ALPHA);
        double[] accelerationVector = filter.filter(x, y, z);
        if (accelerationVector == null) throw new IllegalArgumentException("ERROR: values arg cannot be null");
        if (accelerationVector.length < 1)
            throw new IllegalArgumentException("ERROR: values array arg cannot be empty");
        return Math.sqrt(sumOfSquares(accelerationVector));
    }

    private double sumOfSquares(double[] values) {
        if (values == null) throw new IllegalArgumentException("ERROR: values arg cannot be null");
        if (values.length < 1)
            throw new IllegalArgumentException("ERROR: values array arg cannot be empty");
        float sos = 0;
        double squared = 2;
        for (double f : values) {
            sos += Math.pow(f, squared);
        }
        return sos;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public double getXYZMagnitude() {
        return Util.magnitude(new double[]{x, y, z});
    }

}
