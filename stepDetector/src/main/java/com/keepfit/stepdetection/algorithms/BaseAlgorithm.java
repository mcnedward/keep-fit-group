package com.keepfit.stepdetection.algorithms;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Edward on 2/24/2016.
 */
public abstract class BaseAlgorithm implements IAlgorithm {
    private final static String TAG = "BaseAlgorithm";
    private static final char CSV_DELIM = ',';
    private static final int MILLISEC_FACTOR = 1000000;

    protected Context context;
    private PrintWriter printWriter;
    private File dataFile;
    private long startTime;
    private boolean runAlgorithm;   // Boolean to determine whether the algorithm should be run, or if only data should be gathered.
    private boolean writeData;
    private String name;

    public BaseAlgorithm(Context context) {
        this(context, "BaseAlgorithm");
    }

    public BaseAlgorithm(Context context, String name) {
        this.context = context;
        this.name = name;
        startTime = System.currentTimeMillis();
        runAlgorithm = true;
        if (context != null)
            createFile(name);
    }

    public BaseAlgorithm(String name) {
        this(null, name);
        writeData = true;
    }

    @Override
    public void notifySensorDataReceived(AccelerationData ad) {
        double acceleration = ad.getAcceleration();
        if (writeData) {
            ad.setAcceleration(acceleration);
            writeSensorData(ad);
        }
        if (runAlgorithm)
            handleSensorData(ad);
    }

    @Override
    public void notifySensorDataReceived(List<AccelerationData> adList) {
        for (AccelerationData ad : adList) {
            notifySensorDataReceived(ad);
        }
    }

    protected abstract void handleSensorData(AccelerationData ad);

    protected void writeSensorData(AccelerationData data) {
        if (printWriter != null) {
            printWriter.println(String.valueOf((data.getTimeStamp() / MILLISEC_FACTOR) - startTime)
                    + CSV_DELIM + data.getX()
                    + CSV_DELIM + data.getY()
                    + CSV_DELIM + data.getZ()
                    + CSV_DELIM + data.getAcceleration());
            if (printWriter.checkError()) {
                Log.e(TAG, "Failed to write sensor event data");
            }
        }
    }

    protected void writeSensorData(AccelerationData data, double threshold) {
        if (printWriter != null) {
            printWriter.println(String.valueOf((data.getTimeStamp() / MILLISEC_FACTOR) - startTime)
                    + CSV_DELIM + data.getX()
                    + CSV_DELIM + data.getY()
                    + CSV_DELIM + data.getZ()
                    + CSV_DELIM + data.getAcceleration()
                    + CSV_DELIM + threshold);
            if (printWriter.checkError()) {
                Log.e(TAG, "Failed to write sensor event data");
            }
        }
    }

    @Override
    public void createFile(String fileName) {
        DateFormat df = new SimpleDateFormat("EEE_d_MMM_ yyyy_HHmm");
        String date = df.format(Calendar.getInstance().getTime());
        dataFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                + String.format("/%s_AlgorithmData_%s_%s.csv", fileName, date, new Random().nextInt(10)));
        try {
            dataFile.createNewFile();
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(dataFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWriteData(boolean writeData) {
        this.writeData = writeData;
    }

    @Override
    public File getDataFile() {
        return dataFile;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void shouldRunAlgorithm(boolean runAlgorithm) {
        this.runAlgorithm = runAlgorithm;
    }
}
