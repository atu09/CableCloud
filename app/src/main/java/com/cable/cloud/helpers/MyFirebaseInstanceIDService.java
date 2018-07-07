package com.cable.cloud.helpers;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.cable.cloud.helpers.Utils;

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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("refresh_token", refreshedToken);
        editor.apply();
    }

}