package com.keepfit.stepdetection.algorithms.edward;

import android.content.Context;
import android.util.Log;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.BaseAlgorithm;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Edward on 2/19/2016.
 */
public class AngleAlgorithm extends BaseAlgorithm {
    private final static String TAG = "AngleAlgorithm";

    private final static double SENSOR_VALUE = 9.8;
    private final static double THRESHOLD = 0.18;

    private int numberOfSteps;

    private double[] xValues;
    private double[] yValues;
    private double[] zValues;
    private int index = 0;
    private double xAcceleration, yAcceleration, zAcceleration;
    private float timer = 0f;
    private boolean startTimer = false;

    public AngleAlgorithm(Context context) {
        super(context);
        xValues = new double[3];
        yValues = new double[3];
        zValues = new double[3];
    }

    @Override
    public void handleSensorData(AccelerationData ad) {
        notifyAccelerometerSensorChanged(ad.getTimeStamp(), ad.getX(), ad.getY(), ad.getZ(), ad.getXYZMagnitude());
    }

    public void notifyAccelerometerSensorChanged(long eventTime, double xAcceleration, double yAcceleration, double zAcceleration, double acceleration) {
        Log.d(TAG, String.format("Sensor change at: %s; X: %s; Y: %s; Z: %s; Acceleration: %s", eventTime, xAcceleration, yAcceleration, zAcceleration, acceleration));
        xValues[index] = xAcceleration;
        yValues[index] = yAcceleration;
        zValues[index] = zAcceleration;
        if (index != 2)
            index++;

        this.xAcceleration = calculate3PointAverage(xValues);
        this.yAcceleration = calculate3PointAverage(yValues);
        this.zAcceleration = calculate3PointAverage(zValues);
    }

    private double calculate3PointAverage(double[] values) {
        double average = 0f;
        if (values.length == 1)
            return average;

        double v1 = values[0];
        double v2 = values[1];

        if (values.length == 2) {
            // Calculate 2 point average
            double a = (2 * v1) + (v2 * 2);
            average = a / 3;
        } else {
            // Calculate 3 point average
            double v3 = values[2];
            double a = v1 + v2 + v3;
            average = a / 3;
        }
        shuffleValues(values);
        return average;
    }

    public void notifyGravitySensorChanged(long eventTime, float xGravity, float yGravity, float zGravity) {
        Log.d(TAG + "_Gravity", String.format("Gravity at time[%s] - X: %s; Y: %s; Z: %s", new Timestamp(eventTime), xGravity, yGravity, zGravity));
        // Start the timer if not started already or reset
        if (startTimer) {
            timer = System.currentTimeMillis();
        }
        // Calculate the gravity direction acceleration for each axis
        double gravityX = getGravityDirectionAccelerationForAxis(xGravity, xAcceleration);
        double gravityY = getGravityDirectionAccelerationForAxis(yGravity, yAcceleration);
        double gravityZ = getGravityDirectionAccelerationForAxis(zGravity, zAcceleration);

        // Call method to check threshold and wait
        checkThreshold(gravityX);
        checkThreshold(gravityY);
        checkThreshold(gravityZ);
    }

    private void checkThreshold(double gravityDirectionAcceleration) {
        if (!startTimer) {
            if (gravityDirectionAcceleration > THRESHOLD)
                startTimer = true;
        } else {
            float currentTime = System.currentTimeMillis() - timer;
            float time = currentTime / 100;
            if (gravityDirectionAcceleration > THRESHOLD && time < 0.5f) {
                // Count the step!
                numberOfSteps++;
            }
            startTimer = false;
        }
    }

    private double getGravityDirectionAccelerationForAxis(double gravityComponent, double acceleration) {
        double currentAngle = calculateInclinationAngle(gravityComponent);
        return Math.abs(calculateGravityDirectionAcceleration(acceleration, currentAngle));
    }

    private double calculateInclinationAngle(double gravityComponent) {
        return Math.asin(gravityComponent / SENSOR_VALUE);
    }

    private double calculateGravityDirectionAcceleration(double acceleration, double inclinationAngle) {
        return acceleration * Math.sin(inclinationAngle);
    }

    private void shuffleValues(double[] values) {
        for (int i = 0; i < values.length - 1; i++) {
            values[i] = values[i + 1];
        }
        values[values.length - 1] = 0f;
    }

    public int GetNumberOfSteps() {
        return numberOfSteps;
    }

}
