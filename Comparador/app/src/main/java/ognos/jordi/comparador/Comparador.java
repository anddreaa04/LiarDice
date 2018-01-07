package ognos.jordi.comparador;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class Comparador extends AppCompatActivity {
    //Variable aleatoria creada
    Random myRandom;

    //Límite de la variable
    int limite=6;

    TextView textGenerateNumber_a;
    TextView textGenerateNumber_b;
    TextView text_ganador;

    //Valores del resultado de la tirada
    int a;
    int b;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparador);
        myRandom= new Random(limite);

        textGenerateNumber_a = (TextView) findViewById(R.id.txt_a);
        textGenerateNumber_b = (TextView) findViewById(R.id.txt_b);
        text_ganador = (TextView) findViewById(R.id.txt_ganador);
        Button btn_compara=(Button) findViewById(R.id.btn_comparar);
        btn_compara.setVisibility(View.VISIBLE);



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

        //a =myRandom.nextInt(limite)+1;
        int n=6;
        a = (int) (Math.random() * n) + 1;
        textGenerateNumber_a.setText(String.valueOf(a));
        Button btn_a=(Button) findViewById(R.id.btn_a);
        btn_a.setVisibility(View.INVISIBLE);

        contador_vibraciones(a);

    }

    private void contador_vibraciones(int n) {
        //instrucciones de la repetición
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        while (n > 1) {

            long[] pattern = {0, 500, 200};
            v.vibrate(pattern, -1);
            //v.vibrate(500);
            n--;
        }




    }

    public void num_b (View view){

       // b =myRandom.nextInt(limite)+1;
        int n=6;
        b = (int) (Math.random() * n) + 1;
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
