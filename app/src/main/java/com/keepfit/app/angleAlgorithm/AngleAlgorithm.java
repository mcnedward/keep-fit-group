package com.keepfit.app.angleAlgorithm;

import android.util.Log;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.BaseAlgorithm;

import java.sql.Timestamp;

/**
 * Created by Edward on 2/19/2016.
 */
public class AngleAlgorithm extends BaseAlgorithm {
    private final static String TAG = "AngleAlgorithm";

    private final static double SENSOR_VALUE = 9.8;
    private final static double THRESHOLD = 0.18;

    private int numberOfSteps;

    private float[] xValues;
    private float[] yValues;
    private float[] zValues;
    private int index = 0;
    private float xAcceleration, yAcceleration, zAcceleration;
    private float timer = 0f;
    private boolean startTimer = false;

    public AngleAlgorithm() {
        xValues = new float[3];
        yValues = new float[3];
        zValues = new float[3];
    }

    @Override
    protected void notifySensorDataRecieved(AccelerationData ad) {

    }

    public void notifyAccelerometerSensorChanged(long eventTime, float xAcceleration, float yAcceleration, float zAcceleration, double acceleration) {
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

    private float calculate3PointAverage(float[] values) {
        float average = 0f;
        if (values.length == 1)
            return average;

        float v1 = values[0];
        float v2 = values[1];

        if (values.length == 2) {
            // Calculate 2 point average
            float a = (2 * v1) + (v2 * 2);
            average = a / 3;
        } else {
            // Calculate 3 point average
            float v3 = values[2];
            float a = v1 + v2 + v3;
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

    private double getGravityDirectionAccelerationForAxis(float gravityComponent, float acceleration) {
        double currentAngle = calculateInclinationAngle(gravityComponent);
        return Math.abs(calculateGravityDirectionAcceleration(acceleration, currentAngle));
    }

    private double calculateInclinationAngle(float gravityComponent) {
        return Math.asin(gravityComponent / SENSOR_VALUE);
    }

    private double calculateGravityDirectionAcceleration(float acceleration, double inclinationAngle) {
        return acceleration * Math.sin(inclinationAngle);
    }

    private void shuffleValues(float[] values) {
        for (int i = 0; i < values.length - 1; i++) {
            values[i] = values[i + 1];
        }
        values[values.length - 1] = 0f;
    }

    public int GetNumberOfSteps() {
        return numberOfSteps;
    }

}
