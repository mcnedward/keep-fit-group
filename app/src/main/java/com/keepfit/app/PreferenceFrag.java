package com.keepfit.app;


import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferenceFrag extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);
    }
}
