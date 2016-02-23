package com.keepfit.stepdetection.algorithms;

import com.keepfit.stepdetection.accelerometer.filter.Util;

public class AccelerationData {

    private final double x;
    private final double y;
    private final double z;
    private final long timeStamp;

    public AccelerationData(float x, float y, float z, long timeStamp) {
        this.x = (double) x;
        this.y = (double)y;
        this.z = (double)z;
        this.timeStamp = timeStamp;
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

    public double getXYZMagnitude(){
        return Util.magnitude(new double[]{x, y, z});
    }

}
