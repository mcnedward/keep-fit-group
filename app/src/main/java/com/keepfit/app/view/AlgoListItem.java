package com.keepfit.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keepfit.app.R;

import org.w3c.dom.Text;

/**
 * Created by Edward on 3/4/2016.
 */
public class AlgoListItem extends LinearLayout {

    private Context context;

    private TextView txtAlgoTitle;
    private TextView txtRealSteps;
    private TextView txtCountedSteps;

    public AlgoListItem(Context context) {
        super(context);
        this.context = context;
        inflate(context, R.layout.view_algo_list_item, this);
        initialize();
    }

    public AlgoListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate(context, R.layout.view_algo_list_item, this);
        initialize();
    }

    private void initialize() {
        txtAlgoTitle = (TextView) findViewById(R.id.text_algo_title);
        txtRealSteps = (TextView) findViewById(R.id.text_real_steps);
        txtCountedSteps = (TextView) findViewById(R.id.text_counted_steps);
    }

    public void setText(String title, String realSteps, String countedSteps) {
        txtAlgoTitle.setText(title);
        txtRealSteps.setText(realSteps);
        txtCountedSteps.setText(countedSteps);
    }
}
