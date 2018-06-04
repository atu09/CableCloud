package com.mtaj.mtaj_08.cableplus_new;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class Remider_details extends AppCompatActivity {
    ListView lvremider;

    String title;

    ArrayList<HashMap<String,String>> reminderdetails=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remider_details);

        lvremider=(ListView)findViewById(R.id.listView4);

        Intent j=getIntent();
        title=j.getExtras().getString("title");
        reminderdetails=(ArrayList<HashMap<String, String>>)j.getSerializableExtra("remiderdetails");

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

        final SimpleAdapter da=new SimpleAdapter(Remider_details.this,reminderdetails,R.layout.layout_remiders_details,new String[]{"ReminderDate","Status","Note"},new int[]{R.id.textView88,R.id.textView89,R.id.textView90});
        lvremider.setAdapter(da);
    }

    @Override
    public void onBackPressed()
    {

        finish();
    }
}
