package com.keepfit.app;

import java.util.EventListener;

public interface PlotDataEventListener extends EventListener {
    public void onPlotDataChanged(PlotDataEventArgs event);
}
