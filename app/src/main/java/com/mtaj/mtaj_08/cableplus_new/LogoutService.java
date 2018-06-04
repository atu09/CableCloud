package com.mtaj.mtaj_08.cableplus_new;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by MTAJ-08 on 9/14/2016.
 */
public class LogoutService extends Service {

    public static CountDownTimer timer;
    private final String TAG="Service";
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        timer = new CountDownTimer(1 *60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                //Some code
                Log.v(TAG, "Service Started");
            }

            public void onFinish() {
                Log.v(TAG, "Call Logout by Service");
                // Code for Logout
                stopSelf();
            }
        };
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
