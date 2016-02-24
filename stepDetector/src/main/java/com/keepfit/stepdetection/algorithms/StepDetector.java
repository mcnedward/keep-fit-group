package com.keepfit.stepdetection.algorithms;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StepDetector implements IStepDetector {
    private final static String TAG = "StepDetector";

    private List<IAlgorithm> algorithms;

    protected StepDetector() {
        algorithms = new ArrayList<>();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        AccelerationData ad = new AccelerationData(event.values[0], event.values[1], event.values[2], event.timestamp);
        notifyAlgorithms(ad);
    }

    @Override
    public void addAccelerationData(List<AccelerationData> adList) {
        for (AccelerationData ad : adList) {
            notifyAlgorithms(ad);
        }
    }

    @Override
    public void addAccelerationData(AccelerationData ad) {
        notifyAlgorithms(ad);
    }

    private void notifyAlgorithms(AccelerationData ad) {
        for (IAlgorithm algorithm : algorithms) {
            algorithm.notifySensorDataRecieved(ad);
        }
    }

    @Override
    public synchronized void registerAlgorithm(IAlgorithm algorithm) {
        if (algorithm != null && !algorithms.contains(algorithm)) {
            algorithms.add(algorithm);
        }
    }

    @Override
    public synchronized void unregisterAlgorithm(IAlgorithm algorithm) {
        if (algorithms.contains(algorithm)) {
            algorithms.remove(algorithm);
        }
    }

    @Override
    public void reset() {
        algorithms = new ArrayList<>();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.e(TAG, "App doesn't support dynamic sensor accuracy changes");
    }

}
