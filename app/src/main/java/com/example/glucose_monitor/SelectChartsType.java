package com.example.glucose_monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import Classes.ChartsPeriod;

public class SelectChartsType extends AppCompatActivity {
    private Button lastDayBtn;
    private Button lastWeekBtn;
    private Button lastMonthBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_charts_type);
        lastDayBtn = (Button)findViewById(R.id.last_day_btn);
        lastWeekBtn = (Button)findViewById(R.id.last_week_btn);
        lastMonthBtn = (Button)findViewById(R.id.last_month_btn);

        lastDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChartActivity(ChartsPeriod.LAST_DAY);
            }
        });

        lastWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChartActivity(ChartsPeriod.LAST_WEEK);
            }
        });

        lastMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChartActivity(ChartsPeriod.LAST_MONTH);
            }
        });

    }

    private void showChartActivity(ChartsPeriod period) {

    }
}
