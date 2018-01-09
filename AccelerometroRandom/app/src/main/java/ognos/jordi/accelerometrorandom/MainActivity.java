package ognos.jordi.accelerometrorandom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ShakeToGenerateNumber.ShakeListener {

    TextView txt_num;
    int a;
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
        int n=6;
        a = (int) (Math.random() * n) + 1;
        txt_num.setText(String.valueOf(a));
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



