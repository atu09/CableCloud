package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
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

public class AlertListActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    private static final String PREF_NAME = "LoginPref";

    String siteurl,uid,cid,aid,eid,URL;

    SharedPreferences pref;

    ListView lstalert;

    ArrayList<HashMap<String,String>> alertdetails=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_list);

        requestQueue = Volley.newRequestQueue(AlertListActivity.this);

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();
        eid = pref.getString("Entityids", "").toString();

        lstalert=(ListView)findViewById(R.id.listView4);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Alerts");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        URL=siteurl+"/GetAlertDetailsForCollectionApp";

        CallVolley(URL);

    }

    public void CallVolley(String a)
    {


        final SpotsDialog spload;
        spload=new SpotsDialog(AlertListActivity.this,R.style.Custom);
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
                                   // Toast.makeText(AlertListActivity.this,response.getString("message"), Toast.LENGTH_SHORT).show();

                                    if(response.getString("status").toString().equals("True"))
                                    {

                                        final JSONArray entityarray = response.getJSONArray("lstAlertInfo");

                                        for (int i = 0; i < entityarray.length(); i++) {
                                            JSONObject e = (JSONObject) entityarray.get(i);

                                            String cid=e.getString("CustId");
                                            String cname=e.getString("Name");
                                            String acno=e.getString("AccountNo");
                                            String amount=e.getString("Amount");

                                            String addres=e.getString("Address");
                                            String payterm=e.getString("payterm");
                                            String pname=e.getString("packagename");
                                            String pdate=e.getString("packageenddate");

                                            String mqno=e.getString("mono");
                                            String type=e.getString("type");


                                            HashMap<String,String> map=new HashMap<>();
                                            map.put("Name&Account",cname+"("+acno+")");
                                            map.put("Amount",amount);
                                            map.put("Address",addres);
                                            map.put("payterm",payterm);
                                            map.put("packagename",pname);
                                            map.put("packageenddate",pdate);
                                            map.put("mono",mqno);
                                            map.put("type",type);


                                            alertdetails.add(map);
                                        }

                                        lstalert.setAdapter(new CustomAdapter(AlertListActivity.this,alertdetails));

                                        /*final SimpleAdapter da=new SimpleAdapter(AlertListActivity.this,reminderdetails,R.layout.layout_remiders_details,new String[]{"ReminderDate","Status","Note"},new int[]{R.id.textView88,R.id.textView89,R.id.textView90});
                                        lstreminder.setAdapter(da);*/

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
