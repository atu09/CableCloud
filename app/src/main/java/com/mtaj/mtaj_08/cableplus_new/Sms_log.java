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

public class Sms_log extends AppCompatActivity {

    ListView lvsms;

    String title;

    ArrayList<HashMap<String,String>> smslist=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_log);

        Intent j=getIntent();
        title=j.getExtras().getString("title");
        smslist=(ArrayList<HashMap<String, String>>)j.getSerializableExtra("smsdetails");

        lvsms=(ListView)findViewById(R.id.listView4);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        final SimpleAdapter da=new SimpleAdapter(Sms_log.this,smslist,R.layout.layout_sms_design,new String[]{"MobileNo","SMSDate","Message"},new int[]{R.id.textView82,R.id.textView83,R.id.textView84});
        lvsms.setAdapter(da);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            Transition transitionSlideRight =
                    TransitionInflater.from(Sms_log.this).inflateTransition(R.transition.slide_right);
            getWindow().setEnterTransition(transitionSlideRight);

        }


    }


    @Override
    public void onBackPressed()
    {

        finish();
    }
}
