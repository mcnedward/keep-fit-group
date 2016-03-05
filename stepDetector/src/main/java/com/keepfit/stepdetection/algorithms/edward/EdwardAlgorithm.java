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

    private static String NAME = "Edward Algorithm";

    private final static double GRAVITY_1_G = 9.8;
    private final static double THRESHOLD = 0.2;

    private List<AccelerationData> accelerationDataList;
    private int numberOfSteps;

    private double[] xValues;
    private double[] yValues;
    private double[] zValues;
    private int index = 0;
    private double xAcceleration, yAcceleration, zAcceleration;
    private long timestamp;

    public EdwardAlgorithm(Context context) {
        super(context, NAME);
        xValues = new double[3];
        yValues = new double[3];
        zValues = new double[3];
    }

    public EdwardAlgorithm() {
        super(NAME);
        xValues = new double[3];
        yValues = new double[3];
        zValues = new double[3];
    }

    @Override
    public void handleSensorData(AccelerationData ad) {
        writeSensorData(ad);
        notifyAccelerometerSensorChanged(ad.getTimeStamp(), ad.getX(), ad.getY(), ad.getZ(), ad.getAcceleration());
        if (!handlingGravity)
            notifyGravitySensorChanged(ad.getTimeStamp(), ad.getX(), ad.getY(), ad.getZ());
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

    private boolean handlingGravity;
    public void handleGravitySensorData(AccelerationData ad) {
        writeSensorData(ad);
        handlingGravity = true;
        notifyGravitySensorChanged(ad.getTimeStamp(), ad.getX(), ad.getY(), ad.getZ());
    }

    private double currentXGravity;
    private double currentYGravity;
    private double currentZGravity;

    private boolean waitForStep;
    private long timer;
    private long stepTimer;
    private double pointToHit;
    private boolean waitFor0;

    private void notifyGravitySensorChanged(long eventTime, double xGravity, double yGravity, double zGravity) {
        currentXGravity = handlingGravity ? -2 : xGravity;
        currentYGravity = handlingGravity ? -2 : yGravity;
        currentZGravity = zGravity;
        double inclinationAngle = calculateInclinationAngle(zGravity);

        Log.d(TAG, "Inclination angle: " + Math.abs(inclinationAngle));
        if (!waitForStep) {
            if (Math.abs(inclinationAngle) > THRESHOLD) {
                waitForStep = true;
                pointToHit = inclinationAngle * -1;
                timer = System.nanoTime();
            }
        } else {
            if (hasHalfSecondPassed(timer)) {
                waitForStep = false;
                return;
            }
            if (waitFor0) {
                if (inclinationAngle >= 0) {
                    // Step hit
                    numberOfSteps++;
                    waitFor0 = false;
                    waitForStep = false;
                }
            }
            if (Math.abs(inclinationAngle) > THRESHOLD) {
                if (inclinationAngle <= pointToHit) {
                    waitFor0 = true;
                    timer = System.nanoTime();
                }
            }
        }
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

    private boolean checkUp;
    private boolean waitForCheckpoint;
    private static long HALF_SECOND = 500000000;
    private double thresholdToCheck;

    private void checkThreshold(double gravityDirectionAcceleration, double gravityComponent, double gravityAtRestingTime) {
        if (waitForCheckpoint) {
            if (hasHalfSecondPassed(timer)) {
                Log.d(TAG, "TIMEOUT: No step counted");
                waitForCheckpoint = false;
                timer = System.nanoTime();
            } else {
                Log.d(TAG, "GDA: " + gravityDirectionAcceleration + "; POINT TO HIT: " + pointToHit);
                thresholdToCheck = Math.abs(gravityAtRestingTime - gravityComponent);
                if (thresholdToCheck > THRESHOLD) {
                    boolean hitStep;
                    if (checkUp)
                        hitStep = gravityComponent >= pointToHit;
                    else
                        hitStep = gravityComponent <= pointToHit;
                    if (hitStep) {
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
            }
        } else {
            thresholdToCheck = Math.abs(gravityAtRestingTime - gravityComponent);
            if (thresholdToCheck > THRESHOLD) {
                // Start timer and wait for the this gravityDirectionAcceleration value to be hit again
                double differenceToHit = Math.abs(gravityComponent - gravityAtRestingTime);
                if (gravityComponent <= gravityAtRestingTime) {
                    pointToHit = gravityAtRestingTime + differenceToHit;
                    checkUp = true;
                }
                else {
                    pointToHit = gravityAtRestingTime - differenceToHit;
                    checkUp = false;
                }
                waitForCheckpoint = true;
                timer = System.nanoTime();
                Log.d(TAG, "Check up: " + checkUp + "; Point to hit: " + pointToHit + "; Gravity Component: " + gravityComponent + "; Resting Gravity: " + gravityAtRestingTime);
            }
        }
    }

    private boolean hasHalfSecondPassed(long timer) {
        return ((System.nanoTime() / 10) - (timer / 10)) >= HALF_SECOND;
    }

    private double getGravityDirectionAccelerationForAxis(double gravityComponent, double acceleration) {
        double inclinationAngle = calculateInclinationAngle(gravityComponent);
        double gravityDirectionAcceleration = calculateGravityDirectionAcceleration(inclinationAngle, acceleration);
        return gravityDirectionAcceleration;
    }

    /**
     * Figure 6 of the paper
     *
     * @param gravity
     * @return
     */
    private double calculateInclinationAngle(double gravity) {
        return Math.asin(gravity / GRAVITY_1_G);
    }

    /**
     * Figure 7 of the paper
     *
     * @param inclinationAngle
     * @param acceleration
     * @return
     */
    private double calculateGravityDirectionAcceleration(double inclinationAngle, double acceleration) {
        return acceleration * Math.sin(inclinationAngle);
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
        return new AccelerationData(currentXGravity, currentYGravity, currentZGravity, timestamp);
    }

}
