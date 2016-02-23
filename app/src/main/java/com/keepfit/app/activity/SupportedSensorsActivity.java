package com.keepfit.app.activity;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.keepfit.app.R;
import com.keepfit.app.utils.SimpleStringAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 2/23/2016.
 */
public class SupportedSensorsActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private static final int RATE = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supported_sensors);
        initialize();
    }

    private void initialize() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        int accelerometerType = Sensor.TYPE_ACCELEROMETER;
        int linearAccelerationType = Sensor.TYPE_LINEAR_ACCELERATION;
        int gravityType = Sensor.TYPE_GRAVITY;
        int gyroscopeType = Sensor.TYPE_GYROSCOPE;
        boolean hasAccel = sensorManager.registerListener(this, sensorManager.getDefaultSensor(accelerometerType), RATE);
        boolean hasLinearAccel = sensorManager.registerListener(this, sensorManager.getDefaultSensor(linearAccelerationType), RATE);
        boolean hasGravity = sensorManager.registerListener(this, sensorManager.getDefaultSensor(gravityType), RATE);
        boolean hasGyro = sensorManager.registerListener(this, sensorManager.getDefaultSensor(gyroscopeType), RATE);

        List<String> supportedSensors = new ArrayList<>();
        supportedSensors.add(hasAccel ? "Your device has the Accelerometer." : "Your device does not have the Accelerometer.");
        supportedSensors.add(hasLinearAccel ? "Your device has the Linear Acceleration sensor." : "Your device does not have the Linear Acceleration sensor.");
        supportedSensors.add(hasGravity ? "Your device has the Gravity sensor." : "Your device does not have the Gravity sensor.");
        supportedSensors.add(hasGyro ? "Your device has the Gyroscope." : "Your device does not have the Gyroscope.");

        ListAdapter adapter = new SimpleStringAdapter(supportedSensors, this);
        ((ListView)findViewById(R.id.sensors)).setAdapter(adapter);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
