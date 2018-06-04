package com.mtaj.mtaj_08.cableplus_new;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

//import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import dmax.dialog.SpotsDialog;

public class CustomerSignatureActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    SignaturePad mSignaturePad;

    private Location mLastLocation;


    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 1000; // 10 sec
    private static int FATEST_INTERVAL = 1000; // 5 sec
    private static int DISPLACEMENT = 10;


    TextView tvcancel;

    String from, cmpid;

    private static final String PREF_NAME = "LoginPref";

    String billid, accno, paymode, paidamount, cheqdate = "-", cheqno = "-", bankname = "-", email, signaturestring,rdate,custid,Discount,notes;

    String siteurl, uid, cid, aid, eid, URL;

    static InputStream is = null;
    static JSONObject jobj = null;
    static String json = "";
    static JSONArray jarr = null;

    JSONObject jsonobj;

    Calendar calendar;
    int year, cmonth, day;

    RequestQueue requestQueue;

    private LocationManager locationManager;

    Location location; // location
    String latitude = "0.0"; // latitude
    String longitude = "0.0";

    String areatitle;
    boolean isConnected;

    DBHelper myDB;

    boolean AllowRequest=true;

    // variable to track event time
    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signature);
        String str = "\u20B9";


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        isConnected = ConnectivityReceiver.isConnected();
        myDB=new DBHelper(this);

        Intent j = getIntent();
         areatitle= j.getExtras().getString("cname");
        from = j.getExtras().getString("from");
        billid = j.getExtras().getString("billid");
        accno = j.getExtras().getString("acno");
        paidamount = j.getExtras().getString("paidamount");
        paymode = j.getExtras().getString("paymentmode");
        cheqno = j.getExtras().getString("chqno");
        cheqdate = j.getExtras().getString("chqdate");
        bankname = j.getExtras().getString("bankname");
        email = j.getExtras().getString("email");
        cmpid = j.getExtras().getString("complainId");
        custid=j.getExtras().getString("CustomerId");
        Discount=j.getExtras().getString("Discount");
        rdate=j.getExtras().getString("receiptDate");
        notes=j.getExtras().getString("Notes");


        requestQueue = Volley.newRequestQueue(this);


        tvcancel = (TextView) findViewById(R.id.textView28);

        SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        siteurl = pref.getString("SiteURL", "").toString();
        uid = pref.getString("Userid", "").toString();
        cid=pref.getString("Contracotrid","").toString();

        calendar = Calendar.getInstance();

        //SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        //rdate = sdf.format(calendar.getTime());

        if(checkPlayServices()) {

            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }

        createLocationRequest();
        // displayLocation();

        if (from.equals("complain"))
        {
            tvcancel.setText("CANCEL");
            tvcancel.setTextSize(22f);
            tvcancel.setTypeface(null, Typeface.NORMAL);

        }
        else if (pref.getString("from", "").equals("Payment")) {
            tvcancel.setText(str + paidamount);
        }
        else if(from.equals("Payment"))
        {
            tvcancel.setText(str + paidamount);
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

        if(isConnected) {

            Intent i = new Intent(CustomerSignatureActivity.this, CustomerDetails.class);
            i.putExtra("cname", areatitle);
            i.putExtra("A/cNo", accno);
            i.putExtra("CustomerId", custid);
            i.putExtra("from", from);
            startActivity(i);

            finish();
        }
        else
        {
            Intent i = new Intent(CustomerSignatureActivity.this, CustomerDetail_Offline.class);
            i.putExtra("cname", areatitle);
            i.putExtra("A/cNo", accno);
            i.putExtra("CustomerId", custid);
            i.putExtra("billId",billid);
            startActivity(i);

            finish();
        }

    }

    public String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        String img = temp.replace("\n", "%20");

        return img;
    }

    @Override
    public void onClick(View v) {

        hideKeyboard(this);

        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (v.getId()) {
            case R.id.ibtn_clear:
                mSignaturePad.clear();
                break;
            case R.id.llconfirm:

                if(findViewById(R.id.llconfirm).isEnabled()) {

                    if (mSignaturePad.isEmpty()) {

                        //findViewById(R.id.llconfirm).setEnabled(false);
                        //findViewById(R.id.llconfirm).setClickable(false);
                        disableView(findViewById(R.id.llconfirm));

                        //Toast.makeText(this, "Disable", Toast.LENGTH_SHORT).show();

                   /* Snackbar snackbar = Snackbar
                            .make(v, "Please enter customer signature", Snackbar.LENGTH_LONG);
                    snackbar.show();*/

                        signaturestring = "-";
                        SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                        if (pref.getString("from", "").equals("Payment")) {

                            displayLocation();

                        } else if (from.equals("Payment")) {
                            displayLocation();

                        }

                        if (from.equals("complain")) {
                            URL = siteurl + "/ResolveComplain";

                            CallVolleys(URL);
                        }

                        //  return;
                    } else {


                        //findViewById(R.id.llconfirm).setEnabled(false);
                        disableView(findViewById(R.id.llconfirm));

                        //Toast.makeText(this, "Disable", Toast.LENGTH_SHORT).show();

                        SharedPreferences pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                        signaturestring = "-";

                        if (pref.getString("from", "").equals("Payment")) {

                            // URL = siteurl + "/GenerateBillReceiptForCollectionApp";

                            displayLocation();

                        } else if (from.equals("Payment")) {
                            // URL = siteurl + "/GenerateBillReceiptForCollectionApp";

                            displayLocation();
                        }

                        if (from.equals("complain")) {
                            URL = siteurl + "/ResolveComplain";

                            CallVolleys(URL);
                        }

                    }
                }

        }

    }


    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void CallVolley(String a) {

        AllowRequest=false;

        final SpotsDialog spload;
        spload = new SpotsDialog(CustomerSignatureActivity.this, R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        spload.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                AllowRequest=true;

                //findViewById(R.id.llconfirm).setEnabled(true);

                enableView(findViewById(R.id.llconfirm));

            }
        });

        try {
            //jsonobj=makeHttpRequest(params[0]);

            HashMap<String, String> map = new HashMap<>();
            map.put("Content-Type", "application/json; charset=utf-8");
            map.put("accountno", accno);
            map.put("billid", billid);
            map.put("paidamount", paidamount);
            map.put("paymentmode", paymode);
            map.put("chqnumber", cheqno);
            map.put("cheqdate", cheqdate);
            map.put("cheqbankname", bankname);
            map.put("email", email);
            map.put("notes", notes);
            map.put("createdby", uid);
            map.put("signature", signaturestring);
            map.put("receiptdate", rdate);
            map.put("longitude", longitude);
            map.put("latitude", latitude);
            map.put("discount",Discount);
            map.put("isprint", "");

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                try {

                                    //findViewById(R.id.llconfirm).setEnabled(true);
                                    enableView(findViewById(R.id.llconfirm));

                                    //Toast.makeText(CustomerSignatureActivity.this, "Enable", Toast.LENGTH_SHORT).show();

                                  //  Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();

                                    if (response.getString("status").toString().equals("True")) {
                                        String toa = response.getString("TotalOutStandingAmount");

                                        Intent i = new Intent(CustomerSignatureActivity.this, TransactionStatusActivity.class);
                                        i.putExtra("from", from);
                                        i.putExtra("Oa", toa);
                                        i.putExtra("title",areatitle);
                                        startActivity(i);

                                        finish();
                                    }

                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Error:++" + e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(CustomerSignatureActivity.this, "error--" + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            spload.dismiss();

                            //findViewById(R.id.llconfirm).setEnabled(true);

                            enableView(findViewById(R.id.llconfirm));

                            Toast.makeText(CustomerSignatureActivity.this, "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);



        } catch (Exception e) {
            Toast.makeText(CustomerSignatureActivity.this, "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    public void CallVolleys(String a) {
        final SpotsDialog spload;
        spload = new SpotsDialog(CustomerSignatureActivity.this, R.style.Custom);
        spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        spload.setCancelable(true);
        spload.show();

        try {
            //jsonobj=makeHttpRequest(params[0]);

            HashMap<String, String> map = new HashMap<>();
            map.put("complainid", cmpid);
            map.put("usersign", signaturestring);

            Log.e("MAP:",map.toString());

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                spload.dismiss();

                                //findViewById(R.id.llconfirm).setEnabled(true);

                                enableView(findViewById(R.id.llconfirm));

                                try {
                                    if (response.getString("status").toString().equals("True")) {
                                        Intent i = new Intent(CustomerSignatureActivity.this, TransactionStatusActivity.class);
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
                                Toast.makeText(CustomerSignatureActivity.this, "error--" + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(CustomerSignatureActivity.this, "errorr++" + error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

            obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);

        } catch (Exception e) {
            Toast.makeText(CustomerSignatureActivity.this, "--" + e, Toast.LENGTH_SHORT).show();
        }

    }

    public JSONObject makeHttpRequest(String url) {

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = 500000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 500000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httppost = new HttpGet(url);
        try {
            HttpResponse httpresponse = httpclient.execute(httppost);
            HttpEntity httpentity = httpresponse.getEntity();
            is = httpentity.getContent();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {


            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            // BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_16LE), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                if (reader != null) {

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_SHORT).show();
                }

                //is.close();
                json = sb.toString();

                // json= sb.toString().substring(0, sb.toString().length()-1);
                try {
                    jobj = new JSONObject(json);

                    // JSONArray jarrays=new JSONArray(json);

                    // jobj=jarrays.getJSONObject(0);

                    //  org.json.simple.parser.JSONParser jsonparse=new org.json.simple.parser.JSONParser();

                    // jarr =(JSONArray)jsonparse.parse(json);
                    // jobj = jarr.getJSONObject(0);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "**" + e, Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "**" + e, Toast.LENGTH_SHORT).show();
            }
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "**" + e, Toast.LENGTH_SHORT).show();
        }
       /* catch (ParseException e){
            Toast.makeText(MainActivity.this, "**"+e, Toast.LENGTH_SHORT).show();
        }*/
        return jobj;
    }



    @Override
    public void onConnected(Bundle bundle) {
       // displayLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


    }

    public void displayLocation() {

        try
        {
            if(isConnected) {

                int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                if (result == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(CustomerSignatureActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                } else {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                    mLastLocation = LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient);

                    if (mLastLocation != null) {
                        double latitudes = mLastLocation.getLatitude();
                        double longitudes = mLastLocation.getLongitude();

                        latitude = String.valueOf(latitudes);
                        longitude = String.valueOf(longitudes);

                        URL = siteurl + "/withdiscount";

                        disableView(findViewById(R.id.llconfirm));

                        if(AllowRequest) {

                            CallVolley(URL);
                        }

                    } else {


                        AlertDialog.Builder adalert = new AlertDialog.Builder(CustomerSignatureActivity.this);
                        adalert.setMessage("Go to Settings to enable GPS");
                        adalert.setCancelable(true);
                        adalert.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            }
                        });
                        adalert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                URL = siteurl + "/withdiscount";

                                disableView(findViewById(R.id.llconfirm));

                                if(AllowRequest) {

                                    CallVolley(URL);

                                }

                            }
                        });

                        AlertDialog dialog = adalert.create();
                        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                        dialog.show();


                    }

                }

            }
            else
            {
                disableView(findViewById(R.id.llconfirm));

                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();

                int rnd= getRandomNumber(1,99);

                String rcptNo=cid+ts+String.valueOf(rnd);

                if(AllowRequest) {

                    AllowRequest = false;

                    if (myDB.insertReceipt(custid, accno, billid, paidamount, paymode, cheqno, cheqdate, bankname, email, uid, signaturestring, rdate, "0.0", "0.0", Discount, "", "false", rcptNo)) {
                        if (myDB.UpdateOutstanding(custid, paidamount, Discount)) {

                            String toa = myDB.getcustomerOutstanding(custid);

                            Intent i = new Intent(CustomerSignatureActivity.this, TransactionStatusActivity.class);
                            i.putExtra("from", from);
                            i.putExtra("Oa", toa);
                            i.putExtra("title", areatitle);
                            startActivity(i);

                            finish();

                        } else {
                            //findViewById(R.id.llconfirm).setEnabled(true);

                            enableView(findViewById(R.id.llconfirm));

                            //Toast.makeText(CustomerSignatureActivity.this, "Enable", Toast.LENGTH_SHORT).show();

                            Toast.makeText(this, "Something Went Wrong.. Go BACK & Try again..", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //findViewById(R.id.llconfirm).setEnabled(true);

                        enableView(findViewById(R.id.llconfirm));

                        Toast.makeText(this, "Something Went Wrong.. Go BACK & Try again..", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
        catch (SecurityException e)
        {
            Log.e("EXCEPTION",e.toString());
           // Toast.makeText(CustomerSignatureActivity.this, "EXCEPTION"+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation=location;

        //displayLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    displayLocation();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {


                    URL = siteurl + "/withdiscount";

                    Toast.makeText(CustomerSignatureActivity.this, latitude + "--" + longitude, Toast.LENGTH_SHORT).show();

                    //findViewById(R.id.llconfirm).setEnabled(false);

                    disableView(findViewById(R.id.llconfirm));

                    if(AllowRequest) {

                        CallVolley(URL);

                        //TestCall();
                    }

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(CustomerSignatureActivity.this, "Permission denied to access location service", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        1000).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    private class JSONAsynk extends AsyncTask<String,String,JSONObject>
    {

        private ProgressDialog pDialog;
       // public DotProgressBar dtprogoress;

        SpotsDialog spload;


        JSONObject jsn1,jsn,jsnmain;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            spload=new SpotsDialog(CustomerSignatureActivity.this,R.style.Custom);
            spload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            spload.setCancelable(true);
            spload.show();

        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                //jsonobj=makeHttpRequest(params[0]);

                HashMap<String,String> map=new HashMap<>();
                map.put("accountno",accno);
                map.put("billid",billid);
                map.put("paidamount",paidamount);
                map.put("paymentmode",paymode);
                map.put("chqnumber",cheqno);
                map.put("cheqdate",cheqdate);
                map.put("cheqbankname",bankname);
                map.put("email",email);
                map.put("notes","");
                map.put("createdby",uid);
                map.put("signature",signaturestring);
                map.put("receiptdate",rdate);
                map.put("longitude",longitude);
                map.put("latitude",latitude);
                map.put("isprint","");

                JsonObjectRequest obreq;
                obreq = new JsonObjectRequest(Request.Method.POST,params[0],new JSONObject(map),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    spload.dismiss();

                                    try
                                    {
                                        if(response.getString("status").toString().equals("True"))
                                        {
                                            String toa=response.getString("TotalOutStandingAmount");

                                            Intent i = new Intent(CustomerSignatureActivity.this, TransactionStatusActivity.class);
                                            i.putExtra("from",from);
                                            i.putExtra("Oa", toa);
                                            startActivity(i);


                                        }

                                    }
                                    catch (JSONException e)
                                    {
                                        Toast.makeText(getApplicationContext(), "Error:++"+e, Toast.LENGTH_SHORT).show();
                                    }

                                   // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(CustomerSignatureActivity.this, "error--"+e, Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(CustomerSignatureActivity.this, "errorr++"+error.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                obreq.setRetryPolicy(new DefaultRetryPolicy(600000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                // Adds the JSON object request "obreq" to the request queue
                requestQueue.add(obreq);

            }
            catch (Exception e) {
                Toast.makeText(CustomerSignatureActivity.this, "--" + e, Toast.LENGTH_SHORT).show();
            }

           return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {


            //Toast.makeText(CustomerSignatureActivity.this,json.toString(), Toast.LENGTH_SHORT).show();
           /* try
            {
                if(jsonobj.getString("status").toString().equals("True"))
                {
                    String toa=jsonobj.getString("TotalOutStandingAmount");

                    Intent i = new Intent(CustomerSignatureActivity.this, TransactionStatusActivity.class);
                    i.putExtra("from",from);
                    i.putExtra("Oa", toa);
                    startActivity(i);
                }

            }
            catch (JSONException e)
            {
                Toast.makeText(getApplicationContext(), "Error:++"+e, Toast.LENGTH_SHORT).show();
            }
            /*catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), "Error:***"+ex, Toast.LENGTH_LONG).show();
            }*/

        }

    }

    public void disableView(View v)
    {
        v.setClickable(false);
        v.setEnabled(false);
        v.setFocusable(false);
    }

    public void enableView(View v)
    {
        v.setClickable(true);
        v.setEnabled(true);
        v.setFocusable(true);
    }

    private int getRandomNumber(int min,int max) {
        return (new Random()).nextInt((max - min) + 1) + min;
    }
}
