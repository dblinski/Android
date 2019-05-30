package com.example.temperatureconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private RadioButton rbFahrenheit, rbCelsius;
    private EditText inputF, inputC;
    private TextView history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rbFahrenheit = (RadioButton) findViewById(R.id.radioButtonFtoC);
        rbCelsius = (RadioButton) findViewById(R.id.radioButtonCtoF);
        inputF = (EditText) findViewById(R.id.inputDegreesF);
        inputC = (EditText) findViewById(R.id.inputDegreesC);
        history = (TextView) findViewById(R.id.conversionHistory);

    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putString("HISTORY", history.getText().toString());
        outState.putString("FAHRENHEIT", inputF.getText().toString());
        outState.putString("CELSIUS", inputC.getText().toString());

        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        history.setText(savedInstanceState.getString("HISTORY"));
        inputF.setText(savedInstanceState.getString("FAHRENHEIT"));
        inputC.setText(savedInstanceState.getString("CELSIUS"));
    }
    public void convert(View v){
        DecimalFormat oneDecimal = new DecimalFormat("#.#");
        String temp;
        String stringF = inputF.getText().toString();
        String stringC = inputC.getText().toString();
        if(rbFahrenheit.isChecked() && stringF.trim().length() > 0){
            double doubleF = Double.parseDouble(stringF);
            temp = oneDecimal.format(toCelsius(doubleF));
            inputC.setText(temp);
            history.setText(history.getText().toString() + doubleF + "F --> " + temp + "C\n");
        }
        else if(rbCelsius.isChecked() && stringC.trim().length() > 0){
            double doubleC = Double.parseDouble(stringC);
            temp = oneDecimal.format(toFahrenheit(doubleC));
            inputF.setText(temp);
            history.setText(history.getText().toString() + doubleC + "C --> " + temp + "F\n");
        }
        else{
            //do something or do nothing I guess
        }
    }
    public void clearHistory(View v){
        history.setText("");
    }
    public double toCelsius(double fahrenheit){
        return (fahrenheit - 32.0) / 1.8;
    }
    public double toFahrenheit(double celsius){
        return (celsius * 1.8) + 32;
    }
}
