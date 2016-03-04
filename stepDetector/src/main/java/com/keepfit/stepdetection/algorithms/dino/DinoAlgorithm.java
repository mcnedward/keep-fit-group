package com.keepfit.stepdetection.algorithms.dino;

import android.content.Context;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.BaseAlgorithm;

import java.util.List;

/**
 * Created by Dino on 23/02/2016.
 */
public class DinoAlgorithm extends BaseAlgorithm {

    public DinoAlgorithm(Context context) {
        super(context);
    }

    @Override
    public void handleSensorData(AccelerationData ad){
        calculateSteps(ad);
    }

    @Override
    public void notifySensorDataReceived(AccelerationData ad){
        calculateSteps(ad);
    }

    @Override
    public void notifySensorDataReceived(List<AccelerationData> adList){
        for (AccelerationData ad : adList) {
            calculateSteps(ad);
        }
    }
    //threshold formula is th = (a/(i-k)) + b

    private double threshold;
    private double alpha; //slope in line equation
    private int k = 0; // initial sample number for beginning of walk. Working with data from start so this initialised to 0
    private int i = 1; //i is initially K+1 at the start of each new step
    private double beta; // C cofficient in line equation

    private AccelerationData kPoint; //sample number at start of step
    private AccelerationData iPoint; //sample number of current sample to check

    private double max; //highest accZ so far
    private double accZi; //accZ of iPoint

    private double kStepFreq = 5f; // 1(i-k) //not sure if needed //initialised to 0.1f
    private double iStepFreq;

    private boolean walkingStarted = false;
    private boolean dataStreamStart = false;

    private int steps = 0;

    public int getStepCount(){
        return this.steps;
    }

    public void calculateSteps(AccelerationData ad ) {
        writeSensorData(ad);
        //start
        if(!dataStreamStart) { //if this is our first sample
            //k = sample number of sample where start point of walking is detected
            kPoint = ad;
            max = kPoint.getZ();
            k = 0;
            i = k + 1;
            dataStreamStart = true;
        }

        else {
            //while data is coming in
            iPoint = ad;
            accZi = iPoint.getZ(); //ad = iPoint

            double y2, y1, x2, x1;

            y2 = (max - accZi);
            y1 = (max - kPoint.getZ());
            x2 = (1 / (i - k));
            x1 = (kStepFreq/*1(k-k)*/); //unsure here, division by 0 only alternative to 0.1


            alpha = (y2 - y1) / (x2 - x1); // alpha = m = slope, calculate m between kPoint and iPoint
            iStepFreq = 1 / (i - k); //
            beta = 3.1541f;                                           // This bit doesn't do anything, will ensure new steps are detected with each sample EDIT: conclusion in paper states beta constant was found to be noted value
            threshold = (alpha * iStepFreq) + beta;     //

            if(walkingStarted) {
                if ((max - accZi) >= threshold) { //use threshold to check for step
                    steps += 1;

                    k = i;
                    kPoint = iPoint;
                    //  kStepFreq = iStepFreq;
                    max = accZi;
                } else {
                    if (accZi > max) {
                        max = accZi;
                    }

                }
            }
            else
            {
                if((max-accZi) >= 0.08){
                    walkingStarted = true;
                    k = i;
                }
            }
            i = i + 1;
        }
    }

    @Override
    public AccelerationData getAccelerationData() {
        return new AccelerationData(0, 0, accZi, 0);
    }
}
