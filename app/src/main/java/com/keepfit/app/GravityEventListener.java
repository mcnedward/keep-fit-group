package com.keepfit.app;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.keepfit.app.angleAlgorithm.AngleAlgorithm;

/**
 * Created by Edward on 2/19/2016.
 */
public class GravityEventListener implements SensorEventListener {
    private final static String TAG = "GravityEventListener";

    private AngleAlgorithm angleAlgorithm;

    public GravityEventListener(AngleAlgorithm angleAlgorithm) {
        this.angleAlgorithm = angleAlgorithm;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long eventTimeStamp = event.timestamp;

        float xGravity = event.values[0];
        float yGravity = event.values[1];
        float zGravity = event.values[2];

        angleAlgorithm.notifyGravitySensorChanged(eventTimeStamp, xGravity, yGravity, zGravity);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.e(TAG, "App doesn't support dynamic sensor accuracy changes");
    }
}
