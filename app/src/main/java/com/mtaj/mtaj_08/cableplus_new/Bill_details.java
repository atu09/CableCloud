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

public class Bill_details extends AppCompatActivity {

    ListView lvbill;

    String title;

    ArrayList<HashMap<String,String>> billdetails=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            Transition transitionSlideRight =
                    TransitionInflater.from(Bill_details.this).inflateTransition(R.transition.slide_right);
            getWindow().setEnterTransition(transitionSlideRight);

        }



        lvbill=(ListView)findViewById(R.id.listView4);

        Intent j=getIntent();
        title=j.getExtras().getString("title");
        billdetails=(ArrayList<HashMap<String, String>>)j.getSerializableExtra("billdetails");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
               // startActivity(i);

                onBackPressed();
            }
        });


        final SimpleAdapter da=new SimpleAdapter(Bill_details.this,billdetails,R.layout.layout_bill_details,new String[]{"BillMonth","CurrentOutstanding","PrevOutstanding","TotalOutstanding"},new int[]{R.id.textView31,R.id.textView34,R.id.textView36,R.id.textView38});
        lvbill.setAdapter(da);
    }


    @Override
    public void onBackPressed()
    {

        finish();
    }
}
