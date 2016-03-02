package com.keepfit.stepdetection.algorithms;

import java.io.File;
import java.util.List;

/**
 * Created by Edward on 2/23/2016.
 */
public interface IAlgorithm {

    void notifySensorDataReceived(AccelerationData ad);

    void notifySensorDataReceived(List<AccelerationData> adList);

    File getDataFile();

    void shouldRunAlgorithm(boolean runAlgorithm);

    int getStepCount();

    void createFile(String fileName);

    AccelerationData getAccelerationData();

}
