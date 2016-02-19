package com.keepfit.stepdetection.algorithms;

import java.util.List;

public interface StepDetector {
    void addAccelerationData(AccelerationData data);
    void addAccelerationData(List<AccelerationData> data);
    void registerStepDetectedListener(StepDetectedListener listener);
    void unregisterStepDetectedListener(StepDetectedListener listener);
    void reset();
}
