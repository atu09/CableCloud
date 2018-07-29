package com.cable.cloud.helpers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by MTAJ-11 on 2/27/2017.
 */

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String PREF_NAME = "LoginPref";
    String siteUrl, uid, cid, aid, eid, URL;

    private GoogleApiClient mLocationClient;

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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e("BOOMBOOMGOOGLECABLE", location.toString());

        HashMap<String, String> map = new HashMap<>();
        map.put("userId", uid);
        map.put("Longitude", String.valueOf(location.getLongitude()));
        map.put("Latitude", String.valueOf(location.getLatitude()));

        URL = siteUrl + "/UpdateUserLocationfornewcollectionapp";
        CallVolleyUpdateLocation(URL, map);

    }


    @Override
    public void onCreate() {
        super.onCreate();

        try {

            pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            requestQueue = Volley.newRequestQueue(this);

            siteUrl = pref.getString("SiteURL", "");
            uid = pref.getString("Userid", "");
            cid = pref.getString("Contracotrid", "");

            if (checkPlayServices()) {
                mLocationClient = new GoogleApiClient.Builder(LocationService.this)
                        .addApi(LocationServices.API).addConnectionCallbacks(LocationService.this)
                        .addOnConnectionFailedListener(LocationService.this).build();

                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                mLocationRequest.setFastestInterval(10000);

                mLocationClient.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mLocationClient.isConnected() && checkPlayServices()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return Service.START_STICKY;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
        }

        return Service.START_STICKY;
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(this, "Please Update Google Play Service of Your Device..", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }


    public void CallVolleyUpdateLocation(String a, HashMap<String, String> map) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, a, new JSONObject(map), null, null);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (mLocationClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
                mLocationClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
