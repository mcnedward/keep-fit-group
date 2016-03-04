package com.keepfit.app.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.keepfit.stepdetection.algorithms.IAlgorithm;
import com.keepfit.stepdetection.algorithms.chris.ChrisAlgorithm;
import com.keepfit.stepdetection.algorithms.dino.DinoAlgorithm;
import com.keepfit.stepdetection.algorithms.edward.EdwardAlgorithm;
import com.keepfit.stepdetection.algorithms.kornel.KornelAlgorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 3/4/2016.
 */
public class DataFolderLoader extends AsyncTaskLoader<List<DataFolder>> {
    private final static String TAG = "DataFolderLoader";

    protected Context context;
    private List<DataFolder> dataFolders = null;
    private List<IAlgorithm> algorithms;

    public DataFolderLoader(Context context) {
        super(context);
        this.context = context;
        initializeAlgorithms();
    }

    private void initializeAlgorithms() {
        algorithms = new ArrayList<>();
        EdwardAlgorithm edwardAlgorithm = new EdwardAlgorithm();
        DinoAlgorithm dinoAlgorithm = new DinoAlgorithm();
        KornelAlgorithm kornelAlgorithm = new KornelAlgorithm();
        ChrisAlgorithm chrisAlgorithm = new ChrisAlgorithm();
        algorithms.add(edwardAlgorithm);
        algorithms.add(dinoAlgorithm);
        algorithms.add(kornelAlgorithm);
        algorithms.add(chrisAlgorithm);
    }

    @Override
    public List<DataFolder> loadInBackground() {
        List<DataFolder> dataFolders = new ArrayList<>();
        parseDataFiles(dataFolders);
        if (dataFolders.size() > 0)
            analyze(dataFolders);
        return dataFolders;
    }

    private void parseDataFiles(List<DataFolder> dataFolders) {
        try {
            // Call listAssetFiles to populate the List<String> files
            listAssetFiles("Finished");
            for (DataFolder folder : folders) {
                List<DataFile> files = new ArrayList<>();
                int i = 1;
                for (String file : folder.getFileNames()) {
                    InputStream stream = context.getAssets().open(file);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    files.add(Extension.handleReader(reader, folder.getNumberOfSteps(), i++));
                }
                folder.setFiles(files);
                dataFolders.add(folder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    List<DataFolder> folders = new ArrayList<>();
    List<String> files;
    DataFolder folder;
    boolean startNewFolder;
    private boolean listAssetFiles(String path) {
        String[] assetFiles;
        try {
            assetFiles = context.getAssets().list(path);
            if (assetFiles.length > 0) {
                // Directory
                // Don't create a folder for the top level directory
                if (!path.equals("Finished"))
                    folder = new DataFolder(path);
                startNewFolder = true;
                for (String f : assetFiles) {
                    if (!listAssetFiles(path + "/" + f))
                        return false;
                }
                if (folder != null && files != null && files.size() > 0) {
                    folder.setFileNames(files);
                    folders.add(folder);
                }
            } else {
                // File
                if (startNewFolder) {
                    files = new ArrayList<>();
                    startNewFolder = false;
                }
                files.add(path);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    private void analyze(List<DataFolder> dataFolders) {
        for (DataFolder folder : dataFolders) {
            for (DataFile file : folder.getFiles()) {
                for (IAlgorithm algorithm : algorithms) {
                    algorithm.notifySensorDataReceived(file.getData());
                    file.addAlgorithm(algorithm);
                    Log.i(TAG, String.format(
                            "Algorithm: %s; Author: %s; Number Of Real Steps: %s; Mode: %s; Orientation: %s; Run Number: %s; Number of Algorithm Steps: %s",
                            algorithm.getName(), folder.getAuthor(), folder.getNumberOfSteps(), folder.getMode(),
                            folder.getOrientation(), file.getRunNumber(), algorithm.getStepCount()));
                }
                initializeAlgorithms();
            }
        }
    }

    /**
     * Runs on the UI thread, routing the results from the background thread to whatever is using the dataList.
     *
     * @param dataList The list of data.
     */
    @Override
    public void deliverResult(List<DataFolder> dataList) {
        Log.d(TAG, String.format("Delivering results!"));
        if (isReset()) {
            resetDataList(dataList);
            return;
        }
        List<DataFolder> oldDataList = dataFolders;
        dataFolders = dataList;
        if (isStarted()) {
            super.deliverResult(dataFolders);
        }
        if (oldDataList != null && oldDataList != dataList && oldDataList.size() > 0) {
            resetDataList(dataFolders);
        }
    }

    /**
     * Starts an asynchronous load of the list data. When the result is ready
     * the callbacks will be called on the UI thread. If a previous load has
     * been completed and is still valid, the result may be passed to the
     * callbacks immediately. Must be called from the UI thread.
     */
    @Override
    public void onStartLoading() {
        if (dataFolders != null) {
            deliverResult(dataFolders);
        }
        if (takeContentChanged() || dataFolders == null || dataFolders.size() == 0) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread, triggered by a call to stopLoading().
     */
    @Override
    public void onStopLoading() {
        cancelLoad();
    }

    /**
     * Must be called from the UI thread, triggered by a call to cancel(). Here,
     * we make sure our Cursor is closed, if it still exists and is not already
     * closed.
     */
    @Override
    public void onCanceled(List<DataFolder> dataList) {
        // TODO Change this
        if (dataList != null && dataList.size() > 0)
            resetDataList(dataList);
    }

    /**
     * Must be called from the UI thread, triggered by a call to reset(). Here,
     * we make sure our Cursor is closed, if it still exists and is not already
     * closed.
     */
    @Override
    public void onReset() {
        super.onReset();
        Log.d(TAG, String.format("onReset called for loader..."));
        // Ensure the loader is stopped
        onStopLoading();
        if (dataFolders != null && dataFolders.size() > 0) {
            resetDataList(dataFolders);
        }
        dataFolders = null;
    }

    private void resetDataList(List<DataFolder> dataList) {
        dataList = new ArrayList<>();
    }
}
