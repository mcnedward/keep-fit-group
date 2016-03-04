package com.keepfit.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.keepfit.app.R;
import com.keepfit.app.utils.DataFile;
import com.keepfit.stepdetection.algorithms.IAlgorithm;
import com.keepfit.stepdetection.algorithms.chris.ChrisAlgorithm;
import com.keepfit.stepdetection.algorithms.dino.DinoAlgorithm;
import com.keepfit.stepdetection.algorithms.kornel.KornelAlgorithm;

/**
 * Created by Edward on 3/4/2016.
 */
public class ResultView extends LinearLayout {

    private DataFile dataFile;
    private Context context;

    private AlgoListItem dinoItem;
    private AlgoListItem kornelItem;
    private AlgoListItem chrisItem;

    public ResultView(DataFile dataFile, Context context) {
        super(context);
        this.dataFile = dataFile;
        this.context = context;
        inflate(context, R.layout.view_result, this);
        initialize();
    }

    public ResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate(context, R.layout.view_result, this);
        initialize();
    }

    private void initialize() {
        dinoItem = (AlgoListItem) findViewById(R.id.dino_algorithm);
        kornelItem = (AlgoListItem) findViewById(R.id.kornel_algorithm);
        chrisItem = (AlgoListItem) findViewById(R.id.chris_algorithm);
        if (dataFile != null)
            update();
    }

    public void update() {
        for (IAlgorithm algorithm : dataFile.getAlgorithms()) {
            if (algorithm instanceof DinoAlgorithm) {
                dinoItem.setText(algorithm.getName(), String.valueOf(dataFile.getNumberOfRealSteps()), String.valueOf(algorithm.getStepCount()));
            }
            if (algorithm instanceof KornelAlgorithm) {
                kornelItem.setText(algorithm.getName(), String.valueOf(dataFile.getNumberOfRealSteps()), String.valueOf(algorithm.getStepCount()));
            }
            if (algorithm instanceof ChrisAlgorithm) {
                chrisItem.setText(algorithm.getName(), String.valueOf(dataFile.getNumberOfRealSteps()), String.valueOf(algorithm.getStepCount()));
            }
        }
    }

}
