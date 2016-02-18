package com.keepfit.stepdetection.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public abstract class BaseStepDetector implements StepDetector {

    private List<AccelerationData> _rawAccelerationData;
    private Vector<StepDetectedListener> _stepDetectedListeners;

    protected BaseStepDetector() {
        _stepDetectedListeners = new Vector<>();
        reset();
    }

    protected void addRawAccelerationData(AccelerationData data) {

        if(null == data) throw new IllegalArgumentException("ERROR: data cannot be null");
        if (_rawAccelerationData != null) _rawAccelerationData.add(data);
    }

    public void reset() {
        _rawAccelerationData = new ArrayList<>();
    }

    public synchronized void registerStepDetectedListener(StepDetectedListener listener) {
        if (!_stepDetectedListeners.contains(listener)) {
            _stepDetectedListeners.add(listener);
        }
    }

    public synchronized void unregisterStepDetectedListener(StepDetectedListener listener) {
        if (_stepDetectedListeners.contains(listener)) {
            _stepDetectedListeners.remove(listener);
        }
    }

    protected void fireStepDetected(StepDetectedEventArgs e) {
        Vector<StepDetectedListener> tempStepDetectedListeners;

        synchronized (this) {
            if (_stepDetectedListeners.size() == 0) return;
            tempStepDetectedListeners = (Vector<StepDetectedListener>) _stepDetectedListeners.clone();
        }

        for (StepDetectedListener listener : tempStepDetectedListeners) {
            listener.onStepDetected(e);
        }
    }

}
