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

public class Attachment_details extends AppCompatActivity {
    ListView lvattachment;

    String title;

    ArrayList<HashMap<String,String>> attachmentdetails=new ArrayList<>();

    public int REQUEST_CAMERA=123;

    public int SELECT_IMAGE=345;

    public int SELECT_FILE=888;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_details);
        lvattachment=(ListView)findViewById(R.id.listView4);

        Intent j=getIntent();
        title=j.getExtras().getString("title");
        attachmentdetails=(ArrayList<HashMap<String, String>>)j.getSerializableExtra("attachmentdetails");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent i = new Intent(getApplicationContext(), CustomerMasterDetailsActivity.class);
             //   startActivity(i);

                onBackPressed();

            }
        });

        final SimpleAdapter da=new SimpleAdapter(Attachment_details.this,attachmentdetails,R.layout.layout_attachment_details,new String[]{"Filename","UploadedFile"},new int[]{R.id.textView88,R.id.textView90});
        lvattachment.setAdapter(da);

    }

    @Override
    public void onBackPressed()
    {

        finish();
    }
}
