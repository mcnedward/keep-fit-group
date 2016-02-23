package com.keepfit.stepdetection.accelerometer.filter;

public class Util {

    public static double magnitude(double[] values) {
        if (values == null) throw new IllegalArgumentException("ERROR: values arg cannot be null");
        if (values.length < 1)
            throw new IllegalArgumentException("ERROR: values array arg cannot be empty");
        return Math.sqrt(sumOfSquares(values));
    }

    public static float sumOfSquares(double[] values) {
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
}
