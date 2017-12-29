package edu.upc.eseiaat.pma.shake_v4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class BucleRondas extends AppCompatActivity {


    //Variable aleatoria creada
    Random myRandom;
    //Límite de la variable
    int limite=6;
    int tiradas_ronda=2;
    int num_rondas=0;
    TextView textGenerateNumber;
    int aux=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucle_rondas);
        myRandom= new Random(limite);
        textGenerateNumber = (TextView) findViewById(R.id.txt_number);
        Button btn_next=(Button) findViewById(R.id.btn_next);
        btn_next.setVisibility(View.INVISIBLE);
        Button btn_finish=(Button) findViewById(R.id.btn_finish);
        btn_finish.setVisibility (View.INVISIBLE);

    }
    //Boton de tirar el dado
    public void shake(View v) {
           if (aux< tiradas_ronda){
               Button shake= (Button) v;
               //Pasa a string el resultado del dado, y el +1 provoca que no salga el 0
               aux++;
               textGenerateNumber.setText(String.valueOf(myRandom.nextInt(limite)+1));
           }

           else{
               textGenerateNumber.setText(String.valueOf(myRandom.nextInt(limite)+1));
               Button btn_tirar= (Button) findViewById(R.id.btn_shake);
               btn_tirar.setVisibility(View.INVISIBLE);
               Button btn_next=(Button) findViewById(R.id.btn_next);
               btn_next.setVisibility(View.VISIBLE);
               Toast.makeText(this, "No tienes más tiradas en esta ronda", Toast.LENGTH_SHORT).show();
               if(num_rondas==2){
                   btn_next.setVisibility(View.INVISIBLE);
                   Button btn_finish=(Button) findViewById(R.id.btn_finish);
                   btn_finish.setVisibility (View.VISIBLE);
                   textGenerateNumber.setVisibility(View.INVISIBLE);

               }

           }
    }

    //Botón de siguiente ronda
    public void next (View v){
        if (num_rondas<2){
            Button next=(Button) v;
            Button btn_tirar= (Button) findViewById(R.id.btn_shake);
            btn_tirar.setVisibility(View.VISIBLE);
            num_rondas++;
            aux=0;
            Button btn_next=(Button) findViewById(R.id.btn_next);
            btn_next.setVisibility(View.INVISIBLE);
        }

        else{
            //Toast.makeText(this, "Fin de la partida", Toast.LENGTH_LONG).show();
            Button btn_finish=(Button) findViewById(R.id.btn_finish);
            btn_finish.setVisibility (View.VISIBLE);

        }
    }
    public void finish (View v){
        Button finish=(Button) v;
        num_rondas=0;
        finish();
    }

}
