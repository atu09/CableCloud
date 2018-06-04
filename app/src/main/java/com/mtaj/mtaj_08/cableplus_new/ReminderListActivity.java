package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class ReminderListActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    private static final String PREF_NAME = "LoginPref";

    String siteurl,uid,cid,aid,eid,URL;

    SharedPreferences pref;

    ListView lstreminder;

    ArrayList<HashMap<String,String>> reminderdetails=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            Transition transitionSlideRight =
                    TransitionInflater.from(ReminderListActivity.this).inflateTransition(R.transition.slide_right);
            getWindow().setEnterTransition(transitionSlideRight);

        }

        requestQueue = Volley.newRequestQueue(ReminderListActivity.this);

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();
        eid = pref.getString("Entityids", "").toString();

        lstreminder=(ListView)findViewById(R.id.listView4);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Reminders");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        URL=siteurl+"/GetReminderDetailsForCollectionApp";

        CallVolley(URL);

        registerForContextMenu(lstreminder);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(getApplicationContext(), DashBoard.class);
        startActivity(i);

        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reminder_complete, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {

            case R.id.action_complete:

               // Toast.makeText(ReminderListActivity.this,reminderdetails.get(info.position).get("ReminderId").toString(), Toast.LENGTH_SHORT).show();
               // reminderdetails.remove(info.position);

                URL=siteurl+"/GetUpdateStatusReminderForCollectionApp";
                CallVolleys(URL,reminderdetails.get(info.position).get("ReminderId").toString());

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void CallVolley(String a)
    {


        final SpotsDialog spload;
        spload=new SpotsDialog(ReminderListActivity.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {

            HashMap<String,String> map=new HashMap<>();
            map.put("contractorId",cid);
            map.put("userId",uid);
            map.put("entityIds",eid);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                try
                                {
                                    if(response.getString("status").toString().equals("True"))
                                    {
                                        final JSONArray entityarray = response.getJSONArray("lstReminderInfo");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);

                                          String rid=e.getString("ReminderId");
                                            String rdate=e.getString("ReminderDate");
                                            String note=e.getString("Note");
                                            String status=e.getString("Status");

                                            HashMap<String,String> map=new HashMap<>();
                                            map.put("ReminderId",rid);
                                            map.put("ReminderDate",rdate);
                                            map.put("Note",note);
                                            map.put("Status",status);

                                            reminderdetails.add(map);
                                        }

                                        final SimpleAdapter da=new SimpleAdapter(ReminderListActivity.this,reminderdetails,R.layout.layout_remiders_details,new String[]{"ReminderDate","Status","Note"},new int[]{R.id.textView88,R.id.textView89,R.id.textView90});
                                        lstreminder.setAdapter(da);

                                    }

                                }
                                catch (JSONException e)
                                {
                                    Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(), "error--"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    public void CallVolleys(String a,String id)
    {


        final SpotsDialog spload;
        spload=new SpotsDialog(ReminderListActivity.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {

            HashMap<String,String> map=new HashMap<>();
            map.put("userId",uid);
            map.put("reminderId",id);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                try
                                {
                                    if(response.getString("status").toString().equals("True"))
                                    {
                                        Toast.makeText(ReminderListActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                                        Intent i=new Intent(getApplicationContext(),ReminderListActivity.class);
                                        startActivity(i);
                                    }

                                }
                                catch (JSONException e)
                                {
                                    Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(), "error--"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "--" + e, Toast.LENGTH_SHORT).show();
        }

    }
}
