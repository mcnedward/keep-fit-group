package com.keepfit.app;

import com.keepfit.stepdetection.algorithms.edward.AngleAlgorithm;

import org.junit.Test;

import java.util.Random;

/**
 * Created by Edward on 2/19/2016.
 */
public class GravityUnitTest {

    private Random random;
    private AngleAlgorithm angleAlgorithm;

    public GravityUnitTest() {
        random = new Random();
        angleAlgorithm = new AngleAlgorithm();
    }

    @Test
    public void testGravity() {
        float gravity = random.nextFloat();
        System.out.println(gravity);
    }

}
