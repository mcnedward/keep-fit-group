package com.keepfit.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.keepfit.app.R;
import com.keepfit.app.utils.DataFile;

/**
 * Created by Edward on 3/4/2016.
 */
public class ResultView extends LinearLayout {

    private DataFile dataFile;
    private Context context;

    private AlgoListItem kornelItem;
    private AlgoListItem dinoItem;
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
        kornelItem = (AlgoListItem) findViewById(R.id.kornel_algorithm);
        dinoItem = (AlgoListItem) findViewById(R.id.dino_algorithm);
        chrisItem = (AlgoListItem) findViewById(R.id.chris_algorithm);
    }

}
