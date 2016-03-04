package com.keepfit.app.activity.fragment.algorithm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.keepfit.app.R;
import com.keepfit.app.activity.fragment.BaseFragment;
import com.keepfit.app.view.AlgorithmView;
import com.keepfit.stepdetection.accelerometer.filter.Constants;
import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.IAlgorithm;
import com.keepfit.stepdetection.algorithms.IStepDetector;
import com.keepfit.stepdetection.algorithms.StepDetector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 2/25/2016.
 */
public abstract class AlgorithmFragment extends BaseFragment {
    private static final String TAG = "AlgorithmFragment";

    protected Context context;
    protected static final int RATE = 10000;
    protected SensorManager sensorManager;
    protected IStepDetector stepDetector;
    private boolean runningAlgorithm;

    // Algorithms
    protected IAlgorithm algorithm;
    private AlgorithmView algorithmView;
    private EditText algorithmName;
    // XY Plot
    private XYPlot xyPlot;
    private static final String X_LABEL = "Elapsed Time (microseconds)";
    private static final String Y_LABEL = "Acceleration (metres/sec^2)";
    private static final int UPPER_BOUNDARY = 15;
    private static final int LOWER_BOUNDARY = -5;
    private static final int MAX_SERIES_SIZE = 30;
    private static final int CHART_REFRESH = 125;
    private static final int MILLISEC_FACTOR = 1000000;
    private long startTime;
    private long lastChartRefresh;
    private float[] gravity;
    private int highPassCount;
    private SimpleXYSeries xAxisSeries;
    private SimpleXYSeries yAxisSeries;
    private SimpleXYSeries zAxisSeries;
    private SimpleXYSeries accelerationSeries;
    private boolean shouldPlotX = true;
    private boolean shouldPlotY = true;
    private boolean shouldPlotZ = true;
    private boolean shouldPlotAccel = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initialize(view);
        return view;
    }

    @Override
     public void onAttach(Context context) {
        super.onAttach(context);
        initializeAlgorithm(context);
    }

    private void initialize(View view) {
        context = view.getContext();
        stepDetector = new StepDetector(context);
        algorithm = getAlgorithm();
        stepDetector.registerAlgorithm(algorithm);
        algorithmName = (EditText) view.findViewById(R.id.algorithm_email_name);

        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

        algorithmView = (AlgorithmView) view.findViewById(R.id.algorithm_view);
        algorithmView.setAlgorithm(algorithm);
        algorithmView.setStartButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!runningAlgorithm) {
                    runningAlgorithm = true;
                    ((Button) v).setText(getResources().getString(R.string.stop));
                    startAlgorithm();
                    String name = algorithmName.getText().toString();
                    if (name.equals(""))
                        name = "NeedsAName";

                    xyPlot.setTitle(getTitle());

                    displayAlgorithmData();
                } else {
                    runningAlgorithm = false;
                    ((Button) v).setText(getResources().getString(R.string.start));
                    stopAlgorithm();
                }
            }
        });

        algorithmView.setRefreshButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAlgorithm();
            }
        });

        algorithmView.setEmailButtonOnClickListener();
        algorithmView.setTitle(getTitle());

        initializeXYPlot(view);
    }

    private void initializeXYPlot(View view) {
        xyPlot = (XYPlot) view.findViewById(R.id.xy_plot);
        xyPlot.setDomainLabel(X_LABEL);
        xyPlot.setRangeLabel(Y_LABEL);
        xyPlot.setBorderPaint(null);
        xyPlot.setRangeBoundaries(LOWER_BOUNDARY, UPPER_BOUNDARY, BoundaryMode.FIXED);
        xyPlot.setRangeStep(XYStepMode.SUBDIVIDE, Math.abs(LOWER_BOUNDARY - UPPER_BOUNDARY));
        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1);
        xyPlot.setTicksPerRangeLabel(1);

        xAxisSeries = new SimpleXYSeries("X Axis");
        yAxisSeries = new SimpleXYSeries("Y Axis");
        zAxisSeries = new SimpleXYSeries("Z Axis");
        accelerationSeries = new SimpleXYSeries("Acceleration");

        gravity = new float[Constants.ACCELEROMETER_VECTOR_SIZE];
        startTime = SystemClock.uptimeMillis();
        highPassCount = 0;

        xyPlot.addSeries(xAxisSeries, new LineAndPointFormatter(Color.RED, Color.RED, Color.TRANSPARENT, null));
        xyPlot.addSeries(yAxisSeries, new LineAndPointFormatter(Color.GREEN, Color.GREEN, Color.TRANSPARENT, null));
        xyPlot.addSeries(zAxisSeries, new LineAndPointFormatter(Color.BLUE, Color.BLUE, Color.TRANSPARENT, null));
        xyPlot.addSeries(accelerationSeries, new LineAndPointFormatter(Color.CYAN, Color.CYAN, Color.TRANSPARENT, null));
    }

    protected abstract void initializeAlgorithm(Context context);

    protected abstract void startAlgorithm();

    protected abstract IAlgorithm getAlgorithm();

    protected abstract String getTitle();

    public abstract void createNewAlgorithm();

    private void stopAlgorithm() {
        sensorManager.unregisterListener(stepDetector);
        sensorManager.flush(stepDetector);
    }

    private void displayAlgorithmData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (runningAlgorithm) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            algorithmView.update(algorithm.getAccelerationData());
                            plotData(algorithm.getAccelerationData());
                        }
                    });
                }
            }
        }).start();
    }

    protected List<AccelerationData> loadFile() {
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

    private void plotData(AccelerationData data) {
        if (xyPlot != null) {
            long current = SystemClock.uptimeMillis();

            if ((current - lastChartRefresh) >= CHART_REFRESH) {
                long timestamp = (data.getTimeStamp() / MILLISEC_FACTOR) - startTime;

                if (shouldPlotX)
                    addDataPoint(xAxisSeries, timestamp, data.getX());
                else
                    addDataPoint(xAxisSeries, timestamp, null);

                if (shouldPlotY)
                    addDataPoint(yAxisSeries, timestamp, data.getY());
                else
                    addDataPoint(yAxisSeries, timestamp, null);

                if (shouldPlotZ)
                    addDataPoint(zAxisSeries, timestamp, data.getZ());
                else
                    addDataPoint(zAxisSeries, timestamp, null);

                if (shouldPlotAccel)
                    addDataPoint(accelerationSeries, timestamp, data.getAcceleration());
                else
                    addDataPoint(accelerationSeries, timestamp, null);

                xyPlot.redraw();
                lastChartRefresh = current;
            }
        }
    }
    private void addDataPoint(SimpleXYSeries series, Number timestamp, Number value) {
        if (series.size() == MAX_SERIES_SIZE) {
            series.removeFirst();
        }
        series.addLast(timestamp, value);
    }

}
