package jabo.com.roommapper2.Screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import jabo.com.roommapper2.R;
import jabo.com.roommapper2.TCP.TcpClient;

public class menu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void ControlPage(View view){
        Intent intent = new Intent(this,controlPage.class);
        startActivity(intent);
    }

    public void map(View view){
        Intent intent = new Intent(this,map.class);
        startActivity(intent);
    }
}
