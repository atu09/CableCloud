package com.mtaj.mtaj_08.cableplus_new;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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

        /*startUserInactivityDetectThread(); // start the thread to detect inactivity
        new ScreenReceiver();*/

        mInstance = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/quicksend_regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

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

    /*public static void startUserInactivityDetectThread(){
        new Thread(new Runnable() {
            public void run() {

                    while (true) {
                        try {
                            Thread.sleep(10000);

                            // checks every 15sec for inactivity
                            if (isScreenOff)// || getLastInteractionTime() > 5000)
                            {

                               // Toast.makeText(MyApplication.this, "Logout...", Toast.LENGTH_SHORT).show();

                              *//* SharedPreferences pref=getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor =pref.edit();
                                editor.clear().apply();*//*

                                System.exit(0);
                               // quit();

                                //...... means USER has been INACTIVE over a period of
                                // and you do your stuff like log the user out
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }).start();
        }


    public long getLastInteractionTime() {
        return lastInteractionTime;
    }

    public long setLastInteractionTime(int lastInteraction) {
        lastInteractionTime = lastInteraction;
        return lastInteractionTime;
    }

    public class ScreenReceiver extends BroadcastReceiver {

        protected ScreenReceiver() {


            // register receiver that handles screen on and screen off logic
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(this, filter);
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenOff = true;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                isScreenOff = false;
            }
        }
    }

    public void quit() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        System.exit(0);
    }*/
}

