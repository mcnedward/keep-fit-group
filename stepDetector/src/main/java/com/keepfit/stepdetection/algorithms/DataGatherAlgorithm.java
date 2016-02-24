package com.keepfit.stepdetection.algorithms;

import android.content.Context;
import android.util.Log;

/**
 * Created by Edward on 2/24/2016.
 */
public class DataGatherAlgorithm extends BaseAlgorithm {
    private static final String TAG = "DataGatherAlgorithm";

    private String dataType;

    public DataGatherAlgorithm(Context context, String dataType) {
        super(context);
        this.dataType = dataType;
    }

    @Override
    protected void handleSensorData(AccelerationData ad) {
        Log.i(TAG, "Gathering data for " + dataType);
    }
}
