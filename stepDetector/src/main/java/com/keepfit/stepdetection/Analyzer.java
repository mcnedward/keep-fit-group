package com.keepfit.stepdetection;

import com.keepfit.stepdetection.accelerometer.filter.Util;
import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.IAlgorithm;
import com.keepfit.stepdetection.algorithms.chris.ChrisAlgorithm;
import com.keepfit.stepdetection.algorithms.dino.DinoAlgorithm;
import com.keepfit.stepdetection.algorithms.edward.EdwardAlgorithm;
import com.keepfit.stepdetection.algorithms.kornel.KornelAlgorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 3/3/2016.
 */
public class Analyzer {

    private List<IAlgorithm> algorithms;

    public Analyzer() {
        EdwardAlgorithm edwardAlgorithm = new EdwardAlgorithm();
        DinoAlgorithm dinoAlgorithm = new DinoAlgorithm(null);
        KornelAlgorithm kornelAlgorithm = new KornelAlgorithm(null);
        ChrisAlgorithm chrisAlgorithm = new ChrisAlgorithm(null);
        algorithms.add(edwardAlgorithm);
        algorithms.add(dinoAlgorithm);
        algorithms.add(kornelAlgorithm);
        algorithms.add(chrisAlgorithm);
    }

    public void analyze() {
        List<AccelerationData> data = parseAccelerationData();

        for (IAlgorithm algorithm : algorithms) {
            algorithm.notifySensorDataReceived(data);
            int steps = algorithm.getStepCount();
        }
    }

    private List<AccelerationData> parseAccelerationData() {
        List<AccelerationData> data = new ArrayList<>();
        File directory = new File("directory");
        File[] folders = directory.listFiles();
        for (File folder : folders) {
            File[] files = folder.listFiles();
            for (File file : files) {
                try {
                    data = readLines(file);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static List<AccelerationData> readLines(File file) throws IOException {
        List<AccelerationData> data = new ArrayList<>();
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        int lineNumber = 1;
        while ((line = bufferedReader.readLine()) != null) {
            if (lineNumber < 6) {
                lineNumber++;
                continue;
            }
            String[] values = line.split(",");
            data.add(new AccelerationData(Double.parseDouble(values[1]), Double.parseDouble(values[2]),
                    Double.parseDouble(values[3]), Long.parseLong(values[0])));
            lines.add(line);
        }
        bufferedReader.close();
        fileReader.close();
        return data;
    }

}
