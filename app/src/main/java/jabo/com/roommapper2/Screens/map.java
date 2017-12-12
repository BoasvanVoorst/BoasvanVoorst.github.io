package jabo.com.roommapper2.Screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import jabo.com.roommapper2.R;
import jabo.com.roommapper2.TCP.TcpClient;
import jabo.com.roommapper2.mapingtools.DrawView;

public class map extends Activity {
    static DrawView drawView;
    static int currentCoords = 0;
    static int[][] coords = new int[1000][2];
    TcpClient mTcpClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ConnectTask().execute("");
        drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.WHITE);
        setContentView(drawView);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose room name");
        EditText input = new EditText(this);
        input.setHint("roomname");
        final EditText _input = input;
        builder.setView(input);
        builder.setPositiveButton("next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name =_input.getText().toString();
                if (mTcpClient != null) {
                    mTcpClient.sendMessage(name+"<NAME>");
                }
                if (mTcpClient != null) {
                    mTcpClient.sendMessage("<RMAP>");
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mTcpClient != null){
            mTcpClient.stopClient();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        drawView.update(coords,drawView.getWidth()/2,drawView.getHeight()/2);
    }

    public static void update(String Message){
        Message = Message.replace("message received","");
        String[] xy = Message.split(",");
        int x = Integer.parseInt(xy[0]);
        int y = Integer.parseInt(xy[1]);
        coords[currentCoords][0]=x;
        coords[currentCoords][1]=y;
        drawView.update(coords,drawView.getWidth()/2,drawView.getHeight()/2);
        currentCoords++;
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
            update(message[0]);
        }
    }
}
