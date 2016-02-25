package com.keepfit.stepdetection.algorithms.dino;

import android.content.Context;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.BaseAlgorithm;

import java.util.List;

public class DinoAlgorithm extends BaseAlgorithm {

    //threshold formula is th = (a/(i-k)) + b
    private int numberOfSteps = 0;

    private double threshold;

    private double alpha; //slope in line equation
    private int k = 0; // initial sample number for beginning of walk. Working with data from start so this initialised to 0
    private int i = 1; //i is initially K+1 at the start of each new step
    private double beta; // C cofficient in line equation
    private AccelerationData kPoint; //sample number at start of step

    private AccelerationData iPoint; //sample number of current sample to check
    private double max; //highest accZ so far

    private double accZi; //accZ of iPoint
    private double kStepFreq; // 1(i-k) //not sure if needed

    private double iStepFreq;
    private boolean dataStreamStart = false;

    public DinoAlgorithm(Context context) {
        super(context);
    }

    @Override
    protected void handleSensorData(AccelerationData ad) {


        //start
        if (!dataStreamStart) { //if this is our first sample
            //k = sample number of sample where start point of walking is detected
            kPoint = ad;
            max = ad.getZ();
            k = 0;
            i = k + 1;
        } else {
            //while data is coming in
            iPoint = ad;
            accZi = ad.getZ(); //ad = iPoint

            /**
             * This is the hard part, cannot figure out how to derive alpha and beta "constants" as described in paper
             * initial step frequency set to 0.1, no idea if this is right call, however for subsequent Ks (new steps) they can be set to the i at which a new step is found
             */
            double y2, y1, x2, x1;

            y2 = (max - accZi);
            y1 = (max - kPoint.getZ());
            x2 = (1 / (i - k));
            x1 = (0.1f/*1(k-k)*/); //unsure here, division by 0 only alternative to 0.1

            alpha = (y2 - y1) / (x2 - x1); // slope = m, calculate m between kpoint and ipoint
            iStepFreq = 1 / (i - k); //
            double tempThreshold = (max - accZi);
            beta = tempThreshold - (iStepFreq * alpha); //
            // This bit doesn't do anything, will ensure new steps are detected with each sample
            threshold = (alpha * iStepFreq) + beta;     //

            if ((max - accZi) >= threshold) { //use threshold to check for step
                numberOfSteps += 1;
                k = i;
                kPoint = iPoint;
                kStepFreq = iStepFreq;
                max = accZi;
            } else {
                if (accZi > max) {
                    max = accZi;
                }
                i = i + 1;
            }
        }
    }

    @Override
    public int getStepCount() {
        return numberOfSteps;
    }
}

