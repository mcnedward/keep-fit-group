package com.keepfit.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.keepfit.app.R;
import com.keepfit.stepdetection.algorithms.AccelerationData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 2/24/2016.
 */
public class Extension {

    public static void emailDataFile(Context context, List<File> dataFiles, String[] toEmails) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (File dataFile : dataFiles)
            uris.add(Uri.fromFile(dataFile));
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, toEmails);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Step Detector");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(Intent.createChooser(intent, "Sending email..."));
    }

    private static final int MS_TO_SEC = 1000;

    public static DataFile handleReader(BufferedReader reader, int numberOfRealSteps, int runNumber) throws IOException {
        DataFile dataFile = new DataFile(numberOfRealSteps, runNumber);
        List<AccelerationData> data = new ArrayList<>();

        String line;
        int lineNumber = 1;
        boolean skip = false;
        while ((line = reader.readLine()) != null) {
            if (lineNumber < 8) {
                lineNumber++;
                continue;
            }
            if (skip) {
                skip = false;
                continue;
            }
            String[] values = line.split("\t");
            double time = Double.parseDouble(values[0]);
            long timestamp = (long) (time * MS_TO_SEC);
            data.add(new AccelerationData(Double.parseDouble(values[1]), Double.parseDouble(values[2]),
                    Double.parseDouble(values[3]), timestamp));
            skip = true;
        }
        reader.close();

        dataFile.setData(data);
        return dataFile;
    }

//    /**
//     * Creates a new RippleDrawable for a ripple effect on a View.
//     *
//     * @param rippleColor     The color of the ripple.
//     * @param backgroundColor The color of the background for the ripple. If this is 0, then there will be no background and the ripple effect will be circular.
//     * @param context         The context.
//     * @return A RippleDrawable.
//     */
//    public static void setRippleBackground(View view, int rippleColor, int backgroundColor, Context context) {
//        view.setBackground(new RippleDrawable(
//                new ColorStateList(
//                        new int[][]
//                                {
//                                        new int[]{android.R.attr.state_window_focused},
//                                },
//                        new int[]
//                                {
//                                        ContextCompat.getColor(context, rippleColor),
//                                }),
//                backgroundColor == 0 ? null : new ColorDrawable(ContextCompat.getColor(context, backgroundColor)),
//                null));
//    }
//
//    /**
//     * Creates a new RippleDrawable for a ripple effect on a View. This will create a ripple with the default color of FireBrick for the ripple and GhostWhite for the background.
//     *
//     * @param context The context.
//     * @return A RippleDrawable.
//     */
//    public static void setRippleBackground(View view, Context context) {
//        setRippleBackground(view, R.color.FireBrick, R.color.GhostWhite, context);
//    }

}
