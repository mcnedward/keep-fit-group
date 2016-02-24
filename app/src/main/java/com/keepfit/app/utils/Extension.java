package com.keepfit.app.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
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
}
