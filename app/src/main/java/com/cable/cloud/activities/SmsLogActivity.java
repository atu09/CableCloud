package com.cable.cloud.activities;

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

import com.cable.cloud.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SmsLogActivity extends AppCompatActivity {

    ListView listView;
    String title;

    ArrayList<HashMap<String, String>> smsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_log);

        Intent j = getIntent();
        title = j.getExtras().getString("title");
        smsList = (ArrayList<HashMap<String, String>>) j.getSerializableExtra("smsdetails");

        listView = (ListView) findViewById(R.id.listView4);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final SimpleAdapter adapter = new SimpleAdapter(SmsLogActivity.this, smsList, R.layout.layout_sms_design, new String[]{"MobileNo", "SMSDate", "Message"}, new int[]{R.id.textView82, R.id.textView83, R.id.textView84});
        listView.setAdapter(adapter);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Transition transitionSlideRight = TransitionInflater.from(SmsLogActivity.this).inflateTransition(R.transition.slide_right);
            getWindow().setEnterTransition(transitionSlideRight);
        }

    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
