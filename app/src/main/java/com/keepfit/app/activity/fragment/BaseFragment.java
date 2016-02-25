package com.keepfit.app.activity.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by Edward on 2/25/2016.
 */
public class BaseFragment extends Fragment {

    public enum FragmentCode {
        MAIN(1, "Algorithms"),
        SENSORS(2, "Sensors");
        int id;
        String title;
        FragmentCode(int id, String title) {
            this.id = id;
            this.title = title;
        }
        public int id() {
            return id;
        }
        public String title() {
            return title;
        }
    }

    public static BaseFragment newInstance(FragmentCode code) {
        switch (code) {
            case MAIN:
                return new MainFragment();
            case SENSORS:
                return new SensorFragment();
        }
        return null;
    }

}
