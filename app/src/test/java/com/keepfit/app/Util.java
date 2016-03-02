package com.keepfit.app;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Edward on 3/1/2016.
 */
public class Util {

    public static void writeConfiguration(Context ctx) {
        BufferedWriter writer = null;
        try {
            FileOutputStream openFileOutput =
                    ctx.openFileOutput("config.txt", Context.MODE_PRIVATE);
            openFileOutput.write("This is a test1.".getBytes());
            openFileOutput.write("This is a test2.".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static InputStream getInputStream(Object obj, String fileName) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(fileName);
        return stream;
    }
}
