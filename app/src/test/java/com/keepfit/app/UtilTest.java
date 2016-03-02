package com.keepfit.app;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileOutputStream;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Edward on 3/1/2016.
 */
public class UtilTest {
    @Mock
    Context context;

    @Mock
    FileOutputStream fileOutputStream;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void writeShouldWriteTwiceToFileSystem() {
        try {
            when(context.openFileOutput(anyString(), anyInt())).thenReturn(fileOutputStream);
            Util.writeConfiguration(context);
            verify(context, times(1)).openFileOutput(anyString(), anyInt());
            verify(fileOutputStream, atLeast(2)).write(any(byte[].class));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void fileStreamShouldNotBeNull() throws Exception {
        String file = "data.csv";
        InputStream stream = Util.getInputStream(this, file);
        assertThat(stream, notNullValue());
    }
}
