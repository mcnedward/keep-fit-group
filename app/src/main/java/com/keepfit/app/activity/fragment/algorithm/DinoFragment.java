package com.keepfit.app.activity.fragment.algorithm;

import android.content.Context;
import android.hardware.Sensor;

import com.keepfit.stepdetection.algorithms.IAlgorithm;
import com.keepfit.stepdetection.algorithms.chris.ChrisAlgorithm;
import com.keepfit.stepdetection.algorithms.dino.DinoAlgorithm;

/**
 * Created by Edward on 3/2/2016.
 */
public class DinoFragment extends AlgorithmFragment {
    private static final String TAG = "DinoFragment";

    DinoAlgorithm dinoAlgorithm;

    @Override
    protected void initializeAlgorithm(Context context) {
        dinoAlgorithm = new DinoAlgorithm(getContext());
    }

    @Override
    protected void startAlgorithm() {
        sensorManager.registerListener(stepDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), RATE);
    }

    @Override
    protected IAlgorithm getAlgorithm() {
        return dinoAlgorithm;
    }

    @Override
    protected String getTitle() {
        return "Dino Algorithm";
    }
}
