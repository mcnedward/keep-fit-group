package com.keepfit.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;
import android.util.Log;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.text.DateFormat;
import java.util.Calendar;


public class DetermineMovementActivity extends Activity {
    private static final String TAG = "DetermineMoveActivity";
    private static final int RATE = SensorManager.SENSOR_DELAY_NORMAL;
    private static final String USE_HIGH_PASS_FILTER_PREFERENCE_KEY = "USE_HIGH_PASS_FILTER_PREFERENCE_KEY";
    private static final String SELECTED_SENSOR_TYPE_PREFERENCE_KEY = "SELECTED_SENSOR_TYPE_PREFERENCE_KEY";
    private Vector<PlotDataEventListener> _plotDataListeners;
    private static final String X_LABEL = "Elapsed Time (milliseconds)";
    private static final String Y_LABEL = "Acceleration (metres/sec^2)";
    private static final String LINEAR_ACCEL_LABEL = "LinearAccelerationSensor";
    private static final String ACCEL_LABEL = "Accelerometer";

    private static final int UPPER_BOUNDARY = 15;
    private static final int LOWER_BOUNDARY = -15;

    ToggleButton _toggleReadSensorButton;
    private SensorManager _sensorManager;
    private RadioGroup _sensorSelector;
    private CheckBox _highPassFilterCheckBox;
    private CheckBox _plotXCheckBox;
    private CheckBox _plotYCheckBox;
    private CheckBox _plotZCheckBox;
    private CheckBox _plotAccelCheckBox;
    private SharedPreferences _preferences;
    private int _selectedSensorType;
    private boolean _readingAccelerationData;
    private boolean _useHighPassFilter;
    private boolean _shouldPlotX;
    private boolean _shouldPlotY;
    private boolean _shouldPlotZ;
    private boolean _shouldPlotAccel;
    private boolean _hasSensor = false;
    private AccelerationEventListener _sensorListener;
    private XYPlot _xyPlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_determine_movement);
        _plotDataListeners = new Vector<>();

        _readingAccelerationData = false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        _sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        _preferences = getPreferences(MODE_PRIVATE);

        initControls();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopReadingAccelerationData();
    }

    private void initControls() {
        _highPassFilterCheckBox = (CheckBox) findViewById(R.id.highPassFilterCheckBox);
        _useHighPassFilter = getResources().getBoolean(R.bool.useHighPassFilterDefaultValue);
        _useHighPassFilter = _preferences.getBoolean(USE_HIGH_PASS_FILTER_PREFERENCE_KEY,
                _useHighPassFilter);
        ((CheckBox) findViewById(R.id.highPassFilterCheckBox)).setChecked(_useHighPassFilter);

        _plotXCheckBox = (CheckBox) findViewById(R.id.xDataPlotCheckBox);
        _shouldPlotX = getResources().getBoolean(R.bool.shouldPlotXDefaultValue);
        _plotXCheckBox.setChecked(_shouldPlotX);

        _plotYCheckBox = (CheckBox) findViewById(R.id.yDataPlotCheckBox);
        _shouldPlotY = getResources().getBoolean(R.bool.shouldPlotYDefaultValue);
        _plotYCheckBox.setChecked(_shouldPlotY);

        _plotZCheckBox = (CheckBox) findViewById(R.id.zDataPlotCheckBox);
        _shouldPlotZ = getResources().getBoolean(R.bool.shouldPlotZDefaultValue);
        _plotZCheckBox.setChecked(_shouldPlotZ);

        _plotAccelCheckBox = (CheckBox) findViewById(R.id.accelDataPlotCheckBox);
        _shouldPlotAccel = getResources().getBoolean(R.bool.shouldPlotAccelDefaultValue);
        _plotAccelCheckBox.setChecked(_shouldPlotAccel);

        setPlotCheckBoxEnabled(false);

        _sensorSelector = (RadioGroup) findViewById(R.id.sensorSelector);
        _selectedSensorType = _preferences.getInt(SELECTED_SENSOR_TYPE_PREFERENCE_KEY, Sensor.TYPE_ACCELEROMETER);

        if (_selectedSensorType == Sensor.TYPE_ACCELEROMETER) {
            ((RadioButton) findViewById(R.id.accelerometer)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.linearAcceleration)).setChecked(true);
        }

        _xyPlot = (XYPlot) findViewById(R.id.XYPlot);
        _xyPlot.setDomainLabel(X_LABEL);
        _xyPlot.setRangeLabel(Y_LABEL);
        _xyPlot.setBorderPaint(null);
        _xyPlot.setRangeBoundaries(LOWER_BOUNDARY, UPPER_BOUNDARY, BoundaryMode.FIXED);
        _xyPlot.setRangeStep(XYStepMode.SUBDIVIDE, Math.abs(LOWER_BOUNDARY - UPPER_BOUNDARY));
        _xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1);
        _xyPlot.setTicksPerRangeLabel(1);
    }

    private void setPlotCheckBoxEnabled(boolean enabled) {
        _plotXCheckBox.setEnabled(enabled);
        _plotYCheckBox.setEnabled(enabled);
        _plotZCheckBox.setEnabled(enabled);
        _plotAccelCheckBox.setEnabled(enabled);
    }

    public void onSensorSelectorClick(View view) {
        int selectedSensorId = _sensorSelector.getCheckedRadioButtonId();
        if (selectedSensorId == R.id.accelerometer) {
            _selectedSensorType = Sensor.TYPE_ACCELEROMETER;
        } else if (selectedSensorId == R.id.linearAcceleration) {
            _selectedSensorType = Sensor.TYPE_LINEAR_ACCELERATION;
        }

        _preferences.edit().putInt(SELECTED_SENSOR_TYPE_PREFERENCE_KEY, _selectedSensorType).apply();
    }

    public void onReadAccelerationDataToggleButtonClicked(View view) {
        _toggleReadSensorButton = (ToggleButton) view;

        if (_toggleReadSensorButton.isChecked()) {
            startReadingAccelerationData();
        } else {
            stopReadingAccelerationData();
        }
    }

    private void readSensor(String title, File outFile, int sensorId) {
        _xyPlot.setTitle(title);
        _sensorListener = new AccelerationEventListener(_xyPlot, _useHighPassFilter, outFile,
                getString(R.string.movementDetectedText));
        registerPlotDataListener(_sensorListener);
        setPlotStatusForListeners();
        if (!_sensorManager.registerListener(_sensorListener,
                _sensorManager.getDefaultSensor(sensorId), RATE)) {
            Log.e(TAG, "Failed to register sensor listener as device doesn't have : " + title);
            _hasSensor = false;
            stopReadingAccelerationData();
        } else {
            _hasSensor = true;
            _readingAccelerationData = true;
            Log.d(TAG, "Started reading acceleration data");
        }
    }

    private void startReadingAccelerationData() {
        if (!_readingAccelerationData) {
            _hasSensor = false;

            // Clear existing plot
            _xyPlot.clear();
            _xyPlot.redraw();

            // Disable sensor selector controls
            for (int i = 0; i < _sensorSelector.getChildCount(); i++) {
                _sensorSelector.getChildAt(i).setEnabled(false);
            }
            _highPassFilterCheckBox.setEnabled(false);

            // Enable plot controls
            setPlotCheckBoxEnabled(true);

            DateFormat df = new SimpleDateFormat("EEE_d_MMM_ yyyy_HHmm");
            String date = df.format(Calendar.getInstance().getTime());

            File dataOutFile = null;
            String title = "";
            if (_selectedSensorType == Sensor.TYPE_ACCELEROMETER) {
                title = "Sensor.TYPE_ACCELEROMETER";
                dataOutFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                        ACCEL_LABEL + date + ".csv");
            } else {
                title = "Sensor.TYPE_LINEAR_ACCELERATION";
                dataOutFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                        LINEAR_ACCEL_LABEL + date + ".csv");
            }

            readSensor(title, dataOutFile, _selectedSensorType);
        }
    }

    private void stopReadingAccelerationData() {
        if (_readingAccelerationData || !_hasSensor) {
            // Enable Ui widgets
            for (int i = 0; i < _sensorSelector.getChildCount(); i++) {
                _sensorSelector.getChildAt(i).setEnabled(true);
            }

            if (_sensorListener != null) {
                unregisterPlotDataListener(_sensorListener);
                _sensorManager.unregisterListener(_sensorListener);
                _sensorListener.stop();
            }

            setPlotCheckBoxEnabled(false);
            _highPassFilterCheckBox.setEnabled(true);
            _readingAccelerationData = false;

            if (!_hasSensor) Log.d(TAG, "Stopped reading acceleration data");

            if (_toggleReadSensorButton != null)
                _toggleReadSensorButton.setChecked(false);
        }
    }

    private void setPlotStatusForListeners() {
        firePlotData(new PlotDataEventArgs(DataSeries.X, _shouldPlotX));
        firePlotData(new PlotDataEventArgs(DataSeries.Y, _shouldPlotY));
        firePlotData(new PlotDataEventArgs(DataSeries.Z, _shouldPlotZ));
        firePlotData(new PlotDataEventArgs(DataSeries.ACCELERATION, _shouldPlotAccel));
    }

    public void onHighPassFilterCheckBoxClicked(View view) {
        _useHighPassFilter = ((CheckBox) view).isChecked();
        _preferences.edit().putBoolean(USE_HIGH_PASS_FILTER_PREFERENCE_KEY, _useHighPassFilter)
                .apply();
    }

    public void onPlotXCheckBoxClicked(View view) {
        _shouldPlotX = ((CheckBox) view).isChecked();
        firePlotData(new PlotDataEventArgs(DataSeries.X, _shouldPlotX));
    }

    public void onPlotYCheckBoxClicked(View view) {
        _shouldPlotY = ((CheckBox) view).isChecked();
        firePlotData(new PlotDataEventArgs(DataSeries.Y, _shouldPlotY));
    }

    public void onPlotZCheckBoxClicked(View view) {
        _shouldPlotZ = ((CheckBox) view).isChecked();
        firePlotData(new PlotDataEventArgs(DataSeries.Z, _shouldPlotZ));
    }

    public void onPlotAccelCheckBoxClicked(View view) {
        _shouldPlotAccel = ((CheckBox) view).isChecked();
        firePlotData(new PlotDataEventArgs(DataSeries.ACCELERATION, _shouldPlotAccel));
    }


    public synchronized void registerPlotDataListener(PlotDataEventListener listener) {
        if (!_plotDataListeners.contains(listener)) {
            _plotDataListeners.add(listener);
        }
    }

    public synchronized void unregisterPlotDataListener(PlotDataEventListener listener) {
        if (_plotDataListeners.contains(listener)) {
            _plotDataListeners.remove(listener);
        }
    }

    protected void firePlotData(PlotDataEventArgs e) {
        Vector<PlotDataEventListener> tempPlotDataEventListeners;

        synchronized (this) {
            if (_plotDataListeners.size() == 0) return;
            tempPlotDataEventListeners = (Vector<PlotDataEventListener>) _plotDataListeners.clone();
        }

        for (PlotDataEventListener listener : tempPlotDataEventListeners) {
            listener.onPlotDataChanged(e);
        }
    }

}
