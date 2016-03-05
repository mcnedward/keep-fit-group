package com.keepfit.app.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.PositionMetrics;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.util.PixelUtils;
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
import com.keepfit.stepdetection.algorithms.chris.ChrisAlgorithm;
import com.keepfit.stepdetection.algorithms.dino.DinoAlgorithm;
import com.keepfit.stepdetection.algorithms.edward.EdwardAlgorithm;
import com.keepfit.stepdetection.algorithms.kornel.KornelAlgorithm;

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

    private XYPlot plot;
    private XYSeries seriesReal;
    private XYSeries seriesEdward;
    private XYSeries seriesDino;
    private XYSeries seriesKornel;
    private XYSeries seriesChris;
    private Pair<Integer, XYSeries> selection;
    private MyBarFormatter selectionFormatter;
    private MyBarFormatter formatterReal;
    private MyBarFormatter formatterEdward;
    private MyBarFormatter formatterDino;
    private MyBarFormatter formatterKornel;
    private MyBarFormatter formatterChris;
    private SeekBar sbFixedWidth, sbVariableWidth;


    public ResultView(List<DataFile> dataFiles, Context context) {
        super(context);
        this.dataFiles = dataFiles;
        this.context = context;
        inflate(context, R.layout.view_result, this);
        initializePlot();
    }

    public ResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate(context, R.layout.view_result, this);
        initializePlot();
    }

    private void setUpStep() {
        plot.setTicksPerRangeLabel(4);
        plot.setTicksPerDomainLabel(5);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2);
        plot.getLegendWidget().setSize(new SizeMetrics(15, SizeLayoutType.ABSOLUTE, 500, SizeLayoutType.ABSOLUTE));
    }

    private void initializePlot() {
        plot = (XYPlot) findViewById(R.id.data_plot);
        formatterReal = new MyBarFormatter(ContextCompat.getColor(context, R.color.Yellow), Color.LTGRAY);
        formatterEdward = new MyBarFormatter(ContextCompat.getColor(context, R.color.FireBrick), Color.LTGRAY);
        formatterDino = new MyBarFormatter(ContextCompat.getColor(context, R.color.DodgerBlue), Color.LTGRAY);
        formatterKornel = new MyBarFormatter(ContextCompat.getColor(context, R.color.LimeGreen), Color.LTGRAY);
        formatterChris = new MyBarFormatter(ContextCompat.getColor(context, R.color.DarkMagenta), Color.LTGRAY);
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
        sbFixedWidth.setProgress(50);
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
        seriesReal = new SimpleXYSeries(realSteps, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Real Steps");
        seriesEdward = new SimpleXYSeries(edwardSteps, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Edward Steps");
        seriesDino = new SimpleXYSeries(dinoSteps, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Dino Steps");
        seriesKornel = new SimpleXYSeries(kornelSteps, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Kornel Steps");
        seriesChris = new SimpleXYSeries(chrisSteps, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Chris Steps");

        // add a new series' to the xyplot:
        plot.addSeries(seriesReal, formatterReal);
        plot.addSeries(seriesEdward, formatterEdward);
        plot.addSeries(seriesDino, formatterDino);
        plot.addSeries(seriesKornel, formatterKornel);
        plot.addSeries(seriesChris, formatterChris);

        // Setup the BarRenderer with our selected options
        MyBarRenderer renderer = ((MyBarRenderer)plot.getRenderer(MyBarRenderer.class));
        renderer.setBarRenderStyle(BarRenderer.BarRenderStyle.SIDE_BY_SIDE);
        renderer.setBarWidthStyle(BarRenderer.BarWidthStyle.FIXED_WIDTH);
        renderer.setBarWidth((float) (sbFixedWidth.getProgress() * 1.5));
        renderer.setBarGap(2);

        plot.setRangeTopMin(dataFiles.get(0).getNumberOfRealSteps() + 5);

        plot.redraw();
    }

    List<Integer> realSteps;
    List<Integer> edwardSteps;
    List<Integer> dinoSteps;
    List<Integer> kornelSteps;
    List<Integer> chrisSteps;
    private void getDataToPlot() {
        realSteps = new ArrayList<>();
        edwardSteps = new ArrayList<>();
        dinoSteps = new ArrayList<>();
        kornelSteps = new ArrayList<>();
        chrisSteps = new ArrayList<>();
        realSteps.add(0);
        edwardSteps.add(0);
        dinoSteps.add(0);
        kornelSteps.add(0);
        chrisSteps.add(0);
        for (DataFile file : dataFiles) {
            realSteps.add(file.getNumberOfRealSteps());
            for (IAlgorithm algorithm : file.getAlgorithms()) {
                if (algorithm instanceof EdwardAlgorithm) {
                    edwardSteps.add(algorithm.getStepCount());
                    continue;
                }
                if (algorithm instanceof DinoAlgorithm) {
                    dinoSteps.add(algorithm.getStepCount());
                    continue;
                }
                if (algorithm instanceof KornelAlgorithm) {
                    kornelSteps.add(algorithm.getStepCount());
                    continue;
                }
                if (algorithm instanceof ChrisAlgorithm) {
                    chrisSteps.add(algorithm.getStepCount());
                    continue;
                }
            }
        }
        realSteps.add(0);
        edwardSteps.add(0);
        dinoSteps.add(0);
        kornelSteps.add(0);
        chrisSteps.add(0);
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
