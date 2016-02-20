package com.keepfit.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.prefs.Preferences;

enum StepDetectAlgorithm {FREQ_INDEPENDENT};

public class MainActivity extends AppCompatActivity {

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
    }

    private void initControls() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.settings:
            {
                Intent intent = new Intent(this, Preferences.class);
                intent.putExtra( MyPreferenceActivity.EXTRA_SHOW_FRAGMENT, PreferenceFrag.class.getName() );
                intent.putExtra( MyPreferenceActivity.EXTRA_NO_HEADERS, true );
                intent.setClassName(this, "com.keepfit.app.MyPreferenceActivity");
                startActivity(intent);
                return true;
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
