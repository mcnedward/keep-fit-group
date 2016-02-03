package com.keepfit.app;

public class PlotDataEventArgs {
    public boolean isSelected;
    public DataSeries dataSeries;

    public PlotDataEventArgs(DataSeries dataSeries, boolean isSelected) {
        this.dataSeries = dataSeries;
        this.isSelected = isSelected;
    }
}
