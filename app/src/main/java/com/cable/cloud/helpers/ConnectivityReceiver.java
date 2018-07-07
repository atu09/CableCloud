package com.cable.cloud.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cable.cloud.helpers.MyApplication;
import com.cable.cloud.helpers.Utils;

/**
 * Created by MTAJ-08 on 8/13/2016.
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected());
        }
    }

    public static boolean isConnected() {
        return Utils.isInternetAvailable(MyApplication.getInstance().getApplicationContext());
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
