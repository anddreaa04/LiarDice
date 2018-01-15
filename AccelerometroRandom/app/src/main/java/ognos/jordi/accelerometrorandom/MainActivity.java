package ognos.jordi.accelerometrorandom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ShakeToGenerateNumber.ShakeListener {

    TextView txt_num;
    int a, num_tiradas=3;
    private ShakeToGenerateNumber shakeToGenerateNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_num = (TextView) findViewById(R.id.txt_number);
        shakeToGenerateNumber = new ShakeToGenerateNumber();
        shakeToGenerateNumber.setListener(this);
        shakeToGenerateNumber.init(this);
    }

    @Override
    public void onShake() {
        int n=12;
        a = (int) (Math.random() * n) + 1;

        if(num_tiradas>0) {

            txt_num.setText(String.valueOf(a));
            Log.e("Andrea", "Has vuelto a tirar");
            num_tiradas--;
        }
        else {
            txt_num.setText("No puedes tirar mas");
            shakeToGenerateNumber.deregister();
        }
    }

   @Override
    protected void onResume() {
        super.onResume();
        shakeToGenerateNumber.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shakeToGenerateNumber.deregister();
    }
}



