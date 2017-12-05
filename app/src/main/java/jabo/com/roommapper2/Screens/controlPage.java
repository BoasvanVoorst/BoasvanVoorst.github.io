package jabo.com.roommapper2.Screens;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import jabo.com.roommapper2.R;
import jabo.com.roommapper2.TCP.TcpClient;
import jabo.com.roommapper2.extraControls.JoystickView;

public class controlPage extends Activity {

    private TcpClient mTcpClient;
    private JoystickView joystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_page);
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
                        message[1] = 0;//halt
                }
                message[0] = (byte)(message.length+16);
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }

    public class ConnectTask extends AsyncTask<String, String, TcpClient> {

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
    }
}
