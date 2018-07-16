package com.cable.cloud;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cable.cloud.activities.TransactionStatusActivity;
import com.cable.cloud.helpers.Utils;
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONObject;

import java.util.HashMap;

public class ComplainSignatureActivity extends AppCompatActivity implements View.OnClickListener {

    SignaturePad mSignaturePad;

    TextView tvcancel;

    String from, cmpid, areatitle;

    private static final String PREF_NAME = "LoginPref";

    String billid, accno, paymode, paidamount, cheqdate = "-", cheqno = "-", bankname = "-", email, signaturestring, rdate, custid, Discount;

    String siteurl, uid, cid, aid, eid, URL;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_signature);


        Intent j = getIntent();
        cmpid = j.getExtras().getString("complainId");
        from = j.getExtras().getString("from");
        areatitle = j.getExtras().getString("cname");
        custid = j.getExtras().getString("CustomerId");

        requestQueue = Volley.newRequestQueue(this);


        tvcancel = (TextView) findViewById(R.id.btnCancel);

        SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl = pref.getString("SiteURL", "");
        uid = pref.getString("Userid", "");

        if (from.equals("complain")) {
            tvcancel.setText("CANCEL");

        } else if (pref.getString("from", "").equals("Payment")) {
            tvcancel.setText(paidamount);
        } else if (from.equals("Payment")) {
            tvcancel.setText(paidamount);
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(areatitle);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        findViewById(R.id.ibtn_clear).setOnClickListener(this);
        findViewById(R.id.ibtn_clear).setVisibility(View.GONE);

        findViewById(R.id.textView29).setOnClickListener(this);

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                findViewById(R.id.ibtn_clear).setVisibility(View.VISIBLE);
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
            }

            @Override
            public void onClear() {
                findViewById(R.id.ibtn_clear).setVisibility(View.GONE);
                //Event triggered when the pad is cleared
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ComplainDetails.class);
        i.putExtra("title", areatitle);
        i.putExtra("customerId", custid);
        i.putExtra("complainId", cmpid);
        startActivity(i);

        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ibtn_clear:
                mSignaturePad.clear();
                break;
            case R.id.textView29:
                if (mSignaturePad.isEmpty()) {

                    findViewById(R.id.textView29).setEnabled(false);

                    signaturestring = "-";
                    SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                    if (pref.getString("from", "").equals("Payment")) {

                        // URL = siteURL + "/GenerateBillReceiptForCollectionApp";

                        // GPSon(getApplicationContext());
                        //turnGpsOn(getApplicationContext());

                        //
                        // getLOcation();

                        //displayLocation();

                        //  Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();


                        // CallVolley(URL);
                    } else if (from.equals("Payment")) {
                        // URL = siteURL + "/GenerateBillReceiptForCollectionApp";

                        // GPSon(getApplicationContext());
                        //turnGpsOn(getApplicationContext());

                        //
                        // getLOcation();

                        //displayLocation();

                        //  Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();


                        // CallVolley(URL);
                    }

                    if (from.equals("complain")) {
                        URL = siteurl + "/ResolveComplain";

                        CallVolleys(URL);
                    }

                    //  return;
                } else {
                    SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                    signaturestring = "-";

                    if (pref.getString("from", "").equals("Payment")) {

                        // URL = siteURL + "/GenerateBillReceiptForCollectionApp";

                        // GPSon(getApplicationContext());
                        //turnGpsOn(getApplicationContext());

                        //
                        // getLOcation();

                        //displayLocation();

                        //  Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();


                        // CallVolley(URL);
                    } else if (from.equals("Payment")) {
                        // URL = siteURL + "/GenerateBillReceiptForCollectionApp";

                        // GPSon(getApplicationContext());
                        //turnGpsOn(getApplicationContext());

                        //
                        // getLOcation();

                        //displayLocation();

                        //  Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();


                        // CallVolley(URL);
                    }

                    if (from.equals("complain")) {
                        URL = siteurl + "/ResolveComplain";

                        CallVolleys(URL);
                    }

                }

        }

    }

    public void CallVolleys(String a) {

        final Dialog loader = Utils.getLoader(this);
        loader.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("complainid", cmpid);
        map.put("usersign", signaturestring);

        Log.e("MAP:", map.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loader.isShowing()) {
                            loader.dismiss();
                        }

                        try {

                            findViewById(R.id.textView29).setEnabled(true);

                            if (response.getString("status").equalsIgnoreCase("True")) {
                                Intent i = new Intent(ComplainSignatureActivity.this, TransactionStatusActivity.class);
                                i.putExtra("from", from);
                                i.putExtra("Oa", "");
                                startActivity(i);

                                finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                        error.printStackTrace();

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

}
