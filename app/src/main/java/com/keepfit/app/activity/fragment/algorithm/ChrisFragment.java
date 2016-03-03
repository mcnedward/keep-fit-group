package com.keepfit.app.activity.fragment.algorithm;

import android.content.Context;
import android.hardware.Sensor;

import com.keepfit.stepdetection.algorithms.IAlgorithm;
import com.keepfit.stepdetection.algorithms.chris.ChrisAlgorithm;
import com.keepfit.stepdetection.algorithms.edward.EdwardAlgorithm;

/**
 * Created by Edward on 3/2/2016.
 */
public class ChrisFragment extends AlgorithmFragment {
    private static final String TAG = "ChrisFragment";

    ChrisAlgorithm chrisAlgorithm;

    @Override
    protected void initializeAlgorithm(Context context) {
        chrisAlgorithm = new ChrisAlgorithm(getContext());
    }

    @Override
    protected void startAlgorithm() {
        sensorManager.registerListener(stepDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), RATE);
    }

    @Override
    protected IAlgorithm getAlgorithm() {
        return chrisAlgorithm;
    }

    @Override
    protected String getTitle() {
        return "Chris Algorithm";
    }
}
