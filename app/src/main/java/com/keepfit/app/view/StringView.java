package com.keepfit.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keepfit.app.R;
import com.keepfit.app.utils.SimpleStringAdapter;

/**
 * Created by Edward on 2/23/2016.
 */
public class StringView extends RelativeLayout {

    private Context context;
    private TextView txtListItem;
    private SimpleStringAdapter adapter;
    private String text;

    public StringView(String text, Context context) {
        super(context);
        this.text = text;
        this.context = context;
        initialize();
    }

    public StringView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        inflate(context, R.layout.string_list_item, this);
        txtListItem = (TextView) findViewById(R.id.string_list_item);
        if (text != null)
            txtListItem.setText(text);
    }

    public void update(String text, SimpleStringAdapter adapter) {
        this.adapter = adapter;
        txtListItem.setText(text);
    }
}
