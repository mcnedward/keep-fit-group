package com.keepfit.stepdetection.algorithms;

import java.util.List;

/**
 * Created by Edward on 2/23/2016.
 */
public interface BaseAlgorithm {

    public void notifySensorDataRecieved(AccelerationData ad);

    public void notifySensorDataRecieved(List<AccelerationData> adList);
}
