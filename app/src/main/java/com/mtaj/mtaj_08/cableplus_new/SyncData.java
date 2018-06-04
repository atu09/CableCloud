package com.mtaj.mtaj_08.cableplus_new;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class SyncData extends AppCompatActivity {

    int successcount=0;
    int errorCount=0;

    String siteurl,uid,cid,eid,URL,aid;
    private static final String PREF_NAME = "LoginPref";

    SharedPreferences pref;
    RequestQueue requestQueue,requestQueues;
    SpotsDialog spload;

    ArrayList<HashMap<String,String>> areadetails=new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String,String>> customerlist=new ArrayList<HashMap<String, String>>();

    DBHelper myDB;

    Button btnsync;

    //SlackLoadingView loadingView;

    int page=0;
    ProgressDialog progressDialog;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;
    JSONObject jsonobj;

    boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        isConnected = (NetworkUtil.getConnectivityStatus(getApplicationContext())==NetworkUtil.TYPE_CONNECTED?true:false);

        myDB=new DBHelper(this);
        progressDialog=new ProgressDialog(this);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sync");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        btnsync=(Button)findViewById(R.id.button4);
        //loadingView=(SlackLoadingView)findViewById(R.id.loading_view);

        //loadingView.setLineLength(0.5f);
        /*loadingView.setDuration(10f);*/

        spload=new SpotsDialog(SyncData.this,R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);

        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid = pref.getString("Contracotrid", "").toString();
        eid = pref.getString("Entityids", "").toString();

        btnsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isConnected) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(SyncData.this);
                    adb.setMessage("This Process may take a longer time..!" + "\n\n" + "Please don't TURN OFF your internet connection till process finish." + "\n \n" + "Press OK to start process");
                    adb.setCancelable(true);
                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            myDB.deleteData();

                            URL = siteurl + "/GetAreaByUserForCollectionApp";

                            CallVolleysAREA(URL);

                        }
                    });

                    AlertDialog ad = adb.create();
                    ad.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                    ad.show();
                }
                else
                {
                    AlertDialog.Builder adb = new AlertDialog.Builder(SyncData.this);
                    adb.setMessage("You are not Connected to Internet.. \n \n Please TURN ON your Data Connection to Sync data..!! ");
                    adb.setCancelable(true);
                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {



                        }
                    });

                    AlertDialog ad = adb.create();
                    ad.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                    ad.show();
                }


            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }


    public void getCustomers(final ArrayList<HashMap<String,String>> list){

        try {

            if (list.size() > 0) {
                URL = siteurl + "/GetAreawiseCustomerOutStandingByUserNEntityIdsForCollectionApp";

                for (int i = 0; i < list.size(); i++) {
                    String areaId = list.get(i).get("AreaId");

                    CallVolleysCUSTOMER(URL, areaId);
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("Eror",e.toString());
        }

    }


    public void CallVolleysAREA(String a)
    {
        JsonObjectRequest obreqs;

        HashMap<String, String> map=new HashMap<>();
        map.put("contractorId", cid);
        map.put("userId", uid);
        map.put("entityId",eid);
        map.put("startindex","0");
        map.put("noofrecords", "1000000000");

        progressDialog.setTitle("Syncing");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        obreqs = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {


                            if (response.getString("status").toString().equals("True")) {

                                DecimalFormat format = new DecimalFormat();
                                format.setDecimalSeparatorAlwaysShown(false);

                                final JSONArray entityarray = response.getJSONArray("AreaInfoList");

                                for (int i = 0; i < entityarray.length(); i++) {
                                    JSONObject e = (JSONObject) entityarray.get(i);

                                    String aid = e.getString("AreaId");
                                    String aname = e.getString("AreaName");
                                    String acode = e.getString("AreaCode");
                                    String Aoa = e.getString("Outstanding");
                                    String Acol = e.getString("Collection");

                                    if(myDB.insertArea(aid,aname,acode,Aoa,Acol)) {

                                        // Toast.makeText(SyncData.this, "A"+i, Toast.LENGTH_SHORT).show();

                                        HashMap<String, String> map = new HashMap<>();

                                        map.put("AreaId", aid);
                                        map.put("AreaName", aname);
                                        map.put("AreaCode", acode);
                                        map.put("Outstanding", Aoa);
                                        map.put("Collection", Acol);

                                        areadetails.add(map);

                                        //URL = siteurl + "/GetAreawiseCustomerOutStandingByUserNEntityIdsForCollectionApp";
                                        //CallVolleysCUSTOMER(URL, aid);
                                    }

                                }

                                getCustomers(areadetails);

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        obreqs.setRetryPolicy(new DefaultRetryPolicy(1200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adds the JSON object request "obreq" to the request queue

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        requestQueue.add(obreqs);


    }


    public void CallVolleysCUSTOMER(String a,final String aid)
    {
        CustomPriorityRequest obreqss;

        HashMap<String,String> map=new HashMap<>();
        map.put("contractorid",cid);
        map.put("userId",uid);
        map.put("entityId",eid);
        map.put("areadId", aid);
        map.put("startindex", "0");
        map.put("noofrecords", "1000000000");
        map.put("filterCustomer", "");

        obreqss = new CustomPriorityRequest(Request.Method.POST,a,new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  try {

                        try {

                            if (response.getString("status").toString().equals("True")) {

                                //Toast.makeText(SyncData.this, "---", Toast.LENGTH_SHORT).show();

                                DecimalFormat format = new DecimalFormat();
                                format.setDecimalSeparatorAlwaysShown(false);

                                final JSONArray entityarray = response.getJSONArray("CustomerInfoList");

                                for (int i = 0; i < entityarray.length(); i++) {
                                    JSONObject e = (JSONObject) entityarray.get(i);

                                    String cid = e.getString("CustomerId");
                                    String cname = e.getString("Name");
                                    String caddress = e.getString("Address");
                                    String carea = e.getString("Area");
                                    String cacno = e.getString("AccountNo");
                                    String cphone = e.getString("Phone");
                                    String cemail = e.getString("Email");
                                    String castatus = e.getString("AccountStatus");
                                    String cmq = e.getString("MQNo");
                                    String ctotaloa = e.getString("TotalOutStandingAmount");
                                    String commentCount = e.getString("CustCommentCount");
                                    String bid=e.getString("billId");

                                    if(myDB.insertCustomer(cid, cname, caddress,carea, cacno, cphone, cemail, castatus, cmq, ctotaloa, commentCount, bid, aid))
                                    {
                                       // Toast.makeText(SyncData.this, "C"+i, Toast.LENGTH_SHORT).show();

                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("CustomerId", cid);
                                        map.put("Name", cname);
                                        map.put("Address", caddress);
                                        map.put("Area", carea);
                                        map.put("AccountNo", cacno);
                                        map.put("Phone", cphone);
                                        map.put("Email", cemail);
                                        map.put("AccountStatus", castatus);
                                        map.put("MQNo", cmq);
                                        map.put("TotalOutStandingAmount",ctotaloa);
                                        map.put("CustCommentCount", commentCount);
                                        map.put("billId", bid);

                                        customerlist.add(map);


                                    }

                                }

                                successcount--;

                                if(successcount==0)
                                {

                                    if(progressDialog.isShowing())
                                    {
                                        progressDialog.dismiss();
                                    }

                                    Toast toast = Toast.makeText(SyncData.this,"Sync Complete..!", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }

                            }
                            else
                            {

                            }


                            }   catch (JSONException e) {
                            e.printStackTrace();
                            }
                            catch (Exception e) {
                                //Toast.makeText(getA(), "error--" + e, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Toast.makeText(getApplicationContext(), "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        obreqss.setRetryPolicy(new DefaultRetryPolicy(1200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //obreqss.setPriority(Request.Priority.HIGH);

        // Adds the JSON object request "obreq" to the request queue

        requestQueues = Volley.newRequestQueue(getApplicationContext());

        requestQueues.add(obreqss);

        successcount++;



        /*StringRequest stringRequest = new StringRequest(Request.Method.POST, a,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response1) {
                        // Toast.makeText(RegistrationActivity.this,response,Toast.LENGTH_LONG).show();

                        try {

                            JSONObject response = new JSONObject(response1);

                            try {

                                if (response.getString("status").toString().equals("True")) {

                                    //Toast.makeText(SyncData.this, "---", Toast.LENGTH_SHORT).show();

                                    DecimalFormat format = new DecimalFormat();
                                    format.setDecimalSeparatorAlwaysShown(false);

                                    final JSONArray entityarray = response.getJSONArray("CustomerInfoList");

                                    for (int i = 0; i < entityarray.length(); i++) {
                                        JSONObject e = (JSONObject) entityarray.get(i);

                                        String cid = e.getString("CustomerId");
                                        String cname = e.getString("Name");
                                        String caddress = e.getString("Address");
                                        String carea = e.getString("Area");
                                        String cacno = e.getString("AccountNo");
                                        String cphone = e.getString("Phone");
                                        String cemail = e.getString("Email");
                                        String castatus = e.getString("AccountStatus");
                                        String cmq = e.getString("MQNo");
                                        String ctotaloa = e.getString("TotalOutStandingAmount");
                                        String commentCount = e.getString("CustCommentCount");
                                        String bid=e.getString("billId");

                                        if(myDB.insertCustomer(cid, cname, caddress,carea, cacno, cphone, cemail, castatus, cmq, ctotaloa, commentCount, bid, aid))
                                        {
                                            // Toast.makeText(SyncData.this, "C"+i, Toast.LENGTH_SHORT).show();

                                            HashMap<String, String> map = new HashMap<>();
                                            map.put("CustomerId", cid);
                                            map.put("Name", cname);
                                            map.put("Address", caddress);
                                            map.put("Area", carea);
                                            map.put("AccountNo", cacno);
                                            map.put("Phone", cphone);
                                            map.put("Email", cemail);
                                            map.put("AccountStatus", castatus);
                                            map.put("MQNo", cmq);
                                            map.put("TotalOutStandingAmount",ctotaloa);
                                            map.put("CustCommentCount", commentCount);
                                            map.put("billId",bid);

                                            customerlist.add(map);
                                        }



                                    }

                                    // page=page+1;

                                    // URL = siteurl + "/GetAreawiseCustomerOutStandingByUserNEntityIdsForCollectionApp";

                                    // CallVolleysCUSTOMER(URL,aid,page);

                                    *//*if(customerlist.size()>0)
                                    {
                                        for(int i=0;i<customerlist.size();i++)
                                        {
                                            if(myDB.insertCustomer(customerlist.get(i).get("CustomerId"), customerlist.get(i).get("Name"), customerlist.get(i).get("Address"), customerlist.get(i).get("Area"), customerlist.get(i).get("AccountNo"), customerlist.get(i).get("Phone"), customerlist.get(i).get("Email"), customerlist.get(i).get("AccountStatus"), customerlist.get(i).get("MQNo"), customerlist.get(i).get("TotalOutStandingAmount"), customerlist.get(i).get("CustCommentCount"), customerlist.get(i).get("billId"), aid))
                                            {
                                                Toast.makeText(SyncData.this, "C"+i, Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        customerlist.clear();
                                    }
                                    *//*
                                    // Toast.makeText(SyncData.this, "DONE..!!", Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    // page=0;

                                    //Toast.makeText(getApplicationContext(), response.getString("message").toString(), Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Toast.makeText(RegistrationActivity.this, "error--" + e, Toast.LENGTH_LONG).show();
                            }



                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // Toast.makeText(getApplicationContext(), "Something went Wrong.. Please Try again..", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("contractorid",cid);
                params.put("userId",uid);
                params.put("entityId",eid);
                params.put("areadId",aid);
                params.put("startindex","0");
                params.put("noofrecords","1000000000");
                params.put("filterCustomer", "");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
*/
    }





}

