package com.keepfit.stepdetection.algorithms;

import android.hardware.SensorEventListener;

import java.util.List;

public interface IStepDetector extends SensorEventListener {
    void addAccelerationData(AccelerationData data);
    void addAccelerationData(List<AccelerationData> data);
    void registerAlgorithm(BaseAlgorithm algorithm);
    void unregisterAlgorithm(BaseAlgorithm algorithm);
    void reset();
}
