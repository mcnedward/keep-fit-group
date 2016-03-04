package com.keepfit.app.activity.fragment.algorithm;

import android.content.Context;
import android.hardware.Sensor;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.IAlgorithm;
import com.keepfit.stepdetection.algorithms.dino.DinoAlgorithm;
import com.keepfit.stepdetection.algorithms.edward.EdwardAlgorithm;
import com.keepfit.stepdetection.algorithms.kornel.KornelAlgorithm;

import java.util.List;

/**
 * Created by Edward on 3/2/2016.
 */
public class KornelFragment extends AlgorithmFragment {
    private static final String TAG = "KornelFragment";

    KornelAlgorithm kornelAlgorithm;

    @Override
    protected void initializeAlgorithm(Context context) {
        kornelAlgorithm = new KornelAlgorithm(context);
    }

    @Override
    protected void startAlgorithm() {
        sensorManager.registerListener(stepDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), RATE);
//        List<AccelerationData> accelerationDataList = loadFile();
//        kornelAlgorithm.notifySensorDataReceived(accelerationDataList);
    }

    @Override
    protected IAlgorithm getAlgorithm() {
        return kornelAlgorithm;
    }

    @Override
    protected String getTitle() {
        return "Kornel Algorithm";
    }

    @Override
    public void createNewAlgorithm() {
        kornelAlgorithm = new KornelAlgorithm(context);
    }

}
