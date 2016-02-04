package com.keepfit.app;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.graphics.Color;
import android.hardware.SensorEvent;
import android.os.SystemClock;
import android.util.Log;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.keepfit.app.sensor.accelerometer.BaseGravityFilter;
import com.keepfit.app.sensor.accelerometer.Constants;
import com.keepfit.app.sensor.accelerometer.GravityFilter;
import com.keepfit.app.sensor.accelerometer.HighPass;
import com.keepfit.app.sensor.accelerometer.LowPass;

class AccelerationEventListener implements SensorEventListener, PlotDataEventListener {
    private final String _tag;
    private static final char CSV_DELIM = ',';
    private static final int RMS_MOVEMENT_THRESHOLD = 2;
    private static final String CSV_HEADER = "Time,X Axis,Y Axis,Z Axis,Acceleration";
    private static final int MAX_SERIES_SIZE = 30;
    private static final int CHART_REFRESH = 125;
    private static final int MILLISEC_FACTOR = 1000000;

    private PrintWriter _printWriter;
    private long _startTime;
    private float[] _gravity;
    private int _highPassCount;
    private SimpleXYSeries _xAxisSeries;
    private SimpleXYSeries _yAxisSeries;
    private SimpleXYSeries _zAxisSeries;
    private SimpleXYSeries _accelerationSeries;
    private XYPlot _xyPlot;
    private long _lastChartRefresh;
    private String _movementText;
    private boolean _shouldPlotX;
    private boolean _shouldPlotY;
    private boolean _shouldPlotZ;
    private boolean _shouldPlotAccel;
    private GravityFilter _lowPass;
    private GravityFilter _highPass;
    private boolean _useHighPassFilter;
    private boolean _useLowPassFilter;

    public AccelerationEventListener(XYPlot xyPlot, boolean useHighPassFilter, boolean useLowPassFilter, File dataFile, String movementText) {
        _tag = this.getClass().getSimpleName();
        _xyPlot = xyPlot;
        _useHighPassFilter = useHighPassFilter;
        _useLowPassFilter = useLowPassFilter;
        if (_useHighPassFilter)
            _highPass = new HighPass(Constants.HIGH_PASS_ALPHA);

        if (_useLowPassFilter)
            _lowPass = new LowPass(Constants.LOW_PASS_ALPHA);

        _movementText = movementText;

        _xAxisSeries = new SimpleXYSeries("X Axis");
        _yAxisSeries = new SimpleXYSeries("Y Axis");
        _zAxisSeries = new SimpleXYSeries("Z Axis");
        _accelerationSeries = new SimpleXYSeries("Acceleration");

        _gravity = new float[Constants.GRAVITY_VECTOR_SIZE];
        _startTime = SystemClock.uptimeMillis();
        _highPassCount = 0;

        try {
            _printWriter = new PrintWriter(new BufferedWriter(new FileWriter(dataFile)));
            _printWriter.println(CSV_HEADER);
        } catch (IOException e) {
            Log.e(_tag, "Failed to open .csv file(s)", e);
        }

        if (xyPlot != null) {
            xyPlot.addSeries(_xAxisSeries, new LineAndPointFormatter(Color.RED, Color.RED, Color.TRANSPARENT, null));
            xyPlot.addSeries(_yAxisSeries, new LineAndPointFormatter(Color.GREEN, Color.GREEN, Color.TRANSPARENT, null));
            xyPlot.addSeries(_zAxisSeries, new LineAndPointFormatter(Color.BLUE, Color.BLUE, Color.TRANSPARENT, null));
            xyPlot.addSeries(_accelerationSeries, new LineAndPointFormatter(Color.CYAN, Color.CYAN, Color.TRANSPARENT, null));
        }

    }

