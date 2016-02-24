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

    public BaseAlgorithm(Context context) {
        this.context = context;
        startTime = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat("EEE_d_MMM_ yyyy_HHmm");
        String date = df.format(Calendar.getInstance().getTime());
        dataFile = new File(String.format("AlgorithmData_%s.csv", date));
        try {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(dataFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writeSensorData(long eventTime, float x, float y, float z, double acceleration) {
        if (printWriter != null) {
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

    public void emailDataFile() {
        if (printWriter != null) {
            printWriter.close();
        }
        Uri uri = Uri.fromFile(dataFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"edwardmcn64@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Step Detector");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, "Sending email..."));
    }
}
