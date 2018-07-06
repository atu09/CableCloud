package com.mtaj.mtaj_08.cableplus_new;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mtaj.mtaj_08.cableplus_new.helpers.Utils;

/**
 * Created by MTAJ-08 on 8/30/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Utils.checkLog(TAG, "Refreshed token: " + refreshedToken, null);
        sharedPreferences.edit().putString("refresh_token", refreshedToken).apply();
    }

}