    private void writeSensorData(PrintWriter printWriter, long eventTime, float x, float y, float z,
                                 double acceleration) {
        if (printWriter != null) {
            printWriter.println(String.valueOf(
                    (eventTime / MILLISEC_FACTOR) - _startTime)
                    + CSV_DELIM + x
                    + CSV_DELIM + y
                    + CSV_DELIM + z
                    + CSV_DELIM + acceleration);

            if (printWriter.checkError()) {
                Log.e(_tag, "Failed to write sensor event data");
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] accelerationVector = event.values.clone();
        long eventTimeStamp = event.timestamp;


        if (_useLowPassFilter) {
            accelerationVector = _lowPass.filter(accelerationVector[0], accelerationVector[1], accelerationVector[2]);
        }

        if (_useHighPassFilter) {
            accelerationVector = _highPass.filter(accelerationVector[0], accelerationVector[1], accelerationVector[2]);
        }
        // HPF only completes filtering out gravity after a set threshold of data points (HIGH_PASS_MINIMUM)
        if (!_useHighPassFilter || (++_highPassCount >= Constants.HIGH_PASS_MINIMUM)) {
            double rmsAcceleration = getAccelerationRMS(accelerationVector);

            writeSensorData(_printWriter, event.timestamp, accelerationVector[0], accelerationVector[1], accelerationVector[2], rmsAcceleration);
            plotData(accelerationVector, rmsAcceleration, eventTimeStamp);

            // rmsAcceleration must be above threshold to qualify as movement
            if (rmsAcceleration > RMS_MOVEMENT_THRESHOLD) {
                Log.i(_tag, _movementText);
            }
        }
    }

    private void plotData(float[] values, double rmsAcceleration, long timeStamp) {
        if (_xyPlot != null) {
            long current = SystemClock.uptimeMillis();

            if ((current - _lastChartRefresh) >= CHART_REFRESH) {
                long timestamp = (timeStamp / MILLISEC_FACTOR) - _startTime;

                if (_shouldPlotX)
                    addDataPoint(_xAxisSeries, timestamp, values[0]);
                else
                    addDataPoint(_xAxisSeries, timestamp, null);

                if (_shouldPlotY)
                    addDataPoint(_yAxisSeries, timestamp, values[1]);
                else
                    addDataPoint(_yAxisSeries, timestamp, null);

                if (_shouldPlotZ)
                    addDataPoint(_zAxisSeries, timestamp, values[2]);
                else
                    addDataPoint(_zAxisSeries, timestamp, null);

                if (_shouldPlotAccel)
                    addDataPoint(_accelerationSeries, timestamp, rmsAcceleration);
                else
                    addDataPoint(_accelerationSeries, timestamp, null);

                _xyPlot.redraw();
                _lastChartRefresh = current;
            }
        }
    }

    private double getAccelerationRMS(float[] values) {
        if (values == null) throw new IllegalArgumentException("ERROR: values arg cannot be null");
        if (values.length < 1)
            throw new IllegalArgumentException("ERROR: values array arg cannot be empty");
        return Math.sqrt(sumOfSquares(values));
    }

    private float sumOfSquares(float[] values) {
        if (values == null) throw new IllegalArgumentException("ERROR: values arg cannot be null");
        if (values.length < 1)
            throw new IllegalArgumentException("ERROR: values array arg cannot be empty");
        float sos = 0;
        double squared = 2;
        for (float f : values) {
            sos += Math.pow(f, squared);
        }
        return sos;
    }

    private void addDataPoint(SimpleXYSeries series, Number timestamp, Number value) {
        if (series.size() == MAX_SERIES_SIZE) {
            series.removeFirst();
        }
        series.addLast(timestamp, value);
    }

    public void stop() {
        if (_printWriter != null) {
            _printWriter.close();
            if (_printWriter.checkError()) {
                Log.e(_tag, "Error closing writer");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.e(_tag, "App doesn't support dynamic sensor accuracy changes");
    }

    @Override
    public void onPlotDataChanged(PlotDataEventArgs e) {
        switch (e.dataSeries) {
            case X:
                _shouldPlotX = e.isSelected;
                break;

            case Y:
                _shouldPlotY = e.isSelected;
                break;

            case Z:
                _shouldPlotZ = e.isSelected;
                break;

            case ACCELERATION:
                _shouldPlotAccel = e.isSelected;
                break;

            default:
                break;
        }
    }
}
