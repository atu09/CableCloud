package com.mtaj.mtaj_08.cableplus_new;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by MTAJ-11 on 2/27/2017.
 */

public class TestLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String PREF_NAME = "LoginPref";
    String siteurl,uid,cid,aid,eid,URL;

    private GoogleApiClient mLocationClient;

    private Location mCurrentLocation;
    LocationRequest mLocationRequest;
    SharedPreferences pref;
    RequestQueue requestQueue;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {

        //if(servicesConnected()) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;

        Log.e("BOOMBOOMGOOGLECABLE",location.toString());

        //Toast.makeText(this, mCurrentLocation.getLongitude() +", "+ mCurrentLocation.getLatitude(), Toast.LENGTH_SHORT).show();

        HashMap<String,String> map=new HashMap<>();
        map.put("userId",uid);
        map.put("Longitude",String.valueOf(location.getLongitude()));
        map.put("Latitude",String.valueOf(location.getLatitude()));

        URL=siteurl+"/UpdateUserLocationfornewcollectionapp";
        CallVolleyUpdateLocation(URL,map);

    }


    @Override
    public void onCreate() {
        super.onCreate();

        try {

            pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            requestQueue = Volley.newRequestQueue(this);

            siteurl = pref.getString("SiteURL", "").toString();
            uid = pref.getString("Userid", "").toString();
            cid = pref.getString("Contracotrid", "").toString();

            if (checkPlayServices()) {
                mLocationClient = new GoogleApiClient.Builder(TestLocationService.this)
                        .addApi(LocationServices.API).addConnectionCallbacks(TestLocationService.this)
                        .addOnConnectionFailedListener(TestLocationService.this).build();

                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                mLocationRequest.setFastestInterval(10000);

                mLocationClient.connect();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            if (mLocationClient.isConnected()) {

                if (checkPlayServices()) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
                }


            } else {
                //Toast.makeText(this, "Not Connected..", Toast.LENGTH_SHORT).show();
            }
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return Service.START_STICKY;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                Toast.makeText(this, "Please Update Google Play Service of Your Device..", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }


    public void CallVolleyUpdateLocation(String a,HashMap<String,String> map)
    {


        try {
            //jsonobj=makeHttpRequest(params[0]);

            JsonObjectRequest obreq;
            obreq = new JsonObjectRequest(Request.Method.POST,a,new JSONObject(map),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {



                                try
                                {
                                    if(response.getString("status").toString().equals("True"))
                                    {
                                        //Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                    else
                                    {
                                        //Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                catch (JSONException e)
                                {
                                    //Toast.makeText(getApplicationContext(), "JSON:++"+e, Toast.LENGTH_SHORT).show();
                                }

                                // Toast.makeText(CustomerSignatureActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                //Toast.makeText(getApplicationContext(), "error--"+e, Toast.LENGTH_SHORT).show();
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


    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (mLocationClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
                mLocationClient.disconnect();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
