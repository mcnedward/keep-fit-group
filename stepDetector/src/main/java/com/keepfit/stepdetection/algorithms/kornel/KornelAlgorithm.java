package com.keepfit.stepdetection.algorithms.kornel;

import android.content.Context;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.BaseAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kornelkotan on 23/02/2016.
 */
public class KornelAlgorithm extends BaseAlgorithm {

    private static String NAME = "Kornel Algorithm";

    private int stepsCounted;
    private int overallSteps;
    private long timeWindow = 5000;
    List<AccelerationData> buffer = new ArrayList<>();
    private AccelerationData cachedData;

    public KornelAlgorithm(Context context) {
        super(context, NAME);
        cachedData = new AccelerationData(0, 0, 0, 0);
    }

    public KornelAlgorithm() {
        super(NAME);
        cachedData = new AccelerationData(0, 0, 0, 0);
    }

    @Override
    public void handleSensorData(AccelerationData data) {
        buffer.add(data);
        if (timeWindow < (buffer.get(buffer.size() - 1).getTimeStamp() - buffer.get(0).getTimeStamp())) {
            handleSensorData(buffer);
            buffer.clear();
        }
    }

    public void handleSensorData(List<AccelerationData> adList) {
        double threshold;
        threshold = calculateThreshold(adList);
        stepsCounted = calculateStepCount(adList, threshold);
        overallSteps += stepsCounted;
    }

    private double calculateThreshold(List<AccelerationData> adList) {
        double peakMean;
        int peakCount = 0;
        double peakAccumulate = 0;

        for (int k = 1; k < adList.size() - 1; k++) {
            double forwardSlope = adList.get(k + 1).getXYZMagnitude() - adList.get(k).getXYZMagnitude();
            double backwardSlope = adList.get(k).getXYZMagnitude() - adList.get(k - 1).getXYZMagnitude();

            if (forwardSlope < 0 && backwardSlope > 0) {
                peakCount++;
                peakAccumulate = peakAccumulate + adList.get(k).getXYZMagnitude();
            }
        }
        peakMean = peakAccumulate / peakCount;
        return peakMean;
    }

    private int calculateStepCount(List<AccelerationData> adList, double threshold) {
        int stepCount = 0;
        double thresholdMultiplier = 0.85;
        long minTimeBetweenSteps = 200;
        // TODO set the lowfilter regarder to the values from the csv
        double lowFilter = 11;
        long lastStepFoundTimeStamp = 0;

        for (int k = 1; k < adList.size() - 1; k++) {
            double currentValue = adList.get(k).getXYZMagnitude();
            double nextValue = adList.get(k + 1).getXYZMagnitude();
            double previousValue = adList.get(k - 1).getXYZMagnitude();

            double forwardSlope = nextValue - currentValue;
            double backwardSlope = currentValue - previousValue;

            if ((forwardSlope < 0 && backwardSlope > 0) && (currentValue > (thresholdMultiplier * threshold))
                    && currentValue > lowFilter) {
                if ((minTimeBetweenSteps < adList.get(k).getTimeStamp() - lastStepFoundTimeStamp)) {
                    stepCount++;
                    lastStepFoundTimeStamp = adList.get(k).getTimeStamp();
                }
            }
            cachedData = adList.get(k);
        }
        return stepCount;
    }

    @Override
    public int getStepCount() {
        return overallSteps;
    }

    @Override
    public AccelerationData getAccelerationData() {
        return cachedData;
    }

}
