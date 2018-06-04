package com.mtaj.mtaj_08.cableplus_new;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by MTAJ-08 on 8/30/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sharedPreferences.edit().putString("Refreshed token: ", refreshedToken).apply();

        final Intent intent = new Intent("tokenReceiver");
        // You can also include some extra data.
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        intent.putExtra("token", refreshedToken);
        broadcastManager.sendBroadcast(intent);
    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }
}