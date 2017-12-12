package jabo.com.roommapper2.Screens;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import jabo.com.roommapper2.BT.BTConnectie;
import jabo.com.roommapper2.R;
import jabo.com.roommapper2.TCP.TcpClient;
import jabo.com.roommapper2.extraControls.JoystickView;

public class controlPage extends Activity {

    private static final String TAG = "controlPage";
    private TcpClient mTcpClient;
    private JoystickView joystick;
    private int REQUEST_ENABLE_BT = 1;
    public static BTConnectie BT;
    boolean devicefound = false;
    BluetoothAdapter mBluetoothAdapter;
    static Context context;
    static Toast toast;
    TextView roomname;
    private int RUN = 1;
    private int PAUZE = 0;
    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_page);
        new ConnectTask().execute("");
        roomname = findViewById(R.id.RoomName);
        //toast
        Context context = getApplicationContext();
        this.context =context;
        //BT
        String btdeviceName = "LAPTOP-BOAS";
        //String btdeviceHardwareAddress = "F6:D0:05:15:59:26";
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            //
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        String devices[] = new String[] {};
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            int i =0;
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceName.equalsIgnoreCase(btdeviceName) /*&& deviceHardwareAddress.equalsIgnoreCase(btdeviceHardwareAddress)*/){
                    devicefound = true;
                    try {
                        BT = new BTConnectie(device);
                        BT.start();
                        Log.d("Device","Connected");
                    }
                    catch (Exception e){

                    }
                    break;
                }
                else{
                    i++;
                    devicefound = false;
                    Log.d(deviceName,btdeviceName);
                    popup("device not found please connect device first");
                    //devices[i] = deviceName;
                    //Log.d(deviceHardwareAddress,btdeviceHardwareAddress);
                    Log.d("Device","not found");
                }
            }
        }
        if (devicefound == false){
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
        }
        //Joystick
        joystick = (JoystickView) findViewById(R.id.joystick);

        joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int input_power, int direction) {
                int power= input_power;
                byte[] message = new byte[4];
                message[2] = (byte)power;
                message[3]= (byte)'\n';
                switch (direction) {
                    case JoystickView.FRONT:
                        message[1] = 1;
                        break;
                    case JoystickView.FRONT_RIGHT:
                        message[1] = 2;
                        break;
                    case JoystickView.RIGHT:
                        message[1] = 3;
                        break;
                    case JoystickView.RIGHT_BOTTOM:
                        message[1] = 4;
                        break;
                    case JoystickView.BOTTOM:
                        message[1] = 5;
                        break;
                    case JoystickView.BOTTOM_LEFT:
                        message[1] = 6;
                        break;
                    case JoystickView.LEFT:
                        message[1] = 7;
                        break;
                    case JoystickView.LEFT_FRONT:
                        message[1] = 8;
                        break;
                    default:
                        message[1] = 0;
                        break;
                }
                message[0] = (byte)(message.length+16);
                try {
                    if (BTConnectie.mConnectedThread.isAlive()) {
                        BTConnectie.mConnectedThread.write(message);
                    } else {
                        popup("cant send to device");
                    }
                }
                catch (Exception e){
                    popup("cant send to device");
                }
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }

    public void start(View v){
        if(state == RUN) {
            if (!roomname.getText().toString().equalsIgnoreCase("")) {
                state = PAUZE;
                popup("started");
                if (mTcpClient != null) {
                    mTcpClient.sendMessage("Start<LOG>");
                }
            }
            else{
                popup("Please give a room name");
            }
        }
        else{
            popup("stopped");
            state = RUN;
            if (mTcpClient != null) {
                mTcpClient.sendMessage("Stop<LOG>");
            }
        }
    }
    
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy: destroyed");
        if (mTcpClient != null){
            mTcpClient.stopClient();
        }
        //BT.cancel();

    }
    
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause: paused");
    }
    
    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop: stopped");
    }

    private class ConnectTask extends AsyncTask<String, String, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... message) {
            //we create a TCPClient object and
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    //publishProgress(message);
                    Log.i("Debug","Input message: " + message);
                }
            });
            mTcpClient.run();

            return null;
        }

        protected void onProgressUpdate(String... message){

        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    public static void popup(String message){
        int duration = Toast.LENGTH_SHORT;
        if(toast != null) {
            toast.cancel();
            toast = Toast.makeText(context,message,duration);
            toast.show();
        }
        else {
            toast = Toast.makeText(context,message,duration);
            toast.show();
        }
    }
}
