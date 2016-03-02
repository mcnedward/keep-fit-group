package com.keepfit.stepdetection.algorithms.edward;

import android.content.Context;
import android.util.Log;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.BaseAlgorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 2/25/2016.
 */
public class EdwardAlgorithm extends BaseAlgorithm {
    private final static String TAG = "EdwardAlgorithm";

    private final static double SENSOR_VALUE = 9.8;
    private final static double THRESHOLD = 0.14;

    private List<AccelerationData> accelerationDataList;
    private int numberOfSteps;

    private double[] xValues;
    private double[] yValues;
    private double[] zValues;
    private int index = 0;
    private double xAcceleration, yAcceleration, zAcceleration;
    private long timestamp;

    public EdwardAlgorithm(Context context) {
        super(context);
        xValues = new double[3];
        yValues = new double[3];
        zValues = new double[3];
        createFile("Edward");
        loadGravityData();
    }

    public EdwardAlgorithm() {
        super(null);
        xValues = new double[3];
        yValues = new double[3];
        zValues = new double[3];
        loadGravityData();
    }

    private int dataIndex = 0;

    @Override
    public void handleSensorData(AccelerationData ad) {
        notifyAccelerometerSensorChanged(ad.getTimeStamp(), ad.getX(), ad.getY(), ad.getZ(), ad.getAcceleration());
        if (dataIndex == accelerationDataList.size())
            dataIndex = 0;
        handleGravitySensorData(accelerationDataList.get(dataIndex++));
    }

    private void notifyAccelerometerSensorChanged(long eventTime, double xAcceleration, double yAcceleration, double zAcceleration, double acceleration) {
        xValues[index] = xAcceleration;
        yValues[index] = yAcceleration;
        zValues[index] = zAcceleration;
        if (index != 2)
            index++;

        this.xAcceleration = calculate3PointAverage(xValues);
        this.yAcceleration = calculate3PointAverage(yValues);
        this.zAcceleration = calculate3PointAverage(zValues);
        timestamp = eventTime;
    }

    public void handleGravitySensorData(AccelerationData ad) {
        notifyGravitySensorChanged(ad.getTimeStamp(), ad.getX(), ad.getY(), ad.getZ());
    }

    private double currentXGravity;
    private double currentYGravity;
    private double currentZGravity;
    private double gravityXAtRestingTime;
    private double gravityYAtRestingTime;
    private double gravityZAtRestingTime;
    private boolean shouldCalculateRestingTime = true;

    private void notifyGravitySensorChanged(long eventTime, double xGravity, double yGravity, double zGravity) {
        currentXGravity = xGravity;
        currentYGravity = yGravity;
        currentZGravity = zGravity;
        // Calculate the gravity direction acceleration for each axis
        double gravityX = getGravityDirectionAccelerationForAxis(xGravity, xAcceleration);
        double gravityY = getGravityDirectionAccelerationForAxis(yGravity, yAcceleration);
        double gravityZ = getGravityDirectionAccelerationForAxis(zGravity, zAcceleration);

        if (shouldCalculateRestingTime) {
            // Only calculate this once
            gravityXAtRestingTime = gravityX;
            gravityYAtRestingTime = gravityY;
            gravityZAtRestingTime = gravityZ;
        }
        shouldCalculateRestingTime = false;

        // Call method to check threshold and wait
//        checkThreshold(gravityX);
//        checkThreshold(gravityY);
        checkThreshold(gravityZ);

        writeSensorData(eventTime, gravityX, gravityY, gravityZ, 0);
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

    private double pointToHit;
    private boolean waitForCheckpoint;
    private long timer;
    private long stepTimer = System.nanoTime();
    private static long HALF_SECOND = 500000000;

    private void checkThreshold(double gravityDirectionAcceleration) {
        if (waitForCheckpoint) {
            if (hasHalfSecondPassed(timer)) {
                waitForCheckpoint = false;
                Log.d(TAG, "TIMEOUT: No step counted");
                timer = System.nanoTime();
                shouldCalculateRestingTime = true;
            } else {
                Log.d(TAG, "GDA: " + gravityDirectionAcceleration + "; POINT TO HIT: " + pointToHit);
                if (gravityDirectionAcceleration <= pointToHit && Math.abs(gravityDirectionAcceleration) > THRESHOLD) {
                    if (!hasHalfSecondPassed(timer)) {
                        // Stepped!
                        if (hasHalfSecondPassed(stepTimer)) {
                            Log.d(TAG, "COUNTING STEPS: " + stepTimer);
                            numberOfSteps++;
                            stepTimer = System.nanoTime();
                        }
                        waitForCheckpoint = false;
                        timer = System.nanoTime();
                    }
                }
            }
        } else {
            Log.d(TAG, "GDA: " + gravityDirectionAcceleration);
            if (Math.abs(gravityDirectionAcceleration) > THRESHOLD) {
                // Start timer and wait for the this gravityDirectionAcceleration value to be hit again
                double differenceToHit = gravityDirectionAcceleration - gravityZAtRestingTime;
                pointToHit = gravityZAtRestingTime - differenceToHit;
                waitForCheckpoint = true;
                timer = System.nanoTime();
            } else {
                shouldCalculateRestingTime = true;
            }
        }
    }

    private boolean hasHalfSecondPassed(long timer) {
        return (System.nanoTime() - timer) >= HALF_SECOND;
    }

    private double getGravityDirectionAccelerationForAxis(double gravityComponent, double acceleration) {
//        double inclinationAngle = calculateInclinationAngle(gravityComponent);
        double gravityDirectionAcceleration = calculateGravityDirectionAcceleration(gravityComponent, acceleration);
        return gravityDirectionAcceleration;
    }

    /**
     * Figure 6 of the paper
     *
     * @param gravity
     * @return
     */
    private double calculateInclinationAngle(double gravity) {
        return Math.asin(gravity / 1);
    }

    /**
     * Figure 7 of the paper
     *
     * @param inclinationAngle
     * @param acceleration
     * @return
     */
    private double calculateGravityDirectionAcceleration(double inclinationAngle, double acceleration) {
        return inclinationAngle * Math.sin(inclinationAngle);
    }

    private void shuffleValues(double[] values) {
        for (int i = 0; i < values.length - 1; i++) {
            values[i] = values[i + 1];
        }
        values[values.length - 1] = 0f;
    }

    @Override
    public int getStepCount() {
        return numberOfSteps;
    }

    public AccelerationData getAccelerationData() {
        return new AccelerationData(xAcceleration, yAcceleration, zAcceleration, timestamp);
    }

    private void loadGravityData() {
        accelerationDataList = new ArrayList<>();
        try {
            InputStream stream = context.getResources().getAssets().open("gravityZ.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                AccelerationData accelerationData = new AccelerationData(0, 0, Double.parseDouble(line), 0.0, 0);
                accelerationDataList.add(accelerationData);
            }
            reader.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
