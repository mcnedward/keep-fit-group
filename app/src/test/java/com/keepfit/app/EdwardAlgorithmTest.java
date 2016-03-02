package com.keepfit.app;

import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.edward.EdwardAlgorithm;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class EdwardAlgorithmTest extends BaseTest {

    private EdwardAlgorithm algorithm;

    @Before
    public void setUp() {
    }

    @Test
    public void edwardAlgorithm_shouldRunFine() {
        for (int i = 1; i <= 10; i++) {
            int steps = getNumberOfStepsFromDataFile();
            System.out.println(i + "| Number of steps: " + steps);
            assertThat(steps, is(30));
        }
    }

    private int getNumberOfStepsFromDataFile() {
        algorithm = new EdwardAlgorithm();
        List<AccelerationData> dataList = getAccelerationDataFromFile("data2.csv");
        for (AccelerationData data : dataList) {
            algorithm.handleSensorData(data);
        }
        int steps = algorithm.getStepCount();
        return steps;
    }

}