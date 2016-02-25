package com.keepfit.app.activity.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.keepfit.app.R;
import com.keepfit.app.utils.Extension;
import com.keepfit.app.utils.SimpleStringAdapter;
import com.keepfit.stepdetection.algorithms.DataGatherAlgorithm;
import com.keepfit.stepdetection.algorithms.IAlgorithm;
import com.keepfit.stepdetection.algorithms.IStepDetector;
import com.keepfit.stepdetection.algorithms.StepDetector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 2/25/2016.
 */
public class SensorFragment extends BaseFragment {

    private Context context;
    private View view;
    private SensorManager sensorManager;
    private static final int RATE = 10000;
    private List<Holder> holders;
    private static final String FROM_EMAIL = "edwardmcn64@gmail.com";
    private List<String> toEmails;
    private EditText editEmail;
    private LinearLayout emailsLayout;
    private Button btnSendEmail;
    private List<View> emailViews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sensors, container, false);
        initialize();
        return view;
    }

    private void initialize() {
        context = view.getContext();
        holders = new ArrayList<>();
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        toEmails = new ArrayList<>();
        emailViews = new ArrayList<>();
        initializeAlgorithmHolders();
        initializeSensors();
        initializeView();
        setUserVisibleHint(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            stop();
        } else {
            if (holders == null || holders.isEmpty())
                initializeAlgorithmHolders();
            initializeSensors();
        }
    }

    public void emailDataFiles() {
        List<File> dataFiles = new ArrayList<>();
        for (Holder holder : holders) {
            if (holder.hasSensor) {
                List<IAlgorithm> algorithms = holder.stepDetector.getAlgorithms();
                for (IAlgorithm algorithm : algorithms) {
                    dataFiles.add(algorithm.getDataFile());
                }
            }
        }
        Extension.emailDataFile(context, dataFiles, toEmails.toArray(new String[toEmails.size()]));
    }

    private void initializeAlgorithmHolders() {
        holders = new ArrayList<>();

        IStepDetector detector1 = new StepDetector(context);
        detector1.registerAlgorithm(new DataGatherAlgorithm(context, "Accelerometer"));
        Holder holder1 = new Holder(detector1, Sensor.TYPE_ACCELEROMETER);

        IStepDetector detector2 = new StepDetector(context);
        detector2.registerAlgorithm(new DataGatherAlgorithm(context, "Linear Accelerometer"));
        Holder holder2 = new Holder(detector2, Sensor.TYPE_LINEAR_ACCELERATION);

        IStepDetector detector3 = new StepDetector(context);
        detector3.registerAlgorithm(new DataGatherAlgorithm(context, "Gravity"));
        Holder holder3 = new Holder(detector3, Sensor.TYPE_GRAVITY);

        IStepDetector detector4 = new StepDetector(context);
        detector4.registerAlgorithm(new DataGatherAlgorithm(context, "Gyroscope"));
        Holder holder4 = new Holder(detector4, Sensor.TYPE_GYROSCOPE);

        holders.add(holder1);
        holders.add(holder2);
        holders.add(holder3);
        holders.add(holder4);
    }

    private void initializeSensors() {
        List<String> supportedSensors = new ArrayList<>();

        for (Holder holder : holders) {
            holder.hasSensor = sensorManager.registerListener(holder.stepDetector, sensorManager.getDefaultSensor(holder.sensorType), RATE);
            switch (holder.sensorType) {
                case Sensor.TYPE_ACCELEROMETER:
                    supportedSensors.add(holder.hasSensor ? "Your device has the Accelerometer." : "Your device does not have the Accelerometer.");
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    supportedSensors.add(holder.hasSensor ? "Your device has the Linear Acceleration sensor." : "Your device does not have the Linear Acceleration sensor.");
                    break;
                case Sensor.TYPE_GRAVITY:
                    supportedSensors.add(holder.hasSensor ? "Your device has the Gravity sensor." : "Your device does not have the Gravity sensor.");
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    supportedSensors.add(holder.hasSensor ? "Your device has the Gyroscope." : "Your device does not have the Gyroscope.");
                    break;
            }
        }

        ListAdapter adapter = new SimpleStringAdapter(supportedSensors, context);
        ((ListView) view.findViewById(R.id.sensors)).setAdapter(adapter);
    }

    private void initializeView() {
        btnSendEmail = (Button) view.findViewById(R.id.btn_send_email);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailDataFiles();
            }
        });

        emailsLayout = (LinearLayout) view.findViewById(R.id.emails);

        addEmailTextView(FROM_EMAIL);

        editEmail = (EditText) view.findViewById(R.id.edit_email);
        editEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addEmailTextView(v.getText().toString());
                v.setText("");
                return true;
            }
        });
    }

    private void addEmailTextView(String email) {
        final TextView emailView = new TextView(context);
        emailView.setText(email);
        emailView.setPadding(24, 10, 10, 0);
        emailView.setTextColor(ContextCompat.getColor(context, R.color.Black));
        emailsLayout.addView(emailView);
        emailViews.add(emailView);
        btnSendEmail.setEnabled(true);
        toEmails.add(email);
        emailView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                emailsLayout.removeView(v);
                if (emailViews.contains(v)) {
                    emailViews.remove(v);
                }
                if (emailViews.size() == 0) {
                    btnSendEmail.setText("Add an email");
                    btnSendEmail.setEnabled(false);
                } else {
                    btnSendEmail.setText("Email");
                    btnSendEmail.setEnabled(true);
                }
                return true;
            }
        });
    }

    private void stop() {
        if (holders != null)
            for (Holder holder : holders) {
                sensorManager.unregisterListener(holder.stepDetector);
                sensorManager.flush(holder.stepDetector);
                holder.stepDetector.reset();
            }
        holders = null;
    }

}

class Holder {
    protected IStepDetector stepDetector;
    protected int sensorType;
    protected boolean hasSensor = false;

    protected Holder(IStepDetector stepDetector, int sensorType) {
        this.stepDetector = stepDetector;
        this.sensorType = sensorType;
    }
}
