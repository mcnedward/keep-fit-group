package com.keepfit.stepdetection.algorithms;

public class AccelerationData {

    private final float x;
    private final float y;
    private final float z;
    private final long timeStamp;

    public AccelerationData(float x, float y, float z, long timeStamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timeStamp = timeStamp;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

}
