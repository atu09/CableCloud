package com.mtaj.mtaj_08.cableplus_new;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by MTAJ-11 on 2/22/2017.
 */

public class TrackLocationService extends Service implements GpsStatus.Listener{

    private static final String PREF_NAME = "LoginPref";

    private static final String TAG = "BOOMBOOMGPS_CABLE";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 0f;

    String siteurl,uid,cid,aid,eid,URL;

    SharedPreferences pref;
    private GpsStatus mStatus;

    RequestQueue requestQueue;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            Toast.makeText(getApplicationContext(),location.getLatitude()+"--"+location.getLongitude(), Toast.LENGTH_SHORT).show();

            HashMap<String,String> map=new HashMap<>();
            map.put("userId",uid);
            map.put("Longitude",String.valueOf(location.getLongitude()));
            map.put("Latitude",String.valueOf(location.getLatitude()));

            URL=siteurl+"/UpdateUserLocationfornewcollectionapp";
            CallVolleyUpdateLocation(URL,map);

        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        //initializeLocationManager();

        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");

        try {

            pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            requestQueue = Volley.newRequestQueue(this);

            siteurl = pref.getString("SiteURL", "").toString();
            uid = pref.getString("Userid", "").toString();
            cid = pref.getString("Contracotrid", "").toString();

            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.addGpsStatusListener(this);

            initializeLocationManager();

        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            try {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        mLocationListeners[0]);

            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "gps provider does not exist " + ex.getMessage());
            }
        }
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
    public void onGpsStatusChanged(int event) {

        mStatus = mLocationManager.getGpsStatus(mStatus);

        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                // Do Something with mStatus info

                //Toast.makeText(this, "gps started..", Toast.LENGTH_SHORT).show();

                initializeLocationManager();

                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                // Do Something with mStatus info

                //Toast.makeText(this, "gps stopped..", Toast.LENGTH_SHORT).show();

                break;

            case GpsStatus.GPS_EVENT_FIRST_FIX:
                // Do Something with mStatus info

                //Toast.makeText(this, "gps first fix..", Toast.LENGTH_SHORT).show();

                initializeLocationManager();

                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                // Do Something with mStatus info

                //Toast.makeText(this, "gps satelite..", Toast.LENGTH_SHORT).show();

                break;
        }

    }

}
