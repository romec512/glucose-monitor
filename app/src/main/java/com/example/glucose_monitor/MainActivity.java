package com.example.glucose_monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button saveValueBtn;
    private Button graphicsBtn;
    private Button notificationsBtn;
    private TextView forecastedNumTV;
    private TextView forecastedTimeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveValueBtn = (Button)findViewById(R.id.save_value_btn);
        graphicsBtn = (Button)findViewById(R.id.graphics_btn);
        notificationsBtn = (Button)findViewById(R.id.notifications_btn);
        forecastedNumTV = (TextView)findViewById(R.id.forecasted_num_tv);
        forecastedTimeTV = (TextView)findViewById(R.id.forecasted_time_tv);
    }
}
