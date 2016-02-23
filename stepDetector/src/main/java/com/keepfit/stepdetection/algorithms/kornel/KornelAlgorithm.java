package com.keepfit.stepdetection.algorithms.kornel;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.BaseAlgorithm;

import java.util.List;

/**
 * Created by kornelkotan on 23/02/2016.
 */
public class KornelAlgorithm implements BaseAlgorithm{


    @Override
    public void notifySensorDataRecieved(AccelerationData ad) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void notifySensorDataRecieved(List<AccelerationData> adList) {

    }
}
