package ognos.jordi.comparador;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Comparador extends AppCompatActivity {


    //Límite de la variable
    int limite=6;

    TextView textGenerateNumber_a;
    TextView textGenerateNumber_b;
    TextView text_ganador;

    //Valores del resultado de la tirada
    int a;
    int b;
    Timer timer = new Timer();

    Vibrator v;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparador);

        textGenerateNumber_a = (TextView) findViewById(R.id.txt_a);
        textGenerateNumber_b = (TextView) findViewById(R.id.txt_b);
        text_ganador = (TextView) findViewById(R.id.txt_ganador);
        Button btn_compara=(Button) findViewById(R.id.btn_comparar);
        btn_compara.setVisibility(View.VISIBLE);
        v = (Vibrator) getSystemService(VIBRATOR_SERVICE);




    }

    //Metodo para comparar resultados
    private void Comparador_tirada(int a, int b) {


        if(a<b){
            String gana_b= "Gana el jugador B";
            text_ganador.setText(gana_b);
            //Toast.makeText(this, "Gana el jugador B", Toast.LENGTH_SHORT).show();
        }
        else if (a>b){
            String gana_a= "Gana el jugador A";
            text_ganador.setText(gana_a);
            //Toast.makeText(this, "Gana el jugador A", Toast.LENGTH_SHORT).show();
        }
        else{
            String empate= "Empate";
            text_ganador.setText(empate);
            //Toast.makeText(this, "Empate", Toast.LENGTH_SHORT).show();
        }
    }

    public void num_a (View view){

        a = (int) (Math.random() * limite) + 1;
        textGenerateNumber_a.setText(String.valueOf(a));
        Button btn_a=(Button) findViewById(R.id.btn_a);
        btn_a.setVisibility(View.INVISIBLE);


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                contador_vibraciones(a);
            }
        },0,2000);


    }

    private void contador_vibraciones(int n)
    {
        //instrucciones de la repetición

        //int Patron [] = new int[] {0, 150, 50, 150, 50, 150, 50, 150, 50, 150, 50, 150, 50};
        //long [] pattern ={[Patron][(6-n)]}

       if(n==1) {
           long [] pattern ={0,150,150};
           v.vibrate(pattern, -1);

       }
        if(n==2) {

            long[] pattern = {0, 150, 150, 150,150};
            v.vibrate(pattern, -1);

        }
        if(n==3) {

            long[] pattern = {0, 150, 150,150, 150, 150,150 };
            v.vibrate(pattern, -1);

        }
        if(n==4) {

            long[] pattern = {0, 150, 150, 150, 150, 150, 150, 150, 150};
            v.vibrate(pattern, -1);

        }
        if(n==5) {

            long[] pattern = {0, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150};
            v.vibrate(pattern, -1);

        }
        if(n==6) {

            long[] pattern = {0, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150};
            v.vibrate(pattern, -1);

        }
    }



    public void num_b (View view){

        b = (int) (Math.random() * limite) + 1;
        textGenerateNumber_b.setText(String.valueOf(b));
        Button btn_b=(Button) findViewById(R.id.btn_b);
        btn_b.setVisibility(View.INVISIBLE);

    }

    public void compare (View view){


        Comparador_tirada(a,b);
        Button btn_a=(Button) findViewById(R.id.btn_a);
        btn_a.setVisibility(View.VISIBLE);
        Button btn_b=(Button) findViewById(R.id.btn_b);
        btn_b.setVisibility(View.VISIBLE);
    }
}
