package com.keepfit.app.activity.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.keepfit.app.R;

public class PreferenceFrag extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);
    }
}
