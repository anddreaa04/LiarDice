package ognos.jordi.liardice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class ShakeActivity extends AppCompatActivity {


    // Creamos el objeto para acceder al servicio de sensores
    SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    Sensor mSensorAcc;


    //Variable aleatoria creada
    Random myRandom;
    //Límite de la variable
    int limite=6;
    TextView textGenerateNumber;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            }else {
                textGenerateNumber.setText(String.valueOf(myRandom.nextInt(limite)+1));
            }
            /*else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                mGyrox.setText(R.string.act_main_no_acuracy);
                mGyroy.setText(R.string.act_main_no_acuracy);
                mGyroz.setText(R.string.act_main_no_acuracy);
            }*/
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectShake(event);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        myRandom= new Random(limite);
        textGenerateNumber = (TextView) findViewById(R.id.txt_number);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        detectShake();

    }

   //Boton de tirar el dado
   /* public void shake(View v)
        {
            Button shake= (Button) v;
            //Pasa a string el resultado del dado, y el +1 provoca que no salga el 0
            textGenerateNumber.setText(String.valueOf(myRandom.nextInt(limite)+1));

        };*/



    private void detectShake(SensorEvent event) {
        long now = System.currentTimeMillis();

        if ((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
            mShakeTime = now;

            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement
            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            // Change background color if gForce exceeds threshold;
            // otherwise, reset the color
            if (gForce > SHAKE_THRESHOLD) {
                soundAcc.start();
            }
        }
    }

}

