package com.cable.cloud.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cable.cloud.helpers.ConnectivityReceiver;
import com.cable.cloud.R;
import com.cable.cloud.helpers.MyApplication;

public class NoConnectionActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Button btnretry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);

        btnretry = (Button) findViewById(R.id.button3);

        btnretry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkConnection();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";

            //  Toast.makeText(NoConnectionActivity.this, message, Toast.LENGTH_SHORT).show();

            finish();

            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";

            // Intent i=new Intent(getApplicationContext(),NoConnectionActivity.class);
            // startActivity(i);
            color = Color.RED;
        }

        //Snackbar snackbar = Snackbar
        //     .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);



      /*  View sbView = Toast.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();*/
    }


}
