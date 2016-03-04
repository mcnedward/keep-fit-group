package com.keepfit.app.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.androidplot.ui.SeriesRenderer;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.keepfit.app.R;
import com.keepfit.app.utils.DataFile;
import com.keepfit.stepdetection.algorithms.IAlgorithm;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward on 3/4/2016.
 */
public class ResultView extends LinearLayout {

    private List<DataFile> dataFiles;
    private Context context;

    private AlgoListItem dinoItem;
    private AlgoListItem kornelItem;
    private AlgoListItem chrisItem;

    private XYPlot plot;
    private XYSeries series1;
    private XYSeries series2;
    private Pair<Integer, XYSeries> selection;
    private MyBarFormatter selectionFormatter;
    private MyBarFormatter formatter1;
    private MyBarFormatter formatter2;
    private SeekBar sbFixedWidth, sbVariableWidth;


    public ResultView(List<DataFile> dataFiles, Context context) {
        super(context);
        this.dataFiles = dataFiles;
        this.context = context;
        inflate(context, R.layout.view_result, this);
//        initialize();
        initializePlot();
    }

    public ResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate(context, R.layout.view_result, this);
        initializePlot();
    }

    private void setUpStep() {
        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
        plot.setTicksPerDomainLabel(2);
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
    }

    private void initializePlot() {
        plot = (XYPlot) findViewById(R.id.data_plot);
        formatter1 = new MyBarFormatter(Color.argb(200, 100, 150, 100), Color.LTGRAY);
        formatter2 = new MyBarFormatter(Color.argb(200, 100, 100, 150), Color.LTGRAY);
        selectionFormatter = new MyBarFormatter(Color.YELLOW, Color.WHITE);

        if (dataFiles != null)
            setUpStep();

        plot.setDomainValueFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
                return new StringBuffer("Algorithms");
            }

            @Override
            public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }

            @Override
            public Number parse(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });

        sbFixedWidth = (SeekBar) findViewById(R.id.sb_width);
        sbFixedWidth.setProgress(25);
        sbFixedWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updatePlot();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if (dataFiles != null)
            updatePlot();
    }

    private void updatePlot() {
        // Remove all current series from each plot
        plot.clear();

        getDataToPlot();

        // Setup our Series with the selected number of elements
        series1 = new SimpleXYSeries(realSteps, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Real Steps");
        series2 = new SimpleXYSeries(countedSteps, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Counted Steps");

        // add a new series' to the xyplot:
        plot.addSeries(series1, formatter1);
        plot.addSeries(series2, formatter2);

        // Setup the BarRenderer with our selected options
        MyBarRenderer renderer = ((MyBarRenderer)plot.getRenderer(MyBarRenderer.class));
        renderer.setBarRenderStyle(BarRenderer.BarRenderStyle.SIDE_BY_SIDE);
        renderer.setBarWidthStyle(BarRenderer.BarWidthStyle.FIXED_WIDTH);
        renderer.setBarWidth(sbFixedWidth.getProgress());
        renderer.setBarGap(2);

        plot.setRangeTopMin(dataFiles.get(0).getNumberOfRealSteps() + 5);

        plot.redraw();
    }

    List<Integer> realSteps;
    List<Integer> countedSteps;
    private void getDataToPlot() {
        realSteps = new ArrayList<>();
        countedSteps = new ArrayList<>();
        for (DataFile file : dataFiles)
            for (IAlgorithm algorithm : file.getAlgorithms()) {
                realSteps.add(file.getNumberOfRealSteps());
                countedSteps.add(algorithm.getStepCount());
            }
    }

    class MyBarFormatter extends BarFormatter {
        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer getRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        /**
         * Implementing this method to allow us to inject our
         * special selection getFormatter.
         * @param index index of the point being rendered.
         * @param series XYSeries to which the point being rendered belongs.
         * @return
         */
        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if(selection != null &&
                    selection.second == series &&
                    selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }

}
