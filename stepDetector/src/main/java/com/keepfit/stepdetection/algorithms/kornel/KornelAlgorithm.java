package com.keepfit.stepdetection.algorithms.kornel;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.BaseAlgorithm;

import java.util.List;

/**
 * Created by kornelkotan on 23/02/2016.
 */
public class KornelAlgorithm implements BaseAlgorithm {


    @Override
    public void notifySensorDataRecieved(AccelerationData ad) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void notifySensorDataRecieved(List<AccelerationData> adList) {
        double threshold;
        int stepsCounted;

        threshold = calculateThreshold(adList);
        stepsCounted = calculateStepcount(adList,threshold);

    }

    private double calculateThreshold(List<AccelerationData> adList) {
        double peakMean;
        int peakCount = 0;
        double peakAccumulate = 0;


        for (int k = 0; k < adList.size(); k++) {
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

    private int calculateStepcount(List<AccelerationData> adList, double threshold) {
        int stepCount = 0;
        double thresholdMultiplier = 0.7;
        //TODO set the lowfilter regarder to the values from the csv
        double lowFilter = 15;


        for (int k = 0; k < adList.size(); k++) {
            double currentValue = adList.get(k).getXYZMagnitude();
            double nextValue = adList.get(k + 1).getXYZMagnitude();
            double previousValue = adList.get(k - 1).getXYZMagnitude();

            double forwardSlope = nextValue - currentValue;
            double backwardSlope = currentValue - previousValue;
            if ((forwardSlope < 0 && backwardSlope > 0) && (currentValue > (thresholdMultiplier * threshold)) && currentValue > lowFilter) {
                stepCount++;
            }
        }
        return stepCount;
    }


}
