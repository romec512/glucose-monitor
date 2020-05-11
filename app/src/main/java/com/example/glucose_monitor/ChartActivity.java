package com.example.glucose_monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import weka.core.Instance;
import weka.core.Instances;

public class ChartActivity extends AppCompatActivity {
    private int numOfDays;
    public GraphView graphView;
    public TextView chartsPeriodTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        chartsPeriodTV = (TextView) findViewById(R.id.charts_period_tv);

        graphView = (GraphView) findViewById(R.id.graph);

        numOfDays = getIntent().getIntExtra("numOfDays", 1);

        String chartsPeriod = "";
        if (numOfDays > 1) {
            chartsPeriod = "за последние " + numOfDays + " дней";
        } else {
            chartsPeriod = "за последние сутки";
        }

        chartsPeriodTV.setText(chartsPeriod);

        String pathToData = "/storage/emulated/0/glucose-monitor/test.arff";
        Instances instances = null;
        try {
            instances = new Instances(new BufferedReader(new FileReader(pathToData)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        instances.sort(0);
        int size = instances.size();
        Instance lastInstance = instances.lastInstance();
        Date toDate = new Date((long)lastInstance.value(0));

        // fromdate = todate - numdays * 24 hours * 60 minutes * 60 seconds * 1000 msecodns
        long time = toDate.getTime();
        long daysTime = numOfDays * 24 * 60 * 60;
        daysTime *= 1000;
        long timestamp = time - daysTime;

        Date fromDate = new Date(timestamp);
        ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
        //from last instance to first instance
        for (int i = size - 1; i > 0; i--) {
            Instance instance = instances.get(i);
            Date timeOfMeasuring = new Date((long)instance.value(0));

            if (timeOfMeasuring.after(fromDate) && timeOfMeasuring.before(toDate)) {
                double value = instance.value(1) / 18;
                dataPoints.add(new DataPoint(timeOfMeasuring, value));
            }
        }
        Collections.reverse(dataPoints);
        DataPoint[] data = new DataPoint[]{};
        data = dataPoints.toArray(data);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(data);
        graphView.addSeries(series);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScrollableY(true);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(30);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM HH:mm");
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, format));
    }
}
