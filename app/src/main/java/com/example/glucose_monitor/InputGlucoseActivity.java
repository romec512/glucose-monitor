package com.example.glucose_monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import Classes.ArffHelper;

public class InputGlucoseActivity extends AppCompatActivity {
    private EditText glucoseValueET;
    private Button saveGlucose;
    private String filepath = "/storage/emulated/0/glucose-monitor/arff.arff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_glucose);
        glucoseValueET = (EditText)findViewById(R.id.glucose_value_et);
        saveGlucose = (Button)findViewById(R.id.save_glucose_value_btn);
        saveGlucose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputValue = glucoseValueET.getText().toString();
                float value = Float.parseFloat(inputValue) * 18; //См. формулу для перевода из ммоль/л в мг/дл для глюкозы
                if (value < 0 || value == 0) {
                    Toast.makeText(InputGlucoseActivity.this, "Введите корректное значение", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String data = format.format(new Date()) + ',' + value + "\n";
                ArffHelper.Append(filepath, data);
                Toast.makeText(InputGlucoseActivity.this, "Значение успешно сохранено", Toast.LENGTH_LONG).show();
                Intent mainActivity = new Intent(InputGlucoseActivity.this, MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });
    }
}
