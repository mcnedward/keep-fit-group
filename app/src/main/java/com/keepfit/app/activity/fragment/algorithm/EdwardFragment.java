package com.keepfit.app.activity.fragment.algorithm;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;

import com.keepfit.stepdetection.algorithms.IAlgorithm;
import com.keepfit.stepdetection.algorithms.edward.EdwardAlgorithm;

/**
 * Created by Edward on 3/2/2016.
 */
public class EdwardFragment extends AlgorithmFragment {
    private static final String TAG = "EdwardFragment";

    EdwardAlgorithm edwardAlgorithm;
    private Context context;

    @Override
    protected void initializeAlgorithm(Context context) {
        this.context = context;
        createNewAlgorithm();
    }

    @Override
    protected void registerAlgorithm() {
        boolean accelerometerRegistered = sensorManager.registerListener(stepDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), RATE);
        boolean gravityRegister = sensorManager.registerListener(stepDetector, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), RATE);
        Log.d(TAG, String.format("Accelerometer Registered? %s; Gravity Registered? %s", accelerometerRegistered, gravityRegister));
    }

    @Override
    protected IAlgorithm getAlgorithm() {
        return edwardAlgorithm;
    }

    @Override
    public EdwardAlgorithm createNewAlgorithm() {
        edwardAlgorithm = new EdwardAlgorithm(context);
        return edwardAlgorithm;
    }

    @Override
    protected String getTitle() {
        return "Edward Algorithm";
    }
}
