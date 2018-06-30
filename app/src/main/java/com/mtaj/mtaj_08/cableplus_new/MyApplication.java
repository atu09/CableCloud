package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;


/**
 * Created by MTAJ-08 on 8/13/2016.
 */
public class MyApplication extends MultiDexApplication {

    private static final String PREF_NAME = "LoginPref";

    private static MyApplication mInstance;
    private int lastInteraction;
    private static Boolean isScreenOff = false;
    private long lastInteractionTime;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }

}

