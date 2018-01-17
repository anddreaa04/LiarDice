package edu.upc.eseiaat.pma.V10LIARDICE;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ShakeToGenerateNumber.ShakeListener {

    private TextView status,txt_num_you,txt_winner,txt_round;
    private Button btnConnect,btnFinish,btnCompara, btnPlantarse;
    private Dialog dialog;
    private BluetoothAdapter bluetoothAdapter;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "Device name";


    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private ChatController chatController;
    private BluetoothDevice connectingDevice;
    private ArrayAdapter<String> discoveredDevicesAdapter;

    private ShakeToGenerateNumber shakeToGenerateNumber;
    private Configuration config = new Configuration();

    TextView shake_phone;

    String txt_num;
    int     a, b, c, d=0, e=0, f=0,g, numDisp1, numDisp2,
            num_read,limite = 6,time_limit=60000,
            num_send,
            num_rondas=1,
            rondas_ganadas_me=0,
            rondas_ganadas_you=0;
    Vibrator v;
    long[] pattern;
    CountDownTimer cuenta_atras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        findViewsByIds();

       //check device support bluetooth or not
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, getResources().getString(R.string.bt_not_available), Toast.LENGTH_SHORT).show();
            finish();
        }
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //show bluetooth devices dialog when click connect button
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrinterPickDialog();
            }
        });

        btnFinish.setVisibility (View.INVISIBLE);
        shake_phone.setVisibility(View.INVISIBLE);

        shakeToGenerateNumber = new ShakeToGenerateNumber();
        shakeToGenerateNumber.setListener(this);
    }
    //Handler, para controlar y saber el estado del dispositivo
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChatController.STATE_CONNECTED:

                            setStatus(getResources().getString(R.string.connected_to) + connectingDevice.getName());
                            btnConnect.setEnabled(false);
                            shakeToGenerateNumber.init(getApplicationContext());
                            onShake();
                            shake_phone.setVisibility(View.VISIBLE);

                            break;
                        case ChatController.STATE_CONNECTING:
                            setStatus(getResources().getString(R.string.connecting));
                            btnConnect.setEnabled(false);

                            btnCompara.setVisibility(View.INVISIBLE);
                            btnCompara.setEnabled(false);
                            btnPlantarse.setVisibility(View.INVISIBLE);
                            btnPlantarse.setEnabled(false);
                            break;
                        case ChatController.STATE_LISTEN:
                        case ChatController.STATE_NONE:
                            setStatus(getResources().getString(R.string.not_connected));
                            btnConnect.setEnabled(true);

                            btnCompara.setVisibility(View.INVISIBLE);
                            btnCompara.setEnabled(false);
                            btnPlantarse.setVisibility(View.INVISIBLE);
                            btnPlantarse.setEnabled(false);

                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    num_send=Integer.parseInt(writeMessage);
                    if(num_send<(limite+1)){
                        numDisp1=num_send;
                    }

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    num_read=Integer.parseInt(readMessage);

                    if(num_read<(limite+1)){
                        btnPlantarse.setEnabled(true);
                        txt_num_you.setText(""/*String.valueOf(num_read)*/);
                        numDisp2=num_read;
                        if (f==0){
                            cuenta_atras = new CountDownTimer(time_limit, 3000) {

                                public void onTick(long millisUntilFinished) {
                                    v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                    contador_vibraciones(numDisp2);
                                }

                                public void onFinish() {
                                }
                            }.start();

                            f=1;
                        }
                        else{
                            cuenta_atras.cancel();
                            cuenta_atras = new CountDownTimer(time_limit, 3000) {

                                public void onTick(long millisUntilFinished) {
                                    v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                    contador_vibraciones(numDisp2);
                                }

                                public void onFinish() {
                                }
                            }.start();
                        }


                    }
                    else if (num_read==30){
                        btnCompara.setEnabled(true);
                    }
                    else if (num_read==50){

                        if(btnCompara.isEnabled())
                        {
                         e=1;
                        }
                        else{
                            shakeToGenerateNumber.register();
                            shake_phone.setVisibility(View.VISIBLE);
                        }

                    }
                    else if (num_read == 60)
                    {
                        shakeToGenerateNumber.deregister();
                        shake_phone.setVisibility(View.INVISIBLE);
                    }
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.connected_to) + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    if (msg.getData().getString("toast")== "Connection was lost"){
                        finish();
                    }
                    break;
            }
            return false;
        }
    });

    private void contador_vibraciones(int n)
    {
        //instrucciones de la repetición

        if(n==1) {
            pattern = new long[]{0, 100, 150};
            //v.vibrate(pattern, -1);

        }
        if(n==2) {

            pattern = new long[]{0, 100, 150, 100,150};
            //v.vibrate(pattern, -1);

        }
        if(n==3) {

            pattern = new long[]{0, 100, 150,100, 150, 100,150 };
            //v.vibrate(pattern, -1);

        }
        if(n==4) {

            pattern = new long[]{0, 100, 150, 100, 150, 100, 150, 100, 150};
            //v.vibrate(pattern, -1);

        }
        if(n==5) {

            pattern = new long[]{0, 100, 150, 100, 150, 100, 150, 100, 150, 100, 150};
            //v.vibrate(pattern, -1);

        }
        if(n==6) {

            pattern = new long[]{0, 100, 150, 100, 150, 100, 150, 100, 150, 100, 150, 100, 150};
            //v.vibrate(pattern, -1);

        }
        v.vibrate(pattern, -1);

        return;
    }

    private void showPrinterPickDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_bluetooth);
        dialog.setTitle(getResources().getString(R.string.bt_devices));

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        //Initializing bluetooth adapters
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        //locate listviews and attatch the adapters
        ListView listView = dialog.findViewById(R.id.pairedDeviceList);
        ListView listView2 = dialog.findViewById(R.id.discoveredDeviceList);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevicesAdapter.add(getString(R.string.none_paired));
        }

        //Handling listview item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }

        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void setStatus(String s) {
        status.setText(s);
    }

    private void connectToDevice(String deviceAddress) {
        bluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        chatController.connect(device);
    }

    private void findViewsByIds() {
        status = (TextView) findViewById(R.id.status);
        btnConnect = (Button) findViewById(R.id.btn_connect);
        txt_num_you= (TextView) findViewById(R.id.txt_num_you);
        txt_winner = (TextView) findViewById(R.id.txt_winner);
        btnCompara= (Button) findViewById(R.id.btn_comparar);
        txt_winner.setText(getResources().getString(R.string.question));
        btnFinish = (Button) findViewById(R.id.btn_finish);
        txt_round = (TextView) findViewById(R.id.txt_round);
        btnPlantarse = (Button) findViewById(R.id.btn_plantarse);
        btnFinish=(Button) findViewById(R.id.btn_finish);
        shake_phone =(TextView) findViewById(R.id.txt_shake);
    }

    @Override
    public void onShake() {

        txt_winner.setText("");
        if (d==0)
        {
            d++;
        }
        else {
            Toast.makeText(this, R.string.aviso_tirada, Toast.LENGTH_SHORT).show();
            txt_round.setText(getResources().getString(R.string.round) + num_rondas);
            //Pasa a string el resultado del dado, y el +1 provoca que no salga el 0
            a = (int) (Math.random() * limite) + 1;
            txt_num = String.valueOf(a);
            sendMessage(txt_num);
            btnPlantarse.setVisibility(View.VISIBLE);
            btnPlantarse.setEnabled(true);
        }
    }

    private void sendMessage(String message) {
        if (chatController.getState() != ChatController.STATE_CONNECTED) {
            Toast.makeText(this, R.string.connection_lost, Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatController.write(send);
        }
    }
    public void compare (View view){

        f=0;
        cuenta_atras.cancel();
        Comparador_tirada(numDisp1,numDisp2);

        if (num_rondas<3){
            c=50;
            txt_num = String .valueOf(c);
            sendMessage(txt_num);

            num_rondas++;
            btnCompara.setVisibility(View.INVISIBLE);
            btnCompara.setEnabled(false);
            if(e==1){
                shake_phone.setVisibility(View.VISIBLE);
                shakeToGenerateNumber.register();
                e=0;
            }
        }

        else {
            g=60;
            txt_num = String .valueOf(g);
            sendMessage(txt_num);

            btnCompara.setVisibility(View.INVISIBLE);
            btnCompara.setEnabled(false);
            shakeToGenerateNumber.deregister();

            btnFinish.setVisibility(View.VISIBLE);
            btnFinish.setEnabled(true);
            shake_phone.setVisibility(View.GONE);
            if(rondas_ganadas_me>rondas_ganadas_you)
            {
                txt_winner.setText("");
                txt_num_you.setText(getResources().getString(R.string.winthegame));

            }
            if (rondas_ganadas_you>rondas_ganadas_me)
            {
                txt_winner.setText("");
                txt_num_you.setText(getResources().getString(R.string.losethegame));

            }
            if(rondas_ganadas_me==rondas_ganadas_you) {
                txt_winner.setText("");
                txt_num_you.setText(getResources().getString(R.string.tie));
            }
        }

    }
    //Metodo para comparar resultados
    private void Comparador_tirada(int a, int b) {
        txt_num_you.setText("");
        if(a<b){
            String gana_b= getResources().getString(R.string.lose);
            txt_winner.setText(gana_b);
            rondas_ganadas_you++;
        }
        else if (a>b){
            String gana_a= getResources().getString(R.string.win);
            txt_winner.setText(gana_a);
            rondas_ganadas_me++;
        }
        else{
            String empate= getResources().getString(R.string.tie);
            txt_winner.setText(empate);
        }
    }

    public void  plantarse (View v){

        b=30;
        txt_num = String .valueOf(b);
        sendMessage(txt_num);
        btnPlantarse.setVisibility(View.INVISIBLE);
        btnCompara.setVisibility(View.VISIBLE);
        shake_phone.setVisibility(View.INVISIBLE);
        shakeToGenerateNumber.deregister();
    }
    public void finish (View v){

        num_rondas=1;
        finish();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {

                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                    startActivity(discoverableIntent);

                    chatController = new ChatController(this, handler);

                } else {
                    Toast.makeText(this, R.string.msg_bt, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            chatController = new ChatController(this, handler);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (chatController != null) {
            if (chatController.getState() == ChatController.STATE_NONE) {
                chatController.start();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        v.cancel();
        if (chatController != null)
            chatController.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        v.cancel();
        if (chatController != null)
            chatController.stop();
    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.question_exit);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        builder.setNegativeButton(R.string.cancel, null);
        Dialog dialog_exit = builder.create();
        dialog_exit.show();
    }

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add(getString(R.string.none_found));
                }
            }
        }
    };


}