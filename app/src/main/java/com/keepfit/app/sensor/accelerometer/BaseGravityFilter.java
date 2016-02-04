package com.keepfit.app.sensor.accelerometer;


public abstract class BaseGravityFilter implements GravityFilter {

    protected static final int VECTOR_SIZE = Constants.GRAVITY_VECTOR_SIZE;
    protected static final int X = 0;
    protected static final int Y = 1;
    protected static final int Z = 2;
    private final float _alpha;

    BaseGravityFilter(float alpha)
    {
        if (alpha < 0.0f || alpha > 1.0f) throw new IllegalArgumentException("alpha must be between 0 and 1");
        _alpha = alpha;
    }

    public float getAlpha() {
        return _alpha;
    }

}
