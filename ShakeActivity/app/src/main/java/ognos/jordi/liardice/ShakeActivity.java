package ognos.jordi.liardice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class ShakeActivity extends AppCompatActivity {

    //Variable aleatoria creada
    Random myRandom;
    //Límite de la variable
    int limite=6;
    TextView textGenerateNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);

        myRandom= new Random(limite);
        textGenerateNumber = (TextView) findViewById(R.id.txt_number);

    }

   //Boton de tirar el dado
    public void shake(View v)
        {
            Button shake= (Button) v;
            //Pasa a string el resultado del dado, y el +1 provoca que no salga el 0
            textGenerateNumber.setText(String.valueOf(myRandom.nextInt(limite)+1));

        };

}

