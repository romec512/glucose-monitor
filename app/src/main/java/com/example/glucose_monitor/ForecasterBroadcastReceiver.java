package com.example.glucose_monitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.timeseries.WekaForecaster;
import weka.core.Instances;

import static android.content.Context.NOTIFICATION_SERVICE;


public class ForecasterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //ToDo: настроить периоды предсказания и кол-во предсказаний
        String pathToData = "/storage/emulated/0/glucose-monitor/test.arff";
        String pathToSavePredicted = "/storage/emulated/0/glucose-monitor/predicted.txt";
        Instances instances = null;
        try {
            instances = new Instances(new BufferedReader(new FileReader(pathToData)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // new forecaster
        WekaForecaster forecaster = new WekaForecaster();

        // set the targets we want to forecast. This method calls
        // setFieldsToLag() on the lag maker object for us
        try {
            forecaster.setFieldsToForecast("glucose");
        } catch (Exception e) {
            e.printStackTrace();
        }

        forecaster.setBaseForecaster(new LinearRegression());
        forecaster.getTSLagMaker().setTimeStampField("date"); // date time stamp
        forecaster.getTSLagMaker().setMinLag(1);
        forecaster.getTSLagMaker().setMaxLag(12);

        // build the model
        try {
            forecaster.buildForecaster(instances, System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // prime the forecaster with enough recent historical data
        // to cover up to the maximum lag. In our case, we could just supply
        // the 12 most recent historical instances, as this covers our maximum
        // lag period
        try {
            forecaster.primeForecaster(instances);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // forecast for 12 units (months) beyond the end of the
        // training data
        long deltaTime = (long)forecaster.getTSLagMaker().getDeltaTime();

        long numbersOfSteps;

        if (deltaTime < 60 * 60 * 1000) {
            numbersOfSteps = (60 * 60 * 1000) / deltaTime;
        } else {
            numbersOfSteps = 1;
        }

        List<List<NumericPrediction>> forecast = null;
        try {
            forecast = forecaster.forecast((int)numbersOfSteps, System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String lastDate = instances.lastInstance().stringValue(0);
        Date firstPredictedDate = null;
        try {
            firstPredictedDate = new Date(format.parse(lastDate).getTime() + numbersOfSteps * deltaTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String data = format.format(firstPredictedDate) + ',' + forecast.get(forecast.size() - 1).get(0).predicted();

        //Save predicted glucose value and time in file
        File outputPreditcted = new File(pathToSavePredicted);
        try {
            if(!outputPreditcted.exists()) {
                outputPreditcted.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(pathToSavePredicted);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OutputStreamWriter writer = new OutputStreamWriter(fOut);
        try {
            writer.write(data, 0, data.length());
            writer.close();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double predicted = forecast.get(forecast.size() - 1).get(0).predicted();
        if (predicted > 8.5) {
            Intent saveValueIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, saveValueIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Внимание!")
                            .setContentText("Ожидается опасно высокое значение гликемии!")
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

            Notification notification = builder.build();

            NotificationManager notificationManager =
                    (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(2, notification);
        } else if (predicted < 3.0) {
            Intent saveValueIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, saveValueIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Внимание!")
                            .setContentText("Ожидается опасно низкое значение гликемии!")
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

            Notification notification = builder.build();

            NotificationManager notificationManager =
                    (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(2, notification);
        }
    }
}
