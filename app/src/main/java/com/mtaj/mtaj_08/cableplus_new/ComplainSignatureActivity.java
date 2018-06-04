package com.mtaj.mtaj_08.cableplus_new;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class ComplainSignatureActivity extends AppCompatActivity implements View.OnClickListener {

    SignaturePad mSignaturePad;

    TextView tvcancel;

    String from, cmpid,areatitle;

    private static final String PREF_NAME = "LoginPref";

    String billid, accno, paymode, paidamount, cheqdate = "-", cheqno = "-", bankname = "-", email, signaturestring, rdate,custid,Discount;

    String siteurl, uid, cid, aid, eid, URL;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    Calendar calendar;
    int year, cmonth, day;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_signature);


        Intent j = getIntent();
        cmpid = j.getExtras().getString("complainId");
        from = j.getExtras().getString("from");
        areatitle= j.getExtras().getString("cname");
        custid=j.getExtras().getString("CustomerId");

        requestQueue = Volley.newRequestQueue(this);


        tvcancel = (TextView) findViewById(R.id.textView28);

        SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();

        if (from.equals("complain"))
        {
            tvcancel.setText("CANCEL");
            //tvcancel.setTextSize(22f);
            //tvcancel.setTypeface(null, Typeface.NORMAL);

        }
        else if (pref.getString("from", "").equals("Payment")) {
            tvcancel.setText( paidamount);
        }
        else if(from.equals("Payment"))
        {
            tvcancel.setText(paidamount);
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(areatitle);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        setSupportActionBar(toolbar);

        //getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

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

        findViewById(R.id.llconfirm).setOnClickListener(this);

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
        i.putExtra("customerId",custid);
        i.putExtra("complainId",cmpid);
        startActivity(i);

        finish();
    }

    @Override
    public void onClick(View v) {

        //hideKeyboard(this);
        switch (v.getId()) {
            case R.id.ibtn_clear:
                mSignaturePad.clear();
                break;
            case R.id.llconfirm:
                if (mSignaturePad.isEmpty()) {

                    findViewById(R.id.llconfirm).setEnabled(false);

                   /* Snackbar snackbar = Snackbar
                            .make(v, "Please enter customer signature", Snackbar.LENGTH_LONG);
                    snackbar.show();*/

                    signaturestring="-";
                    SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                    if (pref.getString("from", "").equals("Payment")) {

                        // URL = siteurl + "/GenerateBillReceiptForCollectionApp";

                        // GPSon(getApplicationContext());
                        //turnGpsOn(getApplicationContext());

                        //
                        // getLOcation();

                        //displayLocation();

                        //  Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();


                        // CallVolley(URL);
                    }
                    else if(from.equals("Payment"))
                    {
                        // URL = siteurl + "/GenerateBillReceiptForCollectionApp";

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

                    signaturestring="-";

                    if (pref.getString("from", "").equals("Payment")) {

                        // URL = siteurl + "/GenerateBillReceiptForCollectionApp";

                        // GPSon(getApplicationContext());
                        //turnGpsOn(getApplicationContext());

                        //
                        // getLOcation();

                        //displayLocation();

                        //  Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();


                        // CallVolley(URL);
                    }
                    else if(from.equals("Payment"))
                    {
                        // URL = siteurl + "/GenerateBillReceiptForCollectionApp";

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
        final SpotsDialog spload;
        spload = new SpotsDialog(ComplainSignatureActivity.this, R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {
            //jsonobj=makeHttpRequest(params[0]);

            HashMap<String, String> map = new HashMap<>();
            map.put("complainid", cmpid);
            map.put("usersign", signaturestring);

            Log.e("MAP:", map.toString());

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                findViewById(R.id.llconfirm).setEnabled(true);

                                try {
                                    if (response.getString("status").toString().equals("True")) {
                                        Intent i = new Intent(ComplainSignatureActivity.this, TransactionStatusActivity.class);
                                        i.putExtra("from", from);
                                        i.putExtra("Oa", "");
                                        startActivity(i);

                                        finish();
                                    }

                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(ComplainSignatureActivity.this, "error--" + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(ComplainSignatureActivity.this, "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        } catch (Exception e) {
            Toast.makeText(ComplainSignatureActivity.this, "--" + e, Toast.LENGTH_SHORT).show();
        }

    }


    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
