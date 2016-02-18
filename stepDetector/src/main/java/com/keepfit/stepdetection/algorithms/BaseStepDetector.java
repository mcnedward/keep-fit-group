package com.keepfit.stepdetection.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public abstract class BaseStepDetector implements StepDetector {

    private List<AccelerationData> _rawAccelerationData;
    private Vector<StepDetectedListener> _stepDetectedListeners;

    private BaseStepDetector() {
        _stepDetectedListeners = new Vector<>();
        reset();
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

    protected void reset()
    {
        _rawAccelerationData =  new ArrayList<>();
    }

}
