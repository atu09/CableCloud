package com.mtaj.mtaj_08.cableplus_new;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class Payment_details extends AppCompatActivity {

    ListView lvpay;

    ArrayList<HashMap<String,String>> paydetails=new ArrayList<>();

    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        Intent j=getIntent();
        title=j.getExtras().getString("title");
        paydetails=(ArrayList<HashMap<String, String>>)j.getSerializableExtra("paymnetdetails");

        lvpay=(ListView)findViewById(R.id.listView4);

        final Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
                //startActivity(i);

                onBackPressed();

            }
        });



        final SimpleAdapter da=new SimpleAdapter(Payment_details.this,paydetails,R.layout.layout_payment_details,new String[]{"Receiptno","ReceiptDate","BillMonth","PaidAmount","PayMode"},new int[]{R.id.textView34,R.id.textView36,R.id.textView38,R.id.textView40,R.id.textView42});
        lvpay.setAdapter(da);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            Transition transitionSlideRight =
                    TransitionInflater.from(Payment_details.this).inflateTransition(R.transition.slide_right);
            getWindow().setEnterTransition(transitionSlideRight);
        }


    }

    @Override
    public void onBackPressed()
    {

        finish();
    }
}
