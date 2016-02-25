package com.keepfit.stepdetection.algorithms.edward;

import android.content.Context;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.BaseAlgorithm;

/**
 * Created by Edward on 2/19/2016.
 */
public class AngleAlgorithm extends BaseAlgorithm {
    private final static String TAG = "AngleAlgorithm";

    private EdwardAlgorithm algorithm;

    public AngleAlgorithm(Context context, EdwardAlgorithm algorithm) {
        super(context);
        this.algorithm = algorithm;
    }

    @Override
    public void handleSensorData(AccelerationData ad) {
        algorithm.notifyAccelerometerSensorChanged(ad.getTimeStamp(), ad.getX(), ad.getY(), ad.getZ(), ad.getAcceleration());
        algorithm.notifyGravitySensorChanged(ad.getTimeStamp(), ad.getX(), ad.getY(), ad.getZ());
    }

    @Override
    public int getStepCount() {
        return algorithm.getNumberOfSteps();
    }

}
