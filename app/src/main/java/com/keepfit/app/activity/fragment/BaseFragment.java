package com.keepfit.app.activity.fragment;

import android.support.v4.app.Fragment;

import com.keepfit.app.activity.fragment.algorithm.ChrisFragment;
import com.keepfit.app.activity.fragment.algorithm.DinoFragment;
import com.keepfit.app.activity.fragment.algorithm.EdwardFragment;
import com.keepfit.app.activity.fragment.algorithm.KornelFragment;

/**
 * Created by Edward on 2/25/2016.
 */
public class BaseFragment extends Fragment {

    public enum FragmentCode {
        EDWARD(1, "Edward"),
        KORNEL(2, "Kornel"),
        DINO(3, "Dino"),
        CHRIS(4, "Chris"),
        SENSORS(5, "Sensors");
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
            case EDWARD:
                return new EdwardFragment();
            case KORNEL:
                return new KornelFragment();
            case DINO:
                return new DinoFragment();
            case CHRIS:
                return new ChrisFragment();
            case SENSORS:
                return new SensorFragment();
        }
        return null;
    }

}
