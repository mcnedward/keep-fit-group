package com.keepfit.app.activity.fragment.algorithm;

import android.content.Context;
import android.hardware.Sensor;

import com.keepfit.stepdetection.algorithms.IAlgorithm;
import com.keepfit.stepdetection.algorithms.chris.ChrisAlgorithm;
import com.keepfit.stepdetection.algorithms.dino.DinoAlgorithm;
import com.keepfit.stepdetection.algorithms.edward.EdwardAlgorithm;

/**
 * Created by Edward on 3/2/2016.
 */
public class DinoFragment extends AlgorithmFragment {
    private static final String TAG = "DinoFragment";

    DinoAlgorithm dinoAlgorithm;
    private Context context;

    @Override
    protected void initializeAlgorithm(Context context) {
        dinoAlgorithm = new DinoAlgorithm(context);
    }

    @Override
    protected void registerAlgorithm() {
        sensorManager.registerListener(stepDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), RATE);
        sensorManager.registerListener(stepDetector, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), RATE);
    }

    @Override
    protected IAlgorithm getAlgorithm() {
        return dinoAlgorithm;
    }

    @Override
    protected String getTitle() {
        return "Dino Algorithm";
    }

    @Override
    public DinoAlgorithm createNewAlgorithm() {
        dinoAlgorithm = new DinoAlgorithm(context);
        return dinoAlgorithm;
    }
}
