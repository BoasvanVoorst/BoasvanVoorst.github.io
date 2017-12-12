package jabo.com.roommapper2.BT;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jabo.com.roommapper2.TCP.TcpClient;

/**
 * Created by Server on 31-10-2017.
 */

public class BTConnected extends Thread{
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler mHandler;
    private boolean state;
    private TcpClient mTcpClient;

    public void start(){
        super.start();
        run();
    }

    public BTConnected(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int begin = 0;
        int bytes = 0;
        while (true) {
            try {
                bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                //Log.d("",buffer.toString());
                if(buffer[0] == 0x33) {
                    int X = buffer[2]|(buffer[1]<<8);
                    int Y = buffer[4]|(buffer[3]<<8);//3300010001 =x,1 en y,1
                    Log.d("x,y",X+","+Y);
                    if (mTcpClient != null) {
                        mTcpClient.sendMessage(X+","+Y+"<DP>");
                    }
                }
                else{

                }

                for(int i = 0;i < buffer.length; i++){
                    buffer[i] = 0;
                }
                bytes = 0;
            } catch (IOException e) {
                Log.e("BTConnected","no connection",e);
                //ControlPage.popup("No device Connected");
                break;
            }
        }
        //Log.e("BTC","end while");
    }

    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e("BTC","write",e);
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}

