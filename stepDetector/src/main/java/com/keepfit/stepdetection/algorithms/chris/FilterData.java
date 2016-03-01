package com.keepfit.stepdetection.algorithms.chris;

/**
 * Created by Chris on 01/03/2016.
 */
public class FilterData {


    private final long time;
    private final double raw;
    private final double kalmanFiltered;
    private final double algoFiltered;
    private final double stepData;
    private final double devA;
    private final double devB;

    public FilterData(long time, double raw, double kalmanFiltered, double algoFiltered, double stepData, double devA,
                      double devB)
    {
        super();
        this.time = time;
        this.raw = raw;
        this.kalmanFiltered = kalmanFiltered;
        this.algoFiltered = algoFiltered;
        this.stepData = stepData;
        this.devA = devA;
        this.devB = devB;
    }

    public long getTime()
    {
        return time;
    }

    public double getRaw()
    {
        return raw;
    }

    public double getKalmanFiltered()
    {
        return kalmanFiltered;
    }

    public double getAlgoFiltered()
    {
        return algoFiltered;
    }

    public double getStepData()
    {
        return stepData;
    }

    @Override
    public String toString()
    {
        return String.format("time:%s\n\traw:%s\n\tkFil:%s\n\taFil:%s\n\taStp:%s\n",
                getTime(), getRaw(), getKalmanFiltered(), getAlgoFiltered(), getStepData());
    }

    public double getDevA()
    {
        return devA;
    }

    public double getDevB()
    {
        return devB;
    }
}
