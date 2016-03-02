package com.keepfit.app;

import android.util.Log;

import com.keepfit.stepdetection.algorithms.AccelerationData;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by Edward on 3/1/2016.
 */
public class BaseTest {

    @Before
    public void setupClass() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Parses the data from a csv file into a list of AccelerationData.
     * NOTE: This may change depending on the format of the csv file, with the values being taken based on their column in the csv file.
     * @param fileName The name of the file, located in the src/test/resources directory of the app.
     * @return A list of AccelerationData.
     */
    protected List<AccelerationData> getAccelerationDataFromFile(String fileName) {
        List<AccelerationData> dataList = new ArrayList<>();
        try {
            InputStream stream = Util.getInputStream(this, "data.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            boolean skip = true;
            while ((line = reader.readLine()) != null) {
                if (skip) {
                    skip = false;
                    continue;
                }
                String[] values = line.split(",");
                AccelerationData data = new AccelerationData(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2]), Double.parseDouble(values[3]), Long.parseLong(values[4]));
                dataList.add(data);
            }

            reader.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        return dataList;
    }

    @Test
    public void edwardAlgorithm_shouldRunFine() {
        List<AccelerationData> dataList = getAccelerationDataFromFile("data.csv");
        assertThat(dataList.isEmpty(), is(false));
    }

}
