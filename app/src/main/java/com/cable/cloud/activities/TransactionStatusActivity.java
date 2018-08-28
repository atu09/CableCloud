package com.cable.cloud.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cable.cloud.R;

import java.text.DecimalFormat;

public class TransactionStatusActivity extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPref";

    TextView txthome, txtnextpament, txtstatus, txtoutsatnding, txtrs;

    ImageView imgstatus;

    String from, oa, title;

    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_status);

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String str = "\u20B9";

        Intent j = getIntent();
        from = j.getExtras().getString("from");
        oa = j.getExtras().getString("Oa");
        title = j.getExtras().getString("title");


        txthome = (TextView) findViewById(R.id.btnCancel);
        txtnextpament = (TextView) findViewById(R.id.btnNext);
        txtstatus = (TextView) findViewById(R.id.textView51);
        txtoutsatnding = (TextView) findViewById(R.id.textView52);
        txtrs = (TextView) findViewById(R.id.textView53);

        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        imgstatus = (ImageView) findViewById(R.id.imageView7);

        if (from.equals("complain")) {
            txtnextpament.setText("Next Complaint");
            txtstatus.setText("Successfully Done");

            imgstatus.setImageResource(R.drawable.ic_success);
            imgstatus.setBackgroundResource(R.drawable.circle_pay);
            txtoutsatnding.setVisibility(View.GONE);
            txtrs.setVisibility(View.GONE);

        } else {
            txtnextpament.setText("Next Payment");
            txtstatus.setText("Payment Received");
            txtrs.setText(str + format.format(Double.parseDouble(oa)));

            imgstatus.setImageResource(R.drawable.ic_wallet);
            imgstatus.setBackgroundColor(Color.TRANSPARENT);
            txtoutsatnding.setVisibility(View.VISIBLE);
            txtrs.setVisibility(View.VISIBLE);
        }
        imgstatus.setColorFilter(Color.WHITE);


        Animation anim1 = new AlphaAnimation(1.0f, 0.6f);
        anim1.setDuration(1000);
        anim1.setRepeatMode(Animation.REVERSE);
        anim1.setRepeatCount(Animation.INFINITE);
        imgstatus.setAnimation(anim1);


        Animation anim2 = new AlphaAnimation(1.0f, 0.6f);
        anim2.setDuration(1000);
        anim2.setRepeatMode(Animation.REVERSE);
        anim2.setRepeatCount(Animation.INFINITE);
        txtstatus.setAnimation(anim2);


        txthome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txtnextpament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (from.equalsIgnoreCase("complain")) {
                    onBackPressed();
                } else {

                    if (pref.getString("AreaStatus", "").equalsIgnoreCase("true")) {

                        Intent i = new Intent(TransactionStatusActivity.this, CustomerListActivity.class);
                        startActivity(i);

                        finish();
                    } else {
                        onBackPressed();

                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
