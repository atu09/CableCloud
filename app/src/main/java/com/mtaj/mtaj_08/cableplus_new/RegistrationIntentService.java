package com.mtaj.mtaj_08.cableplus_new;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by Atirek on 3/22/2016.
 */

public class RegistrationIntentService extends IntentService {



    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_TOKEN = "gcmToken";

    // abbreviated tag name
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = "1058933468760";

        // Fetch token here
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);

           // String token = instanceID.getToken(senderId,
                 //   GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            //String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                  //  GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.d(TAG, "GCM Registration Token: " + token);

            Toast.makeText(RegistrationIntentService.this, token, Toast.LENGTH_SHORT).show();

            // save token
            sharedPreferences.edit().putString(GCM_TOKEN, token).apply();

            // pass along this data
            sendRegistrationToServer(token);
        } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) {
        // send network request


    }

}
