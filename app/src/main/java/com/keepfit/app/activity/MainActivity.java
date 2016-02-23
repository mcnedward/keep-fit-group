package com.keepfit.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.keepfit.app.activity.fragment.PreferenceFrag;
import com.keepfit.app.R;
import com.keepfit.app.utils.StepDetectAlgorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private SharedPreferences _preferences;
    private StepDetectAlgorithm _stepAlgorithm = StepDetectAlgorithm.FREQ_INDEPENDENT;
    private boolean _recordSensorData = false;
    private String _dataFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //_readingAccelerationData = false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // _sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        _preferences = getPreferences(MODE_PRIVATE);

        initControls();

        loadFile();
    }

    private void initControls() {
    }

    private void loadFile() {
        InputStream iS = null;
        try {
            iS = getResources().getAssets().open("accelerometer_highPassFilter.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(iS));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d(TAG, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings: {
                Intent intent = new Intent(this, Preferences.class);
                intent.putExtra(MyPreferenceActivity.EXTRA_SHOW_FRAGMENT, PreferenceFrag.class.getName());
                intent.putExtra(MyPreferenceActivity.EXTRA_NO_HEADERS, true);
                intent.setClassName(this, "com.keepfit.app.activity.MyPreferenceActivity");
                startActivity(intent);
                return true;
            }
            case R.id.action_supported_sensors: {
                Intent intent = new Intent(this, SupportedSensorsActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //private void initControls() {
    //    _lowPassFilterCheckBox = (CheckBox) findViewById(R.id.lowPassFilterCheckBox);
    //    _useLowPassFilter = getResources().getBoolean(R.bool.useLowPassFilterDefaultValue);
    //    _useLowPassFilter = _preferences.getBoolean(USE_HIGH_PASS_FILTER_PREFERENCE_KEY,
    //            _useLowPassFilter);
    //    ((CheckBox) findViewById(R.id.lowPassFilterCheckBox)).setChecked(_useLowPassFilter);
    //}
    //
    //public void onSwitchModeSelectorClicked(View view) {
    //    _recordOnlyModeSwitch = (Switch) findViewById(R.id.stepDetectorModeSwitch);
    //    _stepDetectorMode = _recordOnlyModeSwitch.get
    //    _useHighPassFilter = getResources().getBoolean(R.bool.useHighPassFilterDefaultValue);
    //    _useHighPassFilter = _preferences.getBoolean(USE_HIGH_PASS_FILTER_PREFERENCE_KEY,
    //            _useHighPassFilter);
    //    ((CheckBox) findViewById(R.id.highPassFilterCheckBox)).setChecked(_useHighPassFilter);
    //}
}
