package com.example.glucose_monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
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

        saveValueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inputGlucoseActivity = new Intent(MainActivity.this, InputGlucoseActivity.class);
                startActivity(inputGlucoseActivity);
                finish();
            }
        });

        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getBaseContext();
                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, ForecasterBroadcastReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 1000, 10 * 60 * 1000, alarmIntent);
            }
        });

    }
}
