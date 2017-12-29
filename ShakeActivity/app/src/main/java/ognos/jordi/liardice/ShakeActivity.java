package ognos.jordi.liardice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;



public class ShakeActivity extends AppCompatActivity {

    //Variable aleatoria creada
    Random myRandom;
    //Límite de la variable
    int limite=6;
    TextView textGenerateNumber;
    //Acceleròmetre
    private SensorManager mSensorManager;
    private PowerManager mPowerManager;
    private WindowManager mWindowManager;
    private Display mDisplay;
    //private SimulationView mSimulationView;
    private float mSensorX;
    private float mSensorY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        myRandom= new Random(limite);
        textGenerateNumber = (TextView) findViewById(R.id.txt_number);

        //Sensor moviment
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        // INSERTAR FONDO AQUÍ
        //mSimulationView = new SimulationView(this);
        // mSimulationView.setBackgroundResource(R.drawable.//TODO: dibujos jordi.
        //setContentView(mSimulationView);
    }

   //Boton de tirar el dado
   public void shake(View v)
        {
            Button shake= (Button) v;
            //Pasa a string el resultado del dado, y el +1 provoca que no salga el 0
            textGenerateNumber.setText(String.valueOf(myRandom.nextInt(limite)+1));

        };
}

