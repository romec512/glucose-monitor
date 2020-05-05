package com.example.glucose_monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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


public class ForecasterBroadcastReceiver extends BroadcastReceiver {
    public static int Num = 0;
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

        List<List<NumericPrediction>> forecast = null;
        try {
            forecast = forecaster.forecast(2, System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String lastDate = instances.lastInstance().stringValue(0);
        Date firstPredictedDate = null;
        try {
            firstPredictedDate = new Date(format.parse(lastDate).getTime() + deltaTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date secondPredictedDate = new Date(firstPredictedDate.getTime() + deltaTime);

        String data = format.format(firstPredictedDate) + ',' + forecast.get(0).get(0).predicted();
        data += '\n' + format.format(secondPredictedDate) + ',' + forecast.get(1).get(0).predicted();

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
    }
}
