package com.mtaj.mtaj_08.cableplus_new;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by MTAJ-11 on 2/22/2017.
 */

public class ScreenReceiver extends BroadcastReceiver {

    private boolean screenOff;

    private Context context;

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 3000;
    private static final float LOCATION_DISTANCE = 0f;

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public void onReceive(Context context, Intent intent) {

        initializeLocationManager(context);

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;

            Log.e("SCREENSTATE","ScreenOff");

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;

            Log.e("SCREENSTATE","ScreenOn");
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        /*Intent i = new Intent(context, TrackLocationService.class);
        i.putExtra("screen_state", screenOff);
        context.startService(i);*/
    }



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


    private void initializeLocationManager(Context context) {

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            Log.e(TAG, "initializeLocationManager");
        }
    }



}
