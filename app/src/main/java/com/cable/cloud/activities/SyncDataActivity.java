package com.cable.cloud.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.cable.cloud.ConnectivityReceiver;
import com.cable.cloud.CustomPriorityRequest;
import com.cable.cloud.DBHelper;
import com.cable.cloud.MyApplication;
import com.cable.cloud.NetworkUtil;
import com.cable.cloud.R;
import com.cable.cloud.helpers.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SyncDataActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    DBHelper myDB;
    SharedPreferences pref;
    private static final String PREF_NAME = "LoginPref";

    int successCount = 0;
    String siteurl, uid, cid, eid, URL;

    RequestQueue requestQueue, requestQueues;
    ArrayList<HashMap<String, String>> areaDetails = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> customerList = new ArrayList<HashMap<String, String>>();

    Button btnSync;
    ProgressDialog progressDialog;
    boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        myDB = new DBHelper(this);
        progressDialog = new ProgressDialog(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sync");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        btnSync = (Button) findViewById(R.id.button4);

        siteurl = pref.getString("SiteURL", "");
        uid = pref.getString("Userid", "");
        cid = pref.getString("Contracotrid", "");
        eid = pref.getString("Entityids", "");

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isConnected = Utils.isInternetAvailable(SyncDataActivity.this);

                if (isConnected) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(SyncDataActivity.this);
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
                } else {
                    AlertDialog.Builder adb = new AlertDialog.Builder(SyncDataActivity.this);
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

    public void getCustomers(final ArrayList<HashMap<String, String>> list) {

        try {

            if (list.size() > 0) {
                URL = siteurl + "/GetAreawiseCustomerOutStandingByUserNEntityIdsForCollectionApp";
                for (int i = 0; i < list.size(); i++) {
                    String areaId = list.get(i).get("AreaId");
                    CallVolleysCUSTOMER(URL, areaId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", e.toString());
        }

    }

    public void CallVolleysAREA(String a) {

        HashMap<String, String> map = new HashMap<>();
        map.put("contractorId", cid);
        map.put("userId", uid);
        map.put("entityId", eid);
        map.put("startindex", "0");
        map.put("noofrecords", "1000000000");

        progressDialog.setTitle("Syncing");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Utils.checkLog("json", response, null);
                            if (response.getString("status").equalsIgnoreCase("True")) {

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

                                    if (myDB.insertArea(aid, aname, acode, Aoa, Acol)) {

                                        HashMap<String, String> map = new HashMap<>();

                                        map.put("AreaId", aid);
                                        map.put("AreaName", aname);
                                        map.put("AreaCode", acode);
                                        map.put("Outstanding", Aoa);
                                        map.put("Collection", Acol);

                                        areaDetails.add(map);
                                    }

                                }

                                getCustomers(areaDetails);

                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(1200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        requestQueue.add(request);


    }

    public void CallVolleysCUSTOMER(String a, final String aid) {


        HashMap<String, String> map = new HashMap<>();
        map.put("contractorid", cid);
        map.put("userId", uid);
        map.put("entityId", eid);
        map.put("areadId", aid);
        map.put("startindex", "0");
        map.put("noofrecords", "1000000000");
        map.put("filterCustomer", "");

        CustomPriorityRequest request = new CustomPriorityRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Utils.checkLog("json", response, null);
                            if (response.getString("status").equalsIgnoreCase("True")) {

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
                                    String bid = e.getString("billId");

                                    if (myDB.insertCustomer(cid, cname, caddress, carea, cacno, cphone, cemail, castatus, cmq, ctotaloa, commentCount, bid, aid)) {

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
                                        map.put("TotalOutStandingAmount", ctotaloa);
                                        map.put("CustCommentCount", commentCount);
                                        map.put("billId", bid);

                                        customerList.add(map);
                                    }

                                }

                                successCount--;

                                if (successCount == 0) {

                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }

                                    Toast toast = Toast.makeText(SyncDataActivity.this, "Sync Complete..!", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }


                        } catch (Exception e) {
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

        request.setRetryPolicy(new DefaultRetryPolicy(1200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueues = Volley.newRequestQueue(getApplicationContext());
        requestQueues.add(request);
        successCount++;

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
    }
}

