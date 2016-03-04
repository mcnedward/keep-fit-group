package com.keepfit.stepdetection.algorithms.chris;

import android.content.Context;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.BaseAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 01/03/2016.
 */

enum Axis {
    X, Y, Z
};

enum Mode {
    POCKET, HAND
};

public class ChrisAlgorithm extends BaseAlgorithm {

    private static String NAME = "Chris Algorithm";
    private static final boolean DEBUG = false;
    public final static double GRAVITY_MS2 = 9.80665;

    private Axis modeAxis = Axis.Y; // Y in pocket mode
    Mode mode = Mode.POCKET;
    boolean skip = false;
    boolean halfFrequency = false;
    private static final long MIN_TIME_BETWEEN_STEPS = 340;
    private long timeOfLastStep = 0;

    //Thresholds
    private static final double T_POCKET = 0.0078; // Good 0.0086
    private static final double T_HAND = 0.01;

    //Noise
    private static final double Q = 0.1d; // Good 0.1
    private static final double R = 20d; // Good 20
    private static final double K_MARKUP_A = 0.25d;  // Good 0.25

    // at k=0
    private static final double Z0_POCKET = 1d;
    private static final double P0_POCKET = 1d;

    // Kalman data
    List<Double[]> kalman;
    int Xk_idx = 0;
    int Pk_idx = 1;

    // Step data
    private int numSteps = 0;
    private List<FilterData> fData;
    private boolean pSlope;
    private boolean nSlope;
    public double maxDevA = 0d;
    public double maxDevB = 0d;
    private double aMax = 0d;
    private double stepData = 0d;

    public ChrisAlgorithm(Context context) {
        super(context, NAME);
        initialize();
    }

    public ChrisAlgorithm() {
        super(NAME);
        initialize();
    }

    private void initialize() {
        kalman = new ArrayList<>();
        fData = new ArrayList<>();

        // k=0
        Double[] k0 = new Double[]{Z0_POCKET, P0_POCKET};
        kalman.add(k0);
        // (long time, double raw, double kalmanFiltered, double algoFiltered, double stepData, double devA, double devB
        fData.add(new FilterData(0, k0[Xk_idx], k0[Xk_idx], 0d, 0d, 0d, 0d));
        resetSlope();
    }

    @Override
    public void handleSensorData(AccelerationData ad) {
        if (halfFrequency && skip == true) {
            skip = false;
            return;
        }

        Double[] kLast = kalman.get(kalman.size() - 1);

        ///////// Time Update (Predict) //////////////////
        // Get Xkprime
        Double Xkprime = kLast[Xk_idx];
        // Get Pkprime
        Double Pkprime = kLast[Pk_idx] + Q;
        //////////////////////////////////////////////////

        ///////// Measurement Update (Correct) ///////////
        // Get Kk
        Double Kk = Xkprime / (Pkprime + R);
        // Get Xk
        double measured = getAxisValue(ad, modeAxis);
        Double Xk = Xkprime + (Kk * (measured - Xkprime));  // Xk is current kalman filtered value
        // Get Pk
        Double Pk = (1 - Kk) * Pkprime;
        //////////////////////////////////////////////////

        kalman.add(new Double[]{Xk, Pk});


        long time = ad.getTimeStamp();
        double raw = getAxisValue(ad, modeAxis);
        double kFiltered = Xk;
        double aFiltered = 0d;
        stepData = 0d;
        double deviationA = 0;
        double deviationB = 0;
        // Algorithm Filter
        if (fData.size() > 0) {
            // Previous
            double lastKfiltered = fData.get(fData.size() - 1).getKalmanFiltered();
            double lastAfiltered = fData.get(fData.size() - 1).getAlgoFiltered();
            double threshold = getThreshold();
            deviationA = kFiltered - lastKfiltered;
            deviationB = lastKfiltered - kFiltered;

            if (deviationA > maxDevA)
                maxDevA = deviationA;

            if (deviationB > maxDevB)
                maxDevB = deviationB;

            if (deviationA > threshold) {
                aFiltered = lastAfiltered + K_MARKUP_A;
            }
            if (deviationB > threshold) {
                if (lastAfiltered == 0) {
                    aFiltered = aMax;
                }
                if (lastAfiltered > 0) {
                    aFiltered = lastAfiltered - K_MARKUP_A;
                }
                if (lastAfiltered < 0) {
                    aFiltered = 0;
                }
            }
            if (Math.abs(lastAfiltered - aFiltered) < threshold) {
                aFiltered = 0;
            }

            if (aFiltered > aMax)
                aMax = aFiltered;


            // Step counter - count pairs of +ve, -ve slopes
            if (aFiltered > lastAfiltered)
                pSlope = true;
            else if (aFiltered < lastAfiltered)
                nSlope = true;

            if (pSlope == false && nSlope == false) {
                // Do Nothing
            } else if (pSlope == true && nSlope == false) {
                // Do Nothing
            } else if (pSlope == false && nSlope == true) {
                // Do Nothing
            } else if (pSlope == true && nSlope == true) {
                if (aFiltered == 0) {
                    if (hasStepTimePassed(time)) {
                        timeOfLastStep = time;
                        numSteps++;
                        stepData = 1;
                    }
                    resetSlope();
                }
            }

            if (DEBUG) {
                System.out.println(String.format("time:%s\n\traw:%s\n\tkFil:%s\n\taDv:%s\n\tbDv:%s\n",
                        time, raw, kFiltered, deviationA, deviationB));
            }
        }

        fData.add(new FilterData(time, raw, kFiltered, aFiltered, stepData, deviationA, deviationB));
        skip = true;
    }

    private void resetSlope() {
        pSlope = false;
        nSlope = false;
    }

    private double getAxisValue(AccelerationData ad, Axis axis) {
        double pt = 0d;
        switch (axis) {
            case X:
                pt = ad.getX();
                break;

            case Y:
                pt = ad.getY();
                break;

            case Z:
                pt = ad.getZ();
                break;

            default:
                break;
        }

        return pt / GRAVITY_MS2;
    }

    private double getThreshold() {
        double t = 0d;
        switch (this.mode) {
            case HAND:
                t = T_HAND;
                break;

            case POCKET:
                t = T_POCKET;
                break;

            default:
                break;
        }

        return t;
    }

    public int getStepCount() {
        return this.numSteps;
    }

    private boolean hasStepTimePassed(long timeNow) {
        if (timeNow - timeOfLastStep >= MIN_TIME_BETWEEN_STEPS)
            return true;
        else
            return false;
    }

    @Override
    public AccelerationData getAccelerationData() {
        if (fData.size() > 0) {
            return new AccelerationData(0, 0,
                    fData.get(fData.size() - 1).getStepData(),
                    fData.get(fData.size() - 1).getTime());
        } else {
            return new AccelerationData(0, 0, 0, 0);
        }
    }
}
