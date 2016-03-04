package com.keepfit.app.utils;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.IAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 3/4/2016.
 */
public class DataFile {

    private List<AccelerationData> data;
    private List<IAlgorithm> algorithms;
    private int numberOfRealSteps;

    public DataFile(int numberOfRealSteps) {
        this.numberOfRealSteps = numberOfRealSteps;
        data = new ArrayList<>();
        algorithms = new ArrayList<>();
    }

    public void addAlgorithm(IAlgorithm algorithm) {
        algorithms.add(algorithm);
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

    public List<IAlgorithm> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<IAlgorithm> algorithms) {
        this.algorithms = algorithms;
    }

    public int getNumberOfRealSteps() {
        return numberOfRealSteps;
    }

    public void setNumberOfRealSteps(int numberOfRealSteps) {
        this.numberOfRealSteps = numberOfRealSteps;
    }
}
