package com.keepfit.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keepfit.app.R;
import com.keepfit.app.utils.Extension;
import com.keepfit.stepdetection.algorithms.AccelerationData;
import com.keepfit.stepdetection.algorithms.IAlgorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 2/25/2016.
 */
public class AlgorithmView extends LinearLayout {

    private Context context;

    private TextView txtTitle;
    private TextView txtX;
    private TextView txtY;
    private TextView txtZ;
    private TextView txtSteps;
    private Button refreshButton;
    private Button startButton;

    private IAlgorithm algorithm;

    public AlgorithmView(Context context) {
        super(context);
        this.context = context;
        inflate(context, R.layout.view_algorithm, this);
        initialize();
    }

    public AlgorithmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate(context, R.layout.view_algorithm, this);
        initialize();
    }

    private void initialize() {
        txtTitle = (TextView) findViewById(R.id.title);
        txtX = (TextView) findViewById(R.id.txt_x);
        txtY = (TextView) findViewById(R.id.txt_y);
        txtZ = (TextView) findViewById(R.id.txt_z);
        txtSteps = (TextView) findViewById(R.id.txt_steps);
        refreshButton = (Button) findViewById(R.id.btn_refresh);
        startButton = (Button) findViewById(R.id.btn_algorithm);
    }

    public void setStartButtonOnClickListener(OnClickListener listener) {
        startButton.setOnClickListener(listener);
    }

    public void setRefreshButtonOnClickListener(OnClickListener listener) {
        refreshButton.setOnClickListener(listener);
    }

    public void setTitle(String title) {
        txtTitle.setText(title);
    }

    public void update(AccelerationData ad) {
        txtX.setText(String.valueOf(ad.getX()));
        txtY.setText(String.valueOf(ad.getY()));
        txtZ.setText(String.valueOf(ad.getZ()));
        if (algorithm != null)
            txtSteps.setText(String.valueOf(algorithm.getStepCount()));
    }

    public void setAlgorithm(IAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
