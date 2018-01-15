package edu.upc.eseiaat.pma.v8liardice;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView status,txt_num_you,txt_winner,txt_round;
    private Button btnConnect,btnSend,btnFinish,btnCompara, btnPlantarse;
    private Dialog dialog;
    private BluetoothAdapter bluetoothAdapter;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "device_name";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private ChatController chatController;
    private BluetoothDevice connectingDevice;
    private ArrayAdapter<String> discoveredDevicesAdapter;

    String txt_num;
    int     a, b, c, numDisp1, numDisp2,
            num_read,limite = 6,
            num_send,
            num_rondas=1,
            rondas_ganadas_me=0,
            rondas_ganadas_you=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        findViewsByIds();


        //check device support bluetooth or not
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            finish();
        }

        //show bluetooth devices dialog when click connect button
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrinterPickDialog();
            }
        });

        btnFinish.setVisibility (View.INVISIBLE);

    }
    //Handler, para controlar y saber el estado del dispositivo
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChatController.STATE_CONNECTED:
                            setStatus("Connected to: " + connectingDevice.getName());
                            btnConnect.setEnabled(false);
                            btnSend.setVisibility(View.VISIBLE);
                            btnSend.setEnabled(true);


                            break;
                        case ChatController.STATE_CONNECTING:
                            setStatus("Connecting...");
                            btnConnect.setEnabled(false);
                            btnSend.setVisibility(View.INVISIBLE);
                            btnSend.setEnabled(false);
                            btnCompara.setVisibility(View.INVISIBLE);
                            btnCompara.setEnabled(false);
                            btnPlantarse.setVisibility(View.INVISIBLE);
                            btnPlantarse.setEnabled(false);
                            break;
                        case ChatController.STATE_LISTEN:
                        case ChatController.STATE_NONE:
                            setStatus("Not connected");
                            btnConnect.setEnabled(true);
                            btnSend.setVisibility(View.INVISIBLE);
                            btnSend.setEnabled(false);
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
                    if(num_send<7){
                        numDisp1=num_send;
                    }

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    num_read=Integer.parseInt(readMessage);

                    if(num_read<7){
                        btnPlantarse.setEnabled(true);
                        txt_num_you.setText(String.valueOf(num_read));
                        numDisp2=num_read;
                    }
                    else if (num_read==30){
                        btnCompara.setEnabled(true);
                    }
                    else if (num_read==50){
                        btnSend.setEnabled(true);
                    }


                    break;
                case MESSAGE_DEVICE_OBJECT:
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), "Connected to " + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void showPrinterPickDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_bluetooth);
        dialog.setTitle("Bluetooth Devices");

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
        btnSend = (Button) findViewById(R.id.btn_send);
        txt_num_you= (TextView) findViewById(R.id.txt_num_you);
        txt_winner = (TextView) findViewById(R.id.txt_winner);
        btnCompara= (Button) findViewById(R.id.btn_comparar);
        txt_winner.setText("Quien ganara?");
        btnFinish = (Button) findViewById(R.id.btn_finish);
        txt_round = (TextView) findViewById(R.id.txt_round);
        btnPlantarse = (Button) findViewById(R.id.btn_plantarse);
        btnFinish=(Button) findViewById(R.id.btn_finish);
    }
    public void compare (View view){

        Comparador_tirada(numDisp1,numDisp2);

        c=50;
        txt_num = String .valueOf(c);
        sendMessage(txt_num);

        if (num_rondas<3){
            btnSend.setVisibility(View.VISIBLE);
            num_rondas++;
            btnCompara.setVisibility(View.INVISIBLE);
            btnCompara.setEnabled(false);

        }

        else{
            //Toast.makeText(this, "Fin de la partida", Toast.LENGTH_LONG).show();
            btnCompara.setVisibility(View.INVISIBLE);
            btnCompara.setEnabled(false);

            if(rondas_ganadas_me>rondas_ganadas_you)
            {

                txt_winner.setText("");
                txt_num_you.setText("Ganas la partida");
            }
            if (rondas_ganadas_you>rondas_ganadas_me)
            {
                txt_winner.setText("");
                txt_num_you.setText("Pierdes la partida");
            }

            btnFinish.setVisibility(View.VISIBLE);
            btnFinish.setEnabled(true);


        }

    }
    //Metodo para comparar resultados
    private void Comparador_tirada(int a, int b) {
        txt_num_you.setText("");
        if(a<b){
            String gana_b= "Pierdes";
            txt_winner.setText(gana_b);
            rondas_ganadas_you++;
        }
        else if (a>b){
            String gana_a= "Ganas";
            txt_winner.setText(gana_a);
            rondas_ganadas_me++;
        }
        else{
            String empate= "Empate";
            txt_winner.setText(empate);
        }
    }

    public void enviar (View view)
    {
        txt_round.setText("Round "+ num_rondas);
        //Pasa a string el resultado del dado, y el +1 provoca que no salga el 0
        a = (int) (Math.random() * limite) + 1;
        txt_num = String .valueOf(a);
        sendMessage(txt_num);
        btnPlantarse.setVisibility(View.VISIBLE);
        btnPlantarse.setEnabled(true);

    }

    public void finish (View v){

        num_rondas=1;
        startActivity(new Intent(getBaseContext(), InicioActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }

    public void  plantarse (View v){

        b=30;
        txt_num = String .valueOf(b);
        sendMessage(txt_num);
        btnPlantarse.setVisibility(View.INVISIBLE);
        btnSend.setVisibility(View.INVISIBLE);
        btnSend.setEnabled(false);
        btnCompara.setVisibility(View.VISIBLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    chatController = new ChatController(this, handler);
                } else {
                    Toast.makeText(this, "¡Por favor, active el bluetooth para poder jugar!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }



    private void sendMessage(String message) {
        if (chatController.getState() != ChatController.STATE_CONNECTED) {
            Toast.makeText(this, "Connection was lost!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatController.write(send);
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
    public void onDestroy() {
        super.onDestroy();
        if (chatController != null)
            chatController.stop();
    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
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