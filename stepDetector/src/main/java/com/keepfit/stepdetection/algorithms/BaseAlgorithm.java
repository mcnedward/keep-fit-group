package com.keepfit.stepdetection.algorithms;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

    private Context context;
    private PrintWriter printWriter;
    private File dataFile;
    private long startTime;
    private boolean runAlgorithm;   // Boolean to determine whether the algorithm should be run, or if only data should be gathered.

    public BaseAlgorithm(Context context) {
        this.context = context;
        startTime = System.currentTimeMillis();
        runAlgorithm = true;

        DateFormat df = new SimpleDateFormat("EEE_d_MMM_ yyyy_HHmm");
        String date = df.format(Calendar.getInstance().getTime());
        dataFile = new File(context.getExternalCacheDir() + String.format("AlgorithmData_%s_%s.csv", date, new Random().nextInt(10)));
        try {
            dataFile.createNewFile();
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(dataFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifySensorDataReceived(AccelerationData ad) {
        double acceleration = ad.getAcceleration();
        writeSensorData(ad.getTimeStamp(), ad.getX(), ad.getY(), ad.getZ(), acceleration);
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

    protected void writeSensorData(long eventTime, double x, double y, double z, double acceleration) {
        if (printWriter != null) {
            Log.d(TAG, "Writing " + acceleration);
            printWriter.println(String.valueOf((eventTime / MILLISEC_FACTOR) - startTime)
                    + CSV_DELIM + x
                    + CSV_DELIM + y
                    + CSV_DELIM + z
                    + CSV_DELIM + acceleration);
            if (printWriter.checkError()) {
                Log.e(TAG, "Failed to write sensor event data");
            }
        }
    }

    @Override
    public File getDataFile() {
        return dataFile;
    }

    @Override
    public void shouldRunAlgorithm(boolean runAlgorithm) {
        this.runAlgorithm = runAlgorithm;
    }
}
