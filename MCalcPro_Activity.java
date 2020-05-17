package com.example.vivaanxr.mcalcpro;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivaanxr.mcalcpro.R;

import java.util.Locale;
import ca.roumani.i2c.MPro;

public class MCalcPro_Activity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mcalcpro_layout);
        this.tts = new TextToSpeech(this, this);
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private TextToSpeech tts;

    public void onInit(int initStatus)
    {
        this.tts.setLanguage(Locale.US);
    }

    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
    }

    public void onButtonClick(View v)
    {

        try {
            MPro mp = new MPro();
            EditText pBox = (EditText) findViewById(R.id.pBox);
            String price = pBox.getText().toString();
            EditText aBox = (EditText) findViewById(R.id.aBox);
            String amortization = aBox.getText().toString();
            EditText iBox = (EditText) findViewById(R.id.iBox);
            String interest = iBox.getText().toString();
            mp.setPrinciple(price);
            mp.setAmortization(amortization);
            mp.setInterest(interest);
            String s = "Monthly Payment = " + mp.computePayment("$%,.2f");
            s += "\n\n";
            s += "By making this payment monthly for " + amortization + " years, the mortgage will be paid in full. But if you terminate the mortgage on its nth anniversary, the balance still owing depends on n as shown below:" + "\n\n\n";
            s += String.format("%8s", "n") + String.format("%16s", "Balance") + "\n\n";

            int i = 1;
            for (i = 0; i <= 5; i++) {
                s += String.format("%8d", i) + mp.outstandingAfter(i, "%,16.0f");
                s += "\n\n";
            }

            for (i = 10; i <= Integer.parseInt(amortization); i = i + 5) {
                s += String.format("%8d", i) + mp.outstandingAfter(i, "%,16.0f");
                s += "\n\n";
            }
            ((TextView) findViewById(R.id.output)).setText(s);
            s = "Monthly Payment = " + mp.computePayment("$%,.2f");
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }

        catch (Exception e) {
            Toast label = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            label.show();
        }
    }

    public void onSensorChanged(SensorEvent event)
    {
        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double a = Math.sqrt(ax*ax + ay*ay + az*az);
        if (a > 20)
        {
            ((EditText) findViewById(R.id.pBox)).setText("");
            ((EditText) findViewById(R.id.aBox)).setText("");
            ((EditText) findViewById(R.id.iBox)).setText("");
            ((TextView) findViewById(R.id.output)).setText("");
        }
    }
}