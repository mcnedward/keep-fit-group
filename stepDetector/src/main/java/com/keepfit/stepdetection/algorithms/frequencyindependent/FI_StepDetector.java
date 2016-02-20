package com.keepfit.stepdetection.algorithms.frequencyindependent;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.BaseStepDetector;

import java.util.List;

public class FI_StepDetector extends BaseStepDetector {
    private FI_StepDetector() {
        super();
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void addAccelerationData(AccelerationData data) {
        super.addRawAccelerationData(data);
    }

    @Override
    public void addAccelerationData(List<AccelerationData> data) {
        super.addRawAccelerationData(data);
    }
}
