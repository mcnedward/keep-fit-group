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
        DINO(2, "Dino"),
        KORNEL(3, "Kornel"),
        CHRIS(4, "Chris"),
        RESULTS(5, "Results"),
        SENSORS(6, "Sensors");
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
            case DINO:
                return new DinoFragment();
            case EDWARD:
                return new EdwardFragment();
            case KORNEL:
                return new KornelFragment();
            case CHRIS:
                return new ChrisFragment();
            case RESULTS:
                return new ResultsFragment();
            case SENSORS:
                return new SensorFragment();
        }
        return null;
    }

}
