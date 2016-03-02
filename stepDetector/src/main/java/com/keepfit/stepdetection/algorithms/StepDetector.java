package com.keepfit.stepdetection.algorithms;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import com.keepfit.stepdetection.algorithms.edward.EdwardAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class StepDetector implements IStepDetector {
    private final static String TAG = "StepDetector";

    private SensorManager sensorManager;
    private List<IAlgorithm> algorithms;

    public StepDetector(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        algorithms = new ArrayList<>();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        AccelerationData ad = new AccelerationData(event.values[0], event.values[1], event.values[2], event.timestamp);
        notifyAlgorithms(ad);
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
            notifyAlgorithmsGravityChanged(ad);
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
            algorithm.notifySensorDataReceived(ad);
        }
    }

    private void notifyAlgorithmsGravityChanged(AccelerationData ad) {
        for (IAlgorithm algorithm : algorithms) {
            if (algorithm instanceof EdwardAlgorithm) {
                ((EdwardAlgorithm)algorithm).handleGravitySensorData(ad);
            }
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
    public List<IAlgorithm> getAlgorithms() {
        return algorithms;
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
