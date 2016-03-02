package com.keepfit.app.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.keepfit.app.R;
import com.keepfit.app.view.AlgorithmView;
import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.IStepDetector;
import com.keepfit.stepdetection.algorithms.StepDetector;
import com.keepfit.stepdetection.algorithms.chris.ChrisAlgorithm;
import com.keepfit.stepdetection.algorithms.edward.AngleAlgorithm;
import com.keepfit.stepdetection.algorithms.edward.EdwardAlgorithm;
import com.keepfit.stepdetection.algorithms.kornel.KornelAlgorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 2/25/2016.
 */
public class MainFragment extends BaseFragment {
    private static final String TAG = "MainFragment";

    private Context context;
    private static final int RATE = 10000;
    private SensorManager sensorManager;
    private IStepDetector stepDetector;
    private LinearLayout algorithmContainer;
    private boolean runningAlgorithms;

    // Algorithms
    private EdwardAlgorithm edwardAlgorithm;
    private AlgorithmView edwardAlgorithmView;
    private KornelAlgorithm kornelAlgorithm;
    private AlgorithmView kornelAlgorithmView;
    private ChrisAlgorithm chrisAlgorithm;
    private AlgorithmView chrisAlgorithmView;
    // DINO ALGORITHM
    private AlgorithmView dinoAlgorithmView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {
        context = view.getContext();
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        stepDetector = new StepDetector(context);
        algorithmContainer = (LinearLayout) view.findViewById(R.id.algorithm_container);

        edwardAlgorithmView = (AlgorithmView) view.findViewById(R.id.edward_algorithm);
        kornelAlgorithmView = (AlgorithmView) view.findViewById(R.id.kornel_algorithm);
        dinoAlgorithmView = (AlgorithmView) view.findViewById(R.id.dino_algorithm);
        chrisAlgorithmView = (AlgorithmView) view.findViewById(R.id.chris_algorithm);

        initializeAlgorithms();
    }

    private void initializeAlgorithms() {
        initializeEdwardAlgorithm();
        initializeKornelAlgorithm();
        initializeDinoAlgorithm();
        initializeChrisAlgorithm();
    }

    private void initializeEdwardAlgorithm() {
        edwardAlgorithm = new EdwardAlgorithm(context);
        AngleAlgorithm algorithm = new AngleAlgorithm(context, edwardAlgorithm);
        stepDetector.registerAlgorithm(algorithm);

        edwardAlgorithmView.setAlgorithm(algorithm);
        edwardAlgorithmView.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!runningAlgorithms) {
                    runningAlgorithms = true;
                    ((Button) v).setText(getResources().getString(R.string.stop));
                    startEdwardAlgorithm();
                } else {
                    runningAlgorithms = false;
                    ((Button) v).setText(getResources().getString(R.string.start));
                    stopAlgorithms();
                }
            }
        });
    }

    private void initializeKornelAlgorithm() {
        kornelAlgorithm = new KornelAlgorithm(context);
        List<AccelerationData> accelerationDataList = loadFile();
//        kornelAlgorithm.notifySensorDataReceived(accelerationDataList);

        kornelAlgorithmView.setAlgorithm(kornelAlgorithm);
    }

    private void initializeDinoAlgorithm() {
        // dinoAlgorithm = new DinoAlgorithm(context);

        // dinoAlgorithmView.setAlgorithm(dinoAlgorithm);
    }

    private void initializeChrisAlgorithm() {
        chrisAlgorithm = new ChrisAlgorithm(context);
        chrisAlgorithmView.setAlgorithm(chrisAlgorithm);
        stepDetector.registerAlgorithm(chrisAlgorithm);
        chrisAlgorithmView.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!runningAlgorithms) {
                    runningAlgorithms = true;
                    ((Button) v).setText(getResources().getString(R.string.stop));
                    startChrisAlgorithm();
                } else {
                    runningAlgorithms = false;
                    ((Button) v).setText(getResources().getString(R.string.start));
                    stopAlgorithms();
                }
            }
        });
    }

    private void startEdwardAlgorithm() {
        boolean accelerometerRegistered = sensorManager.registerListener(stepDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), RATE);
        boolean gravityRegister = sensorManager.registerListener(stepDetector, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), RATE);
        Log.d(TAG, String.format("Accelerometer Registered? %s; Gravity Registered? %s", accelerometerRegistered, gravityRegister));
        displayAlgorithmData();
    }

    private void startKornelAlgorithm() {

    }

    private void startDinoAlgorithm() {

    }

    private void startChrisAlgorithm() {
        boolean accelerometerRegistered = sensorManager.registerListener(stepDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), RATE);
        Log.d(TAG, String.format("Accelerometer Registered? %s;", accelerometerRegistered));
        displayAlgorithmData();
    }

    private List<AccelerationData> loadFile() {
        List<AccelerationData> accelerationDataList = new ArrayList<>();
        InputStream iS = null;
        try {
            iS = getResources().getAssets().open("accelerometer_highPassFilter.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(iS));
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] lineSplit = line.split(",");
                AccelerationData accelerationData = new AccelerationData(Double.parseDouble(lineSplit[0]), Double.parseDouble(lineSplit[1]), Double.parseDouble(lineSplit[2]), Double.parseDouble(lineSplit[3]), Long.parseLong(lineSplit[4]));
                accelerationDataList.add(accelerationData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accelerationDataList;
    }

    private void stopAlgorithms() {
        sensorManager.unregisterListener(stepDetector);
        sensorManager.flush(stepDetector);
    }

    private void displayAlgorithmData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (runningAlgorithms) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edwardAlgorithmView.update(edwardAlgorithm.getAccelerationData());
                            chrisAlgorithmView.update(chrisAlgorithm.getAccelerationData());
                        }
                    });
                }
            }
        }).start();
    }

}
