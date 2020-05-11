package com.example.glucose_monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button saveValueBtn;
    private Button graphicsBtn;
    private ToggleButton notificationsBtn;
    private TextView forecastedNumTV;
    private TextView forecastedTimeTV;
    private Button instructionBtn;
    public static final String APP_PREFERENCES = "app_settings";
    public static final String APP_PREFERENCES_NOTIFICATIONS = "notifications_enabled";
    private SharedPreferences mSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveValueBtn = (Button)findViewById(R.id.save_value_btn);
        graphicsBtn = (Button)findViewById(R.id.graphics_btn);
        notificationsBtn = (ToggleButton) findViewById(R.id.notifications_btn);
        forecastedNumTV = (TextView)findViewById(R.id.forecasted_num_tv);
        forecastedTimeTV = (TextView)findViewById(R.id.forecasted_time_tv);
        instructionBtn = (Button)findViewById(R.id.instruction_btn);

        instructionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InstructionActivity.class));
            }
        });

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (!mSettings.contains(APP_PREFERENCES_NOTIFICATIONS)) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(APP_PREFERENCES_NOTIFICATIONS, true);
            editor.apply();
        }

        if (mSettings.getBoolean(APP_PREFERENCES_NOTIFICATIONS, true)) {
            notificationsBtn.setChecked(true);
            notificationsBtn.setBackgroundResource(R.drawable.bell_on);
        } else {
            notificationsBtn.setChecked(false);
            notificationsBtn.setBackgroundResource(R.drawable.bell_off);
        }

        setPrediction();

        saveValueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inputGlucoseActivity = new Intent(MainActivity.this, InputGlucoseActivity.class);
                startActivity(inputGlucoseActivity);
            }
        });

        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mSettings.edit();
                // If notifications disabled now, then notifications should be enabled and alarm should be started after click btn
                if (!mSettings.getBoolean(APP_PREFERENCES_NOTIFICATIONS, false)) {
                    editor.putBoolean(APP_PREFERENCES_NOTIFICATIONS, true);
                    setNotifications();
                    notificationsBtn.setBackgroundResource(R.drawable.bell_on);
                } else {
                    editor.putBoolean(APP_PREFERENCES_NOTIFICATIONS, false);
                    stopNotificationsAlarm();
                    notificationsBtn.setBackgroundResource(R.drawable.bell_off);
                }
                editor.apply();
            }
        });

        graphicsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectOfChartsType = new Intent(MainActivity.this, SelectChartsType.class);
                startActivity(selectOfChartsType);
            }
        });

        if (!hasAlarmAlreadyStarted()) {
            setAlarm();
        }

        if (!hasNotificationsAlarmStarted() && mSettings.getBoolean(APP_PREFERENCES_NOTIFICATIONS, true)) {
            setNotifications();
        }


    }

    protected void setPrediction() {
        FileInputStream fin = null;
        byte[] bytes = null;
        try {
            fin = new FileInputStream(new File("/storage/emulated/0/glucose-monitor/predicted.txt"));
            bytes = new byte[fin.available()];
            fin.read(bytes);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bytes != null) {
            String fileContent = new String(bytes);
            String[] predictions = fileContent.split("\n");
            String[] prediction = predictions[0].split(",");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date predictedDate = null;
            try {
                predictedDate = format.parse(prediction[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Float predicted = Float.parseFloat(prediction[1]) / 18;
            if (predicted > 8.5 || predicted < 3.0) {
                forecastedNumTV.setTextColor(getResources().getColor(R.color.colorDanger));
            }
            forecastedNumTV.setText(String.format("%.2f", predicted));
            forecastedTimeTV.setText("Ожидается в " + timeFormat.format(predictedDate));
        }
    }

    protected void setAlarm() {
        Context context = getBaseContext();
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ForecasterBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 1000, 10 * 60 * 1000, alarmIntent);
    }

    protected void setNotifications() {
        Context context = getBaseContext();
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationsBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 1000, 1 * 60 * 1000, alarmIntent);
    }

    protected boolean hasAlarmAlreadyStarted() {
        Context context = getBaseContext();
        return PendingIntent.getBroadcast(context, 0, new Intent(context, ForecasterBroadcastReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null;
    }

    protected boolean hasNotificationsAlarmStarted() {
        Context context = getBaseContext();
        return PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationsBroadcastReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null;
    }

    protected  void stopNotificationsAlarm() {
        Context context = getBaseContext();
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationsBroadcastReceiver.class),
                PendingIntent.FLAG_NO_CREATE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
