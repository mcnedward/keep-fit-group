package com.keepfit.app.utils;

import com.keepfit.stepdetection.algorithms.AccelerationData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 3/4/2016.
 */
public class DataFile {

    private List<AccelerationData> data;

    public DataFile() {
        data = new ArrayList<>();
    }

    /**
     * @return the data
     */
    public List<AccelerationData> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<AccelerationData> data) {
        this.data = data;
    }

}
