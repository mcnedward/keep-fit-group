package com.keepfit.app.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keepfit.app.R;
import com.keepfit.app.utils.DataFolder;

/**
 * Created by Edward on 3/4/2016.
 */
public class DataFolderListView extends LinearLayout {
    private static String TAG = "DataFolderListView";

    private Context context;
    private TextView txtAuthor;
    private TextView txtMode;
    private TextView txtOrientation;
    private TextView txtSteps;

    public DataFolderListView(DataFolder dataFolder, Context context) {
        super(context);
        this.context = context;

        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        inflate(context, R.layout.data_folder_list_view, this);

        txtAuthor = (TextView) findViewById(R.id.text_author);
        txtMode = (TextView) findViewById(R.id.text_mode);
        txtOrientation = (TextView) findViewById(R.id.text_orientation);
        txtSteps = (TextView) findViewById(R.id.text_steps);
        update(dataFolder);
    }

    public void update(DataFolder dataFolder) {
        txtAuthor.setText(dataFolder.getAuthor());
        txtMode.setText(dataFolder.getMode());
        txtOrientation.setText(dataFolder.getOrientation());
        txtSteps.setText(String.valueOf(dataFolder.getNumberOfSteps()));
    }

}
